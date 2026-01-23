package io.agentscope.examples.fightlandlord;

import io.agentscope.examples.fightlandlord.Card.Rank;
import io.agentscope.examples.fightlandlord.Card.Suit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一副标准 54 张斗地主牌，包括大小王。
 */
public class Deck {

    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        // 普通牌：四种花色 * 13 点
        for (Suit suit : new Suit[] {Suit.CLUBS, Suit.DIAMONDS, Suit.HEARTS, Suit.SPADES}) {
            for (Rank rank :
                    new Rank[] {
                        Rank.THREE,
                        Rank.FOUR,
                        Rank.FIVE,
                        Rank.SIX,
                        Rank.SEVEN,
                        Rank.EIGHT,
                        Rank.NINE,
                        Rank.TEN,
                        Rank.JACK,
                        Rank.QUEEN,
                        Rank.KING,
                        Rank.ACE,
                        Rank.TWO
                    }) {
                cards.add(new Card(suit, rank));
            }
        }
        // 大小王
        cards.add(new Card(Suit.JOKER, Rank.BLACK_JOKER));
        cards.add(new Card(Suit.JOKER, Rank.RED_JOKER));
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> getCards() {
        return cards;
    }

    /**
     * 发牌：返回长度为 3 的列表，依次为 玩家 0、1、2 的手牌，最后一项为 3 张底牌。
     */
    public List<List<Card>> deal() {
        if (cards.size() != 54) {
            throw new IllegalStateException("发牌前请重新创建或洗牌一副完整的牌。");
        }
        List<Card> all = new ArrayList<>(cards);
        List<Card> p0 = new ArrayList<>(17);
        List<Card> p1 = new ArrayList<>(17);
        List<Card> p2 = new ArrayList<>(17);
        List<Card> bottom = new ArrayList<>(3);

        for (int i = 0; i < 51; i++) {
            Card c = all.get(i);
            switch (i % 3) {
                case 0 -> p0.add(c);
                case 1 -> p1.add(c);
                case 2 -> p2.add(c);
                default -> throw new IllegalStateException("不可能到这里");
            }
        }
        bottom.add(all.get(51));
        bottom.add(all.get(52));
        bottom.add(all.get(53));

        p0.sort(Card::compareTo);
        p1.sort(Card::compareTo);
        p2.sort(Card::compareTo);
        bottom.sort(Card::compareTo);

        List<List<Card>> res = new ArrayList<>();
        res.add(p0);
        res.add(p1);
        res.add(p2);
        res.add(bottom);
        return res;
    }
}

