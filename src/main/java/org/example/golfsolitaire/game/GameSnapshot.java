package org.example.golfsolitaire.game;

import org.example.golfsolitaire.model.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameSnapshot {
    private final List<List<Card>> tableColumns;
    private final List<Card> stockCards;
    private final Card activeCard;

    public GameSnapshot(List<List<Card>> tableColumns, List<Card> stockCards, Card activeCard) {
        this.tableColumns = new ArrayList<>();
        for (List<Card> column : tableColumns) {
            this.tableColumns.add(new ArrayList<>(column));
        }
        this.stockCards = new ArrayList<>(stockCards);
        this.activeCard = activeCard;
    }

    public List<List<Card>> getTableColumns() {
        List<List<Card>> copy = new ArrayList<>();
        for (List<Card> column : tableColumns) {
            copy.add(Collections.unmodifiableList(new ArrayList<>(column)));
        }
        return Collections.unmodifiableList(copy);
    }

    public List<Card> getStockCards() {
        return Collections.unmodifiableList(new ArrayList<>(stockCards));
    }

    public Card getActiveCard() {
        return activeCard;
    }
}
