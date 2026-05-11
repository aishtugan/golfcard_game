package org.example.golfsolitaire.game;

import org.example.golfsolitaire.model.Card;
import org.example.golfsolitaire.model.Rank;
import org.example.golfsolitaire.model.Suit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GolfSolitaireGameTest {
    @Test
    void removesOnlyCardsOneRankAwayFromActiveCard() {
        Card six = new Card(Suit.CLUBS, Rank.SIX);
        Card eight = new Card(Suit.HEARTS, Rank.EIGHT);
        Card king = new Card(Suit.SPADES, Rank.KING);
        Card seven = new Card(Suit.DIAMONDS, Rank.SEVEN);

        GameState state = new GameState(
                List.of(new CardColumn(List.of(six)), new CardColumn(List.of(eight)), new CardColumn(List.of(king))),
                List.of(),
                seven
        );
        GolfSolitaireGame game = new GolfSolitaireGame(state, false);

        assertTrue(game.canRemove(six));
        assertTrue(game.canRemove(eight));
        assertFalse(game.canRemove(king));

        assertTrue(game.removeCard(eight));
        assertEquals(eight, game.getState().getActiveCard());
        assertEquals(2, game.getState().getTableCards().size());
    }

    @Test
    void losesWhenNoMovesAndStockIsEmpty() {
        Card five = new Card(Suit.CLUBS, Rank.FIVE);
        Card king = new Card(Suit.SPADES, Rank.KING);

        GameState state = new GameState(List.of(new CardColumn(List.of(king))), List.of(), five);
        GolfSolitaireGame game = new GolfSolitaireGame(state, false);

        assertEquals(GameStatus.LOST, game.getState().getStatus());
    }

    @Test
    void winsWhenLastTableCardIsRemoved() {
        Card six = new Card(Suit.CLUBS, Rank.SIX);
        Card seven = new Card(Suit.DIAMONDS, Rank.SEVEN);

        GameState state = new GameState(List.of(new CardColumn(List.of(six))), List.of(), seven);
        GolfSolitaireGame game = new GolfSolitaireGame(state, false);

        assertTrue(game.removeCard(six));
        assertEquals(GameStatus.WON, game.getState().getStatus());
    }

    @Test
    void onlyTopCardOfEachColumnCanBeRemoved() {
        Card topSix = new Card(Suit.CLUBS, Rank.SIX);
        Card coveredEight = new Card(Suit.HEARTS, Rank.EIGHT);
        Card seven = new Card(Suit.DIAMONDS, Rank.SEVEN);

        GameState state = new GameState(List.of(new CardColumn(List.of(topSix, coveredEight))), List.of(), seven);
        GolfSolitaireGame game = new GolfSolitaireGame(state, false);

        assertTrue(game.canRemove(topSix));
        assertTrue(game.canPlayTopCard(0));
        assertFalse(game.canRemove(coveredEight));
        assertEquals(List.of(topSix), game.getAvailableMoves());
    }

    @Test
    void drawFromStockChangesActiveCard() {
        Card five = new Card(Suit.CLUBS, Rank.FIVE);
        Card king = new Card(Suit.SPADES, Rank.KING);
        Card queen = new Card(Suit.HEARTS, Rank.QUEEN);

        GameState state = new GameState(List.of(new CardColumn(List.of(king))), List.of(queen), five);
        GolfSolitaireGame game = new GolfSolitaireGame(state, false);

        Card drawnCard = game.drawFromStock();

        assertNotNull(drawnCard);
        assertEquals(queen, game.getState().getActiveCard());
        assertTrue(game.isStockEmpty());
        assertTrue(game.hasAvailableMoves());
    }

    @Test
    void newGameDealsSevenColumnsWithFiveCardsAndSixteenStockCards() {
        GolfSolitaireGame game = new GolfSolitaireGame();

        assertEquals(GolfSolitaireGame.TABLE_COLUMN_COUNT, game.getState().getTableColumns().size());
        for (CardColumn column : game.getState().getTableColumns()) {
            assertEquals(GolfSolitaireGame.CARDS_PER_COLUMN, column.getCards().size());
        }
        assertEquals(16, game.getState().getStockCount());
    }

    @Test
    void aceAndKingArePlayableAfterEachOther() {
        Card ace = new Card(Suit.CLUBS, Rank.ACE);
        Card king = new Card(Suit.SPADES, Rank.KING);
        Card queen = new Card(Suit.HEARTS, Rank.QUEEN);
        Card two = new Card(Suit.DIAMONDS, Rank.TWO);

        GameState kingActiveState = new GameState(
                List.of(new CardColumn(List.of(ace)), new CardColumn(List.of(queen))),
                List.of(),
                king
        );
        GolfSolitaireGame kingActiveGame = new GolfSolitaireGame(kingActiveState, false);

        assertTrue(kingActiveGame.canRemove(ace));
        assertTrue(kingActiveGame.canRemove(queen));

        GameState aceActiveState = new GameState(
                List.of(new CardColumn(List.of(king)), new CardColumn(List.of(two))),
                List.of(),
                ace
        );
        GolfSolitaireGame aceActiveGame = new GolfSolitaireGame(aceActiveState, false);

        assertTrue(aceActiveGame.canRemove(king));
        assertTrue(aceActiveGame.canRemove(two));
    }
}
