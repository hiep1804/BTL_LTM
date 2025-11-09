/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author hn235
 */
class Room implements Runnable {
    private Player p1, p2;
    private NetworkManager networkManager1, networkManager2;
    private int score1 = 0, score2 = 0;
    private volatile boolean gameRunning = true;
    private final Set<String> submittedPlayers = new HashSet<>();
    private ArrayList<Integer> sortedArray = new ArrayList<>();
    private String firstCorrectSubmitter = null;
    private static final int TOTAL_ROUNDS = 3;
    private static final int ROUND_TIME_SECONDS = 20;
    private int currentRound = 0;

    public Room(Player p1, Player p2, NetworkManager networkManager1, NetworkManager networkManager2) {
        this.p1 = p1;
        this.p2 = p2;
        this.networkManager1=networkManager1;
        this.networkManager2=networkManager2;
    }

    @Override
    public void run() {
        System.out.println("[Room] Phòng game bắt đầu cho " + p1.getUsername() + " vs " + p2.getUsername());
        
        try {
            // Đợi một chút để đảm bảo client đã sẵn sàng
            Thread.sleep(500);
            
            // Chơi 3 ván
            for (currentRound = 1; currentRound <= TOTAL_ROUNDS && gameRunning; currentRound++) {
                System.out.println("[Room] === VÁN " + currentRound + "/" + TOTAL_ROUNDS + " ===");
                startRound();
            }
            
            // Kết thúc game, gửi thông báo tổng kết
            if (gameRunning) {
                sendGameOver();
            }
            
        } catch (Exception e) {
            System.out.println("[Room] Lỗi trong Room:");
            e.printStackTrace();
        }
    }
    
    private void startRound() throws Exception {
        // Tạo mảng cần sắp xếp cho ván mới
        ArrayList<Integer> arr = new ArrayList<>();
        ArrayList<Integer> arrAfterSort = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int phanTu = (int)(Math.random() * 100 + 1);
            arr.add(phanTu);
            arrAfterSort.add(phanTu);
        }
        Collections.sort(arrAfterSort);
        
        synchronized (this) {
            this.sortedArray = new ArrayList<>(arrAfterSort);
            this.submittedPlayers.clear();
            this.firstCorrectSubmitter = null;
        }
        
        System.out.println("[Room] Ván " + currentRound + " - Mảng: " + arr);
        
        // Gửi mảng và thông tin ván cho cả 2 người chơi
        RoundInfo roundInfo = new RoundInfo(currentRound, TOTAL_ROUNDS, ROUND_TIME_SECONDS, arr);
        ObjectSentReceived roundData = new ObjectSentReceived("start_round", roundInfo);
        
        networkManager1.send(roundData);
        networkManager2.send(roundData);
        
        System.out.println("[Room] Đã gửi mảng ván " + currentRound + " cho cả 2 người chơi");
        
        // Đợi 20 giây hoặc cho đến khi cả 2 đã nộp bài
        long startTime = System.currentTimeMillis();
        long timeout = ROUND_TIME_SECONDS * 1000;
        
        while (gameRunning) {
            long elapsed = System.currentTimeMillis() - startTime;
            
            synchronized (this) {
                // Nếu cả 2 đã nộp bài, chuyển ván ngay
                if (submittedPlayers.size() >= 2) {
                    System.out.println("[Room] Cả 2 người chơi đã nộp bài, chuyển ván");
                    break;
                }
            }
            
            // Hết thời gian
            if (elapsed >= timeout) {
                System.out.println("[Room] Hết thời gian ván " + currentRound);
                break;
            }
            
            Thread.sleep(100);
        }
        
        // Gửi thông báo kết thúc ván
        sendRoundEnd();
    }
    
    private void sendRoundEnd() throws Exception {
        ScoreUpdate update = new ScoreUpdate(
            p1.getUsername(), score1,
            p2.getUsername(), score2,
            null, false
        );
        
        ObjectSentReceived roundEnd = new ObjectSentReceived("round_end", update);
        networkManager1.send(roundEnd);
        networkManager2.send(roundEnd);
        
        // Nghỉ 2 giây giữa các ván
        Thread.sleep(2000);
    }
    
    private void sendGameOver() throws Exception {
        ScoreUpdate finalScore = new ScoreUpdate(
            p1.getUsername(), score1,
            p2.getUsername(), score2,
            null, false
        );
        
        ObjectSentReceived gameOver = new ObjectSentReceived("game_over", finalScore);
        networkManager1.send(gameOver);
        networkManager2.send(gameOver);
        
        System.out.println("[Room] Kết thúc trận đấu. Tỉ số: " + p1.getUsername() + " " + score1 + " - " + score2 + " " + p2.getUsername());
        
        // Cập nhật database
        updatePlayerStats();
    }
    
    private void updatePlayerStats() {
        try {
            shared.services.PlayerService playerService = new shared.services.PlayerService();
            
            boolean p1Won = score1 > score2;
            boolean p2Won = score2 > score1;
            boolean draw = score1 == score2;
            
            // Cập nhật cho player 1
            playerService.updateMatchStats(p1.getUsername(), p1Won, draw);
            System.out.println("[Room] Cập nhật stats cho " + p1.getUsername() + 
                             " - Thắng: " + p1Won + ", Hòa: " + draw);
            
            // Cập nhật cho player 2
            playerService.updateMatchStats(p2.getUsername(), p2Won, draw);
            System.out.println("[Room] Cập nhật stats cho " + p2.getUsername() + 
                             " - Thắng: " + p2Won + ", Hòa: " + draw);
            
        } catch (Exception e) {
            System.err.println("[Room] Lỗi khi cập nhật stats:");
            e.printStackTrace();
        }
    }

    public synchronized ScoreUpdate handleSubmission(String username, ArrayList<Integer> submission) {
        if (!gameRunning || sortedArray == null || sortedArray.isEmpty()) {
            return null;
        }

        // Đánh dấu người chơi đã nộp bài
        boolean alreadySubmitted = submittedPlayers.contains(username);
        if (alreadySubmitted) {
            // Không cho nộp lại trong cùng 1 ván
            return new ScoreUpdate(
                p1.getUsername(), score1,
                p2.getUsername(), score2,
                username, false
            );
        }
        
        submittedPlayers.add(username);

        // Kiểm tra đúng sai
        boolean correct = submission != null && submission.size() == sortedArray.size();
        if (correct) {
            for (int i = 0; i < sortedArray.size(); i++) {
                if (!sortedArray.get(i).equals(submission.get(i))) {
                    correct = false;
                    break;
                }
            }
        }

        // Cộng điểm
        if (correct) {
            // Cộng 1 điểm cho đáp án đúng
            if (username.equals(p1.getUsername())) {
                score1++;
            } else if (username.equals(p2.getUsername())) {
                score2++;
            }
            
            // Nếu là người đầu tiên nộp đúng, cộng thêm 1 điểm
            if (firstCorrectSubmitter == null) {
                firstCorrectSubmitter = username;
                if (username.equals(p1.getUsername())) {
                    score1++;
                } else if (username.equals(p2.getUsername())) {
                    score2++;
                }
                System.out.println("[Room] " + username + " nộp đúng TRƯỚC, được +2 điểm!");
            } else {
                System.out.println("[Room] " + username + " nộp đúng, được +1 điểm");
            }
        } else {
            System.out.println("[Room] " + username + " nộp sai");
        }

        return new ScoreUpdate(
                p1.getUsername(),
                score1,
                p2.getUsername(),
                score2,
                username,
                correct
        );
    }

    public synchronized void endGame() {
        gameRunning = false;
    }
}

