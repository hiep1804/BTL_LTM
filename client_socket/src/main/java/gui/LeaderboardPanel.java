package gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;
import model.Player;

public class LeaderboardPanel extends JPanel {
    private final DefaultTableModel tableModel;

    public LeaderboardPanel() {
        setLayout(new BorderLayout());
        String[] columns = {"Rank", "Name", "Score", "Wins", "Matches"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder("Leaderboard"));
    }

    public void updateData(List<Player> players) {
        tableModel.setRowCount(0);
        if (players == null) {
            return;
        }
        int rank = 1;
        for (Player player : players) {
            tableModel.addRow(new Object[]{
                rank++,
                player.getName(),
                player.getTotal_score(),
                player.getTotal_wins(),
                player.getMatches_played()
            });
        }
    }
}
