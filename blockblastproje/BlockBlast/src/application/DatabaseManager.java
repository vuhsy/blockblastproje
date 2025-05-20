package application;

import java.sql.*;

public class DatabaseManager {
    private final String dbUrl, dbUser, dbPass;
    private Connection conn;

    public DatabaseManager(String dbUrl, String dbUser, String dbPass) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

    public void connect() throws SQLException, ClassNotFoundException {
        if (conn == null || conn.isClosed()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        }
    }

    public void saveScore(String user, int score) {
        String sql = "INSERT INTO game_results(username, score, played_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setInt(2, score);
            ps.setObject(3, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public String[] getHighScore() {
        String[] result = {"-", "0"};
        String sql = "SELECT username, score FROM game_results ORDER BY score DESC, id ASC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result[0] = rs.getString("username");
                result[1] = Integer.toString(rs.getInt("score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

 // Her oyuncunun en yüksek skorunu döndürür: [ [username, skor], ... ]
    public String[][] getAllPlayersHighScoresArray() {
        String[][] array = new String[10][2]; // ilk 10 oyuncu için
        String sql = "SELECT username, MAX(score) AS max_score FROM game_results GROUP BY username ORDER BY max_score DESC LIMIT 10";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next() && i < 10) {
                array[i][0] = rs.getString("username");
                array[i][1] = Integer.toString(rs.getInt("max_score"));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return array;
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
