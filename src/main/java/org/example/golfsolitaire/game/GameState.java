package org.example.golfsolitaire.game;

import org.example.golfsolitaire.model.Card;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameState {
    private final List<CardColumn> tableColumns;
    private final Deque<Card> stockPile;
    private Card activeCard;
    private GameStatus status;

    public GameState(List<CardColumn> tableColumns, List<Card> stockPile, Card activeCard) {
        this.tableColumns = new ArrayList<>(tableColumns);
        this.stockPile = new ArrayDeque<>(stockPile);
        this.activeCard = Objects.requireNonNull(activeCard, "activeCard");
        this.status = GameStatus.IN_PROGRESS;
    }

    public List<CardColumn> getTableColumns() {
        return Collections.unmodifiableList(tableColumns);
    }

    public List<Card> getTableCards() {
        List<Card> tableCards = new ArrayList<>();
        for (CardColumn column : tableColumns) {
            tableCards.addAll(column.getCards());
        }
        return Collections.unmodifiableList(tableCards);
    }

    public List<Card> getTopCards() {
        List<Card> topCards = new ArrayList<>();
        for (CardColumn column : tableColumns) {
            column.getTopCard().ifPresent(topCards::add);
        }
        return Collections.unmodifiableList(topCards);
    }

    public int getStockCount() {
        return stockPile.size();
    }

    public List<Card> getStockCards() {
        return Collections.unmodifiableList(new ArrayList<>(stockPile));
    }

    public Card getActiveCard() {
        return activeCard;
    }

    public GameStatus getStatus() {
        return status;
    }

    void setStatus(GameStatus status) {
        this.status = status;
    }

    void setActiveCard(Card activeCard) {
        this.activeCard = Objects.requireNonNull(activeCard, "activeCard");
    }

    boolean removeTableCard(Card card) {
        for (CardColumn column : tableColumns) {
            if (column.removeTopCard(card)) {
                return true;
            }
        }
        return false;
    }

    boolean isTopCard(Card card) {
        for (CardColumn column : tableColumns) {
            if (column.isTopCard(card)) {
                return true;
            }
        }
        return false;
    }

    boolean isTableEmpty() {
        for (CardColumn column : tableColumns) {
            if (!column.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    Optional<Card> drawFromStock() {
        return Optional.ofNullable(stockPile.pollFirst());
    }
}
