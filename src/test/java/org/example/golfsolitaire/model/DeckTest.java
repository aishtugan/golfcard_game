package org.example.golfsolitaire.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeckTest {
    @Test
    void standardDeckHas52UniqueCards() {
        Deck deck = Deck.createStandardDeck();

        Set<Card> drawnCards = new HashSet<>();
        while (!deck.isEmpty()) {
            drawnCards.add(deck.draw().orElseThrow());
        }

        assertEquals(52, drawnCards.size());
    }

    @Test
    void aceAndKingAreAdjacent() {
        assertTrue(Rank.ACE.isOneAwayFrom(Rank.TWO, false));
        assertTrue(Rank.KING.isOneAwayFrom(Rank.QUEEN, false));
        assertTrue(Rank.ACE.isOneAwayFrom(Rank.KING, false));
        assertTrue(Rank.KING.isOneAwayFrom(Rank.ACE, false));
    }
}
