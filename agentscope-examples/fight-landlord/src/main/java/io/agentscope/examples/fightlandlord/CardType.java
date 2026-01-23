package io.agentscope.examples.fightlandlord;

/**
 * 斗地主牌型枚举。
 */
public enum CardType {
    /** 单牌 */
    SINGLE(1, "单牌"),
    /** 对子 */
    PAIR(2, "对子"),
    /** 三张 */
    TRIPLE(3, "三张"),
    /** 三带一 */
    TRIPLE_WITH_SINGLE(4, "三带一"),
    /** 三带二 */
    TRIPLE_WITH_PAIR(5, "三带二"),
    /** 单顺（至少5张连续单牌，不包括2点和双王） */
    STRAIGHT(6, "单顺"),
    /** 双顺（至少3组连续对子，不包括2点和双王） */
    CONSECUTIVE_PAIRS(7, "双顺"),
    /** 三顺（至少2组连续三张，不包括2点和双王） */
    AIRPLANE(8, "三顺"),
    /** 飞机带翅膀（三顺+同数量的单牌或对牌） */
    AIRPLANE_WITH_SINGLES(9, "飞机带单"),
    /** 飞机带翅膀（三顺+同数量的单牌或对牌） */
    AIRPLANE_WITH_PAIRS(10, "飞机带对"),
    /** 四带二（四张牌+两手牌，注意：四带二不是炸弹） */
    FOUR_WITH_TWO(11, "四带二"),
    /** 炸弹（四张相同） */
    BOMB(12, "炸弹"),
    /** 王炸（大小王） */
    ROCKET(13, "王炸");

    private final int priority; // 优先级，用于比较
    private final String displayName;

    CardType(int priority, String displayName) {
        this.priority = priority;
        this.displayName = displayName;
    }

    public int getPriority() {
        return priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 判断当前牌型是否能压过另一个牌型。
     * 规则：炸弹可以压非炸弹，王炸最大，同类型比较大小。
     */
    public boolean canBeat(CardType other) {
        if (this == ROCKET) {
            return true; // 王炸最大
        }
        if (other == ROCKET) {
            return false;
        }
        if (this == BOMB && other != BOMB) {
            return true; // 炸弹压非炸弹
        }
        if (other == BOMB && this != BOMB) {
            return false;
        }
        // 同类型才能比较
        return this.priority == other.priority;
    }
}
