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
package io.agentscope.examples.fightlandlord.util;

import io.agentscope.examples.fightlandlord.Card;
import io.agentscope.examples.fightlandlord.CardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 牌型判断和比较工具类。
 *
 * <p>简化实现：主要支持单牌、对子、三张、炸弹、王炸等基本牌型。
 */
public class CardComparator {

    /**
     * 判断牌型并返回CardType。
     *
     * @param cards 要判断的牌
     * @return 牌型
     */
    public static CardType getCardType(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }

        int size = cards.size();

        // 王炸
        if (size == 2) {
            boolean hasBlackJoker = cards.stream()
                    .anyMatch(c -> c.getRank() == Card.Rank.BLACK_JOKER);
            boolean hasRedJoker = cards.stream()
                    .anyMatch(c -> c.getRank() == Card.Rank.RED_JOKER);
            if (hasBlackJoker && hasRedJoker) {
                return CardType.ROCKET;
            }
        }

        // 统计每种点数的数量
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            int value = card.getRank().getValue();
            rankCount.put(value, rankCount.getOrDefault(value, 0) + 1);
        }

        // 炸弹（四张相同）
        if (size == 4 && rankCount.size() == 1) {
            return CardType.BOMB;
        }

        // 单牌
        if (size == 1) {
            return CardType.SINGLE;
        }

        // 对子
        if (size == 2 && rankCount.size() == 1) {
            return CardType.PAIR;
        }

        // 三张
        if (size == 3 && rankCount.size() == 1) {
            return CardType.TRIPLE;
        }

        // 三带一
        if (size == 4 && rankCount.size() == 2) {
            boolean hasTriple = rankCount.values().stream().anyMatch(count -> count == 3);
            if (hasTriple) {
                return CardType.TRIPLE_WITH_SINGLE;
            }
        }

        // 三带二
        if (size == 5 && rankCount.size() == 2) {
            boolean hasTriple = rankCount.values().stream().anyMatch(count -> count == 3);
            boolean hasPair = rankCount.values().stream().anyMatch(count -> count == 2);
            if (hasTriple && hasPair) {
                return CardType.TRIPLE_WITH_PAIR;
            }
        }

        // 单顺（至少5张连续单牌，不能包含2或王）
        if (size >= 5) {
            // 检查是否所有牌都是单张（没有重复的点数）
            boolean allSingles = rankCount.values().stream().allMatch(count -> count == 1);
            if (allSingles) {
                // 提取所有点数并排序
                List<Integer> values = cards.stream()
                        .map(c -> c.getRank().getValue())
                        .sorted()
                        .collect(Collectors.toList());
                
                // 检查是否连续（不能包含2或王，2的值是16，王的值是18和20）
                boolean isConsecutive = true;
                for (int i = 0; i < values.size() - 1; i++) {
                    int current = values.get(i);
                    int next = values.get(i + 1);
                    
                    // 不能包含2（值为16）或王（值为18和20）
                    if (current >= 16 || next >= 16) {
                        isConsecutive = false;
                        break;
                    }
                    
                    // 检查是否连续
                    if (next != current + 1) {
                        isConsecutive = false;
                        break;
                    }
                }
                
                if (isConsecutive) {
                    return CardType.STRAIGHT;
                }
            }
        }

        // 双顺（至少3组连续对子，不能包含2或王）
        if (size >= 6 && size % 2 == 0) {
            // 检查是否都是对子
            boolean allPairs = rankCount.values().stream().allMatch(count -> count == 2);
            if (allPairs) {
                List<Integer> pairValues = rankCount.keySet().stream()
                        .sorted()
                        .collect(Collectors.toList());
                
                // 至少3组对子
                if (pairValues.size() >= 3) {
                    // 检查是否连续（不能包含2或王）
                    boolean isConsecutive = true;
                    for (int i = 0; i < pairValues.size() - 1; i++) {
                        int current = pairValues.get(i);
                        int next = pairValues.get(i + 1);
                        
                        if (current >= 16 || next >= 16) {
                            isConsecutive = false;
                            break;
                        }
                        
                        if (next != current + 1) {
                            isConsecutive = false;
                            break;
                        }
                    }
                    
                    if (isConsecutive) {
                        return CardType.CONSECUTIVE_PAIRS;
                    }
                }
            }
        }

        // 三顺（至少2组连续三张，不能包含2或王）
        if (size >= 6 && size % 3 == 0) {
            // 检查是否都是三张
            boolean allTriples = rankCount.values().stream().allMatch(count -> count == 3);
            if (allTriples) {
                List<Integer> tripleValues = rankCount.keySet().stream()
                        .sorted()
                        .collect(Collectors.toList());
                
                // 至少2组三张
                if (tripleValues.size() >= 2) {
                    // 检查是否连续（不能包含2或王）
                    boolean isConsecutive = true;
                    for (int i = 0; i < tripleValues.size() - 1; i++) {
                        int current = tripleValues.get(i);
                        int next = tripleValues.get(i + 1);
                        
                        if (current >= 16 || next >= 16) {
                            isConsecutive = false;
                            break;
                        }
                        
                        if (next != current + 1) {
                            isConsecutive = false;
                            break;
                        }
                    }
                    
                    if (isConsecutive) {
                        return CardType.AIRPLANE;
                    }
                }
            }
        }

        // 飞机带翅膀（三顺+同数量的单牌或对牌）
        // 先检查是否有三顺部分
        Map<Integer, Integer> tripleRanks = new HashMap<>();
        Map<Integer, Integer> singleRanks = new HashMap<>();
        Map<Integer, Integer> pairRanks = new HashMap<>();
        
        for (Map.Entry<Integer, Integer> entry : rankCount.entrySet()) {
            int value = entry.getKey();
            int count = entry.getValue();
            if (count == 3) {
                tripleRanks.put(value, count);
            } else if (count == 1) {
                singleRanks.put(value, count);
            } else if (count == 2) {
                pairRanks.put(value, count);
            }
        }
        
        // 检查是否有三顺
        if (tripleRanks.size() >= 2) {
            List<Integer> tripleValues = tripleRanks.keySet().stream()
                    .sorted()
                    .collect(Collectors.toList());
            
            // 检查是否连续（不能包含2或王）
            boolean isConsecutiveTriples = true;
            for (int i = 0; i < tripleValues.size() - 1; i++) {
                int current = tripleValues.get(i);
                int next = tripleValues.get(i + 1);
                
                if (current >= 16 || next >= 16) {
                    isConsecutiveTriples = false;
                    break;
                }
                
                if (next != current + 1) {
                    isConsecutiveTriples = false;
                    break;
                }
            }
            
            if (isConsecutiveTriples) {
                int tripleCount = tripleRanks.size();
                // 飞机带单：三顺数量 = 单牌数量
                if (singleRanks.size() == tripleCount && pairRanks.isEmpty()) {
                    return CardType.AIRPLANE_WITH_SINGLES;
                }
                // 飞机带对：三顺数量 = 对牌数量
                if (pairRanks.size() == tripleCount && singleRanks.isEmpty()) {
                    return CardType.AIRPLANE_WITH_PAIRS;
                }
            }
        }

        // 四带二（四张牌+两手牌：两张单牌或两个对子）
        // 注意：四带二不是炸弹，所以size必须是6或8
        if (size == 6 || size == 8) {
            Map<Integer, Integer> fourRanks = new HashMap<>();
            Map<Integer, Integer> otherRanks = new HashMap<>();
            
            for (Map.Entry<Integer, Integer> entry : rankCount.entrySet()) {
                int value = entry.getKey();
                int count = entry.getValue();
                if (count == 4) {
                    fourRanks.put(value, count);
                } else {
                    otherRanks.put(value, count);
                }
            }
            
            // 必须有一个四张，且其他牌是两张单牌或两个对子
            if (fourRanks.size() == 1) {
                if (size == 6) {
                    // 四带二单：四张 + 两张单牌
                    if (otherRanks.size() == 2 && otherRanks.values().stream().allMatch(c -> c == 1)) {
                        return CardType.FOUR_WITH_TWO;
                    }
                } else if (size == 8) {
                    // 四带二对：四张 + 两个对子
                    if (otherRanks.size() == 2 && otherRanks.values().stream().allMatch(c -> c == 2)) {
                        return CardType.FOUR_WITH_TWO;
                    }
                }
            }
        }

        // 其他复杂牌型暂不支持，返回null表示无效牌型
        return null;
    }

    /**
     * 判断新出的牌是否能压过上家的牌。
     *
     * @param newCards 新出的牌
     * @param lastCards 上家的牌
     * @return true表示能压过
     */
    public static boolean canBeat(List<Card> newCards, List<Card> lastCards) {
        if (newCards == null || newCards.isEmpty()) {
            return false;
        }

        if (lastCards == null || lastCards.isEmpty()) {
            // 上家没有出牌，新牌可以出
            return true;
        }

        CardType newType = getCardType(newCards);
        CardType lastType = getCardType(lastCards);

        if (newType == null) {
            return false; // 无效牌型
        }

        if (lastType == null) {
            return true; // 上家是无效牌型，新牌可以出
        }

        // 王炸最大
        if (newType == CardType.ROCKET) {
            return true;
        }
        if (lastType == CardType.ROCKET) {
            return false;
        }

        // 炸弹压非炸弹
        if (newType == CardType.BOMB && lastType != CardType.BOMB) {
            return true;
        }
        if (lastType == CardType.BOMB && newType != CardType.BOMB) {
            return false;
        }

        // 炸弹比较：都是炸弹时按牌的分值比大小
        if (newType == CardType.BOMB && lastType == CardType.BOMB) {
            int newBombValue = newCards.get(0).getRank().getValue();
            int lastBombValue = lastCards.get(0).getRank().getValue();
            return newBombValue > lastBombValue;
        }

        // 同类型才能比较，且总张数必须相同
        if (newType.getPriority() != lastType.getPriority()) {
            return false;
        }
        if (newCards.size() != lastCards.size()) {
            return false;
        }

        // 对于带牌的牌型（三带一、三带二），只比较三张部分
        if (newType == CardType.TRIPLE_WITH_SINGLE || newType == CardType.TRIPLE_WITH_PAIR) {
            // 找到三张部分的点数
            Map<Integer, Integer> newRankCount = new HashMap<>();
            for (Card card : newCards) {
                int value = card.getRank().getValue();
                newRankCount.put(value, newRankCount.getOrDefault(value, 0) + 1);
            }
            int newTripleValue = newRankCount.entrySet().stream()
                    .filter(e -> e.getValue() == 3)
                    .mapToInt(Map.Entry::getKey)
                    .findFirst()
                    .orElse(0);

            Map<Integer, Integer> lastRankCount = new HashMap<>();
            for (Card card : lastCards) {
                int value = card.getRank().getValue();
                lastRankCount.put(value, lastRankCount.getOrDefault(value, 0) + 1);
            }
            int lastTripleValue = lastRankCount.entrySet().stream()
                    .filter(e -> e.getValue() == 3)
                    .mapToInt(Map.Entry::getKey)
                    .findFirst()
                    .orElse(0);

            return newTripleValue > lastTripleValue;
        }

        // 对于单顺，比较最大牌的点数（但必须长度相同）
        if (newType == CardType.STRAIGHT) {
            int newMaxValue = newCards.stream()
                    .mapToInt(c -> c.getRank().getValue())
                    .max()
                    .orElse(0);
            int lastMaxValue = lastCards.stream()
                    .mapToInt(c -> c.getRank().getValue())
                    .max()
                    .orElse(0);
            return newMaxValue > lastMaxValue;
        }

        // 对于双顺，比较最大对子的点数（但必须长度相同）
        if (newType == CardType.CONSECUTIVE_PAIRS) {
            Map<Integer, Integer> newRankCount = new HashMap<>();
            for (Card card : newCards) {
                int value = card.getRank().getValue();
                newRankCount.put(value, newRankCount.getOrDefault(value, 0) + 1);
            }
            int newMaxPairValue = newRankCount.keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);

            Map<Integer, Integer> lastRankCount = new HashMap<>();
            for (Card card : lastCards) {
                int value = card.getRank().getValue();
                lastRankCount.put(value, lastRankCount.getOrDefault(value, 0) + 1);
            }
            int lastMaxPairValue = lastRankCount.keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);

            return newMaxPairValue > lastMaxPairValue;
        }

        // 对于三顺，比较最大三张的点数（但必须长度相同）
        if (newType == CardType.AIRPLANE) {
            Map<Integer, Integer> newRankCount = new HashMap<>();
            for (Card card : newCards) {
                int value = card.getRank().getValue();
                newRankCount.put(value, newRankCount.getOrDefault(value, 0) + 1);
            }
            int newMaxTripleValue = newRankCount.entrySet().stream()
                    .filter(e -> e.getValue() == 3)
                    .mapToInt(Map.Entry::getKey)
                    .max()
                    .orElse(0);

            Map<Integer, Integer> lastRankCount = new HashMap<>();
            for (Card card : lastCards) {
                int value = card.getRank().getValue();
                lastRankCount.put(value, lastRankCount.getOrDefault(value, 0) + 1);
            }
            int lastMaxTripleValue = lastRankCount.entrySet().stream()
                    .filter(e -> e.getValue() == 3)
                    .mapToInt(Map.Entry::getKey)
                    .max()
                    .orElse(0);

            return newMaxTripleValue > lastMaxTripleValue;
        }

        // 对于飞机带翅膀，按其中的三顺部分来比，带的牌不影响大小
        if (newType == CardType.AIRPLANE_WITH_SINGLES || newType == CardType.AIRPLANE_WITH_PAIRS) {
            Map<Integer, Integer> newRankCount = new HashMap<>();
            for (Card card : newCards) {
                int value = card.getRank().getValue();
                newRankCount.put(value, newRankCount.getOrDefault(value, 0) + 1);
            }
            int newMaxTripleValue = newRankCount.entrySet().stream()
                    .filter(e -> e.getValue() == 3)
                    .mapToInt(Map.Entry::getKey)
                    .max()
                    .orElse(0);

            Map<Integer, Integer> lastRankCount = new HashMap<>();
            for (Card card : lastCards) {
                int value = card.getRank().getValue();
                lastRankCount.put(value, lastRankCount.getOrDefault(value, 0) + 1);
            }
            int lastMaxTripleValue = lastRankCount.entrySet().stream()
                    .filter(e -> e.getValue() == 3)
                    .mapToInt(Map.Entry::getKey)
                    .max()
                    .orElse(0);

            return newMaxTripleValue > lastMaxTripleValue;
        }

        // 对于四带二，按其中的四张部分来比，带的牌不影响大小
        if (newType == CardType.FOUR_WITH_TWO) {
            Map<Integer, Integer> newRankCount = new HashMap<>();
            for (Card card : newCards) {
                int value = card.getRank().getValue();
                newRankCount.put(value, newRankCount.getOrDefault(value, 0) + 1);
            }
            int newFourValue = newRankCount.entrySet().stream()
                    .filter(e -> e.getValue() == 4)
                    .mapToInt(Map.Entry::getKey)
                    .findFirst()
                    .orElse(0);

            Map<Integer, Integer> lastRankCount = new HashMap<>();
            for (Card card : lastCards) {
                int value = card.getRank().getValue();
                lastRankCount.put(value, lastRankCount.getOrDefault(value, 0) + 1);
            }
            int lastFourValue = lastRankCount.entrySet().stream()
                    .filter(e -> e.getValue() == 4)
                    .mapToInt(Map.Entry::getKey)
                    .findFirst()
                    .orElse(0);

            return newFourValue > lastFourValue;
        }

        // 对于其他牌型（单牌、对子、三张），比较主牌的最大点数
        int newMaxValue = newCards.stream()
                .mapToInt(c -> c.getRank().getValue())
                .max()
                .orElse(0);

        int lastMaxValue = lastCards.stream()
                .mapToInt(c -> c.getRank().getValue())
                .max()
                .orElse(0);

        return newMaxValue > lastMaxValue;
    }

    /**
     * 解析单个牌字符串，返回匹配的Card对象。
     *
     * @param cardStr 牌字符串（如 "♠3", "♥5", "♦K", "小王", "大王"）
     * @param availableCards 可用的牌列表（会从中移除匹配的牌）
     * @param alreadyMatched 已经匹配的牌列表（用于避免重复匹配）
     * @return 匹配的Card对象，如果无法匹配则返回null
     */
    public static Card matchCard(String cardStr, List<Card> availableCards, List<Card> alreadyMatched) {
        if (cardStr == null || cardStr.isEmpty() || availableCards == null || availableCards.isEmpty()) {
            return null;
        }

        // 清理字符串
        cardStr = cardStr.trim().replace("\"", "").replace("'", "");

        // 处理大小王
        if (cardStr.contains("小王") || cardStr.equalsIgnoreCase("BLACK_JOKER") 
                || cardStr.contains("black") || cardStr.contains("Black")) {
            return availableCards.stream()
                    .filter(c -> c.getRank() == Card.Rank.BLACK_JOKER)
                    .findFirst()
                    .orElse(null);
        }
        
        if (cardStr.contains("大王") || cardStr.equalsIgnoreCase("RED_JOKER")
                || cardStr.contains("red") || cardStr.contains("Red")) {
            return availableCards.stream()
                    .filter(c -> c.getRank() == Card.Rank.RED_JOKER)
                    .findFirst()
                    .orElse(null);
        }

        // 处理普通牌：尝试多种匹配方式
        for (Card card : availableCards) {
            String cardStrLower = cardStr.toLowerCase();
            String cardStrUpper = cardStr.toUpperCase();
            String cardDisplay = card.toString();
            
            // 完全匹配
            if (cardDisplay.equals(cardStr) || cardDisplay.equals(cardStrLower) || cardDisplay.equals(cardStrUpper)) {
                return card;
            }
            
            // 匹配点数（忽略花色）
            String rankLabel = card.getRank().getLabel();
            if (cardStr.contains(rankLabel) || cardStrLower.contains(rankLabel.toLowerCase())) {
                // 检查是否已经有相同点数和花色的牌被匹配
                boolean alreadyMatchedThisCard = alreadyMatched != null && alreadyMatched.stream()
                        .anyMatch(c -> c.getRank() == card.getRank() && c.getSuit() == card.getSuit());
                if (!alreadyMatchedThisCard) {
                    return card;
                }
            }
        }

        return null;
    }

    /**
     * 从字符串列表解析为Card列表。
     *
     * @param cardStrings 牌字符串列表（如 ["♠3", "♥5", "♦K"]）
     * @param handCards 手牌列表，用于匹配
     * @return 解析出的Card列表
     */
    public static List<Card> parseCards(List<String> cardStrings, List<Card> handCards) {
        if (cardStrings == null || cardStrings.isEmpty()) {
            return new ArrayList<>();
        }

        List<Card> result = new ArrayList<>();
        List<Card> availableCards = new ArrayList<>(handCards);

        for (String cardStr : cardStrings) {
            Card matched = matchCard(cardStr, availableCards, result);
            if (matched != null) {
                result.add(matched);
                availableCards.remove(matched);
            }
        }

        return result;
    }

    /**
     * 将Card列表转换为字符串表示。
     */
    public static String cardsToString(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return "无";
        }
        return cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(", "));
    }
}
