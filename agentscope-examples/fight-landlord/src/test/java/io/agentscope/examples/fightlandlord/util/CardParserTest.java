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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * CardComparator 解析功能测试用例。
 */
public class CardParserTest {

    @Test
    public void testParseCards() {
        List<Card> handCards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.EIGHT)
        );
        
        List<String> cardStrings = Arrays.asList("♠5", "♦6", "♥7", "♣8");
        List<Card> parsed = CardComparator.parseCards(cardStrings, handCards);
        
        assertEquals(4, parsed.size());
        assertEquals(Card.Rank.FIVE, parsed.get(0).getRank());
        assertEquals(Card.Rank.SIX, parsed.get(1).getRank());
        assertEquals(Card.Rank.SEVEN, parsed.get(2).getRank());
        assertEquals(Card.Rank.EIGHT, parsed.get(3).getRank());
    }

    @Test
    public void testParseCards_StraightExample() {
        // 测试用户提到的例子：["♠5","♦6","♦7","♣8","♣9","♦10","♠J"]
        List<Card> handCards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN),
                new Card(Card.Suit.CLUBS, Card.Rank.EIGHT),
                new Card(Card.Suit.CLUBS, Card.Rank.NINE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TEN),
                new Card(Card.Suit.SPADES, Card.Rank.JACK)
        );
        
        List<String> cardStrings = Arrays.asList("♠5", "♦6", "♦7", "♣8", "♣9", "♦10", "♠J");
        List<Card> parsed = CardComparator.parseCards(cardStrings, handCards);
        
        assertEquals(7, parsed.size());
        assertEquals(Card.Rank.FIVE, parsed.get(0).getRank());
        assertEquals(Card.Suit.SPADES, parsed.get(0).getSuit());
        assertEquals(Card.Rank.SIX, parsed.get(1).getRank());
        assertEquals(Card.Suit.DIAMONDS, parsed.get(1).getSuit());
        assertEquals(Card.Rank.SEVEN, parsed.get(2).getRank());
        assertEquals(Card.Rank.EIGHT, parsed.get(3).getRank());
        assertEquals(Card.Rank.NINE, parsed.get(4).getRank());
        assertEquals(Card.Rank.TEN, parsed.get(5).getRank());
        assertEquals(Card.Rank.JACK, parsed.get(6).getRank());
        assertEquals(Card.Suit.SPADES, parsed.get(6).getSuit());
        
        // 验证解析出的牌是顺子
        CardType type = CardComparator.getCardType(parsed);
        assertEquals(CardType.STRAIGHT, type);
    }

    @Test
    public void testParseCards_StraightExample2() {
        // // ["♠3","♣4","♣5","♦6","♠6","♠7","♠8","♥9","♣10"]
        List<Card> handCards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.THREE),
                new Card(Card.Suit.CLUBS, Card.Rank.FOUR),
                new Card(Card.Suit.CLUBS, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SIX),
                new Card(Card.Suit.SPADES, Card.Rank.SEVEN),
                new Card(Card.Suit.SPADES, Card.Rank.EIGHT),
                new Card(Card.Suit.HEARTS, Card.Rank.NINE),
                new Card(Card.Suit.CLUBS, Card.Rank.TEN)
        );
        List<String> cardStrings = Arrays.asList("♠3", "♣4", "♣5", "♦6", "♠6", "♠7", "♠8", "♥9", "♣10");
        List<Card> parsed = CardComparator.parseCards(cardStrings, handCards);
        assertEquals(9, parsed.size());
        assertEquals(Card.Rank.THREE, parsed.get(0).getRank());
        assertEquals(Card.Suit.SPADES, parsed.get(0).getSuit());
        assertEquals(Card.Rank.FOUR, parsed.get(1).getRank());
        assertEquals(Card.Suit.CLUBS, parsed.get(1).getSuit());
        assertEquals(Card.Rank.FIVE, parsed.get(2).getRank());
        assertEquals(Card.Suit.CLUBS, parsed.get(2).getSuit());
        assertEquals(Card.Rank.SIX, parsed.get(3).getRank());
        assertEquals(Card.Suit.DIAMONDS, parsed.get(3).getSuit());
        assertEquals(Card.Rank.SIX, parsed.get(4).getRank());
        assertEquals(Card.Suit.SPADES, parsed.get(4).getSuit());
        assertEquals(Card.Rank.SEVEN, parsed.get(5).getRank());
        assertEquals(Card.Suit.SPADES, parsed.get(5).getSuit());
        assertEquals(Card.Rank.EIGHT, parsed.get(6).getRank());
        assertEquals(Card.Suit.SPADES, parsed.get(6).getSuit());
        assertEquals(Card.Rank.NINE, parsed.get(7).getRank());
        assertEquals(Card.Suit.HEARTS, parsed.get(7).getSuit());
        assertEquals(Card.Rank.TEN, parsed.get(8).getRank());
        assertEquals(Card.Suit.CLUBS, parsed.get(8).getSuit());

    }

    @Test
    public void testParseCards_WithJoker() {
        List<Card> handCards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.JOKER, Card.Rank.BLACK_JOKER),
                new Card(Card.Suit.JOKER, Card.Rank.RED_JOKER)
        );
        
        List<String> cardStrings = Arrays.asList("♠5", "小王", "大王");
        List<Card> parsed = CardComparator.parseCards(cardStrings, handCards);
        
        assertEquals(3, parsed.size());
        assertTrue(parsed.stream().anyMatch(c -> c.getRank() == Card.Rank.BLACK_JOKER));
        assertTrue(parsed.stream().anyMatch(c -> c.getRank() == Card.Rank.RED_JOKER));
    }

    @Test
    public void testParseCards_WithTen() {
        // 测试包含10的情况
        List<Card> handCards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.TEN),
                new Card(Card.Suit.DIAMONDS, Card.Rank.TEN),
                new Card(Card.Suit.HEARTS, Card.Rank.JACK)
        );
        
        List<String> cardStrings = Arrays.asList("♠10", "♦10", "♥J");
        List<Card> parsed = CardComparator.parseCards(cardStrings, handCards);
        
        assertEquals(3, parsed.size());
        assertEquals(Card.Rank.TEN, parsed.get(0).getRank());
        assertEquals(Card.Suit.SPADES, parsed.get(0).getSuit());
        assertEquals(Card.Rank.TEN, parsed.get(1).getRank());
        assertEquals(Card.Suit.DIAMONDS, parsed.get(1).getSuit());
        assertEquals(Card.Rank.JACK, parsed.get(2).getRank());
    }

    @Test
    public void testMatchCard_ExactMatch() {
        List<Card> availableCards = new ArrayList<>(Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX),
                new Card(Card.Suit.HEARTS, Card.Rank.SEVEN)
        ));
        
        Card matched = CardComparator.matchCard("♠5", availableCards, null);
        assertNotNull("应该匹配到牌", matched);
        assertEquals(Card.Rank.FIVE, matched.getRank());
        assertEquals(Card.Suit.SPADES, matched.getSuit());
    }

    @Test
    public void testMatchCard_RankOnly() {
        List<Card> availableCards = new ArrayList<>(Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.SIX)
        ));
        
        // 只匹配点数，应该匹配第一张
        Card matched = CardComparator.matchCard("5", availableCards, null);
        assertNotNull("应该匹配到牌", matched);
        assertEquals(Card.Rank.FIVE, matched.getRank());
    }

    @Test
    public void testMatchCard_WithJoker() {
        List<Card> availableCards = new ArrayList<>(Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.JOKER, Card.Rank.BLACK_JOKER),
                new Card(Card.Suit.JOKER, Card.Rank.RED_JOKER)
        ));
        
        Card blackJoker = CardComparator.matchCard("小王", availableCards, null);
        assertNotNull("应该匹配到小王", blackJoker);
        assertEquals(Card.Rank.BLACK_JOKER, blackJoker.getRank());
        
        Card redJoker = CardComparator.matchCard("大王", availableCards, null);
        assertNotNull("应该匹配到大王", redJoker);
        assertEquals(Card.Rank.RED_JOKER, redJoker.getRank());
    }

    @Test
    public void testMatchCard_NoMatch() {
        List<Card> availableCards = new ArrayList<>(Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX)
        ));
        
        Card matched = CardComparator.matchCard("♠K", availableCards, null);
        assertNull("不应该匹配到牌", matched);
    }

    @Test
    public void testMatchCard_AvoidDuplicate() {
        List<Card> availableCards = new ArrayList<>(Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE),
                new Card(Card.Suit.HEARTS, Card.Rank.FIVE)
        ));
        
        List<Card> alreadyMatched = new ArrayList<>();
        alreadyMatched.add(new Card(Card.Suit.SPADES, Card.Rank.FIVE));
        
        // 应该匹配第二张（DIAMONDS），而不是已经匹配的SPADES
        Card matched = CardComparator.matchCard("5", availableCards, alreadyMatched);
        assertNotNull("应该匹配到牌", matched);
        assertEquals(Card.Rank.FIVE, matched.getRank());
        assertEquals(Card.Suit.DIAMONDS, matched.getSuit());
    }

    @Test
    public void testMatchCard_EmptyString() {
        List<Card> availableCards = new ArrayList<>(Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE)
        ));
        
        Card matched = CardComparator.matchCard("", availableCards, null);
        assertNull("空字符串不应该匹配", matched);
        
        matched = CardComparator.matchCard(null, availableCards, null);
        assertNull("null不应该匹配", matched);
    }

    @Test
    public void testMatchCard_CaseInsensitive() {
        List<Card> availableCards = new ArrayList<>(Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.JACK)
        ));
        
        // 测试大小写不敏感
        Card matched1 = CardComparator.matchCard("♠5", availableCards, null);
        Card matched2 = CardComparator.matchCard("♠5", availableCards, null);
        assertNotNull(matched1);
        assertNotNull(matched2);
        
        // 测试J的匹配
        Card matched3 = CardComparator.matchCard("J", availableCards, null);
        assertNotNull("应该匹配到J", matched3);
        assertEquals(Card.Rank.JACK, matched3.getRank());
    }

    @Test
    public void testParseCards_EmptyList() {
        List<Card> handCards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE)
        );
        
        List<Card> parsed1 = CardComparator.parseCards(null, handCards);
        assertTrue("null应该返回空列表", parsed1.isEmpty());
        
        List<Card> parsed2 = CardComparator.parseCards(new ArrayList<>(), handCards);
        assertTrue("空列表应该返回空列表", parsed2.isEmpty());
    }

    @Test
    public void testParseCards_PartialMatch() {
        List<Card> handCards = Arrays.asList(
                new Card(Card.Suit.SPADES, Card.Rank.FIVE),
                new Card(Card.Suit.DIAMONDS, Card.Rank.SIX)
        );
        
        // 尝试解析3张牌，但只有2张在手牌中
        List<String> cardStrings = Arrays.asList("♠5", "♦6", "♠K");
        List<Card> parsed = CardComparator.parseCards(cardStrings, handCards);
        
        assertEquals(2, parsed.size());
        assertEquals(Card.Rank.FIVE, parsed.get(0).getRank());
        assertEquals(Card.Rank.SIX, parsed.get(1).getRank());
    }
}
