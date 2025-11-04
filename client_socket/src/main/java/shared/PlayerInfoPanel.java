package shared;

import javax.swing.*;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {
    private final Player player;

    public PlayerInfoPanel(Player player) {
        this.player = player;
        setPreferredSize(new Dimension(380, 420));
        setLayout(null);
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        String displayName = (player.getName() != null && !player.getName().isBlank())
                ? player.getName()
                : "(Chưa cập nhật)";
        String username = player.getUsername() != null ? player.getUsername() : "-";
        int totalScore = player.getTotalScore();
        int totalWins = player.getTotalWins();
        int matches = player.getMatchesPlayed();
        double winRateValue = matches > 0 ? (totalWins * 100.0) / matches : 0.0;
        String winRate = String.format("%.1f%%", winRateValue);
        String status = player.isBusy() ? "Đang bận" : "Sẵn sàng";

        JLabel titleLabel = new JLabel("Thông tin người chơi", SwingConstants.CENTER);
        titleLabel.setBounds(20, 20, 340, 28);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(74, 0, 224));
        add(titleLabel);

        JLabel nameLabel = new JLabel(displayName, SwingConstants.CENTER);
        nameLabel.setBounds(20, 58, 340, 26);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(new Color(60, 60, 60));
        add(nameLabel);

        JLabel usernameBadge = new JLabel("@" + username, SwingConstants.CENTER);
        usernameBadge.setBounds(120, 90, 140, 26);
        usernameBadge.setFont(new Font("Arial", Font.PLAIN, 13));
        usernameBadge.setForeground(new Color(142, 45, 226));
        usernameBadge.setOpaque(true);
        usernameBadge.setBackground(new Color(243, 233, 255));
        usernameBadge.setBorder(BorderFactory.createLineBorder(new Color(142, 45, 226, 120), 1, true));
        add(usernameBadge);

        int y = 140;
        addInfoRow("Tổng điểm", String.valueOf(totalScore), y, true);
        y += 55;
        addInfoRow("Số trận thắng", String.valueOf(totalWins), y, true);
        y += 55;
        addInfoRow("Tổng số trận", String.valueOf(matches), y, true);
        y += 55;
        addInfoRow("Tỷ lệ thắng", winRate, y, true);
        y += 55;
        addInfoRow("Trạng thái", status, y, false);
    }

    private void addInfoRow(String title, String value, int y, boolean withSeparator) {
        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(120, 120, 120));
        titleLabel.setBounds(30, y, 300, 14);
        add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(new Color(50, 50, 50));
        valueLabel.setBounds(30, y + 16, 300, 22);
        add(valueLabel);

        if (withSeparator) {
            JSeparator separator = new JSeparator();
            separator.setBounds(30, y + 45, 300, 1);
            separator.setForeground(new Color(230, 230, 230));
            add(separator);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(255, 255, 255),
                0, getHeight(), new Color(235, 222, 255)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        g2d.setColor(new Color(142, 45, 226, 60));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 18, 18);

        g2d.dispose();
    }
}
