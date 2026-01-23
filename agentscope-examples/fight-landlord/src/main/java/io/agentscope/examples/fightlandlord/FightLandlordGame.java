/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agentscope.examples.fightlandlord;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.formatter.dashscope.DashScopeMultiAgentFormatter;
import io.agentscope.core.hook.Hook;
import io.agentscope.core.hook.HookEvent;
import io.agentscope.core.hook.PostCallEvent;
import io.agentscope.core.hook.PreCallEvent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.pipeline.MsgHub;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.examples.fightlandlord.entity.GameState;
import io.agentscope.examples.fightlandlord.entity.Player;
import io.agentscope.examples.fightlandlord.model.BidModel;
import io.agentscope.examples.fightlandlord.model.PlayCardModel;
import io.agentscope.examples.fightlandlord.prompt.PromptProvider;
import io.agentscope.examples.fightlandlord.util.CardComparator;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static io.agentscope.examples.fightlandlord.FightLandlordGameConfig.MAX_ROUNDS;

/**
 * 斗地主游戏主类。
 */
public class FightLandlordGame {

    private final PromptProvider promptProvider;
    private DashScopeChatModel model;
    private GameState gameState;

    public FightLandlordGame() {
        this.promptProvider = new PromptProvider();
    }

    /**
     * 启动游戏。
     */
    public void start() throws Exception {
        System.out.println("=== 斗地主游戏开始 ===");
        System.out.println();

        // 初始化模型
        String apiKey = System.getenv("DASHSCOPE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("请设置环境变量 DASHSCOPE_API_KEY");
        }

        model = DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName(FightLandlordGameConfig.DEFAULT_MODEL)
                .formatter(new DashScopeMultiAgentFormatter())
                .stream(false)
                .build();

        // 初始化游戏
        gameState = initializeGame();
        printGameState();

        // 叫地主阶段（如果所有玩家都不叫，则重新发牌重新叫牌，直到有人叫地主为止）
        Deck deck = new Deck();
        while (!biddingPhase()) {
            System.out.println("所有玩家都不叫地主，重新发牌...");
            System.out.println();
            deck.shuffle();
            List<List<Card>> dealtCards = deck.deal();
            gameState.redealCards(dealtCards);
            printGameState();
        }

        // 出牌阶段
        playingPhase();

        // 宣布结果
        announceWinner();
    }

    /**
     * 初始化游戏：创建玩家、发牌。
     */
    private GameState initializeGame() throws IOException {
        System.out.println("正在初始化游戏...");

        // 创建玩家
        List<Player> players = new ArrayList<>();
        String[] playerNames = {"玩家1", "玩家2", "玩家3"};

        for (String name : playerNames) {
            ReActAgent agent = ReActAgent.builder()
                    .name(name)
                    .sysPrompt(promptProvider.getSystemPrompt(name))
                    .model(model)
                    .memory(new InMemoryMemory())
                    .toolkit(new Toolkit())
                    .hooks(List.of(new Hook() {
                                @Override
                                public <T extends HookEvent> Mono<T> onEvent(T event) {
                                    if (event instanceof PreCallEvent preCallEvent) {
                                        List<String> textContentList = preCallEvent.getInputMessages().stream().map(Msg::getTextContent).toList();
//                                        System.out.println("      > " + preCallEvent.getAgent().getName() + " Input: " + textContentList);
                                    }
                                    if (event instanceof PostCallEvent postCallEvent) {
                                        String textContent = postCallEvent.getFinalMessage().getTextContent();
//                                        System.out.println("      > " + postCallEvent.getAgent().getName() + " Output: " + textContent);
                                    }
                                    return Mono.just(event);
                                }
                            }
                    ))
                    .build();

            Player player = Player.builder()
                    .agent(agent)
                    .name(name)
                    .build();

            players.add(player);
        }

        // 发牌
        Deck deck = new Deck();
        deck.shuffle();
        List<List<Card>> dealtCards = deck.deal();

        for (int i = 0; i < players.size(); i++) {
            players.get(i).setHandCards(dealtCards.get(i));
        }

        GameState state = new GameState(players);
        state.setBottomCards(dealtCards.get(3)); // 底牌

        System.out.println("游戏初始化完成！");
        System.out.println();

        return state;
    }

