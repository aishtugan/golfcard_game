package org.example.golfsolitaire.stats;

public class StrategyStatistics {
    private int games;
    private int wins;
    private int losses;
    private int bestChainRecord;

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getBestChainRecord() {
        return bestChainRecord;
    }

    public void setBestChainRecord(int bestChainRecord) {
        this.bestChainRecord = bestChainRecord;
    }

    public void recordGame(boolean won, int bestChainThisGame) {
        games++;
        if (won) {
            wins++;
        } else {
            losses++;
        }
        updateBestChainRecord(bestChainThisGame);
    }

    public void recordGames(int wins, int losses, int bestChainRecord) {
        if (wins < 0 || losses < 0) {
            throw new IllegalArgumentException("wins and losses must not be negative");
        }

        this.games += wins + losses;
        this.wins += wins;
        this.losses += losses;
        updateBestChainRecord(bestChainRecord);
    }

    private void updateBestChainRecord(int bestChainThisGame) {
        if (bestChainThisGame > bestChainRecord) {
            bestChainRecord = bestChainThisGame;
        }
    }

    public double getWinRate() {
        if (games == 0) {
            return 0.0;
        }
        return (double) wins / games;
    }

    public void reset() {
        games = 0;
        wins = 0;
        losses = 0;
        bestChainRecord = 0;
    }
}
