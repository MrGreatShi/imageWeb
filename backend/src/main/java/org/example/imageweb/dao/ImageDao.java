package org.example.imageweb.dao;

import org.example.imageweb.config.AppConfig;
import org.example.imageweb.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImageDao {

    public ImageDao() {}

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.DBurl, AppConfig.DBuser, AppConfig.DBpassword);
    }

    public List<Image> getAllImages() throws SQLException {
        List<Image> images = new ArrayList<>();
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM images";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Image image = new Image();
                image.setId(rs.getInt("id"));
                image.setTitle(rs.getString("title"));
                image.setPath(rs.getString("path"));
                image.setStored_at(rs.getTimestamp("stored_at"));
                images.add(image);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return images;
    }

    public Image getImageByID(int id) throws SQLException {
        Image image = null;
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM images WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                image = new Image();
                image.setId(rs.getInt("id"));
                image.setUser_id(rs.getInt("user_id"));
                image.setTitle(rs.getString("title"));
                image.setPath(rs.getString("path"));
                image.setStored_at(rs.getTimestamp("stored_at"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return image;
    }

    public List<Image> getImagesOfUser(int id) throws SQLException{
        List<Image> images = new ArrayList<>();
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM images WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Image image = new Image();
                image.setId(rs.getInt("id"));
                image.setTitle(rs.getString("title"));
                image.setUser_id(rs.getInt("user_id"));
                image.setPath(rs.getString("path"));
                image.setStored_at(rs.getTimestamp("stored_at"));
                images.add(image);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return images;
    }

    public void storeImage(Image image) throws SQLException {
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO images (user_id, title, path, stored_at) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, image.getUser_id());
            pstmt.setString(2, image.getTitle());
            pstmt.setString(3, image.getPath());
            pstmt.setTimestamp(4, image.getStored_at());
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void removeImage(int id) throws SQLException {
        try {
            Connection conn = getConnection();
            String sql = "DELETE FROM images WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void modifyImageName(int id, String new_title) throws SQLException {
        try {
            Connection conn = getConnection();
            String sql = "UPDATE images SET title = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, new_title);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
