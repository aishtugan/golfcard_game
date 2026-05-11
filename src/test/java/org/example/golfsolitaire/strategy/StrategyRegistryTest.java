package org.example.golfsolitaire.strategy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyRegistryTest {
    @Test
    void registryContainsAllStrategyIdsInDisplayOrder() {
        List<String> strategyIds = StrategyRegistry.getAllStrategies()
                .stream()
                .map(GameStrategy::getId)
                .toList();

        assertEquals(List.of(
                "first_available",
                "highest_next_move_count",
                "lookahead",
                "column_balance",
                "stock_saving",
                "monte_carlo"
        ), strategyIds);
    }
}
