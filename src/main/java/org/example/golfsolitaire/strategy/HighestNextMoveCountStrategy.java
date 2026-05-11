package org.example.golfsolitaire.strategy;

import org.example.golfsolitaire.game.GameState;
import org.example.golfsolitaire.model.Card;
import org.example.golfsolitaire.game.CardColumn;

import java.util.List;

public class HighestNextMoveCountStrategy implements GameStrategy {
    private final boolean aceKingConnected;

    public HighestNextMoveCountStrategy(boolean aceKingConnected) {
        this.aceKingConnected = aceKingConnected;
    }

    @Override
    public Card chooseMove(GameState state, List<Card> availableMoves) {
        Card bestCard = null;
        int bestScore = -1;

        for (Card possibleMove : availableMoves) {
            int nextMoveCount = countPossibleNextMoves(state, possibleMove);
            if (nextMoveCount > bestScore) {
                bestScore = nextMoveCount;
                bestCard = possibleMove;
            }
        }

        return bestCard;
    }

    @Override
    public String getName() {
        return "Highest Next Move Count";
    }

    @Override
    public String getDescription() {
        return "Chooses the playable card that creates the highest number of immediately playable next cards.";
    }

    @Override
    public String getId() {
        return "highest_next_move_count";
    }

    @Override
    public String toString() {
        return getName();
    }

    private int countPossibleNextMoves(GameState state, Card activeAfterMove) {
        int count = 0;
        for (CardColumn column : state.getTableColumns()) {
            Card nextTopCard = getNextTopCard(column, activeAfterMove);
            if (nextTopCard != null
                    && nextTopCard.getRank().isOneAwayFrom(activeAfterMove.getRank(), aceKingConnected)) {
                count++;
            }
        }
        return count;
    }

    private Card getNextTopCard(CardColumn column, Card activeAfterMove) {
        if (column.isTopCard(activeAfterMove)) {
            return column.getCardUnderTop().orElse(null);
        }
        return column.getTopCard().orElse(null);
    }
}
