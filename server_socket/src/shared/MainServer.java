/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    private ServerSocket ss;
    private ConcurrentHashMap<String, Player> onlinePlayers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, NetworkManager> onlinePlayersNetwork=new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> opponentMap = new ConcurrentHashMap<>(); // username -> opponent username
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
                String username = s.getInetAddress().getHostAddress();
                System.out.println("Client " + username + " connected the server");
                NetworkManager networkManager=new NetworkManager();
                networkManager.connect(s);
                clientExecutor.execute(new ClientHandler(networkManager, onlinePlayers, onlinePlayersNetwork, opponentMap));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MainServer(59);
    }
}

