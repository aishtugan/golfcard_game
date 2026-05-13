package org.example.golfsolitaire.stats;

import org.example.golfsolitaire.game.GameStatus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Statistics {
    private int totalPlayerGames;
    private int playerWins;
    private int playerLosses;
    private int playerBestChainRecord;
    private int totalComputerGames;
    private int computerWins;
    private int computerLosses;
    private final Map<String, StrategyStatistics> computerStrategyStatistics = new LinkedHashMap<>();

    public int getTotalPlayerGames() {
        return totalPlayerGames;
    }

    public void setTotalPlayerGames(int totalPlayerGames) {
        this.totalPlayerGames = totalPlayerGames;
    }

    public int getPlayerWins() {
        return playerWins;
    }

    public void setPlayerWins(int playerWins) {
        this.playerWins = playerWins;
    }

    public int getPlayerLosses() {
        return playerLosses;
    }

    public void setPlayerLosses(int playerLosses) {
        this.playerLosses = playerLosses;
    }

    public int getPlayerBestChainRecord() {
        return playerBestChainRecord;
    }

    public void setPlayerBestChainRecord(int playerBestChainRecord) {
        this.playerBestChainRecord = playerBestChainRecord;
    }

    public int getTotalComputerGames() {
        return totalComputerGames;
    }

    public void setTotalComputerGames(int totalComputerGames) {
        this.totalComputerGames = totalComputerGames;
    }

    public int getComputerWins() {
        return computerWins;
    }

    public void setComputerWins(int computerWins) {
        this.computerWins = computerWins;
    }

    public int getComputerLosses() {
        return computerLosses;
    }

    public void setComputerLosses(int computerLosses) {
        this.computerLosses = computerLosses;
    }

    public void recordPlayerGame(GameStatus result) {
        recordPlayerGame(result, 0);
    }

    public void recordPlayerGame(GameStatus result, int bestChainThisGame) {
        totalPlayerGames++;
        if (result == GameStatus.WON) {
            playerWins++;
        } else if (result == GameStatus.LOST) {
            playerLosses++;
        }
        if (bestChainThisGame > playerBestChainRecord) {
            playerBestChainRecord = bestChainThisGame;
        }
    }

    public void recordComputerGame(GameStatus result) {
        totalComputerGames++;
        if (result == GameStatus.WON) {
            computerWins++;
        } else if (result == GameStatus.LOST) {
            computerLosses++;
        }
    }

    public void recordComputerGame(String strategyId, GameStatus result) {
        recordComputerGame(result);
        if (result == GameStatus.WON || result == GameStatus.LOST) {
            getComputerStrategyStatistics(strategyId).recordGame(result == GameStatus.WON, 0);
        }
    }

    public void recordComputerGame(String strategyId, GameStatus result, int bestChainThisGame) {
        recordComputerGame(result);
        if (result == GameStatus.WON || result == GameStatus.LOST) {
            getComputerStrategyStatistics(strategyId).recordGame(result == GameStatus.WON, bestChainThisGame);
        }
    }

    public void recordComputerGames(int wins, int losses) {
        if (wins < 0 || losses < 0) {
            throw new IllegalArgumentException("wins and losses must not be negative");
        }

        totalComputerGames += wins + losses;
        computerWins += wins;
        computerLosses += losses;
    }

    public void recordComputerGames(String strategyId, int wins, int losses) {
        recordComputerGames(wins, losses);
        getComputerStrategyStatistics(strategyId).recordGames(wins, losses, 0);
    }

    public void recordComputerGames(String strategyId, int wins, int losses, int bestChainRecord) {
        recordComputerGames(wins, losses);
        getComputerStrategyStatistics(strategyId).recordGames(wins, losses, bestChainRecord);
    }

    public StrategyStatistics getComputerStrategyStatistics(String strategyId) {
        return computerStrategyStatistics.computeIfAbsent(strategyId, id -> new StrategyStatistics());
    }

    public Map<String, StrategyStatistics> getAllComputerStrategyStatistics() {
        return Collections.unmodifiableMap(computerStrategyStatistics);
    }

    public void setComputerStrategyStatistics(String strategyId, StrategyStatistics strategyStatistics) {
        computerStrategyStatistics.put(strategyId, strategyStatistics);
    }

    public double getPlayerWinRate() {
        if (totalPlayerGames == 0) {
            return 0.0;
        }
        return (double) playerWins / totalPlayerGames;
    }

    public double getComputerWinRate() {
        if (totalComputerGames == 0) {
            return 0.0;
        }
        return (double) computerWins / totalComputerGames;
    }

    public void reset() {
        totalPlayerGames = 0;
        playerWins = 0;
        playerLosses = 0;
        playerBestChainRecord = 0;
        totalComputerGames = 0;
        computerWins = 0;
        computerLosses = 0;
        computerStrategyStatistics.clear();
    }

    public void resetPlayerStatistics() {
        totalPlayerGames = 0;
        playerWins = 0;
        playerLosses = 0;
        playerBestChainRecord = 0;
    }

    public void resetComputerStatistics() {
        totalComputerGames = 0;
        computerWins = 0;
        computerLosses = 0;
        computerStrategyStatistics.clear();
    }
}
