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
    private int score1 = 0, score2 = 0;

    public Room(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
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
//            p1.getOut().println("Game bắt đầu với " + p2.getName());
//            p2.getOut().println("Game bắt đầu với " + p1.getName());
            //gui thong tin nguoi duoc thach dau
//            ObjectSentReceived player1GuiThongTinNguoiDuocThachDau=new ObjectSentReceived("accept challenge", p2);
//            p1.getObjOut().writeObject(player1GuiThongTinNguoiDuocThachDau);
//            p1.getObjOut().flush();
//            //gui thong tin nguoi thach dau
//            ObjectSentReceived playerGuiThongTinNguoiThachDau=new ObjectSentReceived("accept challenge", p1);
//            p2.getObjOut().writeObject(playerGuiThongTinNguoiThachDau);
//            p2.getObjOut().flush();
            //gui mang can sap xep
            ObjectSentReceived mangCanSapXep=new ObjectSentReceived("mang can sap xep",arr);
            p1.getObjOut().writeObject(mangCanSapXep);
            p1.getObjOut().flush();
            p2.getObjOut().writeObject(mangCanSapXep);
            p2.getObjOut().flush();
//            while (true) {
//                p1.getOut().println("Nhập số:");
//                p2.getOut().println("Nhập số:");

//                String m1 = p1.getIn().readLine();
//                String m2 = p2.getIn().readLine();
//                if (m1 == null || m2 == null) break;
//
//                int n1 = Integer.parseInt(m1);
//                int n2 = Integer.parseInt(m2);
//
//                if (n1 > n2) score1++;
//                else if (n2 > n1) score2++;
//
//                p1.getOut().println("Điểm: " + score1 + "-" + score2);
//                p2.getOut().println("Điểm: " + score2 + "-" + score1);
//
//                if (score1 == 3 || score2 == 3) {
//                    if (score1 > score2) {
//                        p1.getOut().println("Bạn THẮNG!");
//                        p2.getOut().println("Bạn THUA!");
//                    } else {
//                        p1.getOut().println("Bạn THUA!");
//                        p2.getOut().println("Bạn THẮNG!");
//                    }
//                    break;
//                }
//            }

            // Kết thúc, giải phóng trạng thái busy
            p1.setBusy(false);
            p2.setBusy(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

