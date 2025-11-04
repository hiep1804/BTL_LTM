package shared;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    private JTable leaderboardTable;
    private final DefaultTableModel tableModel;
    private List<Player> leaderboardData = new ArrayList<>();

    public LeaderboardPanel() {
        setLayout(null);
        setOpaque(false);

        // Panel container với gradient background
        JPanel container = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 15, 15);
                
                // Vẽ background trắng
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
                
                // Vẽ viền
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
            }
        };
        container.setLayout(null);
        container.setBounds(0, 0, 380, 425);
        container.setOpaque(false);

        // Header với gradient
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 193, 7), 
                                                      0, getHeight(), new Color(255, 152, 0));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        header.setLayout(null);
        header.setBounds(10, 10, 360, 45);
        header.setOpaque(false);

        JLabel title = new JLabel("BẢNG XẾP HẠNG");
        title.setBounds(0, 0, 360, 45);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title);
        container.add(header);

        String[] columnNames = {"#", "Tên", "Điểm", "Thắng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFont(new Font("Arial", Font.PLAIN, 13));
        leaderboardTable.setRowHeight(32);
        leaderboardTable.setSelectionBackground(new Color(255, 245, 220));
        leaderboardTable.setSelectionForeground(Color.BLACK);
        leaderboardTable.setShowGrid(true);
        leaderboardTable.setGridColor(new Color(240, 240, 240));
    leaderboardTable.setToolTipText("Nhấn vào một người chơi để xem chi tiết");
        leaderboardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leaderboardTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    int row = leaderboardTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < leaderboardData.size()) {
                        leaderboardTable.setRowSelectionInterval(row, row);
                        showPlayerInfo(leaderboardData.get(row));
                    }
                }
            }
        });
        
        // Custom header
        JTableHeader tableHeader = leaderboardTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));
        tableHeader.setBackground(new Color(250, 250, 250));
        tableHeader.setForeground(new Color(60, 60, 60));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));
        
        // Custom cell renderer cho ranking
        DefaultTableCellRenderer rankRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 0) { // Ranking column
                    setFont(new Font("Arial", Font.BOLD, 14));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    
                    if (row == 0) {
                        c.setForeground(new Color(255, 193, 7)); // Gold
                        setText(value.toString());
                    } else if (row == 1) {
                        c.setForeground(new Color(158, 158, 158)); // Silver
                        setText(value.toString());
                    } else if (row == 2) {
                        c.setForeground(new Color(205, 127, 50)); // Bronze
                        setText(value.toString());
                    } else {
                        c.setForeground(new Color(100, 100, 100));
                        setText(value.toString());
                    }
                } else {
                    setFont(new Font("Arial", Font.PLAIN, 13));
                    if (column == 1) {
                        setHorizontalAlignment(SwingConstants.LEFT);
                    } else {
                        setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    c.setForeground(new Color(50, 50, 50));
                }
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                
                return c;
            }
        };
        
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(rankRenderer);
        }
        
        // Set column widths
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        leaderboardTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBounds(10, 65, 360, 350);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane);
        
        add(container);
    }

    private void showPlayerInfo(Player player) {
        if (player == null) {
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Thông tin người chơi", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        PlayerInfoPanel infoPanel = new PlayerInfoPanel(player);

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JButton closeButton = new JButton("Đóng") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(142, 45, 226),
                                                      0, getHeight(), new Color(74, 0, 224));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        closeButton.setPreferredSize(new Dimension(120, 38));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dialog.dispose());

        footer.add(closeButton);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(infoPanel, BorderLayout.CENTER);
        dialog.getContentPane().add(footer, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setVisible(true);
    }

    public void updateLeaderboard(ArrayList<Player> leaderboard) {
        tableModel.setRowCount(0);

        if (leaderboard != null) {
            leaderboardData = new ArrayList<>(leaderboard);
        } else {
            leaderboardData = new ArrayList<>();
        }

        for (int i = 0; i < leaderboardData.size(); i++) {
            Player player = leaderboardData.get(i);
            String displayName = (player.getName() != null && !player.getName().isBlank())
                    ? player.getName()
                    : player.getUsername();
            Object[] row = {
                i + 1,
                displayName,
                player.getTotalScore(),
                player.getTotalWins()
            };
            tableModel.addRow(row);
        }
    }
}
