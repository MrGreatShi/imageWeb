package org.example.imageweb.service;

import org.example.imageweb.dao.LabelDao;
import org.example.imageweb.dao.ImageDao;
import org.example.imageweb.entity.Image;
import org.example.imageweb.entity.McpRequest;
import org.example.imageweb.entity.McpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class McpService {
    private final ImageDao imageDao;
    private final LabelDao labelDao;

    public McpService() {
        this.imageDao = new ImageDao();
        this.labelDao = new LabelDao();
    }


    public List<String> getKeysFromConversation(String conversation) {
        Set<String> stop = Set.of("请","帮","我","找","一张","的","图片","要","看看","有没有","可以","给我","展示","显示","有关","关于","主题","内容",
                "有","上","里","，","。","！","？",".",",","!","?","照片");
        for(String s : stop){
            conversation = conversation.replace(s, " ");
        }
        String[] words = conversation.trim().split(" ");
        List<String> keys = new ArrayList<>();
        for(String word : words){
            if(!word.trim().isEmpty()){
                keys.add(word);
            }
        }
        return keys;
    }
    public List<McpResponse> searchByConversation(McpRequest request) throws Exception{
        //System.out.println("user_id: " + request.getUser_id());
        //System.out.println("conversation: " + request.getConversation());
        List<String> keys = getKeysFromConversation(request.getConversation());
        List<Image> images = imageDao.getPotentialImages(request.getUser_id(), keys);
        List<McpResponse> response = new ArrayList<>();
        for(Image image : images){
            response.add(new McpResponse(image.getId(), image.getTitle()));
        }
        return response;
    }
}
