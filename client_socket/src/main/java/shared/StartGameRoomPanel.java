/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.ArrayList;
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

    public StartGameRoomPanel(Player p1, Player p2, ClientMainFrm clientMainFrm,NetworkManager networkManager) throws Exception {
//        setPreferredSize(new Dimension(800, 600));
        this.clientMainFrm=clientMainFrm;
        this.networkManager=networkManager;
        this.p1=p1;
        this.p2=p2;
        setLayout(null);
        JLabel title = new JLabel("Client Room Game Frame");
        title.setBounds(300, 30, 200, 30);
        // Căn giữa theo chiều ngang
        title.setHorizontalAlignment(SwingConstants.CENTER);
        // Căn giữa theo chiều dọc
        title.setVerticalAlignment(SwingConstants.CENTER);
        add(title);
        JLabel tenDoiThu=new JLabel(p2.getUsername());
        tenDoiThu.setBounds(550, 80, 200, 30);
        add(tenDoiThu);
        JLabel diemDoiThu=new JLabel("0");
        diemDoiThu.setBounds(550, 100, 200, 30);
        add(diemDoiThu);
        ObjectSentReceived objectSentReceived=networkManager.receive();
        //lay mang de sap xep
        if(objectSentReceived.getType().equals("mang can sap xep")){
            arr=(ArrayList<Integer>)objectSentReceived.getObj();
        }
        // Tạo mảng đứng im
        for (int i = 0; i < 8; i++) {
            staticRects.add(new Rect(80 + i*80, 150, 60, 60, Color.LIGHT_GRAY, "", false));
        }

        // Tạo mảng di chuyển
        for (int i = 0; i < 8; i++) {
            movableRects.add(new Rect(80 + i*80, 300, 60, 60, Color.CYAN,arr.get(i)+"", true));
        }
        JButton button=new JButton("Gửi");
        button.setBounds(350, 500, 100, 30);
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
                                dragging.getX(), dragging.getY(),
                                s.getX(), s.getY()
                        );
                        if (dist < 20) {
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Rect s : staticRects) s.draw(g);
        for (Rect m : movableRects) m.draw(g);
    }
}
