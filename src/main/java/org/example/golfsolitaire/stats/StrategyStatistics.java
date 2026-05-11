package org.example.golfsolitaire.stats;

public class StrategyStatistics {
    private int games;
    private int wins;
    private int losses;

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

    public void recordGame(boolean won) {
        games++;
        if (won) {
            wins++;
        } else {
            losses++;
        }
    }

    public void recordGames(int wins, int losses) {
        if (wins < 0 || losses < 0) {
            throw new IllegalArgumentException("wins and losses must not be negative");
        }

        this.games += wins + losses;
        this.wins += wins;
        this.losses += losses;
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
    }
}
