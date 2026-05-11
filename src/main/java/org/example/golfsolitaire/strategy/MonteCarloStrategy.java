package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;

import java.util.List;
import java.util.Random;

public class MonteCarloStrategy implements GameStrategy {
    public static final int MONTE_CARLO_SIMULATIONS_PER_MOVE = 50;
    public static final int MAX_SIMULATION_STEPS = 500;

    private final Random random = new Random();

    @Override
    public Card chooseMove(GameState state, List<Card> availableMoves) {
        Card bestMove = null;
        double bestWinRate = -1.0;
        double bestAverageRemovedCards = -1.0;
        int startingTableCards = state.getTableCards().size();

        for (Card move : availableMoves) {
            int wins = 0;
            int removedCardsTotal = 0;

            for (int simulationIndex = 0; simulationIndex < MONTE_CARLO_SIMULATIONS_PER_MOVE; simulationIndex++) {
                StrategySimulationState simulation = StrategyScoring.simulateMove(state, move);
                runRandomSimulation(simulation);
                if (simulation.isWon()) {
                    wins++;
                }
                removedCardsTotal += startingTableCards - simulation.countTableCards();
            }

            double winRate = (double) wins / MONTE_CARLO_SIMULATIONS_PER_MOVE;
            double averageRemovedCards = (double) removedCardsTotal / MONTE_CARLO_SIMULATIONS_PER_MOVE;
            if (winRate > bestWinRate || (winRate == bestWinRate && averageRemovedCards > bestAverageRemovedCards)) {
                bestWinRate = winRate;
                bestAverageRemovedCards = averageRemovedCards;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private void runRandomSimulation(StrategySimulationState simulation) {
        int steps = 0;
        while (!simulation.isFinished() && steps < MAX_SIMULATION_STEPS) {
            List<Card> moves = simulation.getAvailableMoves();
            if (!moves.isEmpty()) {
                simulation.play(moves.get(random.nextInt(moves.size())));
            } else if (!simulation.drawFromStock()) {
                break;
            }
            steps++;
        }
    }

    @Override
    public String getId() {
        return "monte_carlo";
    }

    @Override
    public String getName() {
        return "Monte Carlo Strategy";
    }

    @Override
    public String getDescription() {
        return "Runs random simulations for each possible move and chooses the move with the best estimated win probability.";
    }

    @Override
    public String toString() {
        return getName();
    }
}
