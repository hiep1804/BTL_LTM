package shared;

import java.awt.CardLayout;
import java.io.IOException;
import java.util.HashMap;
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
    private JPanel loginPanel;
    private JPanel registerPanel;
    private ClientMainPanel clientMainPanel;
    private StartGameRoomPanel startGameRoomPanel;
    private MatchResultPanel matchResultPanel;

    public static final String LOGIN_VIEW = "login_view";
    public static final String REGISTER_VIEW = "register_view";
    public static final String CLIENT_MAIN_VIEW = "client_main_view";
    public static final String GAME_ROOM_VIEW = "game_room_view";
    public static final String MATCH_RESULT_VIEW = "match_result_view";
    
    
    public ClientMainFrm(){
        try{
            networkManager=new NetworkManager();
            networkManager.connect("192.168.1.7", 59);
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối tới máy chủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
    
    // 🔹 Khởi tạo và thêm LoginPanel (Modern UI)
    private void setLoginPanel() {
        loginPanel = new LoginForm(this, networkManager);
        cardPanel.add(loginPanel, LOGIN_VIEW);
    }
    // 🔹 Chuyển sang màn Login
    public void showLoginPanel() {
        cardLayout.show(cardPanel, LOGIN_VIEW);
    }
    
    //Khởi tạo và thêm RegisterPanel (Modern UI)
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
    public void setStartGameRoom(Player opponent, NetworkManager networkManager) throws IOException, ClassNotFoundException, Exception{
        try {
            startGameRoomPanel = new StartGameRoomPanel(player, opponent, this, networkManager);
            cardPanel.add(startGameRoomPanel, GAME_ROOM_VIEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //ham show StartGameRoomPanel
    public void showStartGameRoom(){
        cardLayout.show(cardPanel, GAME_ROOM_VIEW);
    }
    


    public void forwardScoreUpdate(ScoreUpdate scoreUpdate) {
        if (startGameRoomPanel != null) {
            startGameRoomPanel.updateScores(scoreUpdate);
        } else {
            System.out.println("[ClientMainFrm] CẢNH BÁO: chưa có StartGameRoomPanel để cập nhật điểm!");
        }
    }
    
    public void forwardRoundInfo(RoundInfo roundInfo) {
        if (startGameRoomPanel != null) {
            System.out.println("[ClientMainFrm] Chuyển tiếp thông tin ván đấu cho StartGameRoomPanel");
            startGameRoomPanel.startNewRound(roundInfo);
        } else {
            System.out.println("[ClientMainFrm] CẢNH BÁO: startGameRoomPanel là null!");
        }
    }
    
    public void forwardRoundEnd(ScoreUpdate scoreUpdate) {
        if (startGameRoomPanel != null) {
            startGameRoomPanel.handleRoundEnd(scoreUpdate);
        } else {
            System.out.println("[ClientMainFrm] CẢNH BÁO: startGameRoomPanel là null!");
        }
    }
    
    public void forwardGameOver(ScoreUpdate finalScore) {
        if (startGameRoomPanel != null) {
            startGameRoomPanel.handleGameOver(finalScore);
        } else {
            System.out.println("[ClientMainFrm] CẢNH BÁO: startGameRoomPanel là null!");
        }
    }
    
    // Phương thức để thông báo đối thủ thoát cho StartGameRoomPanel
    public void notifyOpponentLeft() {
        if (startGameRoomPanel != null) {
            System.out.println("[ClientMainFrm] Thông báo đối thủ thoát cho StartGameRoomPanel");
            startGameRoomPanel.handleOpponentLeft();
        } else {
            System.out.println("[ClientMainFrm] CẢNH BÁO: startGameRoomPanel là null!");
        }
    }
    
    // Xóa game room panel
    public void removeGameRoomPanel() {
        if (startGameRoomPanel != null) {
            cardPanel.remove(startGameRoomPanel);
            startGameRoomPanel = null;
            System.out.println("[ClientMainFrm] Đã xóa StartGameRoomPanel");
        }
    }
    
    // Xóa match result panel
    public void removeMatchResultPanel() {
        if (matchResultPanel != null) {
            cardPanel.remove(matchResultPanel);
            matchResultPanel = null;
            System.out.println("[ClientMainFrm] Đã xóa MatchResultPanel");
        }
    }
    
    // Hiển thị màn hình kết quả trận đấu
    public void showMatchResult(Player winner, Player loser, int winnerScore, int loserScore) {
        matchResultPanel = new MatchResultPanel(winner, loser, winnerScore, loserScore);
        cardPanel.add(matchResultPanel, MATCH_RESULT_VIEW);
        
        // Xử lý sự kiện nút "Rematch"
        matchResultPanel.setRematchAction(e -> {
            JOptionPane.showMessageDialog(this, "Tính năng rematch đang được phát triển!");
        });

        // Xử lý sự kiện nút "Quay về trang chủ"
        matchResultPanel.setBackToHomeAction(e -> {
            // Đánh dấu người chơi không còn bận
            player.setBusy(false);
            
            // Yêu cầu refresh thông tin từ server
            try {
                networkManager.send(new ObjectSentReceived("refreshPlayerInfo", null));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            // Xóa các panel game
            removeGameRoomPanel();
            removeMatchResultPanel();
            
            // Quay về ClientMainPanel (không reload, chỉ show lại)
            cardLayout.show(cardPanel, CLIENT_MAIN_VIEW);
        });

        cardLayout.show(cardPanel, MATCH_RESULT_VIEW);
    }
    
    // Reload ClientMainPanel (quay về lobby và refresh)
    public void reloadClientMainPanel() {
        try {
            // Xóa ClientMainPanel cũ nếu có
            HashMap<String, Player> oldOnlinePlayers = this.clientMainPanel.getPlayers();
            if (clientMainPanel != null) {
                clientMainPanel.stopListening(); // Dừng listener cũ
                cardPanel.remove(clientMainPanel);
                System.out.println("[ClientMainFrm] Đã xóa ClientMainPanel cũ");
            }
            
            // Tạo mới ClientMainPanel
            clientMainPanel = new ClientMainPanel(player, this, networkManager);
            clientMainPanel.setPlayers(oldOnlinePlayers);
            cardPanel.add(clientMainPanel, CLIENT_MAIN_VIEW);
            
            // Hiển thị ClientMainPanel
            cardLayout.show(cardPanel, CLIENT_MAIN_VIEW);
            System.out.println("[ClientMainFrm] Đã reload và hiển thị ClientMainPanel mới");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi quay lại sảnh: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
        }
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
