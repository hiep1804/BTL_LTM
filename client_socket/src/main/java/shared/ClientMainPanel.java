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
        setBackground(new Color(245, 247, 250));

        // Tiêu đề với gradient
        JLabel title = new JLabel("SẢNH CHỜ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(getFont());
                
                // Vẽ shadow
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.drawString(getText(), (getWidth() - g2d.getFontMetrics().stringWidth(getText())) / 2 + 2, 32);
                
                // Vẽ gradient text
                GradientPaint gp = new GradientPaint(0, 0, new Color(66, 133, 244), 
                                                      getWidth(), 0, new Color(52, 168, 83));
                g2d.setPaint(gp);
                g2d.drawString(getText(), (getWidth() - g2d.getFontMetrics().stringWidth(getText())) / 2, 30);
            }
        };
        title.setBounds(200, 15, 400, 50);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Chào mừng, " + p.getUsername() + "!");
        welcomeLabel.setBounds(30, 75, 400, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(60, 60, 60));
        add(welcomeLabel);

        players = new HashMap<>();
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        // Tiêu đề "Online Players" với style đẹp
        JPanel onlineHeader = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(76, 175, 80), 
                                                      0, getHeight(), new Color(56, 142, 60));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        onlineHeader.setLayout(null);
        onlineHeader.setBounds(430, 115, 340, 40);
        onlineHeader.setOpaque(false);
        
        JLabel onlineTitle = new JLabel("Người chơi trực tuyến");
        onlineTitle.setBounds(15, 0, 310, 40);
        onlineTitle.setFont(new Font("Arial", Font.BOLD, 16));
        onlineTitle.setForeground(Color.WHITE);
        onlineHeader.add(onlineTitle);
        add(onlineHeader);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBounds(430, 160, 340, 380);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane);

        leaderboardPanel = new LeaderboardPanel();
        leaderboardPanel.setBounds(30, 115, 380, 425);
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
                                case "challenger_busy" -> {
                                    String message = (String) finalReceived.getObj();
                                    JOptionPane.showMessageDialog(ClientMainPanel.this, message, "Thách đấu thất bại", JOptionPane.WARNING_MESSAGE);
                                }
                                case "refreshPlayerInfo" -> {
                                    Player updatedPlayer = (Player) finalReceived.getObj();
                                    // Cập nhật thông tin player hiện tại
                                    p.setTotalScore(updatedPlayer.getTotalScore());
                                    p.setTotalWins(updatedPlayer.getTotalWins());
                                    p.setMatchesPlayed(updatedPlayer.getMatchesPlayed());
                                    System.out.println("[ClientMainPanel] Đã cập nhật thông tin player: " + 
                                                     p.getUsername() + " - Score: " + p.getTotalScore());
                                    
                                    // Reload leaderboard
                                    try {
                                        networkManager.send(new ObjectSentReceived("getLeaderboard", null));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                case "want to challenge" -> {
                                    Player challenger = (Player) finalReceived.getObj();
                                    int choice = JOptionPane.showConfirmDialog(
                                            ClientMainPanel.this,
                                            "Người chơi " + challenger.getUsername() + " muốn thách đấu bạn. Bạn có đồng ý không?",
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
                                    // Tạo StartGameRoomPanel ngay
                                    clientMainFrm.setStartGameRoom(opponent, networkManager);
                                    clientMainFrm.showStartGameRoom();
                                }
                                case "start_round" -> {
                                    // Chuyển tiếp thông tin ván đấu cho StartGameRoomPanel
                                    System.out.println("[ClientMainPanel] Nhận thông tin ván đấu mới");
                                    RoundInfo roundInfo = (RoundInfo) finalReceived.getObj();
                                    clientMainFrm.forwardRoundInfo(roundInfo);
                                }
                                case "round_end" -> {
                                    System.out.println("[ClientMainPanel] Kết thúc ván");
                                    ScoreUpdate update = (ScoreUpdate) finalReceived.getObj();
                                    clientMainFrm.forwardRoundEnd(update);
                                }
                                case "game_over" -> {
                                    System.out.println("[ClientMainPanel] Kết thúc trận đấu");
                                    ScoreUpdate finalScore = (ScoreUpdate) finalReceived.getObj();
                                    clientMainFrm.forwardGameOver(finalScore);
                                }
                                case "update_score" -> {
                                    System.out.println("[ClientMainPanel] Nhận cập nhật điểm");
                                    ScoreUpdate update = (ScoreUpdate) finalReceived.getObj();
                                    clientMainFrm.forwardScoreUpdate(update);
                                }
                                case "doi thu thoat" -> {
                                    // Thông báo cho StartGameRoomPanel
                                    System.out.println("[ClientMainPanel] Nhận thông báo đối thủ thoát");
                                    clientMainFrm.notifyOpponentLeft();
                                }
                                case "thoat game" -> {
                                    // Người chơi này đã thoát, không cần làm gì
                                    System.out.println("[ClientMainPanel] Xác nhận đã thoát game");
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

            JPanel row = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(248, 249, 250));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
            };
            row.setLayout(new BorderLayout(10, 0));
            row.setPreferredSize(new Dimension(310, 50));
            row.setMaximumSize(new Dimension(310, 50));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel nameLabel = new JLabel(player.getUsername());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setForeground(new Color(50, 50, 50));

            JButton actionBtn = new JButton("Thách đấu") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (getModel().isPressed()) {
                        GradientPaint gp = new GradientPaint(0, 0, new Color(230, 74, 25),
                                                              0, getHeight(), new Color(207, 56, 9));
                        g2d.setPaint(gp);
                    } else if (getModel().isRollover()) {
                        GradientPaint gp = new GradientPaint(0, 0, new Color(255, 120, 80),
                                                              0, getHeight(), new Color(251, 86, 33));
                        g2d.setPaint(gp);
                    } else {
                        GradientPaint gp = new GradientPaint(0, 0, new Color(255, 87, 34),
                                                              0, getHeight(), new Color(230, 74, 25));
                        g2d.setPaint(gp);
                    }
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    
                    // Vẽ text
                    g2d.setColor(getForeground());
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(getText(), x, y);
                }
            };
            actionBtn.setFont(new Font("Arial", Font.BOLD, 12));
            actionBtn.setForeground(Color.WHITE);
            actionBtn.setPreferredSize(new Dimension(110, 35));
            actionBtn.setFocusPainted(false);
            actionBtn.setBorderPainted(false);
            actionBtn.setContentAreaFilled(false);
            actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            actionBtn.addActionListener(e -> {
                try {
                    networkManager.send(new ObjectSentReceived("challenge", player.getUsername()));
                } catch (Exception ex) {
                    Logger.getLogger(ClientMainPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            row.add(nameLabel, BorderLayout.CENTER);
//            row.add(actionBtn, BorderLayout.EAST);
            //Kiểm tra player có đang bận không --> Bận: báo bận - Không bận: có nút Thách đấu
            if (player.isBusy()) {
                // 3. Nếu ĐANG BẬN: Hiển thị Label "Đang bận"
                JLabel busyLabel = new JLabel("Đang bận");
                busyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                busyLabel.setForeground(Color.GRAY);
                busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                // Đặt kích thước ưu tiên để giữ layout (giống kích thước nút)
                busyLabel.setPreferredSize(new Dimension(110, 35)); 
                
                row.add(busyLabel, BorderLayout.EAST); // Thêm label vào bên phải
            } else {
                //Giữ nguyên logic
                row.add(actionBtn, BorderLayout.EAST);
                listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }

            listPanel.add(row);
//            listPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
    
    /**
    * Trả về bản đồ (HashMap) chứa danh sách người chơi trực tuyến.
    *
    * @return HashMap<String, Player> danh sách người chơi.
    */
    public HashMap<String, Player> getPlayers() {
        return this.players;
    }
    
    /**
    * Thiết lập (ghi đè) danh sách người chơi trực tuyến và tự động làm mới giao diện.
    * <p>
    * Phương thức này đảm bảo việc gán dữ liệu và cập nhật UI
    * được thực hiện an toàn trên Event Dispatch Thread (EDT) của Swing.
    *
    * @param newPlayers Danh sách người chơi mới.
    * --> Hàm này giúp: khi khởi tạo 1 ClientMainPanel mới, nó sẽ lấy lại được danh sách người chơi online
    * từ ClientMainPanel cũ.
    */
    public void setPlayers(HashMap<String, Player> newPlayers) {
        // Đảm bảo việc gán và làm mới UI được thực hiện trên luồng EDT
        SwingUtilities.invokeLater(() -> {
            this.players = newPlayers;
            refreshList(); // Tự động làm mới danh sách trên UI
        });
    }
}
