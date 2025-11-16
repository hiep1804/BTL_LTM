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
    private final ConcurrentHashMap<String, String> opponentMap; // username -> opponent username
    private final ConcurrentHashMap<String, Room> roomMap;
    private final PlayerService playerService = new PlayerService();
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();
    
    public ClientHandler(NetworkManager networkManager, 
            ConcurrentHashMap<String, Player> onlinePlayers, 
            ConcurrentHashMap<String, NetworkManager> onlinePlayersNetwork,
            ConcurrentHashMap<String, String> opponentMap,
            ConcurrentHashMap<String, Room> roomMap) {
        this.networkManager = networkManager;
        this.onlinePlayers = onlinePlayers;
        this.onlinePlayersNetwork = onlinePlayersNetwork;
        this.opponentMap = opponentMap;
        this.roomMap = roomMap;
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
                    case "refreshPlayerInfo" -> handleRefreshPlayerInfo(message);
                    case "challenge" -> handleChallenge(message);
                    case "accept" -> handleAccept(message);
                    case "reject" -> handleReject(message);
                    case "thoat game" -> handleExitGame(message);
                    case "submit_array" -> handleSubmitArray(message);
                    case "request_rematch" -> handleRequestRematch(message);
                    case "accept_rematch" -> handleAcceptRematch(message);
                    case "reject_rematch" -> handleRejectRematch(message);
                    case "back_to_lobby" -> handleBackToLobby(message);
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
    
    private void handleRefreshPlayerInfo(ObjectSentReceived req) {
        try {
            // Lấy thông tin mới nhất từ database
            Player updatedPlayer = playerService.getPlayer(player.getUsername());
            if (updatedPlayer != null) {
                // Cập nhật thông tin trong bộ nhớ
                player.setTotalScore(updatedPlayer.getTotalScore());
                player.setTotalWins(updatedPlayer.getTotalWins());
                player.setMatchesPlayed(updatedPlayer.getMatchesPlayed());
                
                // Cập nhật trong map
                onlinePlayers.put(player.getUsername(), player);
                
                // Gửi thông tin mới về client
                networkManager.send(new ObjectSentReceived("refreshPlayerInfo", player));
                System.out.println("[ClientHandler] Đã refresh thông tin cho " + player.getUsername());
            }
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
        NetworkManager challengerNM = onlinePlayersNetwork.get(challengerName);  
        
        //Kiểm tra xem thằng challenger có bận hay không? Bận thì hiện lên thông báo đối thủ đã vào trận. 
        //Sau khi thoát ra, nhớ broadcast - cập nhật mảng onlinePlayerList
        if(challenger.isBusy()) {
            networkManager.send(new ObjectSentReceived("challenger_busy", "Người thách đấu đã vào trận với người khác."));
            broadcastFullPlayerList();  //gửi yêu cầu cập nhật danh sách onlinePlayer
            
            return;
        }
        
        if(challenger != null) {
            // Đánh dấu cả 2 đang bận
            player.setBusy(true);
            challenger.setBusy(true);
            
            //Gửi thông báo cập nhật list cho TẤT CẢ mọi người
            broadcastFullPlayerList();
            
            // Lưu thông tin đối thủ vào map (2 chiều)
            opponentMap.put(player.getUsername(), challengerName);
            opponentMap.put(challengerName, player.getUsername());

            // Báo cho người thách đấu biết đối thủ đã chấp nhận và gửi thông tin phòng
            ObjectSentReceived msgToChallenger = new ObjectSentReceived("start_game", player);
            challengerNM.send(msgToChallenger);
            
            // Báo cho người được thách đấu biết để bắt đầu và gửi thông tin phòng
            ObjectSentReceived msgToAcceptor = new ObjectSentReceived("start_game", challenger);
            networkManager.send(msgToAcceptor);
            
            Room room = new Room(challenger, player, challengerNM, networkManager);
            roomMap.put(player.getUsername(), room);
            roomMap.put(challengerName, room);
            
            // Tạo thread cho Room và theo dõi khi kết thúc
            Thread roomThread = new Thread(() -> {
                room.run();
                
                // Sau khi Room kết thúc, broadcast lại danh sách để cập nhật trạng thái
                System.out.println("[ClientHandler] Room kết thúc, broadcasting player list...");
                broadcastFullPlayerList();
                
                // Xóa room khỏi map
                roomMap.remove(player.getUsername());
                roomMap.remove(challengerName);
                
                // KHÔNG xóa opponentMap ở đây để cho phép rematch
                // opponentMap sẽ được xóa khi:
                // - Người chơi quay về lobby (handleExitGame hoặc nút "Quay về trang chủ")
                // - Rematch được chấp nhận (sẽ update lại opponentMap với cùng 2 người)
                System.out.println("[ClientHandler] Giữ lại opponent mapping để cho phép rematch");
            });
            roomThread.start();
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
    
    private void handleRequestRematch(ObjectSentReceived req) throws Exception {
        // Lấy thông tin đối thủ từ opponentMap
        String opponentName = opponentMap.get(player.getUsername());
        if (opponentName == null) {
            networkManager.send(new ObjectSentReceived("rematch_error", "Không tìm thấy đối thủ."));
            return;
        }
        
        Player opponent = onlinePlayers.get(opponentName);
        NetworkManager opponentNM = onlinePlayersNetwork.get(opponentName);
        
        if (opponent != null && opponentNM != null) {
            // Gửi yêu cầu rematch cho đối thủ
            ObjectSentReceived msg = new ObjectSentReceived("want_rematch", player);
            opponentNM.send(msg);
            System.out.println("[ClientHandler] " + player.getUsername() + " yêu cầu rematch với " + opponentName);
        }
    }
    
    private void handleAcceptRematch(ObjectSentReceived req) throws Exception {
        String opponentName = (String) req.getObj();
        Player opponent = onlinePlayers.get(opponentName);
        NetworkManager opponentNM = onlinePlayersNetwork.get(opponentName);
        
        if (opponent == null || opponentNM == null) {
            networkManager.send(new ObjectSentReceived("rematch_error", "Đối thủ không còn trực tuyến."));
            return;
        }
        
        // Đánh dấu cả 2 đang bận
        player.setBusy(true);
        opponent.setBusy(true);
        
        // Broadcast cập nhật danh sách
        broadcastFullPlayerList();
        
        // Cập nhật opponent map
        opponentMap.put(player.getUsername(), opponentName);
        opponentMap.put(opponentName, player.getUsername());
        
        // Gửi thông báo start_game cho cả 2
        ObjectSentReceived msgToOpponent = new ObjectSentReceived("start_game", player);
        opponentNM.send(msgToOpponent);
        
        ObjectSentReceived msgToPlayer = new ObjectSentReceived("start_game", opponent);
        networkManager.send(msgToPlayer);
        
        // Tạo Room mới
        Room room = new Room(opponent, player, opponentNM, networkManager);
        roomMap.put(player.getUsername(), room);
        roomMap.put(opponentName, room);
        
        // Chạy Room trong thread riêng
        Thread roomThread = new Thread(() -> {
            room.run();
            
            System.out.println("[ClientHandler] Rematch room kết thúc, broadcasting player list...");
            broadcastFullPlayerList();
            
            // Cleanup - chỉ xóa room, giữ lại opponentMap để có thể rematch tiếp
            roomMap.remove(player.getUsername());
            roomMap.remove(opponentName);
            // Không xóa opponentMap để cho phép rematch nhiều lần
        });
        roomThread.start();
        
        System.out.println("[ClientHandler] Rematch started: " + opponentName + " vs " + player.getUsername());
    }
    
    private void handleRejectRematch(ObjectSentReceived req) throws Exception {
        String opponentName = (String) req.getObj();
        NetworkManager opponentNM = onlinePlayersNetwork.get(opponentName);
        
        if (opponentNM != null) {
            ObjectSentReceived msg = new ObjectSentReceived("rematch_rejected", null);
            opponentNM.send(msg);
            System.out.println("[ClientHandler] " + player.getUsername() + " từ chối rematch với " + opponentName);
        }
    }
    
    private void handleBackToLobby(ObjectSentReceived req) throws Exception {
        System.out.println("[ClientHandler] " + player.getUsername() + " quay về lobby");
        
        // Xóa opponent mapping khi quay về lobby
        String opponentName = opponentMap.remove(player.getUsername());
        if (opponentName != null) {
            opponentMap.remove(opponentName);
            System.out.println("[ClientHandler] Đã xóa opponent mapping: " + player.getUsername() + " <-> " + opponentName);
        }
        
        // Broadcast lại danh sách người chơi
        broadcastFullPlayerList();
    }
    
    private void handleExitGame(ObjectSentReceived req) throws Exception {
        System.out.println(player.getUsername() + " thoát game");
        // Đánh dấu không còn bận
        player.setBusy(false);

        // Lấy thông tin đối thủ từ map
        String opponentName = opponentMap.get(player.getUsername());
        if (opponentName != null) {
            NetworkManager opponentNM = onlinePlayersNetwork.get(opponentName);
            if (opponentNM != null) {
                System.out.println("[ClientHandler] Thông báo cho " + opponentName + " rằng đối thủ đã thoát");
                opponentNM.send(new ObjectSentReceived("doi thu thoat", null));

                // Đánh dấu đối thủ cũng không còn bận
                Player opponent = onlinePlayers.get(opponentName);
                if (opponent != null) {
                    opponent.setBusy(false);
                }
            }

            // Xóa opponent mapping khi người chơi thoát về lobby
            System.out.println("[ClientHandler] Xóa opponent mapping khi thoát game");
            // Xóa cả 2 chiều khỏi map
            opponentMap.remove(player.getUsername());
            opponentMap.remove(opponentName);

            Room room = roomMap.remove(player.getUsername());
            if (room != null) {
                roomMap.remove(opponentName);
                room.endGame();
            }
        } else {
            opponentMap.remove(player.getUsername());
            Room room = roomMap.remove(player.getUsername());
            if (room != null) {
                room.endGame();
            }
        }
        
        // Gửi thông báo cập nhật list cho TẤT CẢ mọi người
        broadcastFullPlayerList();
    }

    private void handleSubmitArray(ObjectSentReceived message) {
        try {
            Room room = roomMap.get(player.getUsername());
            if (room == null) {
                System.out.println("[ClientHandler] Không tìm thấy phòng cho " + player.getUsername());
                return;
            }

            @SuppressWarnings("unchecked")
            ArrayList<Integer> submission = (ArrayList<Integer>) message.getObj();
            ScoreUpdate update = room.handleSubmission(player.getUsername(), submission);
            if (update == null) {
                System.out.println("[ClientHandler] Không thể xử lý bài nộp cho " + player.getUsername());
                return;
            }

            networkManager.send(new ObjectSentReceived("update_score", update));

            String opponentName = opponentMap.get(player.getUsername());
            if (opponentName != null) {
                NetworkManager opponentNM = onlinePlayersNetwork.get(opponentName);
                if (opponentNM != null) {
                    opponentNM.send(new ObjectSentReceived("update_score", update));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    
    /**
     * Gửi (broadcast) toàn bộ danh sách người chơi online 
     * (với trạng thái busy/free) đến TẤT CẢ các client đang kết nối.
     * sử dụng tín hiệu "loadPlayerOnline"
     */
    private void broadcastFullPlayerList() {
        try {
            System.out.println("[Server] Broadcasting updated playerList to all clients...");
            
            // Tạo 1 gói tin CHUNG chứa TOÀN BỘ danh sách onlinePlayers HIỆN TẠI
            ObjectSentReceived msg = new ObjectSentReceived("loadPlayerOnline", onlinePlayers);
            
            // Gửi gói tin này cho TẤT CẢ network managers đang online
            for (NetworkManager nm : onlinePlayersNetwork.values()) {
                nm.send(msg);
            }
        } catch (Exception e) {
            System.out.println("Error broadcasting full playerList: " + e.getMessage());
        }
    }
    
    private void handleDisconnect() {
        if (player == null) return;
        onlinePlayers.remove(player.getUsername());
        onlinePlayersNetwork.remove(player.getUsername());
        System.out.println("Player disconnected: " + player.getUsername());
        player.setBusy(false);

        String opponentName = opponentMap.remove(player.getUsername());
        if (opponentName != null) {
            opponentMap.remove(opponentName);

            NetworkManager opponentNM = onlinePlayersNetwork.get(opponentName);
            if (opponentNM != null) {
                try {
                    opponentNM.send(new ObjectSentReceived("doi thu thoat", null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Player opponent = onlinePlayers.get(opponentName);
            if (opponent != null) {
                opponent.setBusy(false);
            }

            Room room = roomMap.remove(player.getUsername());
            if (room != null) {
                roomMap.remove(opponentName);
                room.endGame();
            }
        } else {
            Room room = roomMap.remove(player.getUsername());
            if (room != null) {
                room.endGame();
            }
        }
        
        // Gửi thông báo cập nhật list (đã xóa player) cho TẤT CẢ mọi người
        broadcastFullPlayerList();
    }
}
