package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;

class StrategyScoring {
    private StrategyScoring() {
    }

    static StrategySimulationState simulateMove(GameState state, Card move) {
        StrategySimulationState simulation = new StrategySimulationState(state);
        simulation.play(move);
        return simulation;
    }

    static int countNextMoves(GameState state, Card move) {
        return simulateMove(state, move).countAvailableMoves();
    }

    static int getSourceColumnSize(GameState state, Card move) {
        return new StrategySimulationState(state).getColumnSize(move);
    }
}
