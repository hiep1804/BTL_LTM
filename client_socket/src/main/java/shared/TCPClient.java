/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

/**
 *
 * @author hn235
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
    public static void main(String[] args) {
        final String SERVER_HOST = "203.162.10.109"; // hoặc IP server thật
        final int SERVER_PORT = 2208;
        final String STUDENT_CODE = "B22DCCN297";
        final String QCODE = "PzPq6RzK";

        try (
            Socket socket = new Socket();
        ) {
            // Thiết lập timeout cho kết nối & đọc dữ liệu (5 giây)
            socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), 5000);
            socket.setSoTimeout(5000);

            // Dùng luồng ký tự
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // (a) Gửi chuỗi mã sinh viên và mã câu hỏi
            String request = STUDENT_CODE + ";" + QCODE;
            writer.write(request);
            writer.newLine();
            writer.flush();
            System.out.println("✅ Đã gửi: " + request);

            // (b) Nhận danh sách tên miền
            String domains = reader.readLine();
            System.out.println("📩 Nhận từ server: " + domains);

            // (c) Lọc ra tên miền .edu
            List<String> eduDomains = new ArrayList<>();
            if (domains != null) {
                String[] parts = domains.split(",\\s*");
                for (String domain : parts) {
                    if (domain.endsWith(".edu")) {
                        eduDomains.add(domain);
                    }
                }
            }

            // Ghép lại danh sách .edu để gửi đi
            String response = String.join(", ", eduDomains);
            writer.write(response);
            writer.newLine();
            writer.flush();
            System.out.println("📤 Đã gửi lại tên miền .edu: " + response);

            // (d) Đóng kết nối
            socket.close();
            System.out.println("🔒 Đã đóng kết nối.");

        } catch (SocketTimeoutException e) {
            System.out.println("⏰ Quá thời gian chờ 5s!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