    /**
     * 叫地主阶段（按照新规则）。
     *
     * @return true表示成功确定地主，false表示所有玩家都不叫（需要重新发牌）
     */
    private boolean biddingPhase() {
        System.out.println("=== 一、叫地主阶段 ===");

        List<Player> players = gameState.getPlayers();
        
        // 第一轮叫牌的玩家由系统随机选定
        Random random = new Random();
        int firstBidderIndex = random.nextInt(players.size());
        Player firstBidder = players.get(firstBidderIndex);
        
        System.out.printf("%s 被随机选为第一个叫牌的玩家。%n", firstBidder.getName());

        // 重新排列玩家顺序，从随机选定的玩家开始
        LinkedBlockingQueue<Player> firstPlayers = gameState.createRoundPlayer(firstBidderIndex);

        // 使用MsgHub让玩家讨论
        try (MsgHub biddingHub = MsgHub.builder()
                .name("BiddingPhase")
                .participants(gameState.getPlayers().stream()
                        .map(Player::getAgent)
                        .toArray(ReActAgent[]::new))
                .announcement(promptProvider.createBiddingAnnouncement(gameState))
                .enableAutoBroadcast(true)
                .build()) {

            biddingHub.enter().block();
            biddingHub.setAutoBroadcast(false);

            // 第一阶段：叫地主
            Player firstBidPlayer = null;
            {
                List<String> firstRoundMsgList = new ArrayList<>();
                firstRoundMsgList.add("第一阶段：叫地主");

                while (!firstPlayers.isEmpty()) {
                    Player player = firstPlayers.poll();
                    String prompt = "======\n" + "你的手牌：" + player.getHandCards() + "\n" +
                            "请选择是否需要叫地主？isBid = true/false\n";

                    Msg msg = Msg.builder()
                            .name(player.getName())
                            .role(MsgRole.USER)
                            .content(TextBlock.builder().text(prompt).build())
                            .build();
                    Msg response = player.getAgent().call(msg, BidModel.class).block();
                    BidModel bidModel = response.getStructuredData(BidModel.class);

                    if (Boolean.TRUE.equals(bidModel.isBid)) {
                        firstBidPlayer = player;
                        firstRoundMsgList.add(String.format("%s 叫地主", player.getName()));
                        System.out.printf("%s 选择叫地主，叫牌阶段结束。%n", player.getName());
                        break; // 立即结束叫牌
                    } else {
                        firstRoundMsgList.add(String.format("%s 不叫", player.getName()));
                        System.out.printf("%s 不叫%n", player.getName());
                    }
                }

                Msg broadcastMsg = Msg.builder().name("system").role(MsgRole.USER).content(
                        TextBlock.builder().text(String.join("\n", firstRoundMsgList)).build()
                ).build();
                biddingHub.broadcast(broadcastMsg).block();

                // 如果所有玩家都不叫，返回false
                if (firstBidPlayer == null) {
                    System.out.println("所有玩家都不叫地主。");
                    return false;
                }
            }

            System.out.println("=== 二、抢地主阶段 ===");

            // 第二阶段：抢地主
            // 从叫地主的玩家之后开始，按顺序给每个玩家一次抢地主的机会
            // 排除已经"不叫"的玩家
            List<Player> secondBidPlayers = new ArrayList<>();
            {
                List<String> secondRoundMsgList = new ArrayList<>();
                secondRoundMsgList.add("第二阶段：抢地主");

                LinkedBlockingQueue<Player> secondPlayers = gameState.createRoundNextPlayer(firstBidPlayer);

                while (!secondPlayers.isEmpty()) {
                    Player nextPlayer = secondPlayers.poll();
                    if (nextPlayer.equals(firstBidPlayer)) {
                        // 忽略第一轮叫地主的玩家
                        continue;
                    }

                    String prompt = "======\n" + "你的手牌：" + nextPlayer.getHandCards() + "\n" +
                            String.format("当前叫地主的玩家：%s\n", firstBidPlayer.getName()) +
                            String.format("当前倍数：%d\n", gameState.getMultiplier()) +
                            "请选择是否需要抢地主？isBid = true/false\n" +
                            "注意：每抢地主一次，游戏倍数 *2。";

                    Msg msg = Msg.builder()
                            .name(nextPlayer.getName())
                            .role(MsgRole.USER)
                            .content(TextBlock.builder().text(prompt).build())
                            .build();
                    Msg response = nextPlayer.getAgent().call(msg, BidModel.class).block();
                    BidModel bidModel = response.getStructuredData(BidModel.class);

                    if (Boolean.TRUE.equals(bidModel.isBid)) {
                        secondBidPlayers.add(nextPlayer);
                        gameState.setMultiplier(gameState.getMultiplier() * 2);
                        System.out.printf("%s 选择抢地主，倍数变为 %d%n", nextPlayer.getName(), gameState.getMultiplier());
                        secondRoundMsgList.add(String.format("%s 抢地主（倍数变为 %d）", nextPlayer.getName(), gameState.getMultiplier()));
                    } else {
                        System.out.printf("%s 不抢%n", nextPlayer.getName());
                        secondRoundMsgList.add(String.format("%s 不抢地主", nextPlayer.getName()));
                    }
                }

                Msg broadcastMsg = Msg.builder().name("system").role(MsgRole.USER).content(
                        TextBlock.builder().text(String.join("\n", secondRoundMsgList)).build()
                ).build();
                biddingHub.broadcast(broadcastMsg).block();

                if (secondBidPlayers.isEmpty()) {
                    // 没有人抢地主，第一轮叫的玩家成为地主
                    finalLandlord(firstBidPlayer, biddingHub);
                    return true;
                }
            }

            System.out.println("=== 三、回抢地主阶段 ===");

            // 第三阶段：是否抢回地主
            {

                List<String> thirdRoundMsgList = new ArrayList<>();
                thirdRoundMsgList.add("第三阶段：抢回地主");

                String prompt = "======\n" + "你的手牌：" + firstBidPlayer.getHandCards() + "\n" +
                        String.format("当前抢地主的玩家：%s\n", secondBidPlayers.stream().map(Player::getName).collect(Collectors.toList())) +
                        String.format("当前倍数：%d\n", gameState.getMultiplier()) +
                        "请选择是否需要抢回地主？isBid = true/false\n" +
                        "注意：抢回地主，游戏倍数 *2。";

                Msg msg = Msg.builder()
                        .name(firstBidPlayer.getName())
                        .role(MsgRole.USER)
                        .content(TextBlock.builder().text(prompt).build())
                        .build();
                Msg response = firstBidPlayer.getAgent().call(msg, BidModel.class).block();
                BidModel bidModel = response.getStructuredData(BidModel.class);

                if (Boolean.TRUE.equals(bidModel.isBid)) {
                    gameState.setMultiplier(gameState.getMultiplier() * 2);
                    System.out.printf("%s 选择抢回地主，倍数变为 %d%n", firstBidPlayer.getName(), gameState.getMultiplier());
                    thirdRoundMsgList.add(String.format("%s 抢地主（倍数变为 %d）", firstBidPlayer.getName(), gameState.getMultiplier()));
                    finalLandlord(firstBidPlayer, biddingHub);
                } else {
                    System.out.printf("%s 不抢回地主%n", firstBidPlayer.getName());
                    thirdRoundMsgList.add(String.format("%s 不抢回地主", firstBidPlayer.getName()));
                    finalLandlord(secondBidPlayers.get(0), biddingHub);
                }

                Msg broadcastMsg = Msg.builder().name("system").role(MsgRole.USER).content(
                        TextBlock.builder().text(String.join("\n", thirdRoundMsgList)).build()
                ).build();
                biddingHub.broadcast(broadcastMsg).block();
            }

            return true;
        }
    }

