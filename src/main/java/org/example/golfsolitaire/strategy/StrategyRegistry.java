package org.example.golfsolitaire.strategy;

import java.util.List;

public class StrategyRegistry {
    private StrategyRegistry() {
    }

    public static List<GameStrategy> getAllStrategies() {
        return List.of(
                new FirstAvailableMoveStrategy(),
                new HighestNextMoveCountStrategy(true),
                new LookaheadStrategy(),
                new ColumnBalanceStrategy(),
                new StockSavingStrategy(),
                new MonteCarloStrategy()
        );
    }
}
