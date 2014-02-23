package com.example.app;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Random;

public class MessageActivity extends ListActivity {

    ArrayList<Message> messages;
    MessageAdapter adapter;
    EditText text;
    static Random rand = new Random();
    static String sender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_text);

        text = (EditText) this.findViewById(R.id.text);

        sender = "Andre Carter";
        this.setTitle(sender);
        messages = new ArrayList<Message>();

        messages.add(new Message("How was your workout yesterday?", true));
        messages.add(new Message("It was really tough.", false));
        messages.add(new Message("You did a great job!", true));
        messages.add(new Message("You stayed in the zones pretty well.", true));
        messages.add(new Message("Yeah?", false));
        messages.add(new Message("Absolutely!", true));


        adapter = new MessageAdapter(this, messages);
        setListAdapter(adapter);
        addNewMessage(new Message("What should I try tomorrow?", false));
    }

    public void sendMessage(View v) {
        String newMessage = text.getText().toString().trim();
        if (newMessage.length() > 0) {
            text.setText("");
            addNewMessage(new Message(newMessage, true));
            new SendMessage().execute();
        }
    }

    private class SendMessage extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000); //simulate a network call
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.publishProgress(String.format("%s started writing", sender));
            try {
                Thread.sleep(2000); //simulate a network call
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.publishProgress(String.format("%s has entered text", sender));
            try {
                Thread.sleep(3000);//simulate a network call
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return Utility.messages[rand.nextInt(Utility.messages.length - 1)];


        }

        @Override
        public void onProgressUpdate(String... v) {

            if (messages.get(messages.size() - 1).isStatusMessage)//check whether we have already added a status message
            {
                messages.get(messages.size() - 1).setMessage(v[0]); //update the status for that
                adapter.notifyDataSetChanged();
                getListView().setSelection(messages.size() - 1);
            } else {
                addNewMessage(new Message(true, v[0])); //add new message, if there is no existing status message
            }
        }

        @Override
        protected void onPostExecute(String text) {
            if (messages.get(messages.size() - 1).isStatusMessage)//check if there is any status message, now remove it.
            {
                messages.remove(messages.size() - 1);
            }

            addNewMessage(new Message(text, false)); // add the original message from server.
        }


    }

    void addNewMessage(Message m) {
        messages.add(m);
        adapter.notifyDataSetChanged();
        getListView().setSelection(messages.size() - 1);
    }
}
