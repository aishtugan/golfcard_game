package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;

import java.util.List;

public interface GameStrategy {
    /*
     * A strategy receives only the currently playable cards. The game layer has already
     * filtered this list so it contains only bottom/top cards that match the active card.
     * To add a stronger computer player later, create another implementation and choose
     * which strategy MainFrame passes to AutoPlayer.
    */
    Card chooseMove(GameState state, List<Card> availableMoves);

    String getId();

    default String getName() {
        return getClass().getSimpleName();
    }

    default String getDescription() {
        return "Chooses one card from the currently playable moves.";
    }
}
