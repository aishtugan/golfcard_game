package org.example.golfsolitaire.ui;

import org.example.golfsolitaire.game.AutoPlayer;
import org.example.golfsolitaire.game.GameStatus;
import org.example.golfsolitaire.game.GolfSolitaireGame;
import org.example.golfsolitaire.model.Card;
import org.example.golfsolitaire.stats.Statistics;
import org.example.golfsolitaire.stats.StatisticsService;
import org.example.golfsolitaire.stats.StrategyStatistics;
import org.example.golfsolitaire.strategy.GameStrategy;
import org.example.golfsolitaire.strategy.StrategyRegistry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.List;

public class MainFrame extends JFrame {
    private static final int AUTO_PLAY_DELAY_MS = 300;
    private static final int HUMAN_RESULT_DELAY_MS = 250;
    private static final int MAX_BATCH_GAMES = 100_000;

    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel timeLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel humanStatisticsLabel = new JLabel("", SwingConstants.LEFT);
    private final JLabel bestStrategyLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton newHumanPlayButton = new JButton("New Human Play");
    private final JButton resetGameButton = new JButton("Reset Game");
    private final JButton computerPlayButton = new JButton("New Computer Play");
    private final JButton clearHumanStatisticsButton = new JButton("Clear Human Statistics");
    private final JButton resetStatisticsButton = new JButton("Reset All Statistics");
    private final JTextField batchGamesField = new JTextField("100", 8);
    private final JButton runBatchButton = new JButton("Run Selected Strategy");
    private final JButton runAllStrategiesButton = new JButton("Run All Strategies");
    private final JLabel batchProgressLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel strategyDescriptionLabel = new JLabel("", SwingConstants.CENTER);
    private final DefaultTableModel strategyTableModel = new DefaultTableModel(
            new Object[]{"Strategy", "Games", "Wins", "Losses", "Win rate"},
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable strategyStatisticsTable = new JTable(strategyTableModel);

    private final StatisticsService statisticsService = new StatisticsService();
    private final Statistics statistics = statisticsService.load();
    private final List<GameStrategy> availableStrategies = StrategyRegistry.getAllStrategies();
    private final JComboBox<GameStrategy> strategySelector = new JComboBox<>(availableStrategies.toArray(new GameStrategy[0]));
    private final CardImageService cardImageService = new CardImageService();
    private final GameBoardPanel boardPanel = new GameBoardPanel(cardImageService, this::removeTableCard, this::drawFromStock);

    private GolfSolitaireGame game = new GolfSolitaireGame();
    private AutoPlayer autoPlayer;
    private Timer autoPlayTimer;
    private Timer clockTimer;
    private GameMode mode = GameMode.PLAYER;
    private boolean resultRecorded;
    private long gameStartedAtMillis;
    private long finishedElapsedSeconds = -1;
    private String currentComputerStrategyId;

    public MainFrame() {
        super("Golf Solitaire");
        buildLayout();
        wireActions();
        startClock();
        startPlayerGame();
    }

    private void buildLayout() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 640));

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        topPanel.setBackground(new Color(74, 40, 18));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        configureTopLabel(scoreLabel);
        configureTopLabel(timeLabel);
        topPanel.add(scoreLabel);
        topPanel.add(timeLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(74, 40, 18));
        buttonPanel.add(newHumanPlayButton);
        buttonPanel.add(resetGameButton);
        buttonPanel.add(computerPlayButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(74, 40, 18));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        bottomPanel.add(messageLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(createStatisticsPanel(), BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createStatisticsPanel() {
        JPanel statisticsPanel = new JPanel(new BorderLayout(0, 12));
        statisticsPanel.setPreferredSize(new Dimension(520, 0));
        statisticsPanel.setBackground(new Color(246, 239, 228));
        statisticsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(160, 132, 98)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel titleLabel = new JLabel("Statistics", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 42, 20));

        humanStatisticsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        humanStatisticsLabel.setForeground(new Color(45, 32, 22));
        humanStatisticsLabel.setVerticalAlignment(SwingConstants.TOP);

        strategyStatisticsTable.setFillsViewportHeight(true);
        strategyStatisticsTable.setRowHeight(24);
        strategyStatisticsTable.getTableHeader().setReorderingAllowed(false);
        strategyStatisticsTable.getColumnModel().getColumn(0).setPreferredWidth(190);
        strategyStatisticsTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        strategyStatisticsTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        strategyStatisticsTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        strategyStatisticsTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        JPanel statsContentPanel = new JPanel(new BorderLayout(0, 10));
        statsContentPanel.setOpaque(false);
        JLabel humanTitleLabel = new JLabel("Human Player", SwingConstants.LEFT);
        humanTitleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        JLabel computerTitleLabel = new JLabel("Computer Strategies", SwingConstants.LEFT);
        computerTitleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        JPanel humanPanel = new JPanel(new BorderLayout(0, 4));
        humanPanel.setOpaque(false);
        humanPanel.add(humanTitleLabel, BorderLayout.NORTH);
        humanPanel.add(humanStatisticsLabel, BorderLayout.CENTER);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 4));
        tablePanel.setOpaque(false);
        tablePanel.add(computerTitleLabel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(strategyStatisticsTable), BorderLayout.CENTER);
        tablePanel.add(bestStrategyLabel, BorderLayout.SOUTH);

        statsContentPanel.add(humanPanel, BorderLayout.NORTH);
        statsContentPanel.add(tablePanel, BorderLayout.CENTER);

        statisticsPanel.add(titleLabel, BorderLayout.NORTH);
        statisticsPanel.add(statsContentPanel, BorderLayout.CENTER);
        statisticsPanel.add(createStatisticsControlsPanel(), BorderLayout.SOUTH);
        return statisticsPanel;
    }

    private JPanel createStatisticsControlsPanel() {
        JPanel controlsPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        controlsPanel.setOpaque(false);

        JLabel batchLabel = new JLabel("Computer games to run:", SwingConstants.CENTER);
        batchLabel.setForeground(new Color(45, 32, 22));

        batchProgressLabel.setForeground(new Color(70, 42, 20));
        batchProgressLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        strategyDescriptionLabel.setForeground(new Color(70, 42, 20));
        strategyDescriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        strategyDescriptionLabel.setVerticalAlignment(SwingConstants.TOP);

        controlsPanel.add(new JLabel("Computer strategy:", SwingConstants.CENTER));
        controlsPanel.add(strategySelector);
        controlsPanel.add(strategyDescriptionLabel);
        controlsPanel.add(batchLabel);
        controlsPanel.add(batchGamesField);
        controlsPanel.add(runBatchButton);
        controlsPanel.add(runAllStrategiesButton);
        controlsPanel.add(batchProgressLabel);
        controlsPanel.add(clearHumanStatisticsButton);
        controlsPanel.add(resetStatisticsButton);
        return controlsPanel;
    }

    private void configureTopLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    }

    private void wireActions() {
        newHumanPlayButton.addActionListener(event -> startPlayerGame());
        resetGameButton.addActionListener(event -> startPlayerGame());
        computerPlayButton.addActionListener(event -> startComputerGame());
        clearHumanStatisticsButton.addActionListener(event -> clearHumanStatistics());
        resetStatisticsButton.addActionListener(event -> resetStatistics());
        runBatchButton.addActionListener(event -> startComputerGamesBatch(List.of(getSelectedStrategy())));
        runAllStrategiesButton.addActionListener(event -> startComputerGamesBatch(availableStrategies));
        strategySelector.addActionListener(event -> updateStrategyDescription());
        updateStrategyDescription();
    }

    private void startPlayerGame() {
        stopAutoPlay();
        mode = GameMode.PLAYER;
        game = new GolfSolitaireGame();
        resultRecorded = false;
        gameStartedAtMillis = System.currentTimeMillis();
        finishedElapsedSeconds = -1;
        messageLabel.setText("Player game reset.");
        refresh();
    }

    private void startComputerGame() {
        stopAutoPlay();
        mode = GameMode.COMPUTER;
        game = new GolfSolitaireGame();
        GameStrategy selectedStrategy = getSelectedStrategy();
        currentComputerStrategyId = selectedStrategy.getId();
        autoPlayer = new AutoPlayer(game, selectedStrategy);
        resultRecorded = false;
        gameStartedAtMillis = System.currentTimeMillis();
        finishedElapsedSeconds = -1;
        messageLabel.setText("Computer game started with " + selectedStrategy.getName() + ".");
        refresh();

        autoPlayTimer = new Timer(AUTO_PLAY_DELAY_MS, event -> runComputerTurn());
        autoPlayTimer.start();
    }

    private void runComputerTurn() {
        if (game.getState().getStatus() != GameStatus.IN_PROGRESS) {
            stopAutoPlay();
            recordFinishedGameIfNeeded();
            refresh();
            return;
        }

        List<Card> movesBeforeTurn = game.getAvailableMoves();
        autoPlayer.playOneTurn();
        if (movesBeforeTurn.isEmpty()) {
            messageLabel.setText("Computer drew from stock.");
        } else {
            messageLabel.setText("Computer removed " + game.getState().getActiveCard() + ".");
        }

        recordFinishedGameIfNeeded();
        refresh();
    }

    private void drawFromStock() {
        if (mode == GameMode.COMPUTER) {
            messageLabel.setText("Computer mode is running.");
            return;
        }

        if (game.drawFromStock() != null) {
            messageLabel.setText("Drew a new active card.");
        } else {
            messageLabel.setText("The stock pile is empty.");
        }

        refresh();
        scheduleHumanResultCheck();
    }

    private void removeTableCard(Card card) {
        if (mode == GameMode.COMPUTER) {
            messageLabel.setText("Computer mode is running.");
            return;
        }

        if (game.removeCard(card)) {
            messageLabel.setText("Removed " + card + ".");
        } else {
            messageLabel.setText(card + " cannot be removed now.");
        }

        refresh();
        scheduleHumanResultCheck();
    }

    private void scheduleHumanResultCheck() {
        Timer resultTimer = new Timer(HUMAN_RESULT_DELAY_MS, event -> recordFinishedGameIfNeeded());
        resultTimer.setRepeats(false);
        resultTimer.start();
    }

    private void resetStatistics() {
        statisticsService.reset(statistics);
        messageLabel.setText("All statistics reset.");
        batchProgressLabel.setText(" ");
        refresh();
    }

    private void clearHumanStatistics() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear human player statistics?",
                "Clear Human Statistics",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        statistics.resetPlayerStatistics();
        statisticsService.save(statistics);
        messageLabel.setText("Human player statistics cleared.");
        refresh();
    }

    private void startComputerGamesBatch(List<GameStrategy> strategiesToRun) {
        Integer gamesToRun = readBatchGamesCount();
        if (gamesToRun == null) {
            return;
        }

        stopAutoPlay();
        List<GameStrategy> batchStrategies = new ArrayList<>(strategiesToRun);
        setBatchControlsEnabled(false);
        batchProgressLabel.setText("Running: 0 / " + gamesToRun);
        if (batchStrategies.size() == 1) {
            messageLabel.setText("Running " + gamesToRun + " computer games with " + batchStrategies.get(0).getName() + ".");
        } else {
            messageLabel.setText("Running " + gamesToRun + " games for each strategy.");
        }

        SwingWorker<BatchComputerResult, String> worker = new SwingWorker<>() {
            @Override
            protected BatchComputerResult doInBackground() {
                BatchComputerResult batchResult = new BatchComputerResult();

                for (GameStrategy strategy : batchStrategies) {
                    int wins = 0;
                    int losses = 0;

                    for (int i = 1; i <= gamesToRun; i++) {
                        GolfSolitaireGame batchGame = new GolfSolitaireGame();
                        AutoPlayer batchAutoPlayer = new AutoPlayer(batchGame, strategy);
                        batchAutoPlayer.playUntilFinished();

                        if (batchGame.getState().getStatus() == GameStatus.WON) {
                            wins++;
                        } else if (batchGame.getState().getStatus() == GameStatus.LOST) {
                            losses++;
                        }

                        if (shouldPublishProgress(i, gamesToRun)) {
                            publish("Running " + strategy.getName() + ": " + i + " / " + gamesToRun);
                        }
                    }

                    batchResult.addStrategyResult(new StrategyBatchResult(strategy.getId(), strategy.getName(), gamesToRun, wins, losses));
                }

                return batchResult;
            }

            @Override
            protected void process(List<String> chunks) {
                batchProgressLabel.setText(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    BatchComputerResult result = get();
                    for (StrategyBatchResult strategyResult : result.strategyResults) {
                        statistics.recordComputerGames(strategyResult.strategyId, strategyResult.wins, strategyResult.losses);
                    }
                    statisticsService.save(statistics);
                    batchProgressLabel.setText("Finished");
                    messageLabel.setText(result.getSummaryText());
                    refresh();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    messageLabel.setText("Computer batch was interrupted.");
                } catch (ExecutionException e) {
                    messageLabel.setText("Computer batch failed: " + e.getCause().getMessage());
                } finally {
                    setBatchControlsEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private Integer readBatchGamesCount() {
        String input = batchGamesField.getText().trim();
        if (input.isEmpty()) {
            showBatchInputError("Enter the number of computer games to run.");
            return null;
        }

        int gamesToRun;
        try {
            gamesToRun = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            showBatchInputError("Enter a whole number, for example 100 or 1000.");
            return null;
        }

        if (gamesToRun <= 0) {
            showBatchInputError("The number of games must be greater than zero.");
            return null;
        }

        if (gamesToRun > MAX_BATCH_GAMES) {
            showBatchInputError("Please enter " + MAX_BATCH_GAMES + " games or fewer.");
            return null;
        }

        return gamesToRun;
    }

    private void showBatchInputError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Computer Games Count", JOptionPane.WARNING_MESSAGE);
    }

    private void setBatchControlsEnabled(boolean enabled) {
        batchGamesField.setEnabled(enabled);
        runBatchButton.setEnabled(enabled);
        runAllStrategiesButton.setEnabled(enabled);
        strategySelector.setEnabled(enabled);
        newHumanPlayButton.setEnabled(enabled);
        resetGameButton.setEnabled(enabled);
        computerPlayButton.setEnabled(enabled);
        clearHumanStatisticsButton.setEnabled(enabled);
        resetStatisticsButton.setEnabled(enabled);
    }

    private boolean shouldPublishProgress(int completedGames, int totalGames) {
        int progressStep = Math.max(1, totalGames / 100);
        return completedGames == totalGames || completedGames % progressStep == 0;
    }

    private void recordFinishedGameIfNeeded() {
        GameStatus status = game.getState().getStatus();
        if (resultRecorded || status == GameStatus.IN_PROGRESS) {
            return;
        }

        if (mode == GameMode.PLAYER) {
            statistics.recordPlayerGame(status);
        } else {
            statistics.recordComputerGame(currentComputerStrategyId, status);
        }
        statisticsService.save(statistics);
        finishedElapsedSeconds = (System.currentTimeMillis() - gameStartedAtMillis) / 1000;
        resultRecorded = true;

        if (mode == GameMode.PLAYER) {
            showHumanResultDialog(status);
        }
    }

    private void showHumanResultDialog(GameStatus status) {
        String message = status == GameStatus.WON
                ? "Congratulations! You won the game."
                : "No more moves. You lost the game.";
        String[] options = {"Start New Game", "Reset Current Game", "Exit"};

        int choice = JOptionPane.showOptionDialog(
                this,
                message + "\n\nWhat would you like to do next?",
                "Game Finished",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            startPlayerGame();
        } else if (choice == 1) {
            resetCurrentGame();
        } else if (choice == 2) {
            System.exit(0);
        }
    }

    private void resetCurrentGame() {
        // TODO: store the initial deal order so this can restart the exact same game.
        startPlayerGame();
        messageLabel.setText("Reset Current Game starts a new shuffled game for now.");
    }

    private void refresh() {
        boardPanel.setGame(game);
        boardPanel.setManualMode(mode == GameMode.PLAYER);
        scoreLabel.setText("Score " + getCurrentScore());
        updateTimeLabel();
        renderStatistics();
    }

    private int getCurrentScore() {
        return GolfSolitaireGame.TABLE_CARD_COUNT - game.getState().getTableCards().size();
    }

    private void startClock() {
        clockTimer = new Timer(1000, event -> updateTimeLabel());
        clockTimer.start();
    }

    private void updateTimeLabel() {
        if (gameStartedAtMillis == 0) {
            timeLabel.setText("Time 00:00");
            return;
        }

        long elapsedSeconds = finishedElapsedSeconds >= 0
                ? finishedElapsedSeconds
                : (System.currentTimeMillis() - gameStartedAtMillis) / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        timeLabel.setText(String.format("Time %02d:%02d", minutes, seconds));
    }

    private void renderStatistics() {
        humanStatisticsLabel.setText(
                "<html>"
                        + "Games: " + statistics.getTotalPlayerGames() + "<br>"
                        + "Wins: " + statistics.getPlayerWins() + "<br>"
                        + "Losses: " + statistics.getPlayerLosses() + "<br>"
                        + "Win rate: " + formatRate(statistics.getPlayerWinRate())
                        + "</html>"
        );
        renderStrategyStatisticsTable();
    }

    private void renderStrategyStatisticsTable() {
        strategyTableModel.setRowCount(0);
        GameStrategy bestStrategy = findBestStrategy();

        if (bestStrategy != null) {
            StrategyStatistics bestStats = statistics.getComputerStrategyStatistics(bestStrategy.getId());
            bestStrategyLabel.setText("Best strategy: " + bestStrategy.getName() + " - " + formatRate(bestStats.getWinRate()));
        } else {
            bestStrategyLabel.setText("Best strategy: N/A");
        }

        for (GameStrategy strategy : availableStrategies) {
            StrategyStatistics strategyStatistics = statistics.getComputerStrategyStatistics(strategy.getId());
            strategyTableModel.addRow(new Object[]{
                    strategy.getName(),
                    strategyStatistics.getGames(),
                    strategyStatistics.getWins(),
                    strategyStatistics.getLosses(),
                    formatRate(strategyStatistics.getWinRate())
            });
        }
    }

    private GameStrategy findBestStrategy() {
        GameStrategy bestStrategy = null;
        double bestWinRate = -1.0;

        for (GameStrategy strategy : availableStrategies) {
            StrategyStatistics strategyStatistics = statistics.getComputerStrategyStatistics(strategy.getId());
            if (strategyStatistics.getGames() == 0) {
                continue;
            }

            if (strategyStatistics.getWinRate() > bestWinRate) {
                bestWinRate = strategyStatistics.getWinRate();
                bestStrategy = strategy;
            }
        }

        return bestStrategy;
    }

    private String formatRate(double rate) {
        return String.format("%.1f%%", rate * 100);
    }

    private GameStrategy getSelectedStrategy() {
        return (GameStrategy) strategySelector.getSelectedItem();
    }

    private void updateStrategyDescription() {
        GameStrategy selectedStrategy = getSelectedStrategy();
        if (selectedStrategy == null) {
            strategyDescriptionLabel.setText(" ");
            return;
        }

        String description = selectedStrategy.getDescription();
        strategyDescriptionLabel.setText("<html><div style='text-align:center;'>" + description + "</div></html>");
        strategySelector.setToolTipText(description);
    }

    private void stopAutoPlay() {
        if (autoPlayTimer != null) {
            autoPlayTimer.stop();
            autoPlayTimer = null;
        }
    }

    public static void showWindow() {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    private static class BatchComputerResult {
        private final List<StrategyBatchResult> strategyResults = new ArrayList<>();

        private void addStrategyResult(StrategyBatchResult strategyResult) {
            strategyResults.add(strategyResult);
        }

        private String getSummaryText() {
            if (strategyResults.size() == 1) {
                StrategyBatchResult result = strategyResults.get(0);
                return result.strategyName + " batch finished. Wins: " + result.wins + ", losses: " + result.losses + ".";
            }

            int totalGames = 0;
            for (StrategyBatchResult result : strategyResults) {
                totalGames += result.totalGames;
            }
            return "All strategies batch finished. Total computer games: " + totalGames + ".";
        }
    }

    private static class StrategyBatchResult {
        private final String strategyId;
        private final String strategyName;
        private final int totalGames;
        private final int wins;
        private final int losses;

        private StrategyBatchResult(String strategyId, String strategyName, int totalGames, int wins, int losses) {
            this.strategyId = strategyId;
            this.strategyName = strategyName;
            this.totalGames = totalGames;
            this.wins = wins;
            this.losses = losses;
        }
    }
}
