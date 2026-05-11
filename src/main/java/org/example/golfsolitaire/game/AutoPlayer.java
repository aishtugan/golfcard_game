package org.example.golfsolitaire.game;

import org.example.golfsolitaire.model.Card;
import org.example.golfsolitaire.strategy.GameStrategy;

import java.util.List;

public class AutoPlayer {
    private final GolfSolitaireGame game;
    private final GameStrategy strategy;

    public AutoPlayer(GolfSolitaireGame game, GameStrategy strategy) {
        this.game = game;
        this.strategy = strategy;
    }

    public boolean playOneTurn() {
        if (game.getState().getStatus() != GameStatus.IN_PROGRESS) {
            return false;
        }

        List<Card> availableMoves = game.getAvailableMoves();
        if (!availableMoves.isEmpty()) {
            Card chosenCard = strategy.chooseMove(game.getState(), availableMoves);
            return game.removeCard(chosenCard);
        }

        return game.drawFromStock() != null;
    }

    public void playUntilFinished() {
        while (game.getState().getStatus() == GameStatus.IN_PROGRESS) {
            playOneTurn();
        }
    }
}
