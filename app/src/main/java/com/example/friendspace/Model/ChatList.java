package com.example.friendspace.Model;

public class ChatList {

    private String id;
    private long lastMessageTime;

    public ChatList() {}

    public ChatList(String id, long lastMessageTime) {
        this.id = id;
        //this.lastMessageTime = lastMessageTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /*public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }*/
}
