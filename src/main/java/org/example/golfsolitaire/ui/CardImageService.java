package org.example.golfsolitaire.ui;

import org.example.golfsolitaire.model.Card;
import org.example.golfsolitaire.model.Suit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CardImageService {
    public static final int CARD_WIDTH = 82;
    public static final int CARD_HEIGHT = 116;

    private final Map<String, ImageIcon> cache = new HashMap<>();

    public ImageIcon getCardImage(Card card) {
        String imageName = card.getShortName() + ".png";
        return cache.computeIfAbsent(imageName, key -> loadImage("/cards/" + key, card));
    }

    public ImageIcon getCardBackImage() {
        return cache.computeIfAbsent("back.png", key -> loadImage("/cards/back.png", null));
    }

    private ImageIcon loadImage(String resourcePath, Card card) {
        URL resource = getClass().getResource(resourcePath);
        if (resource != null) {
            return new ImageIcon(resource);
        }

        if (card == null) {
            return new ImageIcon(createFallbackBackImage());
        }
        return new ImageIcon(createFallbackCardImage(card));
    }

    private BufferedImage createFallbackCardImage(Card card) {
        BufferedImage image = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        configureGraphics(graphics);

        graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(0, 0, CARD_WIDTH - 1, CARD_HEIGHT - 1, 10, 10);
        graphics.setColor(new Color(35, 35, 35));
        graphics.drawRoundRect(0, 0, CARD_WIDTH - 1, CARD_HEIGHT - 1, 10, 10);

        graphics.setColor(getCardColor(card));
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        graphics.drawString(card.getRank().getDisplayName(), 10, 26);
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 34));
        graphics.drawString(card.getSuit().getDisplayName(), CARD_WIDTH / 2 - 11, CARD_HEIGHT / 2 + 12);

        graphics.dispose();
        return image;
    }

    private BufferedImage createFallbackBackImage() {
        BufferedImage image = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        configureGraphics(graphics);

        graphics.setColor(new Color(32, 76, 132));
        graphics.fillRoundRect(0, 0, CARD_WIDTH - 1, CARD_HEIGHT - 1, 10, 10);
        graphics.setColor(Color.WHITE);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRoundRect(7, 7, CARD_WIDTH - 15, CARD_HEIGHT - 15, 8, 8);
        graphics.drawLine(18, 18, CARD_WIDTH - 18, CARD_HEIGHT - 18);
        graphics.drawLine(CARD_WIDTH - 18, 18, 18, CARD_HEIGHT - 18);

        graphics.dispose();
        return image;
    }

    private void configureGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private Color getCardColor(Card card) {
        if (card.getSuit() == Suit.HEARTS || card.getSuit() == Suit.DIAMONDS) {
            return new Color(175, 25, 25);
        }
        return new Color(20, 20, 20);
    }
}
