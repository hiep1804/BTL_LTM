package shared;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkManager {
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    
    public void connect(Socket socket) throws Exception {
        this.socket = socket;
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
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