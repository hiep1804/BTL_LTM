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
import shared.LoginForm;

/**
 *
 * @author lehuy
 */
public class MainFrm extends JFrame{
    private final NetworkManager network = new NetworkManager();
    private final CardLayout cardLayout = new CardLayout();     //CardLayout
    private final JPanel cardPanel = new JPanel(cardLayout);    //Panel chứa các Views
    
    // Khai báo hằng số cho tên các Panel
    public static final String LOGIN_VIEW = "LoginView";
    public static final String REGISTER_VIEW = "RegisterView";
    public static final String MAIN_VIEW = "MainView";
    //.....
    
    
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
            // ... (Phần kết nối mạng) ...
            String host = "192.168.0.104";
            int port = 59;
            network.connect(host, port);
            System.out.println("Đã kết nối tới " + host + ":" + port);
            
            // 1. Khởi tạo các Panel và thêm vào CardPanel
            LoginForm loginForm = new LoginForm(this, network);
            RegisterForm registerForm = new RegisterForm(this, network);
            cardPanel.add(loginForm, LOGIN_VIEW);
            cardPanel.add(registerForm, REGISTER_VIEW);
            // Thêm các Panel khác khi cần...
            
            // 2. Thêm CardPanel vào JFrame
            getContentPane().add(cardPanel, BorderLayout.CENTER);
                    
            // 3. Hiển thị View đầu tiên
            showPanel(LOGIN_VIEW); // Bắt đầu bằng LoginForm
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kết nối tới server thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();  // Đóng app nếu không kết nối được
        }
        
        // Cấu hình JFrame
        setSize(500, 400); // Đặt kích thước cụ thể
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Nên dùng EXIT_ON_CLOSE nếu đã xử lý network.close()
    }
    
    public void showPanel(String viewName) {
        cardLayout.show(cardPanel, viewName);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrm().setVisible(true));
    }
}
