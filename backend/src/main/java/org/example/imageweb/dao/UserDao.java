package org.example.imageweb.dao;

import org.example.imageweb.config.AppConfig;
import org.example.imageweb.entity.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDao {

    public UserDao() {}

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.DBurl, AppConfig.DBuser, AppConfig.DBpassword);
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return users;
    }

    public User getUserByID(int id) throws SQLException {
        User user = null;
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return user;
    }

    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return user;
    }

    public User getUserByEmail(String email) throws SQLException{
        User user = null;
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return user;
    }

    public void addUser(User user) throws SQLException {
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteUser(int id) throws SQLException {
        try {
            Connection conn = getConnection();
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public User queryUserExists(String username, String password) throws SQLException {
        User user = null;
        try {
            Connection conn = getConnection();
            String sql = "SELECT * FROM users WHERE username = ? and password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return user;
    }
}
