import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Swing GUI front-end for the Number Guessing Game.
 * Run with: java -cp out NumberGuessingGameGUI
 */
public class NumberGuessingGameGUI extends JFrame {

    private static final Color BG = new Color(24, 26, 38);
    private static final Color PANEL = new Color(34, 37, 54);
    private static final Color ACCENT = new Color(99, 202, 183);
    private static final Color TEXT = new Color(235, 238, 250);
    private static final Color MUTED = new Color(160, 165, 190);
    private static final Color GOOD = new Color(110, 231, 140);
    private static final Color BAD = new Color(255, 120, 120);
    private static final Color WARN = new Color(255, 205, 110);

    private final GameEngine engine = new GameEngine();

    private final JComboBox<GameEngine.Difficulty> difficultyBox =
            new JComboBox<>(GameEngine.Difficulty.values());
    private final FlatButton newRoundButton = new FlatButton("New Round", ACCENT, new Color(20, 40, 36));
    private final JLabel roundLabel = new JLabel("Press New Round to play");
    private final JLabel rangeLabel = new JLabel(" ");
    private final JLabel hintLabel = new JLabel("?", SwingConstants.CENTER);
    private final JTextField guessField = new JTextField(10);
    private final FlatButton guessButton = new FlatButton("Guess", new Color(90, 140, 250), Color.WHITE);
    private final JLabel attemptsLabel = new JLabel("Attempts: 0/7");
    private final JProgressBar attemptsBar = new JProgressBar();
    private final JLabel feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JTextArea summaryArea = new JTextArea(6, 40);
    private final JLabel bestLabel = new JLabel();

