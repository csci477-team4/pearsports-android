package com.example.app.Database;

/**
 * Message class is the representation of a message that will be used for the
 * local database.  Simply contains the text message, the user sent to, and the
 * timestamp of that message.
 */
public class Message {

    private String uID;
    private String message;
    private long timestamp;

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return message;
    }

}
