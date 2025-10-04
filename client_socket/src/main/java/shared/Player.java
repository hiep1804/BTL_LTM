/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author hn235
 */
class Player implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private transient Socket socket;
    private transient PrintWriter out;
    private transient BufferedReader in;
    private transient DataOutputStream dataOut;
    private transient DataInputStream dataIn;
    private transient ObjectOutputStream objOut;
    private transient ObjectInputStream objIn;
    private boolean busy = false;

    public Player(String name, Socket socket) throws Exception {
        this.name = name;
        this.socket = socket;
//        this.out = new PrintWriter(socket.getOutputStream(), true);
//        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Trên server, trong Player:
        this.objOut = new ObjectOutputStream(socket.getOutputStream()); // tạo trước
        this.objOut.flush(); // flush để header được gửi
        this.objIn = new ObjectInputStream(socket.getInputStream());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public ObjectOutputStream getObjOut() {
        return objOut;
    }

    public void setObjOut(ObjectOutputStream objOut) {
        this.objOut = objOut;
    }

    public ObjectInputStream getObjIn() {
        return objIn;
    }

    public void setObjIn(ObjectInputStream objIn) {
        this.objIn = objIn;
    }
    
}
