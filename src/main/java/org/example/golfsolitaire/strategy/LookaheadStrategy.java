package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;

import java.util.List;

public class LookaheadStrategy implements GameStrategy {
    public static final int LOOKAHEAD_DEPTH = 3;

    @Override
    public Card chooseMove(GameState state, List<Card> availableMoves) {
        Card bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Card move : availableMoves) {
            StrategySimulationState simulation = StrategyScoring.simulateMove(state, move);
            int score = scorePosition(simulation, LOOKAHEAD_DEPTH - 1, 1);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int scorePosition(StrategySimulationState state, int depthRemaining, int removedCards) {
        int baseScore = removedCards * 10
                + state.countAvailableMoves() * 3
                + state.countEmptyColumns() * 5
                - state.countTableCards();

        if (depthRemaining <= 0 || state.isFinished()) {
            return baseScore;
        }

        List<Card> moves = state.getAvailableMoves();
        if (moves.isEmpty()) {
            return baseScore;
        }

        int bestFutureScore = baseScore;
        for (Card move : moves) {
            StrategySimulationState nextState = state.copy();
            nextState.play(move);
            bestFutureScore = Math.max(
                    bestFutureScore,
                    scorePosition(nextState, depthRemaining - 1, removedCards + 1)
            );
        }
        return bestFutureScore;
    }

    @Override
    public String getId() {
        return "lookahead";
    }

    @Override
    public String getName() {
        return "Lookahead Strategy";
    }

    @Override
    public String getDescription() {
        return "Simulates several future moves and chooses the move with the best expected position.";
    }

    @Override
    public String toString() {
        return getName();
    }
}
