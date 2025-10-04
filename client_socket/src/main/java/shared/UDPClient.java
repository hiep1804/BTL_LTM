/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

/**
 *
 * @author hn235
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class UDPClient {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        try {
            // Cấu hình server
            String SERVER_IP = "203.162.10.109"; // nếu server chạy máy khác, đổi IP
            int SERVER_PORT = 2208;

            // Thông tin sinh viên + mã câu hỏi
            String studentCode = "B22DCCN297";
            String qCode = "eR27k6Sp";
            String send=studentCode+";"+qCode;
            socket=new Socket(SERVER_IP,SERVER_PORT);
            PrintWriter pr=new PrintWriter(socket.getOutputStream(),true);
            pr.println(send);
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s=br.readLine();
            System.out.println(s);
            String arr[]=s.split(";");
            String arr1[]=arr[1].split(" ");
            String res="";
            Arrays.sort(arr1);
            for(int i=arr1.length-1;i>=0;i--){
                res+=arr1[i]+" ";
            }
            res=res.substring(0, res.length()-1);
            res=arr[0]+";"+res;
            System.out.println(res);
            pr.println(res);
            pr.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 6. Đóng socket
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Đã đóng socket. Kết thúc chương trình.");
            }
        }
    }
}
