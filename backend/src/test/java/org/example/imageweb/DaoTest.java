package org.example.imageweb;

import org.example.imageweb.dao.UserDao;
import org.example.imageweb.dao.ImageDao;
import org.example.imageweb.dao.LabelDao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DaoTest {
    private UserDao userDao;
    private ImageDao imageDao;
    private LabelDao labelDao;

    public DaoTest() {
        userDao = new UserDao();
        imageDao = new ImageDao();
        labelDao = new LabelDao();
    }

    @Test
    void testGetUser() throws Exception {
        int testUserId = 1; // 替换为实际存在的用户ID
        try {
            userDao = new UserDao();
            var user = userDao.getUserByID(testUserId);
            assertTrue(user != null, "获取的图片列表不应为null");
            // 你可以根据预期的图片数量进行断言
            // assertEquals(expectedImageCount, images.size(), "图片数量不匹配");
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testGetImage() throws Exception {
        int testImageId = 1; // 替换为实际存在的图片ID
        try {
            imageDao = new ImageDao();
            var image = imageDao.getImageByID(testImageId);
            assertTrue(image != null, "获取的图片不应为null");
            // 你可以根据预期的图片属性进行断言
            // assertEquals(expectedTitle, image.getTitle(), "图片标题不匹配");
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testGetLabel() throws Exception {
        int testUserId = 1; // 替换为实际存在的用户ID
        try {
            labelDao = new LabelDao();
            var labels = labelDao.getLabelsOfUser(testUserId);
            assertTrue(labels != null, "获取的标签列表不应为null");
            // 你可以根据预期的标签数量进行断言
            // assertEquals(expectedLabelCount, labels.size(), "标签数量不匹配");
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
