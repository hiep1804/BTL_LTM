/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author hn235
 */
public class Player implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name,password,username;
    private int player_id, total_score,total_wins, matches_played;
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
    public Player(int player_id, String name,String username, String password, int total_score, int total_wins, int matches_played) {
        this.player_id = player_id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.total_score = total_score;
        this.total_wins = total_wins;
        this.matches_played = matches_played;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getTotal_score() {
        return total_score;
    }
    public void setTotal_score(int total_score) {
        this.total_score = total_score;
    }
    public int getTotal_wins() {
        return total_wins;
    }
    public void setTotal_wins(int total_wins) {
        this.total_wins = total_wins;
    }
    public int getMatches_played() {
        return matches_played;
    }
    public void setMatches_played(int matches_played) {
        this.matches_played = matches_played;
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