    private void finalLandlord(Player player, MsgHub biddingHub) {
        // 确定最终地主
        gameState.setLandlord(player);
        player.addCards(gameState.getBottomCards());
        System.out.printf("=== %s 成为地主 ===%n", player.getName());
        System.out.printf("最终倍数：%d%n", gameState.getMultiplier());
        System.out.printf("底牌：%s%n", CardComparator.cardsToString(gameState.getBottomCards()));
        System.out.printf("地主现在有 %d 张牌 - %s%n", player.getHandCards().size(), player.getHandCards());
        if (null != biddingHub) {
            Msg broadcastMsg = Msg.builder().name("system").role(MsgRole.USER).content(
                    TextBlock.builder().text(String.format("%s 成为地主，打牌开始", player.getName())).build()
            ).build();
            biddingHub.broadcast(broadcastMsg).block();
        }
        System.out.println();
    }


    /**
     * 检查是否所有人都过牌了（除了上次出牌的玩家）。
     *
     * @param consecutivePassCount 连续过牌的人数
     * @param gameState 游戏状态
     * @return true表示所有人都过牌了
     */
    private boolean checkAllPassed(int consecutivePassCount, GameState gameState) {
        if (gameState.getLastPlayer() == null) {
            return false; // 没有人出过牌，不能判断
        }
        
        // 如果连续过牌的人数等于其他玩家数量（除了上次出牌的玩家），说明所有人都过牌了
        int otherPlayerCount = gameState.getPlayers().size() - 1;
        return consecutivePassCount >= otherPlayerCount;
    }

