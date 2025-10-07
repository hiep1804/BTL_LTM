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
    public ClientMainFrm(){
        try{
            Socket socket=new Socket("192.168.1.13",59);
            player=new Player(socket.getInetAddress().getHostAddress(), socket);
            //Gui thong tin player moi
//            ObjectSentReceived gui=new ObjectSentReceived("addPlayerOnline",player);
//            player.getObjOut().writeObject(gui);
//            player.getObjOut().flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        ClientMainPanel panel=new ClientMainPanel(player,this);
        panel.setBounds(0, 0, 800, 600);
        cardPanel.add(panel,"client main frame");
        cardLayout.show(cardPanel, "client main frame");
        add(cardPanel);
        //this.pack();
        this.setVisible(true);
    }
    //ham tao StartGameRoomPanel
    public void setStartGameRoom(Player p) throws IOException, ClassNotFoundException{
        StartGameRoomPanel startGameRoomPanel=new StartGameRoomPanel(player, p,this);
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
