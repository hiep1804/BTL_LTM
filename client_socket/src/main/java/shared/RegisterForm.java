package shared;

import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JPanel {
    private final NetworkManager network;
    private final ClientMainFrm mainFrm;
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterForm(ClientMainFrm mainFrm, NetworkManager network) {
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(142, 45, 226),
                                                      0, getHeight(), new Color(74, 0, 224));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setBounds(0, 0, 800, 600);
        backgroundPanel.setLayout(null);

        // Register card with shadow
        JPanel registerCard = new JPanel() {
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
        registerCard.setBounds(200, 80, 400, 480);
        registerCard.setLayout(null);
        registerCard.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("ĐĂNG KÝ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(getFont());
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(142, 45, 226),
                                                      getWidth(), 0, new Color(74, 0, 224));
                g2d.setPaint(gp);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2d.drawString(getText(), x, 35);
            }
        };
        titleLabel.setBounds(0, 25, 400, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerCard.add(titleLabel);

        // Name label and field
        JLabel nameLabel = new JLabel("Họ và tên");
        nameLabel.setBounds(50, 90, 300, 25);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(80, 80, 80));
        registerCard.add(nameLabel);

        nameField = createStyledTextField();
        nameField.setBounds(50, 120, 300, 45);
        registerCard.add(nameField);

        // Username label and field
        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setBounds(50, 180, 300, 25);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(80, 80, 80));
        registerCard.add(usernameLabel);

        usernameField = createStyledTextField();
        usernameField.setBounds(50, 210, 300, 45);
        registerCard.add(usernameField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setBounds(50, 270, 300, 25);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(80, 80, 80));
        registerCard.add(passwordLabel);

        passwordField = createStyledPasswordField();
        passwordField.setBounds(50, 300, 300, 45);
        registerCard.add(passwordField);

        // Confirm password label and field
        JLabel confirmLabel = new JLabel("Xác nhận mật khẩu");
        confirmLabel.setBounds(50, 360, 300, 25);
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmLabel.setForeground(new Color(80, 80, 80));
        registerCard.add(confirmLabel);

        confirmPasswordField = createStyledPasswordField();
        confirmPasswordField.setBounds(50, 390, 300, 45);
        registerCard.add(confirmPasswordField);

        // Register button
        JButton registerButton = new JButton("ĐĂNG KÝ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(102, 25, 166),
                                                          0, getHeight(), new Color(54, 0, 164));
                    g2d.setPaint(gp);
                } else if (getModel().isRollover()) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(162, 65, 246),
                                                          0, getHeight(), new Color(94, 20, 244));
                    g2d.setPaint(gp);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(142, 45, 226),
                                                          0, getHeight(), new Color(74, 0, 224));
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
        registerButton.setBounds(100, 455, 200, 45);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegister());
        registerCard.add(registerButton);

        // Back to login link
        JButton backButton = new JButton("Đã có tài khoản? Đăng nhập tại đây") {
            @Override
            protected void paintComponent(Graphics g) {
                // No background, just text
            }
        };
        backButton.setBounds(50, 510, 300, 25);
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setForeground(new Color(142, 45, 226));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainFrm.showLoginPanel());
        registerCard.add(backButton);

        backgroundPanel.add(registerCard);
        add(backgroundPanel);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
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
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        field.setBackground(new Color(245, 245, 245));
        field.setOpaque(false);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
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
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        field.setBackground(new Color(245, 245, 245));
        field.setOpaque(false);
        return field;
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Player player = new Player(name, username, password);
            ObjectSentReceived req = new ObjectSentReceived("Register", player);
            network.send(req);

            ObjectSentReceived resp = network.receive();
            if ("Register".equals(resp.getType())) {
                boolean status = (Boolean) resp.getObj();
                if (status) {
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    mainFrm.showLoginPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Tên đăng nhập có thể đã tồn tại.", "Thất bại", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
