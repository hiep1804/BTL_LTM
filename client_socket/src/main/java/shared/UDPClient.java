/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

/**
 *
 * @author hn235
 */
import java.net.*;
import java.io.*;
import java.util.*;

public class UDPClient {
    public static void main(String[] args) {
        final String SERVER_IP = "203.162.10.109"; // hoặc IP thật nếu server ở máy khác
        final int SERVER_PORT = 2207;
        final String STUDENT_CODE = "B22DCCN297";
        final String Q_CODE = "DC73CA2E";

        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            // a. Gửi thông điệp ";studentCode;qCode"
            String request = ";" + STUDENT_CODE + ";" + Q_CODE;
            byte[] sendData = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);
            System.out.println("Đã gửi: " + request);

            // b. Nhận thông điệp từ server
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            System.out.println("Nhận từ server: " + response);

            // c. Phân tích chuỗi "requestId;a1,a2,...,aN"
            String[] parts = response.split(";");
            String requestId = parts[0];
            String[] numbersStr = parts[1].split(",");
            int[] numbers = Arrays.stream(numbersStr).mapToInt(Integer::parseInt).toArray();

            int max = Arrays.stream(numbers).max().getAsInt();
            int min = Arrays.stream(numbers).min().getAsInt();
            System.out.println("Max = " + max + ", Min = " + min);

            // d. Gửi lại kết quả "requestId;max,min"
            String result = requestId + ";" + max + "," + min;
            byte[] resultData = result.getBytes();
            DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, serverAddress, SERVER_PORT);
            socket.send(resultPacket);
            System.out.println("Đã gửi kết quả: " + result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
