CREATE TABLE Players (
    player_id       INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    total_score     INT NOT NULL DEFAULT 0,
    total_wins      INT NOT NULL DEFAULT 0,
    matches_played  INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Matches (
    match_id            INT AUTO_INCREMENT PRIMARY KEY,
    player1_id          INT,
    player2_id          INT,
    player1_final_score INT NOT NULL DEFAULT 0,
    player2_final_score INT NOT NULL DEFAULT 0,
    winner_id           INT,
    status              ENUM('hoàn thành', 'hủy') NOT NULL,
    started_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at            TIMESTAMP NULL,

    CONSTRAINT fk_player1 FOREIGN KEY (player1_id) REFERENCES Players(player_id) ON DELETE SET NULL,
    CONSTRAINT fk_player2 FOREIGN KEY (player2_id) REFERENCES Players(player_id) ON DELETE SET NULL,
    CONSTRAINT fk_winner FOREIGN KEY (winner_id) REFERENCES Players(player_id) ON DELETE SET NULL
);

CREATE TABLE Rounds (
    round_id            INT AUTO_INCREMENT PRIMARY KEY,
    match_id            INT NOT NULL,
    round_number        TINYINT NOT NULL,
    problem_data        JSON,
    player1_score_change INT NOT NULL DEFAULT 0,
    player2_score_change INT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_match FOREIGN KEY (match_id) REFERENCES Matches(match_id) ON DELETE CASCADE
);

CREATE INDEX idx_1 ON Players(total_score DESC, total_wins DESC);


INSERT INTO Players (username, password, display_name, total_score, total_wins, matches_played) VALUES
('nha', '123456', 'Gamer Pro', 125, 12, 20),
('nha2', '123456', 'Newbie', 40, 4, 10),
('nha3', '123456', 'Master', 350, 30, 45);

INSERT INTO Matches (player1_id, player2_id, player1_final_score, player2_final_score, winner_id, status, started_at, ended_at) VALUES
(1, 2, 25, -5, 1, 'hoàn thành', '2025-10-08 10:00:00', '2025-10-08 10:05:00');

INSERT INTO Rounds (match_id, round_number, problem_data, player1_score_change, player2_score_change) VALUES
(1, 1, '{"elements": [5, 2, 8], "sort_order": "asc"}', 10, -3),
(1, 2, '{"elements": ["b", "c", "a"], "sort_order": "asc"}', 10, 10),
(1, 3, '{"elements": [100, 50, 200], "sort_order": "desc"}', 5, -12);