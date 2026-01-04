package org.example.imageweb.entity;

public class McpRequest {
    private int user_id;
    private String conversation;

    public McpRequest() {}
    public McpRequest(int user_id, String conversation) {
        this.user_id = user_id;
        this.conversation = conversation;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public String getConversation() {
        return conversation;
    }
    public void setConversation(String conversation) {
        this.conversation = conversation;
    }
}
