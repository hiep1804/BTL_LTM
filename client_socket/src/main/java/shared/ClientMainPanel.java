/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMainPanel extends JPanel {

    private JPanel listPanel;
    //thong tin nguoi choi
    private Player p;
    //danh sach nguoi choi online
    private HashMap<String, Player> players;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton addButton;
    private ObjectSentReceived objectSentReceived;
    private ClientMainFrm clientMainFrm;

    public ClientMainPanel(Player p, ClientMainFrm clientMainFrm) {
        this.clientMainFrm=clientMainFrm;
        this.p = p;
        setLayout(null); // ❌ bỏ Layout Manager, dùng toạ độ tuyệt đối
        JLabel title = new JLabel("Client Main Frame");
        title.setBounds(300, 30, 200, 30);
        // Căn giữa theo chiều ngang
        title.setHorizontalAlignment(SwingConstants.CENTER);
        // Căn giữa theo chiều dọc
        title.setVerticalAlignment(SwingConstants.CENTER);
        add(title);
        players = new HashMap<>();
        // Panel chứa danh sách
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // ScrollPane bao quanh listPanel
        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBounds(200, 150, 400, 300); // 👈 tự đặt vị trí + size
        add(scrollPane);
        new Thread(() -> {
            try {
                while (true) {
                    ObjectSentReceived objectSentReceived = (ObjectSentReceived) p.getObjIn().readObject();
                    //them 1 nguoi choi moi vao danh sach neu da online
                    if (objectSentReceived.getType().equals("addPlayerOnline")) {
                        Player player = (Player) objectSentReceived.getObj();
                        players.put(player.getName(), player);

                        // Cập nhật UI phải chạy trên EDT
                        SwingUtilities.invokeLater(() -> refreshList());
                    }
                    //them danh sach tat ca nguoi choi dang online
                    if (objectSentReceived.getType().equals("loadPlayerOnline")) {
                        players = (HashMap<String, Player>) objectSentReceived.getObj();
                        SwingUtilities.invokeLater(() -> refreshList());
                    }
                    //gui yeu dau den nguoi dang bi thach dau
                    if (objectSentReceived.getType().equals("want to challenge")) {
                        Player player = (Player) objectSentReceived.getObj();
                        int choice = JOptionPane.showConfirmDialog(
                                this, // parent component (JPanel hoặc JFrame)
                                "Người chơi "+player.getName()+" muốn thách đấu với bạn. Bạn có đồng ý không?",
                                "Thách đấu",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (choice == JOptionPane.YES_OPTION) {
                            System.out.println("Bạn chọn: Đồng ý");
                            //gui thong tin dong y thach dau
                            ObjectSentReceived objectSentReceived1=new ObjectSentReceived("accept", player);
                            p.getObjOut().writeObject(objectSentReceived1);
                            p.getObjOut().flush();
                        } else {
                            System.out.println("Bạn chọn: Từ chối");
                            //gui thong tin tu choi thach dau
                            ObjectSentReceived objectSentReceived1=new ObjectSentReceived("reject", player);
                            p.getObjOut().writeObject(objectSentReceived1);
                            p.getObjOut().flush();
                        }
                    }
                    if(objectSentReceived.getType().equals("accept challenge")){
                        Player player=(Player)objectSentReceived.getObj();
                        clientMainFrm.setStartGameRoom(p);
                        clientMainFrm.showStartGameRoom();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Làm mới danh sách
    private void refreshList() {
        listPanel.removeAll();

        for (Player player : players.values()) {
            JPanel row = new JPanel(null); // ❌ bỏ layout
            row.setPreferredSize(new Dimension(360, 40));
            row.setMaximumSize(new Dimension(360, 40));
            row.setMinimumSize(new Dimension(360, 40));

            // Label
            JLabel nameLabel = new JLabel(player.getName());
            nameLabel.setBounds(5, 10, 150, 20);

            // Nút thách đấu
            JButton actionBtn = new JButton("Thách đấu");
            actionBtn.setBounds(200, 5, 70, 30);

            row.add(nameLabel);
            row.add(actionBtn);

            // Sự kiện
            actionBtn.addActionListener(e
                    -> {
                //gui loi thach dau
                ObjectSentReceived objectSentReceived = new ObjectSentReceived("challenge", player.getName());
                try {
                    p.getObjOut().writeObject(objectSentReceived);
                    p.getObjOut().flush();
                } catch (IOException ex) {
                    Logger.getLogger(ClientMainPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            );

            listPanel.add(row);
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

//    // Test
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Dynamic Object List (No Layout)");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(800, 600);
//            frame.setLayout(null); // ❌ bỏ layout
//            frame.setLocationRelativeTo(null);
//
//            ClientMainPanel panel = new ClientMainPanel();
//            panel.setBounds(0, 0, 800, 600); // 👈 đặt panel chiếm full frame
//            frame.add(panel);
//
//            frame.setVisible(true);
//        });
//    }
}
