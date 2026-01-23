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
package io.agentscope.examples.fightlandlord.prompt;

import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.examples.fightlandlord.Card;
import io.agentscope.examples.fightlandlord.entity.GameState;
import io.agentscope.examples.fightlandlord.entity.Player;
import io.agentscope.examples.fightlandlord.util.CardComparator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 提供斗地主游戏各个阶段的Prompt。
 */
public class PromptProvider {

    /**
     * 获取玩家的系统提示词。
     */
    public String getSystemPrompt(String playerName) throws IOException {
        String rule;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("player_prompt.md")) {
            rule = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        rule = rule.replaceAll("__PLAYER_NAME__", playerName);
        return rule;
    }

    /**
     * 创建出牌阶段的Prompt。
     */
    public Msg createPlayCardPrompt(GameState gameState) {
        Player currentPlayer = gameState.getCurrentPlayer();
        StringBuilder prompt = new StringBuilder();

        String playCardFailReason = currentPlayer.getPlayCardFailReason();
        if (null != playCardFailReason) {
            prompt.append("=== 重新出牌阶段 ===\n\n");
            prompt.append(playCardFailReason);
        } else {
            prompt.append("=== 出牌阶段 ===\n\n");
            // 显示游戏状态
            prompt.append(String.format("当前回合：第 %d 回合\n", gameState.getRoundCount()));
            prompt.append(String.format("当前玩家：%s\n", currentPlayer.getName()));
            boolean upPlayerIsLandlord = gameState.getUpPlayer(currentPlayer).isLandlord();
            if (currentPlayer.isFarmer()) {
                prompt.append(String.format("你的身份：%s 地主的%s\n", currentPlayer.getRole().getDisplayName(), upPlayerIsLandlord ? "下家" : "上家"));
            } else {
                prompt.append(String.format("你的身份：%s\n", currentPlayer.getRole().getDisplayName()));
            }

            prompt.append(String.format("你的手牌：%s\n", currentPlayer.getHandCards()));
            prompt.append("\n\n");

            // 显示上家出牌情况
            List<Card> lastCards = gameState.getLastPlayedCards();
            Player lastPlayer = gameState.getLastPlayer();
            if (lastCards != null && !lastCards.isEmpty() && lastPlayer != null) {
                prompt.append(String.format("上家 %s 出的牌：\n", lastPlayer.getSimpleName()));
                prompt.append(CardComparator.cardsToString(lastCards));
                prompt.append("\n");
                prompt.append(String.format("牌型：%s\n\n", CardComparator.getCardType(lastCards).getDisplayName()));
                prompt.append("你需要出能压过上家的牌，或者选择过牌。\n\n");
            } else {
                prompt.append("上家没有出牌，你可以任意出牌。\n\n");
            }

            // 显示其他玩家手牌数量
            prompt.append("其他玩家手牌数量：\n");
            for (Player player : gameState.getPlayers()) {
                if (player != currentPlayer) {
                    prompt.append(String.format("- %s：%d张\n", player.getSimpleName(), player.getHandCards().size()));
                }
            }
            prompt.append("\n");

            prompt.append("请根据你的手牌和游戏情况，决定出牌或过牌。");
            prompt.append("\n出牌时，请用字符串列表表示要出的牌，例如：[\"♠3\", \"♥5\"] 或 [\"小王\", \"大王\"]");
        }

        return Msg.builder()
                .name(currentPlayer.getName())
                .role(MsgRole.USER)
                .content(TextBlock.builder().text(prompt.toString()).build())
                .build();
    }

    /**
     * 创建叫地主讨论公告。
     */
    public Msg createBiddingAnnouncement(GameState gameState) {
        StringBuilder announcement = new StringBuilder();
        announcement.append("=== 叫地主阶段开始 ===\n\n");
        announcement.append("请各位玩家根据手牌情况，决定是否叫地主。\n");
        announcement.append("叫地主规则：\n");
        announcement.append("- 系统随机一名玩家先开始叫地主\n");
        announcement.append("- 每个玩家可以叫或不叫\n");
        announcement.append("- 如果有人叫了地主，其他玩家可以抢地主\n");
        announcement.append("- 如果被抢了，原叫地主的人可以抢回\n");
        announcement.append("- 如果所有玩家都不叫，则重新发牌\n");

        return Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(TextBlock.builder().text(announcement.toString()).build())
                .build();
    }

    /**
     * 创建出牌阶段公告。
     */
    public Msg createPlayingAnnouncement(GameState gameState) {
        StringBuilder announcement = new StringBuilder();
        announcement.append("=== 出牌阶段开始 ===\n\n");
        Player landlord = gameState.getLandlord();
        if (landlord != null) {
            announcement.append(String.format("地主：%s（%d张牌）\n", landlord.getName(), landlord.getHandCards().size()));
            announcement.append("农民：\n");
            for (Player farmer : gameState.getFarmers()) {
                announcement.append(String.format("- %s（%d张牌）\n", farmer.getName(), farmer.getHandCards().size()));
            }
            announcement.append("\n");
            announcement.append(String.format("地主 %s 先出牌。\n", landlord.getName()));
        }

        return Msg.builder()
                .name("system")
                .role(MsgRole.SYSTEM)
                .content(TextBlock.builder().text(announcement.toString()).build())
                .build();
    }

}
