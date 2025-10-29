package shared;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class LeaderboardPanel extends JPanel {
    private JTable leaderboardTable;
    private final DefaultTableModel tableModel;

    public LeaderboardPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Bảng xếp hạng", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        String[] columnNames = {"Hạng", "Tên", "Điểm", "Thắng"};
        tableModel = new DefaultTableModel(columnNames, 0);
        leaderboardTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateLeaderboard(ArrayList<Player> leaderboard) {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        // Thêm dữ liệu mới
        if (leaderboard != null) {
            for (int i = 0; i < leaderboard.size(); i++) {
                Player player = leaderboard.get(i);
                Object[] row = {
                    i + 1,
                    player.getName(),
                    player.getTotalScore(),
                    player.getTotalWins()
                };
                tableModel.addRow(row);
            }
        }
    }
}
