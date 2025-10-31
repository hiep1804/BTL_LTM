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
    private volatile boolean listening = false;
    private Thread listenThread;

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

    public void startListening() {
        if (listening) return;
        listening = true;
        listenThread = new Thread(() -> {
            try {
                networkManager.send(new ObjectSentReceived("getLeaderboard", null));

                while (listening) {
                    ObjectSentReceived received = networkManager.receive();
                    if (received == null) break;

                    String type = received.getType();
                    System.out.println("Received: " + type);

                    // Lưu reference để dùng trong lambda
                    final ObjectSentReceived finalReceived = received;

                    SwingUtilities.invokeLater(() -> {
                        try {
                            switch (type) {
                                case "addPlayerOnline" -> {
                                    Player player = (Player) finalReceived.getObj();
                                    players.put(player.getUsername(), player);
                                    refreshList();
                                }
                                case "loadPlayerOnline" -> {
                                    ConcurrentHashMap<String, Player> players1 = (ConcurrentHashMap<String, Player>) finalReceived.getObj();
                                    players = new HashMap<>(players1);
                                    refreshList();
                                }
                                case "getLeaderboard" -> {
                                    ArrayList<Player> leaderboard = (ArrayList<Player>) finalReceived.getObj();
                                    leaderboardPanel.updateLeaderboard(leaderboard);
                                }
                                case "want to challenge" -> {
                                    Player challenger = (Player) finalReceived.getObj();
                                    int choice = JOptionPane.showConfirmDialog(
                                            ClientMainPanel.this,
                                            "Người chơi " + challenger.getUsername() + " muốn thách đấu với bạn. Bạn có đồng ý không?",
                                            "Thách đấu",
                                            JOptionPane.YES_NO_OPTION
                                    );
                                    if (choice == JOptionPane.YES_OPTION) {
                                        networkManager.send(new ObjectSentReceived("accept", challenger.getUsername()));
                                    } else {
                                        networkManager.send(new ObjectSentReceived("reject", challenger.getUsername()));
                                    }
                                }
                                case "start_game" -> {
                                    Player opponent = (Player) finalReceived.getObj();
                                    // Dừng listener TRƯỚC KHI tạo StartGameRoomPanel
                                    stopListening();
                                    // Chờ một chút để đảm bảo thread đã dừng hoàn toàn
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {}
                                    // Bây giờ mới tạo StartGameRoomPanel
                                    clientMainFrm.setStartGameRoom(opponent, networkManager);
                                    clientMainFrm.showStartGameRoom();
                                }
                                case "mang can sap xep" -> {
                                    // Chuyển tiếp mảng cho StartGameRoomPanel
                                    System.out.println("[ClientMainPanel] Nhận được mảng, chuyển tiếp cho StartGameRoomPanel");
                                    ArrayList<Integer> arr = (ArrayList<Integer>) finalReceived.getObj();
                                    clientMainFrm.forwardArrayToGameRoom(arr);
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(ClientMainPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            } catch (Exception e) {
                if (listening) {
                    e.printStackTrace();
                }
            }
        }, "ClientListenThread");
        listenThread.start();
    }

    public void stopListening() {
        listening = false;
        if (listenThread != null) {
            listenThread.interrupt();
            try {
                // Đợi thread listener thoát để tránh 2 thread cùng đọc ObjectInputStream
                listenThread.join(2000);
            } catch (InterruptedException ignored) {}
            listenThread = null;
        }
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
