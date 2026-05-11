package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;

import java.util.List;

public class ColumnBalanceStrategy implements GameStrategy {
    @Override
    public Card chooseMove(GameState state, List<Card> availableMoves) {
        Card bestMove = null;
        int bestColumnSize = -1;
        int bestNextMoves = -1;

        for (Card move : availableMoves) {
            int columnSize = StrategyScoring.getSourceColumnSize(state, move);
            int nextMoves = StrategyScoring.countNextMoves(state, move);
            if (columnSize > bestColumnSize || (columnSize == bestColumnSize && nextMoves > bestNextMoves)) {
                bestColumnSize = columnSize;
                bestNextMoves = nextMoves;
                bestMove = move;
            }
        }

        return bestMove;
    }

    @Override
    public String getId() {
        return "column_balance";
    }

    @Override
    public String getName() {
        return "Column Balance Strategy";
    }

    @Override
    public String getDescription() {
        return "Prefers moves from longer columns to balance and clear the board more evenly.";
    }

    @Override
    public String toString() {
        return getName();
    }
}
