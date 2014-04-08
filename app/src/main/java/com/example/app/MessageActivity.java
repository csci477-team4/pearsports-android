package com.example.app;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.rtp.AudioStream;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageActivity extends ListActivity {

    ArrayList<Message> messages;
    private JSONArray jsonMessages = null;
    private MessageAdapter adapter;
    private EditText writtenText;
    private String sender;
    private String trainee_id;
    private String token;
    private String sentMessage;
    public ListView messageList;
    private boolean boolAudioPlaying;
    private Intent streamIntent;

    //JSON Keys
    private static final String TAG_MESSAGE_LIST = "message_list";
    private static final String TAG_OUTGOING = "outgoing";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_TYPE = "message_type";
    private static final String TAG_TIMESTAMP = "created_at";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_text);
        streamIntent = new Intent(this, AudioStreamService.class);

        writtenText = (EditText) this.findViewById(R.id.message_text);
        sentMessage = new String();

        //Get trainer token from sharedpref
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = pref.getString("token", null);

        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");
        if (trainee_id != null)
            Log.d("Trainee_ID", trainee_id);
        this.sender = intent.getStringExtra("name");

        //sender = "usc students";
        this.setTitle(sender);
        messages = new ArrayList<Message>();

        messageList = getListView();

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

    public int getItemPosition(View v) {
        return messageList.getPositionForView(v);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.w("Row Clicked", "row: " + position);
        if (messages.get(position).isAudio) {
            Log.w("Audio Clicked", "Audio" + position);
            audioPlayStopClick(position);
        }
    }

    private void audioPlayStopClick(int linkPosition) {
        Log.w("Audio Clicked", "Audio" + linkPosition);
        if (!boolAudioPlaying) {
            //buttonPlayStop.setBackgroundResource(R.drawable.pausebuttonsm);
            playAudio(linkPosition);
            boolAudioPlaying = true;
        } else {
            if (boolAudioPlaying) {
                //buttonPlayStop.setBackgroundResource(R.drawable.playbuttonsm);
                stopMyPlayService();
                boolAudioPlaying = false;
            }
        }
    }

    private void stopMyPlayService() {
        try {
            Log.w("Audio Clicked", "Stopping Audio");
            stopService(streamIntent);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getClass().getName() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void playAudio(int linkPosition) {
        Log.w("Audio Clicked", "Attempt to play" + linkPosition);
        streamIntent.putExtra("audio_url", messages.get(linkPosition).message.trim());
        try {
            startService(streamIntent);
            Log.w("Audio Clicked", "Started Service");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getClass().getName() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    public void sendMessage(View v) {
        EditText tempText = (EditText) this.findViewById(R.id.message_text);
        String temp = tempText.getText().toString();

        sentMessage = sentMessage + (temp);

        Log.w("Sent message became", sentMessage);
        Log.w("edit text field was", writtenText.getText().toString());

        if (sentMessage.length() > 0) {
            writtenText.setText("");
            tempText.setText("");
            addNewMessage(new Message(sentMessage, true), true);
            new SendTextMessageTask().execute();
        }
    }

    void addNewMessage(Message m) {
        messages.add(m);
        //adapter.notifyDataSetChanged();
        //getListView().setSelection(messages.size() - 1);
    }

    void addNewMessage(Message m, boolean t) {
        messages.add(m);
        adapter.notifyDataSetChanged();
        getListView().setSelection((messages.size() - 1));
    }

    public void launchAudioActivity(View v) {
        Intent i = new Intent((Activity) this, RecordAudioActivity.class);
        i.putExtra("trainee_id", trainee_id);
        i.putExtra("name", sender);
        startActivity(i);
    }

    public class SendTextMessageTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.w("reached sendTask", sentMessage);
            APIHandler handler = new APIHandler();
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("trainee_id", trainee_id.trim()));
            parameters.add(new BasicNameValuePair("content", sentMessage));
            parameters.add(new BasicNameValuePair("outgoing", "true"));
            JSONObject jsonObj = handler.sendAPIRequestWithAuth("message/text", handler.POST, token, "", parameters);
            sentMessage = new String();
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
            edit.remove("trainee_id");
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
            parameters.add(new BasicNameValuePair("trainee_id", trainee_id.trim()));
            JSONObject jsonObj = handler.sendAPIRequestWithAuth("trainer/messages", handler.GET, token, "", parameters);

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
                        String ts = m.getString(TAG_TIMESTAMP);
                        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss");
                        df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
                        String formattedDate = df.format(new Date(1000 * Long.parseLong(ts)));
                        String messageType = m.getString(TAG_TYPE);
                        if (wasSender) {
                            String text = m.getString(TAG_CONTENT);
                            Message addM = new Message(text, true);
                            addM.setType(messageType);
                            addM.setTimestamp(formattedDate.toString());
                            addNewMessage(addM);
                        } else {
                            String text = m.getString(TAG_CONTENT);
                            Message addM = new Message(text, false);
                            addM.setType(messageType);
                            addM.setTimestamp(formattedDate.toString());
                            addNewMessage(addM);
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
                getListView().setSelection(messages.size() - 1);
            }
        }
    }
}
