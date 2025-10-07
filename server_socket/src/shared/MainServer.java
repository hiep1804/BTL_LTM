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

public class MainServer {
    private ServerSocket ss;
    private HashMap<String, Player> onlinePlayers = new HashMap<>();

    public MainServer(int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started...");

            while (true) {
                Socket s = ss.accept();
//                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
//                PrintWriter out = new PrintWriter(s.getOutputStream(), true);

                // B1: nhận tên người chơi từ client
                //out.println("Nhập tên:");
                String name = s.getInetAddress().getHostAddress();

                Player p = new Player(name, s);
//                onlinePlayers.put(name, p);
                updateListPlayerOnline(p);
                
                System.out.println(name + " đã online.");
                //out.println("Chào " + name + "! Người chơi online: " + onlinePlayers.keySet());

                // B2: mỗi client chạy thread riêng
                new Thread(() -> handleClient(p)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateListPlayerOnline(Player p) throws IOException{
        for(Player player:onlinePlayers.values()){
            //them 1 player moi online vao danh sach ben client
            ObjectSentReceived objectSentReceived=new ObjectSentReceived("addPlayerOnline", p);
            player.getObjOut().writeObject(objectSentReceived);
            player.getObjOut().flush();
        }
        System.out.println("123");
        //load toan bo player da online vao danh sach ben client
        ObjectSentReceived gui=new ObjectSentReceived("loadPlayerOnline", onlinePlayers);
        p.getObjOut().writeObject(gui);
        p.getObjOut().flush();
        //them player moi online vao danh sach
        onlinePlayers.put(p.getName(), p);
    }
    private void handleClient(Player p) {
        try {
            ObjectSentReceived objectSentReceived;
            while ((objectSentReceived =(ObjectSentReceived) p.getObjIn().readObject()) != null) {
                System.out.println(p.getName() + " gửi: "+objectSentReceived.getType());
                String type=objectSentReceived.getType();
                //gui loi thach dau
                if(type.equals("challenge")){
                    // lay ip cua may duoc thach dau (co the thay bang id sau)
                    String ip=(String)objectSentReceived.getObj();
                    Player opponent = onlinePlayers.get(ip);

                    if (opponent != null && !opponent.isBusy()) {
                        ObjectSentReceived player1GuiThongTinNguoiDuocThachDau=new ObjectSentReceived("want to challenge", p);
                        opponent.getObjOut().writeObject(player1GuiThongTinNguoiDuocThachDau);
                        opponent.getObjOut().flush();
//                        opponent.getOut().println(p.getName() + " muốn thách đấu bạn! Trả lời: accept," + p.getName() + " hoặc reject," + p.getName());
                    } else {
//                        p.getOut().println("Người chơi " + ip + " không khả dụng.");
                    }
                }
                //dong y thach dau
                if(type.equals("accept")){
                    // lay ip cua may thach dau (co the thay bang id sau)
                    String ip=(String) objectSentReceived.getObj();
                    Player p1 = onlinePlayers.get(ip);
                    if (p1 != null) {
//                        p1.getOut().println(p.getName() + " đã chấp nhận! Trận đấu bắt đầu!");
//                        p.getOut().println("Bạn đã chấp nhận thách đấu với " + p1.getName());
                        ObjectSentReceived player1GuiThongTinNguoiDuocThachDau=new ObjectSentReceived("accept challenge", p);
                        p1.getObjOut().writeObject(player1GuiThongTinNguoiDuocThachDau);
                        p1.getObjOut().flush();
                        ObjectSentReceived playerGuiThongTinNguoiThachDau=new ObjectSentReceived("accept challenge", p1);
                        p.getObjOut().writeObject(playerGuiThongTinNguoiThachDau);
                        p.getObjOut().flush();
                        // Cả hai set busy
                        p1.setBusy(true);
                        p.setBusy(true);

                        // Tạo phòng riêng
                        Room room = new Room(p1, p);
                        new Thread(room).start();
                    }
                }
                //tu choi thach dau
                if(type.equals("reject")){
                    // lay ip cua may thach dau (co the thay bang id sau)
                    String ip=(String)objectSentReceived.getObj();
                    Player opponent = onlinePlayers.get(ip);

                    if (opponent != null && !opponent.isBusy()) {
                        ObjectSentReceived player1GuiThongTinNguoiDuocThachDau=new ObjectSentReceived("reject challenge", p);
                        opponent.getObjOut().writeObject(player1GuiThongTinNguoiDuocThachDau);
//                        opponent.getOut().println(p.getName() + " muốn thách đấu bạn! Trả lời: accept," + p.getName() + " hoặc reject," + p.getName());
                    } else {
//                        p.getOut().println("Người chơi " + ip + " không khả dụng.");
                    }
                }
//                String[] parts = msg.split(",", 2);

//                if (parts[0].equals("challenge")) {
//                    String target = parts[1];
//                    Player opponent = onlinePlayers.get(target);
//
//                    if (opponent != null && !opponent.isBusy()) {
//                        opponent.getOut().println(p.getName() + " muốn thách đấu bạn! Trả lời: accept," + p.getName() + " hoặc reject," + p.getName());
//                    } else {
//                        p.getOut().println("Người chơi " + target + " không khả dụng.");
//                    }
//                }

//                if (parts[0].equals("accept")) {
//                    String challenger = parts[1];
//                    Player p1 = onlinePlayers.get(challenger);
//                    if (p1 != null) {
//                        p1.getOut().println(p.getName() + " đã chấp nhận! Trận đấu bắt đầu!");
//                        p.getOut().println("Bạn đã chấp nhận thách đấu với " + p1.getName());
//
//                        // Cả hai set busy
//                        p1.setBusy(true);
//                        p.setBusy(true);
//
//                        // Tạo phòng riêng
//                        Room room = new Room(p1, p);
//                        new Thread(room).start();
//                    }
//                }
//
//                if (parts[0].equals("reject")) {
//                    String challenger = parts[1];
//                    Player p1 = onlinePlayers.get(challenger);
//                    if (p1 != null) {
//                        p1.getOut().println(p.getName() + " từ chối thách đấu.");
//                    }
//                }
            }
        } catch (Exception e) {
            System.out.println("Người chơi " + p.getName() + " đã thoát.");
            onlinePlayers.remove(p.getName());
        }
    }

    public static void main(String[] args) {
        new MainServer(59);
    }
}

