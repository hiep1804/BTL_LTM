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
        System.out.println("[Room] Phòng game bắt đầu cho " + p1.getUsername() + " vs " + p2.getUsername());
        // tạo mảng cần sắp xếp
        ArrayList<Integer> arr=new ArrayList<>();
        ArrayList<Integer> arrAfterSort=new ArrayList<>();
        for(int i=0;i<8;i++){
            int phanTu=(int)(Math.random()*100+1);
            arr.add(phanTu);
            arrAfterSort.add(phanTu);
        }
        Collections.sort(arrAfterSort);
        System.out.println("[Room] Mảng tạo ra: " + arr);
        
        try {
            // Đợi một chút để đảm bảo client đã sẵn sàng
            Thread.sleep(500);
            
            ObjectSentReceived mangCanSapXep=new ObjectSentReceived("mang can sap xep",arr);
            System.out.println("[Room] Gửi mảng cho player 1: " + p1.getUsername());
            networkManager1.send(mangCanSapXep);
            System.out.println("[Room] Gửi mảng cho player 2: " + p2.getUsername());
            networkManager2.send(mangCanSapXep);
            System.out.println("[Room] Đã gửi mảng cho cả 2 người chơi");
            
            // Kết thúc, giải phóng trạng thái busy
            p1.setBusy(false);
            p2.setBusy(false);
        } catch (Exception e) {
            System.out.println("[Room] Lỗi khi gửi mảng:");
            e.printStackTrace();
        }
    }
}

