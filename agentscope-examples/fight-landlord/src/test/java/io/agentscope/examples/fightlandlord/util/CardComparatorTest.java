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
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * CardComparator 测试用例。
 */
public class CardComparatorTest {

    @Test
    public void testGetCardType_Single() {
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE)
        );
        assertEquals(CardType.SINGLE, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_Pair() {
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        assertEquals(CardType.PAIR, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_Triple() {
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE)
        );
        assertEquals(CardType.TRIPLE, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_TripleWithSingle() {
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX)
        );
        assertEquals(CardType.TRIPLE_WITH_SINGLE, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_TripleWithPair() {
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SIX)
        );
        assertEquals(CardType.TRIPLE_WITH_PAIR, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_Bomb() {
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE)
        );
        assertEquals(CardType.BOMB, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_Rocket() {
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.JOKER, Card.Rank.BLACK_JOKER),
                new Card(Card.Suit.JOKER, Card.Rank.RED_JOKER)
        );
        assertEquals(CardType.ROCKET, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_Straight() {
        // 测试用户提到的顺子：["♠5","♦6","♦7","♣8","♣9","♦10","♠J"]
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TEN),
                new Card(Card.Suit.SPADES, Card.Rank.JACK)
        );
        CardType type = CardComparator.getCardType(cards);
        assertNotNull("顺子应该被识别", type);
        assertEquals("应该是顺子类型", CardType.STRAIGHT, type);
    }

    @Test
    public void testGetCardType_Straight5Cards() {
        // 5张顺子
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SEVEN)
        );
        assertEquals(CardType.STRAIGHT, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_StraightWithAce() {
        // 包含A的顺子（A不能作为1，只能作为14）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.TEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.JACK),
                new Card(Card.Suit.HEARTS, Card.Rank.QUEEN),
                new Card(Card.Suit.CLUBS, Card.Rank.KING),
                new Card(Card.Suit.SPADES, Card.Rank.ACE)
        );
        assertEquals(CardType.STRAIGHT, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_NotStraightWithTwo() {
        // 包含2的不能是顺子（2的值是16，不能连续）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.ACE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE)
        );
        assertNotEquals(CardType.STRAIGHT, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_NotStraightWithJoker() {
        // 包含王的不能是顺子
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.JOKER, Card.Rank.BLACK_JOKER)
        );
        assertNotEquals(CardType.STRAIGHT, CardComparator.getCardType(cards));
    }

    @Test
    public void testCanBeat_RocketBeatsAll() {
        List<Card> rocket = Arrays.asList(
                new Card(Card.Suit.JOKER, Card.Rank.BLACK_JOKER),
                new Card(Card.Suit.JOKER, Card.Rank.RED_JOKER)
        );
        List<Card> bomb = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE)
        );
        assertTrue(CardComparator.canBeat(rocket, bomb));
        assertFalse(CardComparator.canBeat(bomb, rocket));
    }

    @Test
    public void testCanBeat_BombBeatsNormal() {
        List<Card> bomb = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE)
        );
        List<Card> triple = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.ACE),
                new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.ACE)
        );
        assertTrue(CardComparator.canBeat(bomb, triple));
        assertFalse(CardComparator.canBeat(triple, bomb));
    }

    @Test
    public void testCanBeat_SameType() {
        List<Card> pair1 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        List<Card> pair2 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX)
        );
        assertTrue(CardComparator.canBeat(pair2, pair1));
        assertFalse(CardComparator.canBeat(pair1, pair2));
    }

    @Test
    public void testCanBeat_Straight() {
        List<Card> straight1 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SEVEN)
        );
        List<Card> straight2 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX),
                new Card(Card.Suit.CLUBS, Card.Rank.SEVEN),
                new Card(Card.Suit.SPADES, Card.Rank.EIGHT)
        );
        assertTrue(CardComparator.canBeat(straight2, straight1));
        assertFalse(CardComparator.canBeat(straight1, straight2));
    }

    @Test
    public void testCanBeat_StraightSameLength() {
        // 相同长度的顺子才能比较
        List<Card> straight5 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SEVEN)
        );
        List<Card> straight7 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TEN),
                new Card(Card.Suit.SPADES, Card.Rank.JACK)
        );
        // 不同长度的顺子不能比较
        assertFalse(CardComparator.canBeat(straight7, straight5));
    }

    @Test
    public void testGetCardType_ConsecutivePairs() {
        // 双顺：334455（至少3组连续对子）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        assertEquals(CardType.CONSECUTIVE_PAIRS, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_ConsecutivePairs4Groups() {
        // 双顺：7788991010（4组连续对子）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.SEVEN),
                new Card(Card.Suit.HEARTS, Card.Rank.SEVEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
                new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
                new Card(Card.Suit.SPADES, Card.Rank.NINE),
                new Card(Card.Suit.HEARTS, Card.Rank.NINE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TEN),
                new Card(Card.Suit.CLUBS, Card.Rank.TEN)
        );
        assertEquals(CardType.CONSECUTIVE_PAIRS, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_Airplane() {
        // 三顺：333444（至少2组连续三张）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR)
        );
        assertEquals(CardType.AIRPLANE, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_Airplane3Groups() {
        // 三顺：555666777（3组连续三张）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.SEVEN),
                new Card(Card.Suit.SPADES, Card.Rank.SEVEN)
        );
        assertEquals(CardType.AIRPLANE, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_AirplaneWithSingles() {
        // 飞机带单：444555+79（三顺+同数量的单牌）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE)
        );
        assertEquals(CardType.AIRPLANE_WITH_SINGLES, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_AirplaneWithPairs() {
        // 飞机带对：333444555+7799JJ（三顺+同数量的对牌）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.SEVEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE),
                new Card(Card.Suit.SPADES, Card.Rank.NINE),
                new Card(Card.Suit.HEARTS, Card.Rank.JACK),
                new Card(Card.Suit.DIAMONDS, Card.Rank.JACK)
        );
        assertEquals(CardType.AIRPLANE_WITH_PAIRS, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_FourWithTwoSingles() {
        // 四带二：5555+3+8（四张+两张单牌）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.EIGHT)
        );
        assertEquals(CardType.FOUR_WITH_TWO, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_FourWithTwoPairs() {
        // 四带二：4444+55+77（四张+两个对子）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)
        );
        assertEquals(CardType.FOUR_WITH_TWO, CardComparator.getCardType(cards));
    }

    @Test
    public void testCanBeat_BombVsBomb() {
        // 都是炸弹时按牌的分值比大小
        List<Card> bomb5 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE)
        );
        List<Card> bombAce = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.ACE),
                new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.ACE),
                new Card(Card.Suit.CLUBS, Card.Rank.ACE)
        );
        assertTrue(CardComparator.canBeat(bombAce, bomb5));
        assertFalse(CardComparator.canBeat(bomb5, bombAce));
    }

    @Test
    public void testCanBeat_ConsecutivePairs() {
        // 双顺：334455 vs 445566
        List<Card> pairs1 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        List<Card> pairs2 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX)
        );
        assertTrue(CardComparator.canBeat(pairs2, pairs1));
        assertFalse(CardComparator.canBeat(pairs1, pairs2));
    }

    @Test
    public void testCanBeat_Airplane() {
        // 三顺：333444 vs 444555
        List<Card> airplane1 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR)
        );
        List<Card> airplane2 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        assertTrue(CardComparator.canBeat(airplane2, airplane1));
        assertFalse(CardComparator.canBeat(airplane1, airplane2));
    }

    @Test
    public void testCanBeat_AirplaneWithSingles() {
        // 飞机带单：444555+79 vs 555666+8K（按三顺部分比较）
        List<Card> airplane1 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE)
        );
        List<Card> airplane2 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.EIGHT),
                new Card(Card.Suit.CLUBS, Card.Rank.KING)
        );
        assertTrue(CardComparator.canBeat(airplane2, airplane1));
        assertFalse(CardComparator.canBeat(airplane1, airplane2));
    }

    @Test
    public void testCanBeat_AirplaneWithPairs() {
        // 飞机带对：333444+77+88 vs 444555+99+1010（按三顺部分比较）
        // 2组三顺需要2个对子
        List<Card> airplane1 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.SEVEN),
                new Card(Card.Suit.SPADES, Card.Rank.EIGHT),
                new Card(Card.Suit.HEARTS, Card.Rank.EIGHT)
        );
        List<Card> airplane2 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.NINE),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE),
                new Card(Card.Suit.SPADES, Card.Rank.TEN),
                new Card(Card.Suit.HEARTS, Card.Rank.TEN)
        );
        assertTrue(CardComparator.canBeat(airplane2, airplane1));
        assertFalse(CardComparator.canBeat(airplane1, airplane2));
    }

    @Test
    public void testCanBeat_FourWithTwo() {
        // 四带二：5555+3+8 vs 6666+4+9（按四张部分比较）
        List<Card> four1 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.EIGHT)
        );
        List<Card> four2 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.NINE)
        );
        assertTrue(CardComparator.canBeat(four2, four1));
        assertFalse(CardComparator.canBeat(four1, four2));
    }

    @Test
    public void testCanBeat_DifferentTypesCannotCompare() {
        // 不同牌型不能比较
        List<Card> pair = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        List<Card> triple = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX)
        );
        assertFalse(CardComparator.canBeat(triple, pair));
    }

    @Test
    public void testCanBeat_DifferentSizesCannotCompare() {
        // 相同牌型但不同张数不能比较
        List<Card> straight5 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SEVEN)
        );
        List<Card> straight6 = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX),
                new Card(Card.Suit.CLUBS, Card.Rank.SEVEN),
                new Card(Card.Suit.SPADES, Card.Rank.EIGHT),
                new Card(Card.Suit.DIAMONDS, Card.Rank.NINE)
        );
        assertFalse(CardComparator.canBeat(straight6, straight5));
    }

    @Test
    public void testCardsToString() {
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.JOKER, Card.Rank.BLACK_JOKER)
        );
        
        String result = CardComparator.cardsToString(cards);
        assertTrue(result.contains("♠5"));
        assertTrue(result.contains("♦6"));
        assertTrue(result.contains("小王"));
    }

    // ========== 负面测试用例：无效牌型 ==========

    @Test
    public void testGetCardType_InvalidTripleWithTwoSingles() {
        // 三带二张单牌（无效：三带一只能带1张单牌）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SEVEN)
        );
        assertNotEquals(CardType.TRIPLE_WITH_SINGLE, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidTripleWithPairAndSingle() {
        // 三带一对加一张（无效：三带二只能带1个对子，不能带对子+单牌）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SEVEN)
        );
        assertNotEquals(CardType.TRIPLE_WITH_PAIR, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidTripleWithTwoPairs() {
        // 三带两个对子（无效：三带二只能带1个对子）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SEVEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN)
        );
        assertNotEquals(CardType.TRIPLE_WITH_PAIR, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidConsecutivePairs2Groups() {
        // 双顺只有2组（无效：至少需要3组）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR)
        );
        assertNotEquals(CardType.CONSECUTIVE_PAIRS, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidAirplane1Group() {
        // 三顺只有1组（无效：至少需要2组）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE)
        );
        assertNotEquals(CardType.AIRPLANE, CardComparator.getCardType(cards));
        assertEquals(CardType.TRIPLE, CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidAirplaneWithSinglesCountMismatch() {
        // 飞机带单但数量不匹配：2组三顺+1张单牌（应该是2张单牌）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN)
        );
        assertNotEquals(CardType.AIRPLANE_WITH_SINGLES, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidAirplaneWithPairsCountMismatch() {
        // 飞机带对但数量不匹配：2组三顺+1个对子（应该是2个对子）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.SEVEN)
        );
        assertNotEquals(CardType.AIRPLANE_WITH_PAIRS, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidAirplaneWithMixedWings() {
        // 飞机带翅膀但混合了单牌和对子（无效：不能混合）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
                new Card(Card.Suit.SPADES, Card.Rank.EIGHT)
        );
        assertNotEquals(CardType.AIRPLANE_WITH_SINGLES, CardComparator.getCardType(cards));
        assertNotEquals(CardType.AIRPLANE_WITH_PAIRS, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidFourWithTwoTriple() {
        // 四带二但带的是三张（无效：应该带两张单牌或两个对子）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX)
        );
        assertNotEquals(CardType.FOUR_WITH_TWO, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidFourWithTwoWrongSize() {
        // 四带二但总张数不对：四张+一个对子=6张（应该是四张+两个对子=8张）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX)
        );
        // 这种情况可能被识别为其他牌型，但不应该是FOUR_WITH_TWO
        CardType type = CardComparator.getCardType(cards);
        assertNotEquals(CardType.FOUR_WITH_TWO, type);
    }

    @Test
    public void testGetCardType_InvalidStraightWithDuplicate() {
        // 顺子但有重复点数（无效）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.HEARTS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.SIX)
        );
        assertNotEquals(CardType.STRAIGHT, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidConsecutivePairsWithGap() {
        // 双顺但不连续（有间隔）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX)
        );
        assertNotEquals(CardType.CONSECUTIVE_PAIRS, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidAirplaneWithGap() {
        // 三顺但不连续（有间隔）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        );
        assertNotEquals(CardType.AIRPLANE, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidConsecutivePairsWithTwo() {
        // 双顺包含2点（无效：不能包含2点）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.ACE),
                new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TWO),
                new Card(Card.Suit.CLUBS, Card.Rank.TWO),
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.HEARTS, Card.Rank.THREE)
        );
        assertNotEquals(CardType.CONSECUTIVE_PAIRS, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }

    @Test
    public void testGetCardType_InvalidAirplaneWithTwo() {
        // 三顺包含2点（无效：不能包含2点）
        List<Card> cards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.ACE),
                new Card(Card.Suit.HEARTS, Card.Rank.ACE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.ACE),
                new Card(Card.Suit.CLUBS, Card.Rank.TWO),
                new Card(Card.Suit.SPADES, Card.Rank.TWO),
                new Card(Card.Suit.HEARTS, Card.Rank.TWO)
        );
        assertNotEquals(CardType.AIRPLANE, CardComparator.getCardType(cards));
        assertNull(CardComparator.getCardType(cards));
    }
}
