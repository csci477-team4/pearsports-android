package com.example.app;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.apply();

            Intent i = new Intent(MessageActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    void addNewMessage(Message m) {
        messages.add(m);
        adapter.notifyDataSetChanged();
        getListView().setSelection(messages.size() - 1);
    }
}
