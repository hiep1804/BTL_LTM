package shared;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class MatchResultPanel extends JPanel {
    private final JButton rematchButton;
    private final JButton backToHomeButton;

    public MatchResultPanel(Player winner, Player loser, int winnerScore, int loserScore) {
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(245, 247, 250));

        JPanel card = buildResultCard(winner, loser, winnerScore, loserScore);
        card.setBounds(150, 80, 500, 420);
        add(card);

        rematchButton = createPrimaryButton("Rematch");
        rematchButton.setBounds(220, 520, 150, 45);
        add(rematchButton);

        backToHomeButton = createSecondaryButton("Quay về trang chủ");
        backToHomeButton.setBounds(420, 520, 180, 45);
        add(backToHomeButton);
    }

    private JPanel buildResultCard(Player winner, Player loser, int winnerScore, int loserScore) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, new Color(142, 45, 226), 0, getHeight(), new Color(74, 0, 224));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 21, 21);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("KẾT QUẢ TRẬN ĐẤU", SwingConstants.CENTER);
        titleLabel.setBounds(30, 30, 440, 40);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        JPanel winnerPanel = createPlayerResultPanel("WIN", winner, winnerScore, true);
        winnerPanel.setBounds(50, 110, 190, 250);
        panel.add(winnerPanel);

        JPanel loserPanel = createPlayerResultPanel("LOSE", loser, loserScore, false);
        loserPanel.setBounds(260, 110, 190, 250);
        panel.add(loserPanel);

        return panel;
    }

    private JPanel createPlayerResultPanel(String label, Player player, int score, boolean isWinner) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color start = isWinner ? new Color(76, 175, 80) : new Color(244, 67, 54);
                Color end = isWinner ? new Color(56, 142, 60) : new Color(211, 47, 47);
                GradientPaint gradient = new GradientPaint(0, 0, start, 0, getHeight(), end);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);

        JLabel resultLabel = new JLabel(label, SwingConstants.CENTER);
        resultLabel.setBounds(20, 15, 150, 30);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultLabel.setForeground(Color.WHITE);
        panel.add(resultLabel);

        String displayName = player != null && player.getName() != null && !player.getName().isBlank()
                ? player.getName()
                : (player != null ? player.getUsername() : "N/A");

        JLabel nameLabel = new JLabel(displayName, SwingConstants.CENTER);
        nameLabel.setBounds(15, 70, 160, 24);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel);

        JLabel scoreTitle = new JLabel("Điểm số", SwingConstants.CENTER);
        scoreTitle.setBounds(15, 120, 160, 18);
        scoreTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        scoreTitle.setForeground(new Color(240, 240, 240));
        panel.add(scoreTitle);

        JLabel scoreValue = new JLabel(String.valueOf(score), SwingConstants.CENTER);
        scoreValue.setBounds(15, 150, 160, 36);
        scoreValue.setFont(new Font("Arial", Font.BOLD, 28));
        scoreValue.setForeground(Color.WHITE);
        panel.add(scoreValue);

        return panel;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(66, 133, 244), 0, getHeight(), new Color(52, 168, 83));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(255, 255, 255, 230));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                g2d.setColor(new Color(142, 45, 226));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);

                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(new Color(142, 45, 226));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void setRematchAction(ActionListener listener) {
        for (ActionListener l : rematchButton.getActionListeners()) {
            rematchButton.removeActionListener(l);
        }
        rematchButton.addActionListener(listener);
    }

    public void setBackToHomeAction(ActionListener listener) {
        for (ActionListener l : backToHomeButton.getActionListeners()) {
            backToHomeButton.removeActionListener(l);
        }
        backToHomeButton.addActionListener(listener);
    }

    public JButton getRematchButton() {
        return rematchButton;
    }

    public JButton getBackToHomeButton() {
        return backToHomeButton;
    }
    
}
