/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import shared.NetworkManager;
import shared.RegisterForm;

/**
 *
 * @author lehuy
 */
public class MainFrm extends JFrame{
    private final NetworkManager network = new NetworkManager();
    
    public MainFrm() {
        super("Sorting Game client");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                network.close();
                dispose();
            }
        });
        
        // Kết nối tới server ngay khi mở app
        try {
            String host = "192.168.0.104";
            int port = 59;
            network.connect(host, port);
            System.out.println("Đã kết nối tới " + host + ":" + port);
            
            // Thêm panel đăng ký vào frame
            RegisterForm registerForm = new RegisterForm(network);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(registerForm, BorderLayout.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kết nối tới server thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();  // Đóng app nếu không kết nối được
        }
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrm().setVisible(true));
    }
}
