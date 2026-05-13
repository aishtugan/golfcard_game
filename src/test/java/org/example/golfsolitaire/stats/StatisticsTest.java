package org.example.golfsolitaire.stats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsTest {
    @Test
    void recordComputerGamesAddsBatchResultsToExistingStatistics() {
        Statistics statistics = new Statistics();
        statistics.recordComputerGames(12, 8);
        statistics.recordComputerGames(64, 36);

        assertEquals(120, statistics.getTotalComputerGames());
        assertEquals(76, statistics.getComputerWins());
        assertEquals(44, statistics.getComputerLosses());
    }

    @Test
    void recordComputerGamesTracksSelectedStrategySeparately() {
        Statistics statistics = new Statistics();
        statistics.recordComputerGames("first_available", 4, 6);
        statistics.recordComputerGames("highest_next_move_count", 7, 3);

        StrategyStatistics firstAvailable = statistics.getComputerStrategyStatistics("first_available");
        StrategyStatistics highestNextMoveCount = statistics.getComputerStrategyStatistics("highest_next_move_count");

        assertEquals(10, firstAvailable.getGames());
        assertEquals(4, firstAvailable.getWins());
        assertEquals(6, firstAvailable.getLosses());
        assertEquals(10, highestNextMoveCount.getGames());
        assertEquals(7, highestNextMoveCount.getWins());
        assertEquals(3, highestNextMoveCount.getLosses());
        assertEquals(20, statistics.getTotalComputerGames());
    }

    @Test
    void chainRecordsUseBestValueOnly() {
        Statistics statistics = new Statistics();
        statistics.recordPlayerGame(org.example.golfsolitaire.game.GameStatus.LOST, 4);
        statistics.recordPlayerGame(org.example.golfsolitaire.game.GameStatus.WON, 2);
        statistics.recordComputerGames("first_available", 4, 6, 8);
        statistics.recordComputerGames("first_available", 4, 6, 5);

        assertEquals(4, statistics.getPlayerBestChainRecord());
        assertEquals(8, statistics.getComputerStrategyStatistics("first_available").getBestChainRecord());
    }

    @Test
    void resetPlayerStatisticsKeepsComputerStrategyStatistics() {
        Statistics statistics = new Statistics();
        statistics.recordComputerGames("first_available", 4, 6);
        statistics.setTotalPlayerGames(5);
        statistics.setPlayerWins(2);
        statistics.setPlayerLosses(3);

        statistics.resetPlayerStatistics();

        assertEquals(0, statistics.getTotalPlayerGames());
        assertEquals(0, statistics.getPlayerWins());
        assertEquals(0, statistics.getPlayerLosses());
        assertEquals(10, statistics.getComputerStrategyStatistics("first_available").getGames());
    }

    @Test
    void resetClearsHumanAndComputerStrategyStatistics() {
        Statistics statistics = new Statistics();
        statistics.recordComputerGames("first_available", 4, 6);
        statistics.setTotalPlayerGames(5);
        statistics.setPlayerWins(2);
        statistics.setPlayerLosses(3);

        statistics.reset();

        assertEquals(0, statistics.getTotalPlayerGames());
        assertEquals(0, statistics.getTotalComputerGames());
        assertEquals(0, statistics.getAllComputerStrategyStatistics().size());
    }
}
