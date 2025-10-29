package shared;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMainPanel extends JPanel {

    private JPanel listPanel;
    private Player p;
    private HashMap<String, Player> players;
    private JScrollPane scrollPane;
    private ClientMainFrm clientMainFrm;
    private NetworkManager networkManager;
    private LeaderboardPanel leaderboardPanel;

    public ClientMainPanel(Player p, ClientMainFrm clientMainFrm, NetworkManager networkManager) {
        this.clientMainFrm = clientMainFrm;
        this.networkManager = networkManager;
        this.p = p;
        setLayout(null);

        JLabel title = new JLabel("Client Main Frame");
        title.setBounds(300, 30, 200, 30);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        add(title);

        players = new HashMap<>();
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBounds(450, 150, 300, 300);
        add(scrollPane);

        leaderboardPanel = new LeaderboardPanel();
        leaderboardPanel.setBounds(50, 150, 350, 300);
        add(leaderboardPanel);

        startListening();
    }

    private void startListening() {
        new Thread(() -> {
            try {
                networkManager.send(new ObjectSentReceived("getLeaderboard", null));

                while (true) {
                    ObjectSentReceived received = networkManager.receive();
                    if (received == null) break;

                    String type = received.getType();
                    System.out.println("Received: " + type);

                    SwingUtilities.invokeLater(() -> {
                        switch (type) {
                            case "addPlayerOnline" -> {
                                Player player = (Player) received.getObj();
                                players.put(player.getUsername(), player);
                                refreshList();
                            }
                            case "loadPlayerOnline" -> {
                                ConcurrentHashMap<String, Player> players1 = (ConcurrentHashMap<String, Player>) received.getObj();
                                players = new HashMap<>(players1);
                                refreshList();
                            }
                            case "getLeaderboard" -> {
                                ArrayList<Player> leaderboard = (ArrayList<Player>) received.getObj();
                                leaderboardPanel.updateLeaderboard(leaderboard);
                            }
                            case "want to challenge" -> {
                                Player challenger = (Player) received.getObj();
                                int choice = JOptionPane.showConfirmDialog(
                                        this,
                                        "Người chơi " + challenger.getUsername() + " muốn thách đấu với bạn. Bạn có đồng ý không?",
                                        "Thách đấu",
                                        JOptionPane.YES_NO_OPTION
                                );
                                try {
                                    if (choice == JOptionPane.YES_OPTION) {
                                        networkManager.send(new ObjectSentReceived("accept", challenger.getUsername()));
                                    } else {
                                        networkManager.send(new ObjectSentReceived("reject", challenger.getUsername()));
                                    }
                                } catch (Exception ex) {
                                    Logger.getLogger(ClientMainPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            case "accept challenge" -> {
                                try {
                                    clientMainFrm.setStartGameRoom((Player) received.getObj());
                                    clientMainFrm.showStartGameRoom();
                                } catch (Exception ex) {
                                    Logger.getLogger(ClientMainPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void refreshList() {
        listPanel.removeAll();

        for (Player player : players.values()) {
            if (player.getUsername().equals(p.getUsername())) continue;

            JPanel row = new JPanel(new BorderLayout());
            row.setPreferredSize(new Dimension(280, 40));
            row.setMaximumSize(new Dimension(280, 40));

            JLabel nameLabel = new JLabel(player.getUsername());
            JButton actionBtn = new JButton("Thách đấu");

            row.add(nameLabel, BorderLayout.CENTER);
            row.add(actionBtn, BorderLayout.EAST);

            actionBtn.addActionListener(e -> {
                try {
                    networkManager.send(new ObjectSentReceived("challenge", player.getUsername()));
                } catch (Exception ex) {
                    Logger.getLogger(ClientMainPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            listPanel.add(row);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}
