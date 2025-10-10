/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author hn235
 */
class Room implements Runnable {
    private Player p1, p2;
    private NetworkManager networkManager1, networkManager2;
    private int score1 = 0, score2 = 0;

    public Room(Player p1, Player p2, NetworkManager networkManager1, NetworkManager networkManager2) {
        this.p1 = p1;
        this.p2 = p2;
        this.networkManager1=networkManager1;
        this.networkManager2=networkManager2;
    }

    @Override
    public void run() {
        // tạo mảng cần sắp xếp
        ArrayList<Integer> arr=new ArrayList<>();
        ArrayList<Integer> arrAfterSort=new ArrayList<>();
        for(int i=0;i<8;i++){
            int phanTu=(int)(Math.random()*100+1);
            arr.add(phanTu);
            arrAfterSort.add(phanTu);
        }
        Collections.sort(arrAfterSort);
        try {
            ObjectSentReceived mangCanSapXep=new ObjectSentReceived("mang can sap xep",arr);
            networkManager1.send(mangCanSapXep);
            networkManager2.send(mangCanSapXep);
            // Kết thúc, giải phóng trạng thái busy
            p1.setBusy(false);
            p2.setBusy(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

