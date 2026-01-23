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
package io.agentscope.examples.fightlandlord.entity;

import io.agentscope.examples.fightlandlord.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 斗地主游戏状态。
 */
public class GameState {
    private final List<Player> players;
    private Player landlord; // 地主
    private List<Card> bottomCards; // 底牌
    private int currentPlayerIndex; // 当前出牌玩家索引
    private List<Card> lastPlayedCards; // 上次出的牌
    private Player lastPlayer; // 上次出牌的玩家
    private int roundCount; // 回合数
    private int multiplier; // 游戏倍数

    public GameState(List<Player> players) {
        this.players = new ArrayList<>(players);
        this.currentPlayerIndex = 0;
        this.roundCount = 0;
        this.lastPlayedCards = null;
        this.lastPlayer = null;
        this.multiplier = 1; // 初始倍数为1
    }

    // Getters
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Player getLandlord() {
        return landlord;
    }

    public List<Card> getBottomCards() {
        return bottomCards != null ? new ArrayList<>(bottomCards) : null;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Card> getLastPlayedCards() {
        return lastPlayedCards != null ? new ArrayList<>(lastPlayedCards) : null;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public List<Player> getFarmers() {
        List<Player> farmers = new ArrayList<>();
        for (Player player : players) {
            if (player.isFarmer()) {
                farmers.add(player);
            }
        }
        return farmers;
    }

    // State modifiers
    public void setLandlord(Player landlord) {
        this.landlord = landlord;
        landlord.setRole(PlayerRole.LANDLORD);
        for (Player player : players) {
            if (player != landlord) {
                player.setRole(PlayerRole.FARMER);
            }
        }
    }

    public void setBottomCards(List<Card> cards) {
        this.bottomCards = new ArrayList<>(cards);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void nextRound() {
        roundCount ++;
    }

    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    public void setLastPlayedCards(List<Card> cards, Player player) {
        this.lastPlayedCards = cards != null ? new ArrayList<>(cards) : null;
        this.lastPlayer = player;
    }

    public void clearLastPlayedCards() {
        this.lastPlayedCards = null;
        this.lastPlayer = null;
    }

    // Game end checks
    public boolean checkGameEnd() {
        for (Player player : players) {
            if (player.hasNoCards()) {
                return true;
            }
        }
        return false;
    }

    public Player getWinner() {
        for (Player player : players) {
            if (player.hasNoCards()) {
                return player;
            }
        }
        return null;
    }

    public boolean isLandlordWin() {
        Player winner = getWinner();
        return winner != null && winner.isLandlord();
    }

    public boolean isFarmerWin() {
        Player winner = getWinner();
        return winner != null && winner.isFarmer();
    }

    public Player findPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 重新发牌（当所有玩家都不叫地主时使用）
     */
    public void redealCards(List<List<Card>> dealtCards) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setHandCards(dealtCards.get(i));
        }
        this.bottomCards = new ArrayList<>(dealtCards.get(3));
    }

    /**
     * 获取上一个出牌玩家（顺时针方向）
     * @param currentPlayer 当前出牌玩家
     * @return 上一个出牌玩家
     */
    public Player getUpPlayer(Player currentPlayer) {
        int upPlayerIndex = (players.indexOf(currentPlayer) - 1 + players.size()) % players.size();
        return players.get(upPlayerIndex);
    }

    public LinkedBlockingQueue<Player> createRoundNextPlayer(Player currentPlayer) {
        int nextPlayerIndex = (players.indexOf(currentPlayer) + 1) % players.size();
        return createRoundPlayer(nextPlayerIndex);
    }

    public LinkedBlockingQueue<Player> createRoundPlayer(int startIndex) {
        LinkedBlockingQueue<Player> queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get((startIndex + i) % players.size());
            queue.add(player);
        }
        return queue;
    }
}