    public NumberGuessingGameGUI() {
        super("Number Guessing Game");
        difficultyBox.setSelectedItem(GameEngine.Difficulty.MEDIUM);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(buildContent());
        pack();
        setMinimumSize(new Dimension(560, 620));
        setLocationRelativeTo(null);

        newRoundButton.addActionListener(e -> startNewRound());
        guessButton.addActionListener(e -> submitGuess());
        guessField.addActionListener(e -> submitGuess());
        difficultyBox.addActionListener(e -> {
            if (!engine.isRoundActive()) {
                startNewRound();
            }
        });

        setRoundControlsEnabled(false);
        refreshBestScores();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(wrapCentered(buildGamePanel(), new Dimension(520, 430)), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        return root;
    }

    /** Centers a panel and caps its size so the layout stays sane when maximized. */
    private JPanel wrapCentered(JPanel inner, Dimension size) {
        inner.setPreferredSize(size);
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);
        wrapper.add(inner);
        return wrapper;
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(BG);

        JLabel title = new JLabel("NUMBER GUESSING GAME");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(ACCENT);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(BG);
        difficultyBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        controls.add(difficultyBox);
        controls.add(newRoundButton);

        panel.add(title, BorderLayout.WEST);
        panel.add(controls, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildGamePanel() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 64, 90), 1, true),
                new EmptyBorder(18, 24, 18, 24)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 0, 4, 0);

        roundLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        roundLabel.setForeground(TEXT);
        rangeLabel.setForeground(MUTED);
        rangeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        hintLabel.setFont(new Font("SansSerif", Font.BOLD, 96));
        hintLabel.setForeground(ACCENT);
        hintLabel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        inputRow.setBackground(PANEL);
        guessField.setFont(new Font("SansSerif", Font.BOLD, 20));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setBackground(new Color(48, 52, 74));
        guessField.setForeground(TEXT);
        guessField.setCaretColor(TEXT);
        guessField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 84, 115), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        guessField.setColumns(7);
        guessButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        inputRow.add(guessField);
        inputRow.add(guessButton);

        attemptsLabel.setForeground(TEXT);
        attemptsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        attemptsBar.setMinimum(0);
        attemptsBar.setForeground(ACCENT);
        attemptsBar.setBackground(new Color(48, 52, 74));
        attemptsBar.setStringPainted(false);
        attemptsBar.setPreferredSize(new Dimension(0, 10));

        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        feedbackLabel.setForeground(MUTED);

        gbc.gridy = 0; card.add(roundLabel, gbc);
        gbc.gridy = 1; card.add(rangeLabel, gbc);
        gbc.gridy = 2; card.add(hintLabel, gbc);
        gbc.gridy = 3; card.add(inputRow, gbc);
        gbc.gridy = 4; card.add(attemptsLabel, gbc);
        gbc.gridy = 5; card.add(attemptsBar, gbc);
        gbc.gridy = 6; card.add(feedbackLabel, gbc);
        return card;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(BG);

        JLabel summaryTitle = new JLabel("SESSION SUMMARY");
        summaryTitle.setForeground(MUTED);
        summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 11));

        summaryArea.setEditable(false);
        summaryArea.setBackground(PANEL);
        summaryArea.setForeground(TEXT);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        summaryArea.setText("No rounds played yet.");
        JScrollPane scroll = new JScrollPane(summaryArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 64, 90), 1, true));

        bestLabel.setForeground(ACCENT);
        bestLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panel.add(summaryTitle, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bestLabel, BorderLayout.SOUTH);
        return panel;
    }

    /** Button that paints itself so the custom colors hold on every look and feel. */
    private static class FlatButton extends JButton {
        private final Color base;

        FlatButton(String text, Color bg, Color fg) {
            super(text);
            this.base = bg;
            setForeground(fg);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(8, 16, 8, 16));
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = base;
            if (!isEnabled()) {
                bg = new Color(base.getRed(), base.getGreen(), base.getBlue(), 150);
            } else if (getModel().isRollover()) {
                bg = base.brighter();
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(isEnabled() ? getForeground() : new Color(150, 154, 185));
            FontMetrics fm = g2.getFontMetrics(getFont());
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.setFont(getFont());
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    private void startNewRound() {
        GameEngine.Difficulty d = (GameEngine.Difficulty) difficultyBox.getSelectedItem();
        engine.startRound(d);

        roundLabel.setText(String.format("Round %d  -  %s", engine.getRoundNumber(), d.label));
        rangeLabel.setText(String.format("I'm thinking of a number between %d and %d", d.min, d.max));
        hintLabel.setText("?");
        hintLabel.setForeground(ACCENT);
        feedbackLabel.setText(" ");
        feedbackLabel.setForeground(MUTED);
        guessField.setText("");
        attemptsBar.setMaximum(d.maxAttempts);
        attemptsBar.setValue(0);
        updateAttemptsLabel();

        setRoundControlsEnabled(true);
        guessField.requestFocusInWindow();
    }

    private void submitGuess() {
        if (!engine.isRoundActive()) {
            return;
        }
        String text = guessField.getText().trim();
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            showFeedback("Enter a whole number!", WARN);
            return;
        }
        if (value < engine.getMin() || value > engine.getMax()) {
            showFeedback("Out of range (" + engine.getMin() + "-" + engine.getMax() + ")!", WARN);
            return;
        }

        GameEngine.Result result = engine.guess(value);
        attemptsBar.setValue(engine.getAttemptsUsed());
        updateAttemptsLabel();
        guessField.setText("");

        switch (result) {
            case TOO_LOW -> showFeedback("Too Low!", WARN);
            case TOO_HIGH -> showFeedback("Too High!", WARN);
            case CORRECT -> {
                hintLabel.setText(String.valueOf(engine.getSecret()));
                hintLabel.setForeground(GOOD);
                String msg = "Correct! Guessed in " + engine.getAttemptsUsed()
                        + (engine.getAttemptsUsed() == 1 ? " attempt" : " attempts");
                if (engine.wasLastWinARecord(engine.getDifficulty(), engine.getAttemptsUsed())) {
                    msg += " - NEW BEST!";
                }
                showFeedback(msg, GOOD);
                endRound();
            }
        }

        if (!engine.isRoundActive() && result != GameEngine.Result.CORRECT) {
            hintLabel.setText(String.valueOf(engine.getSecret()));
            hintLabel.setForeground(BAD);
            showFeedback("You Lost! The number was " + engine.getSecret(), BAD);
            endRound();
        }
    }

    private void endRound() {
        setRoundControlsEnabled(false);
        refreshSummary();
        refreshBestScores();
    }

    private void setRoundControlsEnabled(boolean enabled) {
        guessField.setEnabled(enabled);
        guessButton.setEnabled(enabled);
    }

    private void updateAttemptsLabel() {
        attemptsLabel.setText(String.format("Attempts: %d/%d  (%d remaining)",
                engine.getAttemptsUsed(), engine.getMaxAttempts(), engine.getAttemptsRemaining()));
    }

    private void showFeedback(String message, Color color) {
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(color);
    }

    private void refreshSummary() {
        List<GameEngine.RoundSummary> history = engine.getHistory();
        StringBuilder sb = new StringBuilder();
        for (GameEngine.RoundSummary s : history) {
            sb.append(s).append(System.lineSeparator());
        }
        summaryArea.setText(sb.toString().trim());
        summaryArea.setCaretPosition(summaryArea.getDocument().getLength());
    }

    private void refreshBestScores() {
        StringBuilder sb = new StringBuilder("Best scores:  ");
        for (GameEngine.Difficulty d : GameEngine.Difficulty.values()) {
            Integer best = engine.getBestScore(d);
            sb.append(d.label)
              .append(" = ")
              .append(best == null ? "---" : best + " attempts")
              .append("    ");
        }
        bestLabel.setText(sb.toString());
    }

    public static void main(String[] args) {
        // Metal fully honors custom component colors (Nimbus/system L&Fs tint or ignore them).
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ignored) {
            // Default look and feel is fine.
        }
        SwingUtilities.invokeLater(() -> new NumberGuessingGameGUI().setVisible(true));
    }
}
