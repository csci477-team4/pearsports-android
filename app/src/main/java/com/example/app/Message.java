package com.example.app;

public class Message {
    /**
     * The content of the message
     */
    String message;
    String timestamp;
    String filename;
    /**
     * boolean to determine, who is sender of this message
     */
    boolean isMine;
    /**
     * boolean to determine, whether the message is a status message or not.
     * it reflects the changes/updates about the sender is writing, have entered text etc
     */
    boolean isStatusMessage;
    boolean isAudio;
    boolean isText;
    boolean isPlayingAudio;

    /**
     * Constructor to make a Message object
     */
    public Message(String message, boolean isMine) {
        super();
        this.message = message;
        this.isMine = isMine;
        this.isStatusMessage = false;
    }
    /**
     * Constructor to make a status Message object
     * consider the parameters are swaped from default Message constructor,
     *  not a good approach but have to go with it.
     */
    public Message(boolean status, String message) {
        super();
        this.message = message;
        this.isMine = false;
        this.isStatusMessage = status;
        this.isAudio = false;
        this.isText = false;
        this.isPlayingAudio = false;
    }

    public void setType(String type, String filename){
        if(type.equals("audio")){
            isAudio = true;
            if(filename != null) {
                this.filename = filename;
            }
            return;
        }
        if(type.equals("text")){
            isText = true;
            return;
        }
        isText = true;

    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isMine() {
        return isMine;
    }
    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }
    public boolean isStatusMessage() {
        return isStatusMessage;
    }
    public void setStatusMessage(boolean isStatusMessage) {
        this.isStatusMessage = isStatusMessage;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
