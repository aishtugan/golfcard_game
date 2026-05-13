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

    public void playOneTurn() {
        if (game.getState().getStatus() != GameStatus.IN_PROGRESS) {
            return;
        }

        List<Card> availableMoves = game.getAvailableMoves();
        if (!availableMoves.isEmpty()) {
            Card chosenCard = strategy.chooseMove(game.getState(), availableMoves);
            game.removeCard(chosenCard);
            return;
        }

        game.drawFromStock();
    }

    public void playUntilFinished() {
        while (game.getState().getStatus() == GameStatus.IN_PROGRESS) {
            playOneTurn();
        }
    }
}
