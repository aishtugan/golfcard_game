package org.example.golfsolitaire.stats;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void savesAndLoadsComputerStrategyStatistics() {
        Path statisticsFile = tempDir.resolve("stats.properties");
        StatisticsService statisticsService = new StatisticsService(statisticsFile);
        Statistics statistics = new Statistics();
        statistics.recordComputerGames("first_available", 4, 6);
        statistics.recordComputerGames("highest_next_move_count", 7, 3, 11);
        statistics.recordPlayerGame(org.example.golfsolitaire.game.GameStatus.WON, 9);

        statisticsService.save(statistics);
        Statistics loadedStatistics = statisticsService.load();

        assertEquals(10, loadedStatistics.getComputerStrategyStatistics("first_available").getGames());
        assertEquals(4, loadedStatistics.getComputerStrategyStatistics("first_available").getWins());
        assertEquals(10, loadedStatistics.getComputerStrategyStatistics("highest_next_move_count").getGames());
        assertEquals(7, loadedStatistics.getComputerStrategyStatistics("highest_next_move_count").getWins());
        assertEquals(11, loadedStatistics.getComputerStrategyStatistics("highest_next_move_count").getBestChainRecord());
        assertEquals(9, loadedStatistics.getPlayerBestChainRecord());
    }
}
