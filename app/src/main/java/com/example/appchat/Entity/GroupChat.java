package com.example.appchat.Entity;

public class GroupChat {
    String sender,message,timestamp,type;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GroupChat(String sender, String message, String timestamp, String type) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public GroupChat() {
    }

    @Override
    public String toString() {
        return "GroupChat{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
