package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import shared.ObjectSentReceived;
import shared.ObjectSentReceived;

public class NetworkManager {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    
    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objIn = new ObjectInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    public void send(ObjectSentReceived msg) throws Exception {
        objOut.writeObject(msg);
        objOut.flush();
    }
    
    public ObjectSentReceived receive() throws Exception {
        Object obj = objIn.readObject();
        return (ObjectSentReceived) obj;
    }
    
    public void close() {
        try {
            if(objOut != null) objOut.close();
        } catch (Exception ignored) {}
        try {
            if(objIn != null) objIn.close();
        } catch (Exception ignored) {}
        try {
            if(socket != null) socket.close();
        } catch (Exception ignored) {}
    }
}