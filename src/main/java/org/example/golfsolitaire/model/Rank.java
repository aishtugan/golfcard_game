package org.example.golfsolitaire.model;

public enum Rank {
    ACE(1, "A"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K");

    private final int value;
    private final String displayName;

    Rank(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isOneAwayFrom(Rank other, boolean aceKingConnected) {
        int difference = Math.abs(value - other.value);
        if (difference == 1) {
            return true;
        }

        return (this == ACE && other == KING) || (this == KING && other == ACE);
    }
}
