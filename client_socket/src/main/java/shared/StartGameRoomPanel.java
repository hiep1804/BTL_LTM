/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author hn235
 */
public class StartGameRoomPanel extends JPanel{
    private Player p1;
    private Player p2;
    private ClientMainFrm clientMainFrm;
    ArrayList<Rect> staticRects = new ArrayList<>();
    ArrayList<Rect> movableRects = new ArrayList<>();
    Rect dragging = null;
    int offsetX, offsetY;
    //mang de sap xep
    private ArrayList<Integer> arr;
    private NetworkManager networkManager;
    private JLabel yourScoreLabel;
    private JLabel oppScoreLabel;
    private JLabel roundLabel;
    private JLabel timerLabel;
    private JButton submitButton;
    private int currentRound = 0;
    private int totalRounds = 0;
    private long roundStartTime = 0;
    private int roundTimeSeconds = 0;
    private javax.swing.Timer countdownTimer;
    private boolean hasSubmitted = false;

    public StartGameRoomPanel(Player p1, Player p2, ClientMainFrm clientMainFrm, NetworkManager networkManager) throws Exception {
        this.clientMainFrm=clientMainFrm;
        this.networkManager=networkManager;
        this.p1=p1;
        this.p2=p2;
        setLayout(null);
        
        // Tiêu đề game với font đẹp
        JLabel title = new JLabel("TRÒ CHƠI SẮP XẾP BÓNG BAY");
        title.setBounds(200, 20, 400, 50);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(255, 107, 107));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);
        
        // Panel thông tin người chơi bên trái
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(null);
        playerPanel.setBounds(30, 80, 180, 120);
        playerPanel.setBackground(new Color(255, 255, 255, 200));
        playerPanel.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 2, true));
        
        JLabel yourLabel = new JLabel("Bạn");
        yourLabel.setBounds(10, 10, 160, 25);
        yourLabel.setFont(new Font("Arial", Font.BOLD, 16));
        yourLabel.setForeground(new Color(33, 150, 243));
        playerPanel.add(yourLabel);
        
        JLabel yourName = new JLabel(p1.getUsername());
        yourName.setBounds(10, 40, 160, 25);
        yourName.setFont(new Font("Arial", Font.PLAIN, 14));
        playerPanel.add(yourName);
        
    yourScoreLabel = new JLabel("Điểm: 0");
    yourScoreLabel.setBounds(10, 70, 160, 25);
    yourScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
    yourScoreLabel.setForeground(new Color(76, 175, 80));
    playerPanel.add(yourScoreLabel);
        
        add(playerPanel);
        
        // Panel thông tin đối thủ bên phải
        JPanel opponentPanel = new JPanel();
        opponentPanel.setLayout(null);
        opponentPanel.setBounds(590, 80, 180, 120);
        opponentPanel.setBackground(new Color(255, 255, 255, 200));
        opponentPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 87, 34), 2, true));
        
        JLabel oppLabel = new JLabel("Đối thủ");
        oppLabel.setBounds(10, 10, 160, 25);
        oppLabel.setFont(new Font("Arial", Font.BOLD, 16));
        oppLabel.setForeground(new Color(255, 87, 34));
        opponentPanel.add(oppLabel);
        
        JLabel oppName = new JLabel(p2.getUsername());
        oppName.setBounds(10, 40, 160, 25);
        oppName.setFont(new Font("Arial", Font.PLAIN, 14));
        opponentPanel.add(oppName);
        
    oppScoreLabel = new JLabel("Điểm: 0");
    oppScoreLabel.setBounds(10, 70, 160, 25);
    oppScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
    oppScoreLabel.setForeground(new Color(255, 87, 34));
    opponentPanel.add(oppScoreLabel);
        
        add(opponentPanel);
        
        // Thông tin ván và thời gian ở giữa
        roundLabel = new JLabel("Đang chờ bắt đầu...");
        roundLabel.setBounds(250, 80, 300, 30);
        roundLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roundLabel.setForeground(new Color(255, 107, 107));
        roundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(roundLabel);
        
        timerLabel = new JLabel("00:00");
        timerLabel.setBounds(250, 115, 300, 40);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        timerLabel.setForeground(new Color(255, 152, 0));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(timerLabel);
        
        // Hướng dẫn chơi
        JLabel instruction = new JLabel("Kéo bóng bay để sắp xếp từ nhỏ đến lớn!");
        instruction.setBounds(150, 220, 500, 30);
        instruction.setFont(new Font("Arial", Font.ITALIC, 16));
        instruction.setForeground(new Color(100, 100, 100));
        instruction.setHorizontalAlignment(SwingConstants.CENTER);
        add(instruction);
        
        System.out.println("[StartGameRoomPanel] Constructor hoàn tất, đang chờ nhận ván đấu...");
    }
    
    // Phương thức công khai để nhận thông báo đối thủ thoát từ ClientMainPanel
    public void handleOpponentLeft() {
        javax.swing.JOptionPane.showMessageDialog(
            this,
            "Đối thủ của bạn đã rời khỏi trò chơi.",
            "Đối thủ đã rời đi",
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
        backToLobby();
    }
    
    private void backToLobby() {
        try {
            // Đánh dấu không còn bận
            p1.setBusy(false);
            
            System.out.println("[StartGameRoomPanel] Quay về lobby...");
            
            // Yêu cầu refresh thông tin người chơi từ server
            try {
                networkManager.send(new ObjectSentReceived("refreshPlayerInfo", null));
                System.out.println("[StartGameRoomPanel] Đã gửi yêu cầu refresh thông tin");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            // Xóa panel game room cũ
            clientMainFrm.removeGameRoomPanel();
            
            // Quay về ClientMainPanel (không reload, chỉ show)
            clientMainFrm.showClientGamePanel();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Phương thức công khai để bắt đầu ván đấu mới
    public void startNewRound(RoundInfo roundInfo) {
        System.out.println("[StartGameRoomPanel] Bắt đầu ván " + roundInfo.getCurrentRound());
        
        this.currentRound = roundInfo.getCurrentRound();
        this.totalRounds = roundInfo.getTotalRounds();
        this.roundTimeSeconds = roundInfo.getTimeSeconds();
        this.arr = roundInfo.getArray();
        this.roundStartTime = System.currentTimeMillis();
        this.hasSubmitted = false;
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            roundLabel.setText("VÁN " + currentRound + "/" + totalRounds);
            initializeGame();
            startCountdown();
            repaint();
        });
    }
    
    private void startCountdown() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        countdownTimer = new javax.swing.Timer(100, e -> {
            long elapsed = System.currentTimeMillis() - roundStartTime;
            long remaining = (roundTimeSeconds * 1000L) - elapsed;
            
            if (remaining <= 0) {
                timerLabel.setText("00:00");
                timerLabel.setForeground(Color.RED);
                countdownTimer.stop();
            } else {
                int seconds = (int)(remaining / 1000);
                int millis = (int)((remaining % 1000) / 100);
                timerLabel.setText(String.format("%02d:%d", seconds, millis));
                
                // Đổi màu khi còn 5 giây
                if (seconds <= 5) {
                    timerLabel.setForeground(Color.RED);
                } else {
                    timerLabel.setForeground(new Color(255, 152, 0));
                }
            }
        });
        countdownTimer.start();
    }
    
    private void initializeGame() {
        System.out.println("[StartGameRoomPanel] initializeGame() được gọi");
        staticRects.clear();
        movableRects.clear();
        
        // Kích thước bóng bay lớn hơn
        int balloonSize = 70;
        int spacing = 90;
        int startX = 50;
        
        // Tạo mảng đứng im (vị trí đích) - hàng trên
        for (int i = 0; i < 8; i++) {
            staticRects.add(new Rect(startX + i*spacing, 270, balloonSize, balloonSize, Color.LIGHT_GRAY, "", false));
        }

        // Tạo mảng di chuyển (bóng bay có số) - hàng dưới
        if (arr != null) {
            for (int i = 0; i < 8; i++) {
                movableRects.add(new Rect(startX + i*spacing, 400, balloonSize, balloonSize, Color.CYAN, arr.get(i)+"", true));
            }
        } else {
            System.out.println("[StartGameRoomPanel] CẢNH BÁO: arr là null!");
        }
        
        // Nút Submit (chỉ tạo 1 lần nếu chưa có)
        if (submitButton == null) {
            submitButton = new JButton("Nộp bài");
            submitButton.setBounds(250, 520, 180, 45);
            submitButton.setFont(new Font("Arial", Font.BOLD, 16));
            submitButton.setBackground(new Color(76, 175, 80));
            submitButton.setForeground(Color.WHITE);
            submitButton.setFocusPainted(false);
            submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            submitButton.addActionListener(e -> submitAnswer());
            add(submitButton);
        }
        submitButton.setEnabled(true);
        
        // Nút Exit
        JButton exitButton = new JButton("Thoát");
        exitButton.setBounds(450, 520, 150, 45);
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBackground(new Color(244, 67, 54));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        exitButton.addActionListener(e -> exitGame());
        add(exitButton);
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (Rect r : movableRects) {
                    if (r.getBounds().contains(e.getPoint())) {
                        dragging = r;
                        offsetX = e.getX() - r.getX();
                        offsetY = e.getY() - r.getY();
                        break;
                    }
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (dragging != null) {
                    boolean snapped = false;
                    Rect previousSlot = null;
                    
                    // Tìm vị trí cũ của quả bóng đang kéo (nếu có)
                    for (Rect s : staticRects) {
                        if (s.getValue().equals(dragging.getValue()) &&
                            Math.abs(s.getX() - dragging.getOriginalX()) < 10 &&
                            Math.abs(s.getY() - dragging.getOriginalY()) < 130) {
                            previousSlot = s;
                            break;
                        }
                    }

                    // Kiểm tra gần rect đứng im nào
                    for (Rect s : staticRects) {
                        double dist = Point.distance(
                                dragging.getX() + dragging.getW()/2, 
                                dragging.getY() + dragging.getH()/2,
                                s.getX() + s.getW()/2, 
                                s.getY() + s.getH()/2
                        );
                        if (dist < 50) {
                            // Xóa giá trị ở vị trí cũ (nếu có)
                            if (previousSlot != null && previousSlot != s) {
                                previousSlot.setValue("");
                            }
                            
                            // Snap vị trí
                            dragging.setX(s.getX());
                            dragging.setY(s.getY());

                            // Gán giá trị của ô di chuyển cho ô đứng im
                            s.setValue(dragging.getValue());
                            snapped = true;
                            break;
                        }
                    }

                    if (!snapped) {
                        // Nếu không snap vào vị trí nào, trả về vị trí cũ
                        dragging.setX(dragging.getOriginalX());
                        dragging.setY(dragging.getOriginalY());
                        
                        // Xóa giá trị ở vị trí đã đặt trước đó (nếu có)
                        if (previousSlot != null) {
                            previousSlot.setValue("");
                        }
                    }

                    dragging = null;
                }
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragging != null) {
                    dragging.setX(e.getX() - offsetX);
                    dragging.setY(e.getY() - offsetY);
                    repaint();
                }
            }
        });
    }

    private void submitAnswer() {
        if (hasSubmitted) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Bạn đã nộp bài rồi!",
                "Thông báo",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        if (staticRects.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Chưa có dữ liệu để nộp bài.",
                "Thông báo",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        ArrayList<Integer> submission = new ArrayList<>();
        for (Rect s : staticRects) {
            String value = s.getValue();
            if (value == null || value.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng sắp xếp đủ 8 quả bóng trước khi nộp bài.",
                    "Thiếu dữ liệu",
                    javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            try {
                submission.add(Integer.parseInt(value));
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Phát hiện giá trị không hợp lệ: " + value,
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        try {
            hasSubmitted = true;
            submitButton.setEnabled(false);
            networkManager.send(new ObjectSentReceived("submit_array", submission));
            System.out.println("[StartGameRoomPanel] Đã gửi bài nộp");
        } catch (Exception ex) {
            hasSubmitted = false;
            submitButton.setEnabled(true);
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Không thể gửi kết quả lên máy chủ. Vui lòng thử lại.",
                "Lỗi kết nối",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void updateScores(ScoreUpdate update) {
        if (update == null) {
            return;
        }

        String username = p1.getUsername();
        boolean playerIsFirst = username.equals(update.getPlayer1Username());

        if (playerIsFirst) {
            yourScoreLabel.setText("Điểm: " + update.getPlayer1Score());
            oppScoreLabel.setText("Điểm: " + update.getPlayer2Score());
        } else {
            yourScoreLabel.setText("Điểm: " + update.getPlayer2Score());
            oppScoreLabel.setText("Điểm: " + update.getPlayer1Score());
        }

        if (username.equals(update.getSubmitterUsername())) {
            if (update.isCorrect()) {
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Bạn đã sắp xếp đúng!",
                    "Chính xác",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Kết quả chưa đúng, hãy thử lại nhé.",
                    "Chưa chính xác",
                    javax.swing.JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }
    
    public void handleRoundEnd(ScoreUpdate update) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        // Cập nhật điểm cuối ván
        String username = p1.getUsername();
        boolean playerIsFirst = username.equals(update.getPlayer1Username());
        
        if (playerIsFirst) {
            yourScoreLabel.setText("Điểm: " + update.getPlayer1Score());
            oppScoreLabel.setText("Điểm: " + update.getPlayer2Score());
        } else {
            yourScoreLabel.setText("Điểm: " + update.getPlayer2Score());
            oppScoreLabel.setText("Điểm: " + update.getPlayer1Score());
        }
        
        roundLabel.setText("Kết thúc ván " + currentRound);
        timerLabel.setText("00:00");
    }
    
    public void handleGameOver(ScoreUpdate finalScore) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        // Cập nhật điểm cuối cùng
        String username = p1.getUsername();
        boolean playerIsFirst = username.equals(finalScore.getPlayer1Username());
        
        int yourScore, oppScore;
        Player winner, loser;
        int winnerScore, loserScore;
        
        if (playerIsFirst) {
            yourScore = finalScore.getPlayer1Score();
            oppScore = finalScore.getPlayer2Score();
        } else {
            yourScore = finalScore.getPlayer2Score();
            oppScore = finalScore.getPlayer1Score();
        }
        
        yourScoreLabel.setText("Điểm: " + yourScore);
        oppScoreLabel.setText("Điểm: " + oppScore);
        
        // Xác định người thắng/thua
        if (yourScore > oppScore) {
            winner = p1;
            loser = p2;
            winnerScore = yourScore;
            loserScore = oppScore;
        } else if (yourScore < oppScore) {
            winner = p2;
            loser = p1;
            winnerScore = oppScore;
            loserScore = yourScore;
        } else {
            // Hòa - vẫn truyền vào nhưng MatchResultPanel sẽ xử lý
            winner = p1;
            loser = p2;
            winnerScore = yourScore;
            loserScore = oppScore;
        }
        
        // Hiển thị MatchResultPanel
        clientMainFrm.showMatchResult(winner, loser, winnerScore, loserScore);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(135, 206, 250),           // Sky blue
            0, getHeight(), new Color(255, 250, 205)   // Light yellow
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Vẽ các đám mây trang trí
        drawCloud(g2d, 100, 100, 80, 40);
        drawCloud(g2d, 650, 150, 100, 50);
        drawCloud(g2d, 350, 80, 70, 35);
        
        // Vẽ các bóng bay trong game
        for (Rect s : staticRects) s.draw(g);
        for (Rect m : movableRects) m.draw(g);
    }
    
    // Vẽ đám mây trang trí
    private void drawCloud(Graphics2D g2d, int x, int y, int width, int height) {
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillOval(x, y, width/2, height);
        g2d.fillOval(x + width/4, y - height/3, width/2, height);
        g2d.fillOval(x + width/2, y, width/2, height);
    }
    
    private void exitGame() {
        int choice = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn thoát khỏi trò chơi không?",
            "Thoát trò chơi",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == javax.swing.JOptionPane.YES_OPTION) {
            try {
                System.out.println("[StartGameRoomPanel] Người chơi đã thoát khỏi trò chơi");
                
                // Gửi thông báo thoát lên server
                networkManager.send(new ObjectSentReceived("thoat game", null));
                
                // Quay về lobby
                backToLobby();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
