package shared;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JPanel {
    private final NetworkManager network;
    private final ClientMainFrm mainFrm;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm(ClientMainFrm mainFrm, NetworkManager network) {
        this.network = network;
        this.mainFrm = mainFrm;
        setLayout(null);
        initModernComponents();
    }

    private void initModernComponents() {
        // Gradient background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(66, 133, 244),
                                                      0, getHeight(), new Color(52, 168, 83));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setBounds(0, 0, 800, 600);
        backgroundPanel.setLayout(null);

        // Login card with shadow
        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 20, 20);
                
                // Card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
            }
        };
        loginCard.setBounds(225, 120, 350, 380);
        loginCard.setLayout(null);
        loginCard.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(getFont());
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(66, 133, 244),
                                                      getWidth(), 0, new Color(52, 168, 83));
                g2d.setPaint(gp);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2d.drawString(getText(), x, 35);
            }
        };
        titleLabel.setBounds(0, 30, 350, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginCard.add(titleLabel);

        // Username label
        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setBounds(40, 100, 270, 25);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(80, 80, 80));
        loginCard.add(usernameLabel);

        // Username field with rounded border
        usernameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        usernameField.setBounds(40, 130, 270, 45);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        usernameField.setBackground(new Color(245, 245, 245));
        usernameField.setOpaque(false);
        loginCard.add(usernameField);

        // Password label
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setBounds(40, 190, 270, 25);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(80, 80, 80));
        loginCard.add(passwordLabel);

        // Password field
        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        passwordField.setBounds(40, 220, 270, 45);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        passwordField.setBackground(new Color(245, 245, 245));
        passwordField.setOpaque(false);
        loginCard.add(passwordField);

        // Login button
        JButton loginButton = new JButton("ĐĂNG NHẬP") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(46, 103, 184),
                                                          0, getHeight(), new Color(32, 138, 63));
                    g2d.setPaint(gp);
                } else if (getModel().isRollover()) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(86, 153, 224),
                                                          0, getHeight(), new Color(72, 188, 103));
                    g2d.setPaint(gp);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(66, 133, 244),
                                                          0, getHeight(), new Color(52, 168, 83));
                    g2d.setPaint(gp);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw text
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        loginButton.setBounds(40, 285, 270, 45);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        loginCard.add(loginButton);

        // Register link
        JButton registerButton = new JButton("Chưa có tài khoản? Đăng ký tại đây") {
            @Override
            protected void paintComponent(Graphics g) {
                // No background, just text
            }
        };
        registerButton.setBounds(40, 340, 270, 25);
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setForeground(new Color(66, 133, 244));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> mainFrm.showRegisterPanel());
        loginCard.add(registerButton);

        backgroundPanel.add(loginCard);
        add(backgroundPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Player player = new Player(username, password);
            ObjectSentReceived req = new ObjectSentReceived("Login", player);
            network.send(req);

            ObjectSentReceived resp = network.receive();
            if ("Login".equals(resp.getType())) {
                boolean status = (Boolean) resp.getObj();
                if (status) {
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    mainFrm.setClientGamePanel(player);
                    mainFrm.showClientGamePanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không hợp lệ!", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
