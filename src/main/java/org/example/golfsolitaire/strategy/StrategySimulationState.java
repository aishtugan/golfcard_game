package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.CardColumn;
import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class StrategySimulationState {
    private final List<List<Card>> columns;
    private final Deque<Card> stockCards;
    private Card activeCard;

    StrategySimulationState(GameState state) {
        columns = new ArrayList<>();
        for (CardColumn column : state.getTableColumns()) {
            columns.add(new ArrayList<>(column.cards()));
        }
        stockCards = new ArrayDeque<>(state.getStockCards());
        activeCard = state.getActiveCard();
    }

    private StrategySimulationState(List<List<Card>> columns, Deque<Card> stockCards, Card activeCard) {
        this.columns = columns;
        this.stockCards = stockCards;
        this.activeCard = activeCard;
    }

    StrategySimulationState copy() {
        List<List<Card>> copiedColumns = new ArrayList<>();
        for (List<Card> column : columns) {
            copiedColumns.add(new ArrayList<>(column));
        }
        return new StrategySimulationState(copiedColumns, new ArrayDeque<>(stockCards), activeCard);
    }

    void play(Card card) {
        for (List<Card> column : columns) {
            if (!column.isEmpty() && column.get(0).equals(card)) {
                column.remove(0);
                activeCard = card;
                return;
            }
        }
    }

    boolean drawFromStock() {
        Card drawnCard = stockCards.pollFirst();
        if (drawnCard == null) {
            return false;
        }
        activeCard = drawnCard;
        return true;
    }

    List<Card> getAvailableMoves() {
        List<Card> moves = new ArrayList<>();
        for (List<Card> column : columns) {
            if (!column.isEmpty() && column.get(0).getRank().isOneAwayFrom(activeCard.getRank(), true)) {
                moves.add(column.get(0));
            }
        }
        return moves;
    }

    int countAvailableMoves() {
        return getAvailableMoves().size();
    }

    int getColumnSize(Card card) {
        for (List<Card> column : columns) {
            if (!column.isEmpty() && column.get(0).equals(card)) {
                return column.size();
            }
        }
        return 0;
    }

    int countEmptyColumns() {
        int emptyColumns = 0;
        for (List<Card> column : columns) {
            if (column.isEmpty()) {
                emptyColumns++;
            }
        }
        return emptyColumns;
    }

    int countTableCards() {
        int total = 0;
        for (List<Card> column : columns) {
            total += column.size();
        }
        return total;
    }

    int getStockCount() {
        return stockCards.size();
    }

    boolean isWon() {
        return countTableCards() == 0;
    }

    boolean isLost() {
        return stockCards.isEmpty() && getAvailableMoves().isEmpty() && !isWon();
    }

    boolean isFinished() {
        return isWon() || isLost();
    }
}
