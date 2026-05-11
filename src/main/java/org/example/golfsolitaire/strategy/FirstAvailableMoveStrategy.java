package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;

import java.util.List;

public class FirstAvailableMoveStrategy implements GameStrategy {
    @Override
    public Card chooseMove(GameState state, List<Card> availableMoves) {
        if (availableMoves.isEmpty()) {
            return null;
        }
        return availableMoves.get(0);
    }

    @Override
    public String getId() {
        return "first_available";
    }

    @Override
    public String getName() {
        return "First Available Move";
    }

    @Override
    public String getDescription() {
        return "Chooses the first available playable card. Fast, but does not look ahead.";
    }

    @Override
    public String toString() {
        return getName();
    }
}
