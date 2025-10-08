/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import shared.ObjectSentReceived;
import shared.model.Player;
import shared.services.RegisterService;

public class ClientHandler implements Runnable{ 
    private final Socket socket;
    private final RegisterService registerService;
    
    public ClientHandler(Socket socket, RegisterService registerService) {
        this.socket = socket;
        this.registerService = registerService;
    }
    
    @Override
    public void run() {
        try (ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream())) {
            
            while(true) {
                Object obj = objIn.readObject();
                if(!(obj instanceof ObjectSentReceived)) continue;
                
                ObjectSentReceived request = (ObjectSentReceived) obj;
                String type = request.getType();
                
                if("Register".equals(type)) {
                    Player newPlayer = (Player) request.getObj();
                    boolean ok = registerService.register(newPlayer);
                    
                    // Trả về đúng format yêu cầu
                    ObjectSentReceived response = new ObjectSentReceived("Register", ok);
                    objOut.writeObject(response);
                    objOut.flush();
                } else {
                    // Có thể mở rộng các type khác
                    ObjectSentReceived response =
                            new ObjectSentReceived("Error", "Unsupported type: " + type);
                    objOut.writeObject(response);
                    objOut.flush();

                }
            }
        } catch (Exception e) {
            // Client disconnect hoặc lỗi IO
            e.printStackTrace();
        }
    }
}
