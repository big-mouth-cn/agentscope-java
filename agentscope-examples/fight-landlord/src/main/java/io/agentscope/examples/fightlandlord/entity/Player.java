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

import io.agentscope.core.ReActAgent;
import io.agentscope.examples.fightlandlord.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 斗地主游戏中的玩家。
 */
public class Player {

    private final ReActAgent agent;
    private final String name;
    private List<Card> handCards; // 手牌
    private PlayerRole role; // 身份：地主或农民
    /**
     * 出牌失败的原因
     */
    private String playCardFailReason;

    private Player(Builder builder) {
        this.agent = builder.agent;
        this.name = builder.name;
        this.handCards = new ArrayList<>();
        this.role = PlayerRole.FARMER; // 默认为农民
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public ReActAgent getAgent() {
        return agent;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHandCards() {
        return new ArrayList<>(handCards);
    }

    public PlayerRole getRole() {
        return role;
    }

    public String getPlayCardFailReason() {
        return playCardFailReason;
    }

    public void setPlayCardFailReason(String playCardFailReason) {
        this.playCardFailReason = playCardFailReason;
    }

    public void resetPlayCardFailReason() {
        this.playCardFailReason = null;
    }

    public boolean isLandlord() {
        return role == PlayerRole.LANDLORD;
    }

    public boolean isFarmer() {
        return role == PlayerRole.FARMER;
    }

    public boolean hasNoCards() {
        return handCards.isEmpty();
    }

    public String getSimpleName() {
        return name + Optional.ofNullable(role).map(playerRole -> "(" + playerRole.getDisplayName() + ")").orElse("");
    }

    // State modifiers
    public void setHandCards(List<Card> cards) {
        this.handCards = new ArrayList<>(cards);
        this.handCards.sort(Card::compareTo);
    }

    public void addCards(List<Card> cards) {
        this.handCards.addAll(cards);
        this.handCards.sort(Card::compareTo);
    }

    public void removeCards(List<Card> cards) {
        this.handCards.removeAll(cards);
    }

    public void setRole(PlayerRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %d张牌)", name, role.getDisplayName(), handCards.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static class Builder {
        private ReActAgent agent;
        private String name;

        public Builder agent(ReActAgent agent) {
            this.agent = agent;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Player build() {
            if (agent == null || name == null) {
                throw new IllegalStateException("Agent and name are required");
            }
            return new Player(this);
        }
    }
}