    /**
     * 重置到上次出牌的玩家，并清空上次出牌记录。
     *
     * @param gameState 游戏状态
     */
    private void resetToLastPlayer(GameState gameState) {
        Player lastPlayer = gameState.getLastPlayer();
        if (lastPlayer != null) {
            // 先保存lastPlayer的索引
            int lastPlayerIndex = gameState.getPlayers().indexOf(lastPlayer);
            // 清空上次出牌记录（但lastPlayer会在下次出牌时更新）
            gameState.clearLastPlayedCards();
            // 重置到上次出牌的玩家
            gameState.setCurrentPlayerIndex(lastPlayerIndex);
            // 回合数增加
            gameState.nextRound();;
        }
    }

    /**
     * 出牌阶段。
     */
    private void playingPhase() {
        System.out.println("=== 出牌阶段开始 ===");
        System.out.println();

        // 设置地主先出牌
        Player landlord = gameState.getLandlord();
        int landlordIndex = gameState.getPlayers().indexOf(landlord);
        gameState.setCurrentPlayerIndex(landlordIndex);

        // 使用MsgHub让玩家看到其他玩家的出牌
        try (MsgHub playingHub = MsgHub.builder()
                .name("PlayingPhase")
                .participants(gameState.getPlayers().stream()
                        .map(Player::getAgent)
                        .toArray(ReActAgent[]::new))
                .announcement(promptProvider.createPlayingAnnouncement(gameState))
                .enableAutoBroadcast(true)
                .build()) {

            playingHub.enter().block();
            playingHub.setAutoBroadcast(false);

            // 游戏循环
            int consecutivePassCount = 0; // 连续过牌的人数
            int lastCountCount = -1;
            for (int round = 0; round < MAX_ROUNDS; round++) {
                if (gameState.checkGameEnd()) {
                    break;
                }
                boolean newRoundFirstPlayer = lastCountCount != gameState.getRoundCount();
                lastCountCount = gameState.getRoundCount();

                Player currentPlayer = gameState.getCurrentPlayer();
                System.out.printf("--- 第 %d 回合，%s 出牌 ---%n", gameState.getRoundCount(), currentPlayer.getSimpleName());
                System.out.printf("%s 当前手牌：%s%n", currentPlayer.getSimpleName(), CardComparator.cardsToString(currentPlayer.getHandCards()));
                if (newRoundFirstPlayer) {
                    System.out.println("✨首家出牌✨");
                }

                List<Card> cardsToPlay = this.playCard(currentPlayer, newRoundFirstPlayer);
                if (null == cardsToPlay) {
                    System.out.printf("%s 选择过牌%n", currentPlayer.getSimpleName());
                    playingHub.broadcast(Msg.builder().name("system").role(MsgRole.ASSISTANT).content(TextBlock.builder().text(String.format("%s 选择过牌", currentPlayer.getSimpleName())).build()).build()).block();
                    consecutivePassCount++;
                    if (checkAllPassed(consecutivePassCount, gameState)) {
                        resetToLastPlayer(gameState);
                        consecutivePassCount = 0;
                        System.out.println("所有其他玩家都过牌，开始新的一轮。");
                        continue;
                    }
                } else {
                    // 有人出牌，则重新计算过牌玩家数
                    consecutivePassCount = 0;
                    // 记录出牌信息
                    currentPlayer.removeCards(cardsToPlay);
                    gameState.setLastPlayedCards(cardsToPlay, currentPlayer);

                    String msg = String.format("%s 出牌：%s - %s", currentPlayer.getSimpleName(), CardComparator.getCardType(cardsToPlay).getDisplayName(), CardComparator.cardsToString(cardsToPlay));
                    playingHub.broadcast(Msg.builder().name("system").role(MsgRole.ASSISTANT).content(TextBlock.builder().text(msg).build()).build()).block();
                    System.out.println(msg);
                    System.out.printf("%s 剩余手牌：%d张 - %s%n", currentPlayer.getSimpleName(), currentPlayer.getHandCards().size(), currentPlayer.getHandCards());

                    if (currentPlayer.hasNoCards()) {
                        System.out.printf("*** %s 出完所有牌！***%n", currentPlayer.getSimpleName());
                        break;
                    }
                }

                gameState.nextPlayer();
            }
        }
    }

