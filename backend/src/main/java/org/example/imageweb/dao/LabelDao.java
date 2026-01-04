package org.example.imageweb.dao;

import org.example.imageweb.config.AppConfig;
import org.example.imageweb.entity.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabelDao {

    public LabelDao() {}

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.DBurl, AppConfig.DBuser, AppConfig.DBpassword);
    }

    public List<Label> getLabelsOfUser(int user_id) throws SQLException {
        List<Label> labels = new ArrayList<>();
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM labels WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Label label = new Label();
                label.setId(rs.getInt("id"));
                label.setTitle(rs.getString("title"));
                labels.add(label);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return labels;
    }

    public List<Label> getLabelsOfImage(int image_id) throws SQLException {
        List<Label> labels = new ArrayList<>();
        try {
            Connection conn = getConnection();
            String sql = "SELECT labels.id, labels.title FROM labels JOIN labellinks ON labels.id = labellinks.l_id WHERE labellinks.p_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, image_id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Label label = new Label();
                label.setId(rs.getInt("id"));
                label.setTitle(rs.getString("title"));
                labels.add(label);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return labels;
    }

    public void addLabel(int user_id, String label) throws SQLException {
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO labels (user_id, title) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, label);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteLabel(int id) throws SQLException {
        try {
            Connection conn = getConnection();
            String sql = "DELETE FROM labels WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void attachLabel(int p_id, int l_id){
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO labellinks (p_id, l_id) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, p_id);
            pstmt.setInt(2, l_id);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void detachLabel(int p_id, int l_id) {
        try {
            Connection conn = getConnection();
            String sql = "DELETE FROM labellinks WHERE p_id = ? AND l_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, p_id);
            pstmt.setInt(2, l_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
