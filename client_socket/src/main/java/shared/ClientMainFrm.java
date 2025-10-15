/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.awt.CardLayout;
import java.awt.HeadlessException;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author hn235
 */
public class ClientMainFrm extends JFrame{
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Player player = null;
    private NetworkManager networkManager;
    public ClientMainFrm(){
        try{
            networkManager=new NetworkManager();
            networkManager.connect("192.168.1.30", 59);
            player=new Player(networkManager.getSocket().getInetAddress().getHostAddress());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel);
        setClientGamePanel();
        showClientGamePanel();
        this.setVisible(true);
    }
    //ham tao ClientMainPanel va them ClientMainOPanel vao Cardlayout
    public void setClientGamePanel(){
        ClientMainPanel panel=new ClientMainPanel(player,this,networkManager);
        panel.setBounds(0, 0, 800, 600);
        cardPanel.add(panel,"client main frame");
    }
    public void showClientGamePanel(){
        cardLayout.show(cardPanel, "client main frame");
    }
    //ham tao StartGameRoomPanel
    public void setStartGameRoom(Player p) throws IOException, ClassNotFoundException, Exception{
        StartGameRoomPanel startGameRoomPanel=new StartGameRoomPanel(player, p,this,networkManager);
        cardPanel.add(startGameRoomPanel,"client game panel");
    }
    //ham show StartGameRoomPanel
    public void showStartGameRoom(){
        cardLayout.show(cardPanel, "client game panel");
    }
    public static void main(String[] args) {
        new ClientMainFrm();
    }
}
