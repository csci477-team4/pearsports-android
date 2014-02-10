package com.example.app.Database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Class that will serve as Data Access Object.  Maintains connection to DB
 * and supports adding/retrieving all messages.  Useful for the front-end side
 * to have easy methods of access for the database.  Create an instance of this class,
 * then call a getMessages function type to return a list of relevant database messages
 * in List which can be used in conjunction with an ArrayAdapter for display.
 */

public class MessageDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MessageDB dbHelper;
    private String[] allColumns = {MessageDB.COLUMN_UID,
            MessageDB.COLUMN_MESSAGE, MessageDB.COLUMN_TIMESTAMP};
    private long lastTimeStamp;

    public MessageDataSource(Context context) {
        dbHelper = new MessageDB(context);
        lastTimeStamp = 0;
    }

    public long getLastTimeStamp(){
        return lastTimeStamp;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * @param uID       Username of user to be sent to or received from.
     * @param message   Message text.
     * @param timestamp Time message was sent.(From user or trainer.)
     * @return Reference of created message, can be used to delete from DB.
     */
    public Message createMessage(String uID, String message, long timestamp) {
        ContentValues values = new ContentValues();
        values.put(MessageDB.COLUMN_MESSAGE, message);
        values.put(MessageDB.COLUMN_UID, uID);
        values.put(MessageDB.COLUMN_TIMESTAMP, timestamp);
        long insertId = database.insert(MessageDB.TABLE_MESSAGES, null,
                values);
        Cursor cursor = database.query(MessageDB.TABLE_MESSAGES,
                allColumns, MessageDB.COLUMN_UID
                + " = " + uID + " AND " + MessageDB.COLUMN_TIMESTAMP + " = " + timestamp, null,
                null, null, null);
        cursor.moveToFirst();
        Message newMessage = cursorToMessage(cursor);
        cursor.close();
        lastTimeStamp = timestamp;
        return newMessage;
    }

    public void deleteMessage(Message message) {
        String id = message.getuID();
        long timestamp = message.getTimestamp();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MessageDB.TABLE_MESSAGES, MessageDB.COLUMN_UID
                + " = " + id + " AND " + MessageDB.COLUMN_TIMESTAMP + " = " + timestamp, null);
    }

    /**
     * Selects every row in the database and puts into ArrayList which is returned at end
     * of execution.
     *
     * @return List of all messages in the database.
     */
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<Message>();

        Cursor cursor = database.query(MessageDB.TABLE_MESSAGES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Message message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return messages;
    }

    /**
     * Used to search database for messages pertaining to a specific user.
     *
     * @param uID The username or user ID of the user who the message belongs to.
     * @return List of all messages associated with the user specified in order by timestamp(earliest to latest).
     */
    public List<Message> getUsersMessages(String uID) {
        List<Message> messages = new ArrayList<Message>();

        Cursor cursor = database.query(MessageDB.TABLE_MESSAGES,
                allColumns, MessageDB.COLUMN_UID + " = " + uID, null, null, null, MessageDB.COLUMN_TIMESTAMP + "ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Message message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return messages;
    }

    public void updateDatabaseFromJSON(String jsonArrayString){
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                createMessage(jsonObject.getString("from"), jsonObject.getString("message"),
                Long.parseLong(jsonObject.getString("timestamp")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message cursorToMessage(Cursor cursor) {
        Message message = new Message();
        message.setuID(cursor.getString(0));
        message.setMessage(cursor.getString(1));
        message.setTimestamp(cursor.getLong(2));
        return message;
    }
}

