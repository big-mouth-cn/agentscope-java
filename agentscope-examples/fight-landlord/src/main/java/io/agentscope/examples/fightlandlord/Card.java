package io.agentscope.examples.fightlandlord;

/**
 * 扑克牌定义，包含点数与花色。
 *
 * <p>为简化实现，我们只在控制台打印牌面，不做复杂牌型比较，只支持“单牌”轮流出牌演示。
 */
public class Card implements Comparable<Card> {

    public enum Suit {
        CLUBS("♣"),
        DIAMONDS("♦"),
        HEARTS("♥"),
        SPADES("♠"),
        JOKER("");

        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum Rank {
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13),
        ACE("A", 14),
        TWO("2", 16),
        BLACK_JOKER("小王", 18),
        RED_JOKER("大王", 20);

        private final String label;
        private final int value;

        Rank(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isDiamondThree() {
        return suit == Suit.DIAMONDS && rank == Rank.THREE;
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.rank.getValue(), o.rank.getValue());
    }

    @Override
    public String toString() {
        if (suit == Suit.JOKER) {
            return rank.getLabel();
        }
        return suit.getSymbol() + rank.getLabel();
    }
}

