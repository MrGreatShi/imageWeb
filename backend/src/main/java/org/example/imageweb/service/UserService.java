package org.example.imageweb.service;

import org.example.imageweb.dao.UserDao;
import org.example.imageweb.entity.User;
import org.example.imageweb.config.AppConfig;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public User getInfo(int id) throws Exception{
        return userDao.getUserByID(id);
    }

    public void register(String username, String password, String email) throws Exception {
        if (userDao.getUserByUsername(username) != null) {
            throw new Exception("Username already exists");
        }
        User newUser = new User(1,username,password,email);
        userDao.addUser(newUser);

        // 在 resources/imageRepository 下创建以 username 命名的文件夹
        try {
            Path userDir = AppConfig.IMAGE_REPOSITORY.resolve(username);
            Files.createDirectories(userDir);
        } catch (IOException e) {
            throw new Exception("Failed to create user image directory", e);
        }
    }

    public User login(String username, String password) throws Exception {
        User user = userDao.queryUserExists(username, password);
        if (user == null) {
            throw new Exception("User not found");
        }
        return user;
    }

    public void Logout(int id) throws Exception {
        User user = userDao.getUserByID(id);
        if (user == null) {
            throw new Exception("User not found");
        }
        userDao.deleteUser(id);

        // 删除用户的图片文件夹
        try {
            Path userDir = AppConfig.IMAGE_REPOSITORY.resolve(user.getUsername());
            if (Files.exists(userDir)) {
                Files.walk(userDir)
                     .sorted((a, b) -> b.compareTo(a)) // 先删除子文件夹和文件
                     .forEach(path -> {
                         try {
                             Files.delete(path);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     });
            }
        } catch (IOException e) {
            throw new Exception("Failed to delete user image directory", e);
        }
    }
}
