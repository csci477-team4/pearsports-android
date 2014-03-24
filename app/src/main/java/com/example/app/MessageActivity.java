package com.example.app;

import android.app.Activity;
import android.app.LauncherActivity;
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

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MessageActivity extends ListActivity {

    ArrayList<Message> messages;
    private JSONArray jsonMessages = null;
    private MessageAdapter adapter;
    private EditText text;
    private String sender;
    private String trainee_id;
    private String token;
    private String sentMessage;

    //JSON Keys
    private static final String TAG_MESSAGE_LIST = "message_list";
    private static final String TAG_OUTGOING = "outgoing";
    private static final String TAG_CONTENT = "content";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_text);

        text = (EditText) this.findViewById(R.id.text);

        //Get trainer token from sharedpref
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = pref.getString("token", null);

        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");
        Log.w("Trainee_ID", trainee_id);
        this.sender = intent.getStringExtra("name");

        findViewById(R.id.sendText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });

        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAudioActivity();
            }
        });


        //sender = "usc students";
        this.setTitle(sender);
        messages = new ArrayList<Message>();

        /*messages.add(new Message("How was your workout yesterday?", true));
        messages.add(new Message("It was really tough.", false));
        messages.add(new Message("You did a great job!", true));
        messages.add(new Message("You stayed in the zones pretty well.", true));
        messages.add(new Message("Yeah?", false));
        messages.add(new Message("Absolutely!", true));*/


        adapter = new MessageAdapter(this, messages);
        setListAdapter(adapter);

        /*addNewMessage(new Message("What should I try tomorrow?", false));*/
        new GetMessages().execute();
    }

    public void sendMessage(View v) {
        sentMessage = text.getText().toString().trim();
        if (sentMessage.length() > 0) {
            text.setText("");
            addNewMessage(new Message(sentMessage, true));
            new SendTextMessageTask().execute();
        }
    }

    void addNewMessage(Message m) {
        messages.add(m);
        //adapter.notifyDataSetChanged();
        //getListView().setSelection(messages.size() - 1);
    }

    public void launchAudioActivity(){
        Intent i = new Intent((Activity)this, RecordAudioActivity.class);
        i.putExtra("trainee_id",trainee_id);
        i.putExtra("name", sender);
        startActivity(i);
    }

    public class SendTextMessageTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            APIHandler handler = new APIHandler();
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("trainee_id", trainee_id));
            parameters.add(new BasicNameValuePair("content", sentMessage));
            parameters.add(new BasicNameValuePair("outgoing", "true"));
            JSONObject jsonObj = handler.sendAPIRequestWithAuth("/message/text", handler.POST, token, "", parameters);
            try {
                String wasSuccess = jsonObj.get("message").toString();
                Log.w("sentResponse", jsonObj.toString());
                if (wasSuccess.contains("success")) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
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

    private class GetMessages extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            APIHandler handler = new APIHandler();
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("trainee_id", trainee_id));
            JSONObject jsonObj = handler.sendAPIRequestWithAuth("/trainer/messages?", handler.GET, token, "", parameters);

            Log.d("Response: ", ">>> " + jsonObj);

            if (jsonObj != null) {
                try {
                    jsonMessages = jsonObj.getJSONArray(TAG_MESSAGE_LIST);
                    Log.w("incomingMessages", jsonMessages.toString());
                    //Outgoing means trainer to trainee.
                    for (int i = 0; i < jsonMessages.length(); i++) {
                        JSONObject m = jsonMessages.getJSONObject(i);
                        Log.w("IndividualMessages", m.toString());
                        boolean wasSender = Boolean.valueOf(m.getString(TAG_OUTGOING));
                        if (wasSender) {
                            String text = m.getString(TAG_CONTENT);
                            addNewMessage(new Message(text, true));
                        } else {
                            String text = m.getString(TAG_CONTENT);
                            addNewMessage(new Message(text, false));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                Log.e("APIHandler", "No data from specified URL");
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                adapter.notifyDataSetChanged();
                setListAdapter(adapter);
                setContentView(R.layout.fragment_text);

                setListAdapter(adapter);
            }
        }
    }
}
