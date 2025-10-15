/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainServer {
    private ServerSocket ss;
    private ConcurrentHashMap<String, Player> onlinePlayers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, NetworkManager> onlinePlayersNetwork=new ConcurrentHashMap<>();
    // Thêm ExecutorService
    private ExecutorService clientExecutor = null;

    public MainServer(int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started...");
            // (2) Khởi tạo: Ví dụ: Tối đa 100 luồng hoặc dùng cached pool
            clientExecutor = Executors.newFixedThreadPool(16); 

            while (true) {
                Socket s = ss.accept();
                String name = s.getInetAddress().getHostAddress();
                NetworkManager networkManager=new NetworkManager();
                networkManager.connect(s);
                Player p = new Player(name);
                
                System.out.println(name + " đã online.");
                // (3) Thay thế việc tạo luồng bằng cách submit tác vụ vào pool
                clientExecutor.execute(() -> handleClient(p,networkManager));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateListPlayerOnline(Player p,NetworkManager networkManager) throws Exception{
        for(String name:onlinePlayers.keySet()){
            //them 1 player moi online vao danh sach ben client
            ObjectSentReceived objectSentReceived=new ObjectSentReceived("addPlayerOnline", p);
            onlinePlayersNetwork.get(name).send(objectSentReceived);
        }
        //load toan bo player da online vao danh sach ben client
        ObjectSentReceived gui=new ObjectSentReceived("loadPlayerOnline", onlinePlayers);
        networkManager.send(gui);
        //them player moi online vao danh sach
        onlinePlayers.put(p.getName(), p);
        onlinePlayersNetwork.put(p.getName(), networkManager);
    }
    private void handleClient(Player p,NetworkManager networkManager){
        try {
            updateListPlayerOnline(p,networkManager);
            ObjectSentReceived objectSentReceived;
            while ((objectSentReceived =networkManager.receive()) != null) {
                System.out.println(p.getName() + " gửi: "+objectSentReceived.getType());
                String type=objectSentReceived.getType();
                //gui loi thach dau
                if(type.equals("challenge")){
                    // lay ip cua may duoc thach dau (co the thay bang id sau)
                    String ip=(String)objectSentReceived.getObj();
                    Player opponent = onlinePlayers.get(ip);
                    //socket của player nhận được
                    NetworkManager networkManager1=onlinePlayersNetwork.get(ip);

                    if (opponent != null && !opponent.isBusy()) {
                        ObjectSentReceived player1GuiThongTinNguoiDuocThachDau=new ObjectSentReceived("want to challenge", p);
                        networkManager1.send(player1GuiThongTinNguoiDuocThachDau);
                    } else {
                    }
                }
                //dong y thach dau
                if(type.equals("accept")){
                    // lay ip cua may thach dau (co the thay bang id sau)
                    String ip=(String) objectSentReceived.getObj();
                    Player p1 = onlinePlayers.get(ip);
                    //socket của player nhận được
                    NetworkManager networkManager1=onlinePlayersNetwork.get(ip);
                    if (p1 != null) {
                        ObjectSentReceived player1GuiThongTinNguoiDuocThachDau=new ObjectSentReceived("accept challenge", p);
                        networkManager1.send(player1GuiThongTinNguoiDuocThachDau);
                        ObjectSentReceived playerGuiThongTinNguoiThachDau=new ObjectSentReceived("accept challenge", p1);
                        networkManager.send(playerGuiThongTinNguoiThachDau);
                        // Cả hai set busy
                        //p1.setBusy(true);
                        p.setBusy(true);

                        // Tạo phòng riêng
                        Room room = new Room(p1, p, networkManager1, networkManager);
                        new Thread(room).start();
                    }
                }
                //tu choi thach dau
                if(type.equals("reject")){
                    // lay ip cua may thach dau (co the thay bang id sau)
                    String ip=(String)objectSentReceived.getObj();
                    Player opponent = onlinePlayers.get(ip);
                    NetworkManager networkManager1=onlinePlayersNetwork.get(ip);

                    if (opponent != null && !opponent.isBusy()) {
                        ObjectSentReceived player1GuiThongTinNguoiDuocThachDau=new ObjectSentReceived("reject challenge", p);
                        networkManager1.send(player1GuiThongTinNguoiDuocThachDau);
                    } else {
                        
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Người chơi " + p.getName() + " đã thoát.");
            onlinePlayers.remove(p.getName());
            xoa();
        }
    }
    private void xoa(){
//        for(Player player:onlinePlayers.values()){
//            //them 1 player moi online vao danh sach ben client
//            HashMap<String,Player> mp=new HashMap<>();
//            for(String key:onlinePlayers.keySet()){
//                mp.put(key, onlinePlayers.get(key));
//            }
//            ObjectSentReceived objectSentReceived=new ObjectSentReceived("loadPlayerOnline", mp);
//            try {
//                player.getObjOut().writeObject(objectSentReceived);
//                player.getObjOut().flush();
//            } catch (IOException ex) {
//                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    public static void main(String[] args) {
        new MainServer(59);
    }
}

