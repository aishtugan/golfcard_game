package org.example.golfsolitaire.game;

import org.example.golfsolitaire.model.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record CardColumn(List<Card> cards) {
    public CardColumn(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    @Override
    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    public Optional<Card> getTopCard() {
        if (cards.isEmpty()) {
            return Optional.empty();
        }

        // The first card is the playable card. The UI draws it at the bottom of the column.
        return Optional.of(cards.get(0));
    }

    public Optional<Card> getCardUnderTop() {
        if (cards.size() < 2) {
            return Optional.empty();
        }
        return Optional.of(cards.get(1));
    }

    public boolean isTopCard(Card card) {
        return getTopCard()
                .map(topCard -> topCard.equals(card))
                .orElse(false);
    }

    public boolean removeTopCard(Card card) {
        if (!isTopCard(card)) {
            return false;
        }

        cards.remove(0);
        return true;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
