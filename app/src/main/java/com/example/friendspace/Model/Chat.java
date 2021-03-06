package com.example.friendspace.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;

    private Boolean isUnread;
    private long timeSent;

    public Chat() {}

    public Chat(String sender, String receiver, String message, Boolean isUnread, long timeSent) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isUnread = isUnread;
        this.timeSent = timeSent;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsUnread() {
        return isUnread;
    }

    public void setIsUnread(Boolean isUnread) {
        this.isUnread = isUnread;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }

}
