# Trò chơi Sắp xếp Bóng bay - Báo cáo cuối kỳ Lập trình mạng

Đây là dự án game multiplayer "Sắp xếp Bóng bay" được xây dựng bằng Java Socket cho phần mạng, Java Swing cho giao diện người dùng và MySQL để lưu trữ dữ liệu.

## Mục lục
- [Tính năng chính](#tính-năng-chính)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Hướng dẫn cài đặt và chạy](#hướng-dẫn-cài-đặt-và-chạy)
  - [Yêu cầu](#yêu-cầu)
  - [Cài đặt Database](#cài-đặt-database)
  - [Chạy Server](#chạy-server)
  - [Chạy Client](#chạy-client)
- [Luật chơi](#luật-chơi)
- [Tác giả](#tác-giả)

## Tính năng chính
- **Giao tiếp Real-time:** Người chơi có thể thấy hành động của nhau gần như ngay lập tức nhờ vào Java Socket.
- **Đăng ký & Đăng nhập:** Hệ thống tài khoản người chơi được lưu trữ an toàn trong database.
- **Sảnh chờ (Lobby):** Hiển thị danh sách những người chơi đang online và trạng thái của họ (rảnh hoặc đang trong trận).
- **Mời và thách đấu:** Người chơi có thể mời người chơi khác đang rảnh để bắt đầu một trận đấu.
- **Chat trong trận:** Giao tiếp với đối thủ trong thời gian thực ngay tại phòng game.
- **Game theo vòng đấu:** Trận đấu được chia thành nhiều vòng, mỗi vòng có một bộ số ngẫu nhiên cần sắp xếp.
- **Tính giờ và tính điểm:** Mỗi vòng đấu có thời gian giới hạn. Người chơi nộp bài đúng và nhanh nhất sẽ được cộng điểm. Nộp sai sẽ bị trừ điểm.
- **Bảng xếp hạng:** Vinh danh những người chơi có điểm số cao nhất.
- **Giao diện đồ họa:** Giao diện được xây dựng bằng Java Swing, thân thiện và dễ sử dụng.

## Công nghệ sử dụng
- **Ngôn ngữ:** Java (JDK 21)
- **Giao diện người dùng (Client):** Java Swing
- **Mạng (Client-Server):** Java Socket
- **Cơ sở dữ liệu:** MySQL
- **Build tool:**
  - `client_socket`: Apache Maven
  - `server_socket`: Apache Ant

## Cấu trúc dự án
Dự án được chia thành hai module chính:
- `server_socket/`: Chứa mã nguồn của máy chủ, xử lý logic game, quản lý kết nối từ các client và tương tác với cơ sở dữ liệu.
- `client_socket/`: Chứa mã nguồn của client, xử lý giao diện người dùng, gửi yêu cầu lên server và nhận dữ liệu để hiển thị.

## Hướng dẫn cài đặt và chạy

### Yêu cầu
- JDK 21 hoặc phiên bản mới hơn.
- MySQL Server.
- Apache Maven.
- Apache Ant.
- Một IDE Java như NetBeans, IntelliJ hoặc VS Code.

### Cài đặt Database
1.  Mở MySQL và tạo một database mới với tên `sort_game`.
    ```sql
    CREATE DATABASE sort_game;
    ```
2.  Sử dụng database vừa tạo.
    ```sql
    USE sort_game;
    ```
3.  Tạo bảng `player` để lưu thông tin người chơi.
    ```sql
    CREATE TABLE player (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(255) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        score INT DEFAULT 50,
        status VARCHAR(50) DEFAULT 'offline'
    );
    ```
4.  Cập nhật thông tin kết nối database trong file `server_socket/src/connection/DBConnection.java` nếu cần.
    ```java
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sort_game";
    private static final String USER = "root";    
    private static final String PASS = "your_password"; // <-- Thay đổi mật khẩu của bạn ở đây
    ```

### Chạy Server
1.  Mở dự án `server_socket` trong IDE của bạn (ví dụ: NetBeans đã được cấu hình sẵn với Ant).
2.  Chạy file `src/shared/MainServer.java`.
3.  Server sẽ khởi động và lắng nghe kết nối từ client trên cổng mặc định.

### Chạy Client
1.  Mở dự án `client_socket` trong IDE của bạn (hỗ trợ Maven).
2.  Build dự án để tải các dependency cần thiết. Từ thư mục gốc `client_socket`, chạy lệnh:
    ```bash
    mvn clean install
    ```
3.  Chạy file `src/main/java/shared/ClientMainFrm.java`.
4.  Bạn có thể chạy nhiều client cùng lúc để kiểm tra tính năng multiplayer.

## Luật chơi
1.  **Đăng ký/Đăng nhập:** Bắt đầu bằng việc tạo tài khoản hoặc đăng nhập nếu đã có.
2.  **Mời người chơi:** Tại sảnh chờ, bạn sẽ thấy danh sách người chơi đang online. Nhấn vào tên một người chơi đang "Rảnh" và chọn "Mời chơi" để gửi lời mời.
3.  **Bắt đầu trận đấu:** Khi đối thủ chấp nhận lời mời, cả hai sẽ được chuyển đến màn hình chơi game.
4.  **Sắp xếp bóng:** Kéo và thả các quả bóng bay có số từ hàng dưới lên các ô trống ở hàng trên để sắp xếp chúng theo thứ tự từ nhỏ đến lớn.
5.  **Nộp bài:** Khi đã sắp xếp xong, nhấn nút "Nộp bài". Nếu hết giờ, hệ thống sẽ tự động nộp kết quả hiện tại của bạn.
6.  **Tính điểm:**
    - Người chơi nộp bài đúng đầu tiên sẽ được cộng điểm.
    - Người chơi nộp bài sai sẽ bị trừ điểm.
7.  **Chiến thắng:** Sau một số vòng đấu nhất định, người chơi có tổng điểm cao hơn sẽ là người chiến thắng chung cuộc.

## Tác giả
- [Tên thành viên 1]
- [Tên thành viên 2]
