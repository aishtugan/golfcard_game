package org.example.golfsolitaire.stats;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

public class StatisticsService {
    private static final String FILE_NAME = ".golf-solitaire-stats.properties";

    private final Path statisticsFile;

    public StatisticsService() {
        this(Paths.get(System.getProperty("user.home"), FILE_NAME));
    }

    public StatisticsService(Path statisticsFile) {
        this.statisticsFile = statisticsFile;
    }

    public Statistics load() {
        Statistics statistics = new Statistics();

        if (!Files.exists(statisticsFile)) {
            return statistics;
        }

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(statisticsFile)) {
            properties.load(inputStream);
            statistics.setTotalPlayerGames(readInt(properties, "totalPlayerGames"));
            statistics.setPlayerWins(readInt(properties, "playerWins"));
            statistics.setPlayerLosses(readInt(properties, "playerLosses"));
            statistics.setPlayerBestChainRecord(readInt(properties, "playerBestChainRecord"));
            statistics.setTotalComputerGames(readInt(properties, "totalComputerGames"));
            statistics.setComputerWins(readInt(properties, "computerWins"));
            statistics.setComputerLosses(readInt(properties, "computerLosses"));
            loadStrategyStatistics(properties, statistics);
        } catch (IOException e) {
            System.err.println("Could not load statistics: " + e.getMessage());
        }

        return statistics;
    }

    public void save(Statistics statistics) {
        Properties properties = new Properties();
        properties.setProperty("totalPlayerGames", String.valueOf(statistics.getTotalPlayerGames()));
        properties.setProperty("playerWins", String.valueOf(statistics.getPlayerWins()));
        properties.setProperty("playerLosses", String.valueOf(statistics.getPlayerLosses()));
        properties.setProperty("playerBestChainRecord", String.valueOf(statistics.getPlayerBestChainRecord()));
        properties.setProperty("totalComputerGames", String.valueOf(statistics.getTotalComputerGames()));
        properties.setProperty("computerWins", String.valueOf(statistics.getComputerWins()));
        properties.setProperty("computerLosses", String.valueOf(statistics.getComputerLosses()));
        saveStrategyStatistics(properties, statistics);

        try (OutputStream outputStream = Files.newOutputStream(statisticsFile)) {
            properties.store(outputStream, "Golf Solitaire statistics");
        } catch (IOException e) {
            System.err.println("Could not save statistics: " + e.getMessage());
        }
    }

    public void reset(Statistics statistics) {
        statistics.reset();
        save(statistics);
    }

    private int readInt(Properties properties, String key) {
        String value = properties.getProperty(key, "0");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void loadStrategyStatistics(Properties properties, Statistics statistics) {
        String strategyIds = properties.getProperty("computerStrategyIds", "");
        if (strategyIds.isBlank()) {
            return;
        }

        for (String strategyId : strategyIds.split(",")) {
            String trimmedStrategyId = strategyId.trim();
            if (trimmedStrategyId.isEmpty()) {
                continue;
            }

            StrategyStatistics strategyStatistics = new StrategyStatistics();
            strategyStatistics.setGames(readInt(properties, "strategy." + trimmedStrategyId + ".games"));
            strategyStatistics.setWins(readInt(properties, "strategy." + trimmedStrategyId + ".wins"));
            strategyStatistics.setLosses(readInt(properties, "strategy." + trimmedStrategyId + ".losses"));
            strategyStatistics.setBestChainRecord(readInt(properties, "strategy." + trimmedStrategyId + ".bestChainRecord"));
            statistics.setComputerStrategyStatistics(trimmedStrategyId, strategyStatistics);
        }
    }

    private void saveStrategyStatistics(Properties properties, Statistics statistics) {
        String strategyIds = statistics.getAllComputerStrategyStatistics()
                .keySet()
                .stream()
                .collect(Collectors.joining(","));
        properties.setProperty("computerStrategyIds", strategyIds);

        for (String strategyId : statistics.getAllComputerStrategyStatistics().keySet()) {
            StrategyStatistics strategyStatistics = statistics.getComputerStrategyStatistics(strategyId);
            properties.setProperty("strategy." + strategyId + ".games", String.valueOf(strategyStatistics.getGames()));
            properties.setProperty("strategy." + strategyId + ".wins", String.valueOf(strategyStatistics.getWins()));
            properties.setProperty("strategy." + strategyId + ".losses", String.valueOf(strategyStatistics.getLosses()));
            properties.setProperty("strategy." + strategyId + ".bestChainRecord", String.valueOf(strategyStatistics.getBestChainRecord()));
        }
    }
}
