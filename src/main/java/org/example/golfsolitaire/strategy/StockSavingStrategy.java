package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;

import java.util.List;

public class StockSavingStrategy implements GameStrategy {
    @Override
    public Card chooseMove(GameState state, List<Card> availableMoves) {
        Card bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Card move : availableMoves) {
            StrategySimulationState simulation = StrategyScoring.simulateMove(state, move);
            int nextAvailableMoves = simulation.countAvailableMoves();
            int sourceColumnSize = StrategyScoring.getSourceColumnSize(state, move);
            int score = nextAvailableMoves * 10
                    + simulation.getStockCount()
                    + sourceColumnSize * 2;

            if (nextAvailableMoves > 0) {
                score += 25;
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    @Override
    public String getId() {
        return "stock_saving";
    }

    @Override
    public String getName() {
        return "Stock Saving Strategy";
    }

    @Override
    public String getDescription() {
        return "Chooses moves that are more likely to avoid drawing from stock on the next step.";
    }

    @Override
    public String toString() {
        return getName();
    }
}
