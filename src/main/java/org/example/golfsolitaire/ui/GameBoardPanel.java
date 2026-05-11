package org.example.golfsolitaire.ui;

import org.example.golfsolitaire.game.CardColumn;
import org.example.golfsolitaire.game.GameStatus;
import org.example.golfsolitaire.game.GolfSolitaireGame;
import org.example.golfsolitaire.model.Card;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GameBoardPanel extends JPanel {
    private static final double CARD_ASPECT_RATIO = 82.0 / 116.0;
    private static final int OUTER_PADDING = 24;
    private static final int MIN_CARD_WIDTH = 44;
    private static final int MAX_CARD_WIDTH = 96;

    private final CardImageService cardImageService;
    private final Consumer<Card> cardClickHandler;
    private final Runnable stockClickHandler;
    private final Map<Card, Rectangle> topCardBounds = new HashMap<>();

    private GolfSolitaireGame game;
    private Rectangle stockBounds = new Rectangle();
    private boolean manualMode = true;

    public GameBoardPanel(
            CardImageService cardImageService,
            Consumer<Card> cardClickHandler,
            Runnable stockClickHandler
    ) {
        this.cardImageService = cardImageService;
        this.cardClickHandler = cardClickHandler;
        this.stockClickHandler = stockClickHandler;
        setOpaque(true);
        setPreferredSize(new Dimension(760, 560));
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        addMouseListener(new BoardMouseListener());
        addMouseMotionListener(new BoardMouseListener());
    }

    public void setGame(GolfSolitaireGame game) {
        this.game = game;
        repaint();
    }

    public void setManualMode(boolean manualMode) {
        this.manualMode = manualMode;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        paintTableBackground(g);
        topCardBounds.clear();
        stockBounds = new Rectangle();

        if (game != null) {
            BoardLayout layout = calculateLayout();
            paintColumns(g, layout);
            paintBottomCards(g, layout);
        }

        g.dispose();
    }

    private void paintTableBackground(Graphics2D g) {
        Color base = new Color(116, 63, 25);
        g.setColor(base);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setStroke(new BasicStroke(2));
        for (int y = 0; y < getHeight(); y += 54) {
            g.setColor(new Color(87, 43, 16, 130));
            g.drawLine(0, y, getWidth(), y);
            g.setColor(new Color(165, 94, 39, 90));
            g.drawLine(0, y + 3, getWidth(), y + 3);
        }

        g.setColor(new Color(255, 255, 255, 20));
        for (int x = 30; x < getWidth(); x += 130) {
            g.fillOval(x, 20 + (x % 90), 34, 10);
        }
    }

    private BoardLayout calculateLayout() {
        int availableWidth = Math.max(1, getWidth() - OUTER_PADDING * 2);
        int availableHeight = Math.max(1, getHeight() - OUTER_PADDING * 2);
        int bottomAreaHeight = Math.max(130, availableHeight / 4);
        int columnAreaHeight = Math.max(1, availableHeight - bottomAreaHeight - 16);
        int columnGap = Math.max(6, availableWidth / 90);

        int cardWidthByWidth = (availableWidth - columnGap * (GolfSolitaireGame.TABLE_COLUMN_COUNT - 1))
                / GolfSolitaireGame.TABLE_COLUMN_COUNT;
        int cardWidthByHeight = (int) ((columnAreaHeight * CARD_ASPECT_RATIO) / 1.85);
        int cardWidth = clamp(Math.min(cardWidthByWidth, cardWidthByHeight), MIN_CARD_WIDTH, MAX_CARD_WIDTH);
        int cardHeight = (int) Math.round(cardWidth / CARD_ASPECT_RATIO);

        int overlap = calculateOverlap(columnAreaHeight, cardHeight);
        int totalColumnsWidth = cardWidth * GolfSolitaireGame.TABLE_COLUMN_COUNT
                + columnGap * (GolfSolitaireGame.TABLE_COLUMN_COUNT - 1);
        int startX = Math.max(OUTER_PADDING, (getWidth() - totalColumnsWidth) / 2);
        int startY = OUTER_PADDING;

        int stockY = getHeight() - OUTER_PADDING - cardHeight - Math.max(22, cardHeight / 6);
        int stockX = Math.max(OUTER_PADDING, (getWidth() - cardWidth * 2 - columnGap * 3) / 2);
        int activeX = stockX + cardWidth + columnGap * 3;

        return new BoardLayout(cardWidth, cardHeight, overlap, columnGap, startX, startY, stockX, activeX, stockY);
    }

    private int calculateOverlap(int columnAreaHeight, int cardHeight) {
        int cardCount = GolfSolitaireGame.CARDS_PER_COLUMN;
        if (cardCount <= 1) {
            return 0;
        }

        int availableStepSpace = columnAreaHeight - cardHeight;
        int defaultOverlap = Math.max(18, cardHeight / 3);
        if (availableStepSpace <= 0) {
            return 12;
        }
        return Math.min(defaultOverlap, Math.max(10, availableStepSpace / (cardCount - 1)));
    }

    private int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private void paintColumns(Graphics2D g, BoardLayout layout) {
        for (int columnIndex = 0; columnIndex < game.getState().getTableColumns().size(); columnIndex++) {
            CardColumn column = game.getState().getTableColumns().get(columnIndex);
            int x = layout.columnStartX + columnIndex * (layout.cardWidth + layout.columnGap);
            paintColumn(g, column, x, layout);
        }
    }

    private void paintColumn(Graphics2D g, CardColumn column, int x, BoardLayout layout) {
        for (int cardIndex = column.getCards().size() - 1; cardIndex >= 0; cardIndex--) {
            Card card = column.getCards().get(cardIndex);
            int visualIndexFromTop = column.getCards().size() - 1 - cardIndex;
            int y = layout.columnStartY + visualIndexFromTop * layout.verticalOverlap;
            Rectangle bounds = new Rectangle(x, y, layout.cardWidth, layout.cardHeight);
            paintCard(g, cardImageService.getCardImage(card).getImage(), bounds);

            if (cardIndex == 0) {
                topCardBounds.put(card, bounds);
                paintTopCardBorder(g, card, bounds);
            } else {
                paintCoveredOverlay(g, bounds);
            }
        }
    }

    private void paintBottomCards(Graphics2D g, BoardLayout layout) {
        stockBounds = new Rectangle(layout.stockX, layout.stockY, layout.cardWidth, layout.cardHeight);
        Rectangle activeBounds = new Rectangle(layout.activeX, layout.stockY, layout.cardWidth, layout.cardHeight);

        if (!game.isStockEmpty()) {
            paintCard(g, cardImageService.getCardBackImage().getImage(), stockBounds);
            if (canClickStock()) {
                g.setColor(new Color(46, 125, 50));
                g.setStroke(new BasicStroke(3));
                g.drawRoundRect(stockBounds.x - 2, stockBounds.y - 2, stockBounds.width + 4, stockBounds.height + 4, 8, 8);
            }
        } else {
            paintEmptySlot(g, stockBounds);
        }

        paintCard(g, cardImageService.getCardImage(game.getState().getActiveCard()).getImage(), activeBounds);
        paintBottomLabel(g, "Stock: " + game.getState().getStockCount(), stockBounds);
        paintBottomLabel(g, "Active", activeBounds);
    }

    private void paintCard(Graphics2D g, Image image, Rectangle bounds) {
        g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, this);
    }

    private void paintTopCardBorder(Graphics2D g, Card card, Rectangle bounds) {
        if (game.canRemove(card) && manualMode) {
            g.setColor(new Color(92, 210, 98));
            g.setStroke(new BasicStroke(3));
        } else {
            g.setColor(new Color(255, 255, 255, 110));
            g.setStroke(new BasicStroke(1));
        }
        g.drawRoundRect(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1, 8, 8);
    }

    private void paintCoveredOverlay(Graphics2D g, Rectangle bounds) {
        g.setColor(new Color(0, 0, 0, 25));
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);
    }

    private void paintEmptySlot(Graphics2D g, Rectangle bounds) {
        g.setColor(new Color(255, 255, 255, 55));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8);
    }

    private void paintBottomLabel(Graphics2D g, String text, Rectangle bounds) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, bounds.width / 7)));
        FontMetrics metrics = g.getFontMetrics();
        int x = bounds.x + (bounds.width - metrics.stringWidth(text)) / 2;
        int y = bounds.y + bounds.height + metrics.getAscent() + 6;

        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(text, x + 1, y + 1);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }

    private boolean canClickStock() {
        return manualMode
                && game != null
                && game.getState().getStatus() == GameStatus.IN_PROGRESS
                && !game.isStockEmpty();
    }

    private void updateCursor(Point point) {
        if (canClickStock() && stockBounds.contains(point)) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if (manualMode && game != null && game.getState().getStatus() == GameStatus.IN_PROGRESS) {
            for (Rectangle bounds : topCardBounds.values()) {
                if (bounds.contains(point)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return;
                }
            }
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private class BoardMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            if (game == null || !manualMode || game.getState().getStatus() != GameStatus.IN_PROGRESS) {
                return;
            }

            if (stockBounds.contains(event.getPoint())) {
                if (canClickStock()) {
                    stockClickHandler.run();
                }
                return;
            }

            for (Map.Entry<Card, Rectangle> entry : topCardBounds.entrySet()) {
                if (entry.getValue().contains(event.getPoint())) {
                    cardClickHandler.accept(entry.getKey());
                    return;
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent event) {
            updateCursor(event.getPoint());
        }

        @Override
        public void mouseExited(MouseEvent event) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private static class BoardLayout {
        private final int cardWidth;
        private final int cardHeight;
        private final int verticalOverlap;
        private final int columnGap;
        private final int columnStartX;
        private final int columnStartY;
        private final int stockX;
        private final int activeX;
        private final int stockY;

        private BoardLayout(
                int cardWidth,
                int cardHeight,
                int verticalOverlap,
                int columnGap,
                int columnStartX,
                int columnStartY,
                int stockX,
                int activeX,
                int stockY
        ) {
            this.cardWidth = cardWidth;
            this.cardHeight = cardHeight;
            this.verticalOverlap = verticalOverlap;
            this.columnGap = columnGap;
            this.columnStartX = columnStartX;
            this.columnStartY = columnStartY;
            this.stockX = stockX;
            this.activeX = activeX;
            this.stockY = stockY;
        }
    }
}
