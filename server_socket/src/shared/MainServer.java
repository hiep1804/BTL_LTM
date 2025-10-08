package shared;
import java.net.*;
import shared.services.RegisterService;

public class MainServer {
    private final int port;
    private final RegisterService registerService = new RegisterService();
    
    public MainServer(int port) {
        this.port = port;
    }
    
    public void start() {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Server started on port: " + port);
            System.out.println("Server IP: " + java.net.InetAddress.getLocalHost().getHostAddress());
            while (true) {
                Socket s = ss.accept();     //Mỗi client sẽ có 1 luồng socket riêng với server để giao tiếp
                System.out.println("Client connected: " + s.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(s, registerService)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new MainServer(59).start();
    }
}

