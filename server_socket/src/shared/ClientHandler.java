/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import shared.services.LoginService;
import shared.services.PlayerService;
import shared.services.RegisterService;

/**
 *
 * @author lehuy
 */
public class ClientHandler implements Runnable{
    private Player player;
    private final NetworkManager networkManager;
    private final ConcurrentHashMap<String, Player> onlinePlayers;
    private final ConcurrentHashMap<String, NetworkManager> onlinePlayersNetwork;
    private final PlayerService playerService = new PlayerService();
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();
    
    public ClientHandler(NetworkManager networkManager, 
            ConcurrentHashMap<String, Player> onlinePlayers, 
            ConcurrentHashMap<String, NetworkManager> onlinePlayersNetwork) {
        this.networkManager = networkManager;
        this.onlinePlayers = onlinePlayers;
        this.onlinePlayersNetwork = onlinePlayersNetwork;
    }
    
    @Override
    public void run() {
        try {
            //Giai đoạn đầu tiên: Authentication - khi player chưa được gán 
            while (player == null) {
                ObjectSentReceived req = networkManager.receive();
                if(req == null)     break;  //Client ngắt kết nối
                
                String type = req.getType();
                
                if("Login".equals(type)) {
                    handleLogin(req);
                } else if ("Register".equals(type)) {
                    handleRegister(req);
                } else {
                    System.out.println("Invalid authentication request: " + type);
                }
            }
            
            // Nếu sau vòng lặp Authentication mà player vẫn bằng null
            if(player == null) {
                System.out.println("Client " + networkManager.getSocket().getInetAddress().getHostAddress() +  " disconnected before successful login.");
                return;
            }
            
            //Giai đoạn phiên - chỉ khi player đã được gán
            ObjectSentReceived message;
            while((message = networkManager.receive()) != null) {
                String msgType = message.getType();
                System.out.println(player.getUsername() + " send " + msgType);
                
                switch (msgType) {
                    case "getLeaderboard" -> handleGetLeaderboard(message);
                    case "challenge" -> handleChallenge(message);
                    case "accept" -> handleAccept(message);
                    case "reject" -> handleReject(message);
                    default -> System.out.println("Unknown message type: " + msgType);
                }
            }
        } catch (Exception e) {
            System.out.println("Người chơi " + player.getUsername() + " đã thoát.");
            e.printStackTrace();
        } finally {
            handleDisconnect();
        }
    }
    
    // ===============  HANDLERS FOR DIFFERENT MESSAGES  ===========
    private void handleLogin(ObjectSentReceived req) {
        try {
            Player p = (Player) req.getObj();
            boolean status = loginService.login(p);
            
            // Gửi response
            networkManager.send(new ObjectSentReceived("Login", status));
            
            if(!status) {
                System.out.println("Login failed for user: " + p.getUsername());
                return;
            }
            
            //Login thành công
            this.player = p;
            System.out.println("User logged in: " + player.getUsername());
            
            updateListPlayerOnline(p, networkManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleGetLeaderboard(ObjectSentReceived req) {
        try {
            ArrayList<Player> leaderboard = playerService.getLeaderBoard();
            networkManager.send(new ObjectSentReceived("getLeaderboard", leaderboard));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleRegister(ObjectSentReceived req) {
        try {
            Player p = (Player) req.getObj();
            boolean status = registerService.register(p);
            
            //Gửi response
            networkManager.send(new ObjectSentReceived("Register", status));
            
            System.out.println(status);
            
            if(!status) {
                System.out.println("Register failed: " + p.getUsername());
                return;
            }
            
            //Register thành công
            System.out.println("Insert new player: " + player.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void handleChallenge(ObjectSentReceived req) throws Exception {
        String opponentName = (String) req.getObj();    //Tên đối thủ
        Player opponent = onlinePlayers.get(opponentName);
        NetworkManager opponentNM = onlinePlayersNetwork.get(opponentName);
        
        if(opponent != null && !opponent.isBusy()) {
            ObjectSentReceived msg = new ObjectSentReceived("want to challenge", player);
            opponentNM.send(msg);
        }
    }
    
    private void handleAccept(ObjectSentReceived req) throws Exception {
        String challengerName = (String) req.getObj();
        Player challenger = onlinePlayers.get(challengerName);
        NetworkManager challengerNM = onlinePlayersNetwork.get(challengerName);     //dang null
        
        if(challenger != null) {
            // Đánh dấu cả 2 đang bận
            player.setBusy(true);
            challenger.setBusy(true);

            // Báo cho người thách đấu biết đối thủ đã chấp nhận và gửi thông tin phòng
            ObjectSentReceived msgToChallenger = new ObjectSentReceived("start_game", player);
            challengerNM.send(msgToChallenger);
            
            // Báo cho người được thách đấu biết để bắt đầu và gửi thông tin phòng
            ObjectSentReceived msgToAcceptor = new ObjectSentReceived("start_game", challenger);
            networkManager.send(msgToAcceptor);
            
            Room room = new Room(challenger, player, challengerNM, networkManager);
            new Thread(room).start();
        }
    }
    
    private void handleReject(ObjectSentReceived req) throws Exception {
        String opponentName = (String) req.getObj();
        Player opponent = onlinePlayers.get(opponentName);
        NetworkManager opponentNM = onlinePlayersNetwork.get(opponentName);

        if (opponent != null && !opponent.isBusy()) {
            ObjectSentReceived msg = new ObjectSentReceived("reject challenge", player);
            opponentNM.send(msg);
        }
    }
    
    // =================  HELPER FUNCTIONS  ========================
    private void updateListPlayerOnline(Player p, NetworkManager networkManager) throws Exception {
        
        // Gửi thông báo thêm player mới đến tất cả client đang online
        for (String name : onlinePlayers.keySet()) {
            ObjectSentReceived notifyOthers = new ObjectSentReceived("addPlayerOnline", p);
            onlinePlayersNetwork.get(name).send(notifyOthers);
        }

        // Gửi danh sách player hiện tại cho client mới
        ObjectSentReceived sendAll = new ObjectSentReceived("loadPlayerOnline", onlinePlayers);
        networkManager.send(sendAll);

        // Thêm player mới vào map server
        onlinePlayers.put(p.getUsername(), p);
        onlinePlayersNetwork.put(p.getUsername(), networkManager);
    }
    
    private void handleDisconnect() {
        if (player == null) return;
        onlinePlayers.remove(player.getUsername());
        onlinePlayersNetwork.remove(player.getUsername());
        System.out.println("🟥 Player disconnected: " + player.getUsername());
    }

    
    private void removeFromList() {
        // Code cũ bị comment — giữ nguyên logic, không gửi lại danh sách.
        // Bạn có thể bật lại nếu muốn cập nhật danh sách sau khi player rời đi.
        /*
        for (Player player : onlinePlayers.values()) {
            HashMap<String, Player> mp = new HashMap<>();
            for (String key : onlinePlayers.keySet()) {
                mp.put(key, onlinePlayers.get(key));
            }
            ObjectSentReceived objectSentReceived = new ObjectSentReceived("loadPlayerOnline", mp);
            try {
                player.getObjOut().writeObject(objectSentReceived);
                player.getObjOut().flush();
            } catch (IOException ex) {
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        */
    }
}
