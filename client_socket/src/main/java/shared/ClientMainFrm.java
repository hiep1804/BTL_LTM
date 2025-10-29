package shared;

import java.awt.CardLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClientMainFrm extends JFrame{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    private Player player;
    private NetworkManager networkManager;
    
    //Các panel - giao diện
    private LoginForm loginPanel;
    private RegisterForm registerPanel;
    private ClientMainPanel clientMainPanel;
    private StartGameRoomPanel startGameRoomPanel;

    public static final String LOGIN_VIEW = "login_view";
    public static final String REGISTER_VIEW = "register_view";
    public static final String CLIENT_MAIN_VIEW = "client_main_view";
    public static final String GAME_ROOM_VIEW = "game_room_view";
    
    
    public ClientMainFrm(){
        try{
            networkManager=new NetworkManager();
            networkManager.connect("172.11.49.127", 59);
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối tới server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Tạo CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel);
        
        // Thêm Login Panel đầu tiên
        setLoginPanel();
        setRegisterPanel();
        showLoginPanel();
//        setClientGamePanel();
//        showClientGamePanel();
        this.setVisible(true);
    }
    
    // 🔹 Khởi tạo và thêm LoginPanel
    private void setLoginPanel() {
        loginPanel = new LoginForm(this, networkManager);
        cardPanel.add(loginPanel, LOGIN_VIEW);
    }
    // 🔹 Chuyển sang màn Login
    public void showLoginPanel() {
        cardLayout.show(cardPanel, LOGIN_VIEW);
    }
    
    //Khởi tạo và thêm RegisterPanel
    private void setRegisterPanel() {
        registerPanel = new RegisterForm(this, networkManager);
        cardPanel.add(registerPanel, REGISTER_VIEW);
    }
    public void showRegisterPanel() {
        cardLayout.show(cardPanel, REGISTER_VIEW);
    }

    
    //ham tao ClientMainPanel va them ClientMainOPanel vao Cardlayout
    public void setClientGamePanel(Player p){
        this.player = p;
        clientMainPanel = new ClientMainPanel(player, this, networkManager);
        cardPanel.add(clientMainPanel, CLIENT_MAIN_VIEW);
    }
    public void showClientGamePanel(){
        cardLayout.show(cardPanel, CLIENT_MAIN_VIEW);
    }
    
    //ham tao StartGameRoomPanel
    public void setStartGameRoom(Player p) throws IOException, ClassNotFoundException, Exception{
        try {
            startGameRoomPanel = new StartGameRoomPanel(player, p, this, networkManager);
            cardPanel.add(startGameRoomPanel, GAME_ROOM_VIEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //ham show StartGameRoomPanel
    public void showStartGameRoom(){
        cardLayout.show(cardPanel, GAME_ROOM_VIEW);
    }
    // Getter
    public Player getPlayer() {
        return player;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientMainFrm::new);
    }
}