    /**
     * 玩家出牌
     * @param player 玩家
     * @return 玩家出牌的牌
     */
    private List<Card> playCard(Player player, boolean newRoundFirstPlayer) {
        // 玩家出牌
        Msg playPrompt = promptProvider.createPlayCardPrompt(gameState);
        Msg response = player.getAgent().call(playPrompt, PlayCardModel.class).block();
        PlayCardModel playModel = response.getStructuredData(PlayCardModel.class);

        if (Boolean.TRUE.equals(playModel.willPlay) && playModel.cards != null && !playModel.cards.isEmpty()) {
            List<Card> cardsToPlay = CardComparator.parseCards(playModel.cards, player.getHandCards());
            if (CollectionUtils.isEmpty(cardsToPlay)) {
                // 玩家出的牌不存在，需要重新出牌
                player.setPlayCardFailReason(String.format("出牌失败：你出的牌：%s 不存在，请检查后重新出牌", CardComparator.cardsToString(cardsToPlay)));
                return this.playCard(player, newRoundFirstPlayer);
            }

            CardType cardType = CardComparator.getCardType(cardsToPlay);
            if (null == cardType) {
                // 玩家出的牌类型无效，需要重新出牌
                player.setPlayCardFailReason(String.format("出牌失败：你出的牌：%s 类型无效，请检查后重新出牌", CardComparator.cardsToString(cardsToPlay)));
                return this.playCard(player, newRoundFirstPlayer);
            }

            List<Card> lastPlayedCards = gameState.getLastPlayedCards();
            if (null != lastPlayedCards) {
                if (!CardComparator.canBeat(cardsToPlay, lastPlayedCards)) {
                    // 玩家出的牌类型不能压过上家，重新出牌
                    player.setPlayCardFailReason(String.format("出牌失败：你出的牌：%s 不能压过上家，请检查后重新出牌", CardComparator.cardsToString(cardsToPlay)));
                    return this.playCard(player, newRoundFirstPlayer);
                }
            }

            player.resetPlayCardFailReason();
            return cardsToPlay;
        } else if (newRoundFirstPlayer) {
            // 新轮第一玩家不能过牌
            player.setPlayCardFailReason("出牌失败：新轮第一玩家不能不出牌");
            return this.playCard(player, newRoundFirstPlayer);
        }
        return null;
    }

    /**
     * 宣布游戏结果。
     */
    private void announceWinner() {
        System.out.println();
        System.out.println("=== 游戏结束 ===");

        Player winner = gameState.getWinner();
        if (winner != null) {
            if (winner.isLandlord()) {
                System.out.printf("*** 地主 %s 获胜！***%n", winner.getName());
            } else {
                System.out.printf("*** 农民 %s 获胜！农民方获胜！***%n", winner.getName());
            }
        } else {
            System.out.println("游戏达到最大回合数，平局。");
        }

        System.out.println();
        System.out.println("最终手牌情况：");
        for (Player player : gameState.getPlayers()) {
            System.out.printf("%s（%s）：%d张 - %s%n",
                    player.getName(),
                    player.getRole().getDisplayName(),
                    player.getHandCards().size(),
                    CardComparator.cardsToString(player.getHandCards()));
        }
    }

    /**
     * 打印游戏状态。
     */
    private void printGameState() {
        System.out.println("当前游戏状态：");
        for (Player player : gameState.getPlayers()) {
            System.out.printf("- %s：%d张牌 - %s%n", player.getName(), player.getHandCards().size(), player.getHandCards());
        }
        System.out.printf("底牌：%s%n", CardComparator.cardsToString(gameState.getBottomCards()));
        System.out.println();
    }
}
