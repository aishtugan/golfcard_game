package org.example.golfsolitaire.game;

import org.example.golfsolitaire.model.Card;
import org.example.golfsolitaire.model.Deck;

import java.util.ArrayList;
import java.util.List;

public class GolfSolitaireGame {
    public static final int TABLE_COLUMN_COUNT = 7;
    public static final int CARDS_PER_COLUMN = 5;
    public static final int TABLE_CARD_COUNT = TABLE_COLUMN_COUNT * CARDS_PER_COLUMN;

    private final boolean aceKingConnected;
    private GameState state;
    private int currentChain;
    private int bestChainThisGame;

    public GolfSolitaireGame() {
        this(true);
    }

    public GolfSolitaireGame(boolean aceKingConnected) {
        this.aceKingConnected = true;
        startNewGame();
    }

    GolfSolitaireGame(GameState state, boolean aceKingConnected) {
        this.state = state;
        this.aceKingConnected = true;
        checkWinLoss();
    }

    public void startNewGame() {
        Deck deck = Deck.createStandardDeck();
        deck.shuffle();

        List<CardColumn> tableColumns = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < TABLE_COLUMN_COUNT; columnIndex++) {
            List<Card> columnCards = new ArrayList<>();
            for (int cardIndex = 0; cardIndex < CARDS_PER_COLUMN; cardIndex++) {
                columnCards.add(deck.draw().orElseThrow());
            }
            tableColumns.add(new CardColumn(columnCards));
        }

        Card activeCard = deck.draw().orElseThrow();

        List<Card> stockPile = new ArrayList<>();
        while (!deck.isEmpty()) {
            stockPile.add(deck.draw().orElseThrow());
        }

        state = new GameState(tableColumns, stockPile, activeCard);
        resetChains();
        checkWinLoss();
    }

    public GameState getState() {
        return state;
    }

    public boolean isAceKingConnected() {
        return aceKingConnected;
    }

    public boolean canRemove(Card card) {
        return state.getStatus() == GameStatus.IN_PROGRESS
                && card != null
                && card.isFaceUp()
                && state.isTopCard(card)
                && card.getRank().isOneAwayFrom(state.getActiveCard().getRank(), aceKingConnected);
    }

    public boolean canPlayTopCard(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= state.getTableColumns().size()) {
            return false;
        }

        return state.getTableColumns()
                .get(columnIndex)
                .getTopCard()
                .map(this::canRemove)
                .orElse(false);
    }

    public boolean removeCard(Card card) {
        if (!canRemove(card)) {
            return false;
        }

        state.removeTableCard(card);
        state.setActiveCard(card);
        currentChain++;
        if (currentChain > bestChainThisGame) {
            bestChainThisGame = currentChain;
        }
        checkWinLoss();
        return true;
    }

    public Card drawFromStock() {
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            return null;
        }

        Card drawnCard = state.drawFromStock().orElse(null);
        if (drawnCard != null) {
            state.setActiveCard(drawnCard);
            currentChain = 0;
            checkWinLoss();
        }
        return drawnCard;
    }

    public int getCurrentChain() {
        return currentChain;
    }

    public int getBestChainThisGame() {
        return bestChainThisGame;
    }

    public GameSnapshot createSnapshot() {
        List<List<Card>> tableColumns = new ArrayList<>();
        for (CardColumn column : state.getTableColumns()) {
            tableColumns.add(new ArrayList<>(column.cards()));
        }
        return new GameSnapshot(tableColumns, state.getStockCards(), state.getActiveCard());
    }

    public void restoreFromSnapshot(GameSnapshot snapshot) {
        List<CardColumn> tableColumns = new ArrayList<>();
        for (List<Card> column : snapshot.getTableColumns()) {
            tableColumns.add(new CardColumn(column));
        }
        state = new GameState(tableColumns, snapshot.getStockCards(), snapshot.getActiveCard());
        resetChains();
        checkWinLoss();
    }

    private void resetChains() {
        currentChain = 0;
        bestChainThisGame = 0;
    }

    public List<Card> getAvailableMoves() {
        List<Card> availableMoves = new ArrayList<>();
        for (Card card : state.getTopCards()) {
            if (canRemove(card)) {
                availableMoves.add(card);
            }
        }
        return availableMoves;
    }

    public boolean hasAvailableMoves() {
        return !getAvailableMoves().isEmpty();
    }

    public boolean isStockEmpty() {
        return state.getStockCount() == 0;
    }

    public void checkGameEnd() {
        if (state.isTableEmpty()) {
            state.setStatus(GameStatus.WON);
        } else if (isStockEmpty() && !hasAvailableMoves()) {
            state.setStatus(GameStatus.LOST);
        } else {
            state.setStatus(GameStatus.IN_PROGRESS);
        }
    }

    public void checkWinLoss() {
        checkGameEnd();
    }
}
