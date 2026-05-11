package org.example.golfsolitaire.model;

import java.util.Objects;

public class Card {
    private final Suit suit;
    private final Rank rank;
    private final boolean faceUp;

    public Card(Suit suit, Rank rank) {
        this(suit, rank, true);
    }

    public Card(Suit suit, Rank rank, boolean faceUp) {
        this.suit = Objects.requireNonNull(suit, "suit");
        this.rank = Objects.requireNonNull(rank, "rank");
        this.faceUp = faceUp;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public String getShortName() {
        return rank.getDisplayName() + suit.getDisplayName();
    }

    @Override
    public String toString() {
        return getShortName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        Card card = (Card) o;
        return faceUp == card.faceUp && suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank, faceUp);
    }
}
