/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
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

    public StartGameRoomPanel(Player p1, Player p2, ClientMainFrm clientMainFrm, NetworkManager networkManager) throws Exception {
        this.clientMainFrm=clientMainFrm;
        this.networkManager=networkManager;
        this.p1=p1;
        this.p2=p2;
        setLayout(null);
        
        // Tiêu đề game với font đẹp
        JLabel title = new JLabel("🎈 BALLOON SORT GAME 🎈");
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
        
        JLabel yourLabel = new JLabel("👤 You");
        yourLabel.setBounds(10, 10, 160, 25);
        yourLabel.setFont(new Font("Arial", Font.BOLD, 16));
        yourLabel.setForeground(new Color(33, 150, 243));
        playerPanel.add(yourLabel);
        
        JLabel yourName = new JLabel(p1.getUsername());
        yourName.setBounds(10, 40, 160, 25);
        yourName.setFont(new Font("Arial", Font.PLAIN, 14));
        playerPanel.add(yourName);
        
        JLabel yourScore = new JLabel("Score: 0");
        yourScore.setBounds(10, 70, 160, 25);
        yourScore.setFont(new Font("Arial", Font.BOLD, 14));
        yourScore.setForeground(new Color(76, 175, 80));
        playerPanel.add(yourScore);
        
        add(playerPanel);
        
        // Panel thông tin đối thủ bên phải
        JPanel opponentPanel = new JPanel();
        opponentPanel.setLayout(null);
        opponentPanel.setBounds(590, 80, 180, 120);
        opponentPanel.setBackground(new Color(255, 255, 255, 200));
        opponentPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 87, 34), 2, true));
        
        JLabel oppLabel = new JLabel("🎯 Opponent");
        oppLabel.setBounds(10, 10, 160, 25);
        oppLabel.setFont(new Font("Arial", Font.BOLD, 16));
        oppLabel.setForeground(new Color(255, 87, 34));
        opponentPanel.add(oppLabel);
        
        JLabel oppName = new JLabel(p2.getUsername());
        oppName.setBounds(10, 40, 160, 25);
        oppName.setFont(new Font("Arial", Font.PLAIN, 14));
        opponentPanel.add(oppName);
        
        JLabel oppScore = new JLabel("Score: 0");
        oppScore.setBounds(10, 70, 160, 25);
        oppScore.setFont(new Font("Arial", Font.BOLD, 14));
        oppScore.setForeground(new Color(255, 87, 34));
        opponentPanel.add(oppScore);
        
        add(opponentPanel);
        
        // Hướng dẫn chơi
        JLabel instruction = new JLabel("🎯 Drag balloons to sort from smallest to largest!");
        instruction.setBounds(150, 220, 500, 30);
        instruction.setFont(new Font("Arial", Font.ITALIC, 16));
        instruction.setForeground(new Color(100, 100, 100));
        instruction.setHorizontalAlignment(SwingConstants.CENTER);
        add(instruction);
        
        System.out.println("[StartGameRoomPanel] Constructor hoàn tất, đang chờ nhận mảng...");
    }
    
    // Phương thức công khai để nhận mảng từ bên ngoài
    public void setArray(ArrayList<Integer> array) {
        System.out.println("[StartGameRoomPanel] setArray() được gọi với mảng: " + array);
        this.arr = array;
        javax.swing.SwingUtilities.invokeLater(() -> {
            System.out.println("[StartGameRoomPanel] Khởi tạo giao diện game...");
            initializeGame();
            repaint();
        });
    }
    
    private void initializeGame() {
        System.out.println("[StartGameRoomPanel] initializeGame() được gọi");
        
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
        
        // Nút gửi với thiết kế đẹp
        JButton button = new JButton("✓ Submit Answer");
        button.setBounds(300, 520, 200, 45);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(76, 175, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(button);
        
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

                    // Kiểm tra gần rect đứng im
                    for (Rect s : staticRects) {
                        double dist = Point.distance(
                                dragging.getX() + dragging.getW()/2, 
                                dragging.getY() + dragging.getH()/2,
                                s.getX() + s.getW()/2, 
                                s.getY() + s.getH()/2
                        );
                        if (dist < 50) {
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
                        // Trả về vị trí cũ
                        dragging.setX(dragging.getOriginalX());
                        dragging.setY(dragging.getOriginalY());
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
}
