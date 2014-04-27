package com.example.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private long lastPulledTimestamp;

    //JSON Keys
    private static final String TAG_MESSAGE_LIST = "message_list";
    private static final String TAG_OUTGOING = "outgoing";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_TYPE = "message_type";
    private static final String TAG_TIMESTAMP = "created_at";
    private static final String TAG_FILENAME = "filename";

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
        try {
            String temp = pref.getString("cache_messages",null);

            if(temp != null && !temp.isEmpty())
            {
                jsonMessages = new JSONArray(pref.getString("cache_messages", null));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            }
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

        SharedPreferences.Editor edit = pref.edit();
        edit.putString("trainee_id", trainee_id);
        edit.apply();

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                int tabPos = tab.getPosition();
                switch (tabPos) {
                    case 0:
                        Intent w = new Intent(MessageActivity.this, WorkoutHistoryActivity.class);
                        w.putExtra(TraineeDetailFragment.ARG_ITEM_ID, trainee_id);
                        w.putExtra("trainee_id", trainee_id);
                        w.putExtra("name", sender);
                        startActivity(w);
                        break;
                    case 1:
                        break;
                    case 2:
                        Intent t = new Intent(MessageActivity.this, TraineeDetailActivity.class);
                        t.putExtra(TraineeDetailFragment.ARG_ITEM_ID, trainee_id);
                        t.putExtra("trainee_id", trainee_id);
                        t.putExtra("name", sender);
                        startActivity(t);
                        break;
                }
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        actionBar.addTab(actionBar.newTab()
                .setText("Workouts")
                .setIcon(R.drawable.ic_action_go_to_today)
                .setTabListener(tabListener), 0, false);

        actionBar.addTab(actionBar.newTab()
                .setText("Messages")
                .setIcon(R.drawable.ic_action_chat)
                .setTabListener(tabListener), 1, true);

        actionBar.addTab(actionBar.newTab()
                .setText("Contact")
                .setIcon(R.drawable.ic_action_person)
                .setTabListener(tabListener), 2, false);


        adapter = new MessageAdapter(this, messages);
        setListAdapter(adapter);

        /*addNewMessage(new Message("What should I try tomorrow?", false));*/
        //Invalidate the cache if our data is too old.
        if(System.currentTimeMillis() - lastPulledTimestamp > 20000 && isOnline())
            jsonMessages = null;
        if(jsonMessages==null) {
            lastPulledTimestamp = System.currentTimeMillis();
            new GetMessages().execute();
        }
        else{
            parseMessagesFromCache();
        }
    }

    public void parseMessagesFromCache() {
        try {
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
                String filename = null;

                if(messageType.equals("audio")) {
                    filename = m.getString("filename");
                }

                if (wasSender) {
                    String text = m.getString(TAG_CONTENT);
                    Message addM = new Message(text, true);
                    addM.setType(messageType, filename);
                    addM.setTimestamp(formattedDate.toString());
                    addNewMessage(addM);
                } else {
                    String text = m.getString(TAG_CONTENT);
                    Message addM = new Message(text, false);
                    addM.setType(messageType, filename);
                    addM.setTimestamp(formattedDate.toString());
                    addNewMessage(addM);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        getListView().setSelection((messages.size() - 1));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
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
            //adapter.positionClicked(position);
            adapter.notifyDataSetChanged();
        }
    }

    private void audioPlayStopClick(int linkPosition) {
        Log.w("Audio Clicked", "Audio" + linkPosition);
        if (!boolAudioPlaying) {
            messages.get(linkPosition).isPlayingAudio = true;
            adapter = new MessageAdapter(this, messages);
            getListView().setAdapter(null);
            getListView().setAdapter(adapter);
            adapter.notifyDataSetChanged();
            getListView().setSelection(linkPosition);
            playAudio(linkPosition);
            boolAudioPlaying = true;
        } else {
            if (boolAudioPlaying) {
                stopMyPlayService();
                boolAudioPlaying = false;
                messages.get(linkPosition).isPlayingAudio = false;
                adapter = new MessageAdapter(this, messages);
                getListView().setAdapter(null);
                getListView().setAdapter(adapter);
                adapter.notifyDataSetChanged();
                getListView().setSelection(linkPosition);
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


    public void onBackPressed() {
        Intent intent = new Intent(MessageActivity.this, TraineeListActivity.class);
        startActivity(intent);
        finish();
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
            try {
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
            } catch (IOException e) {
                Log.e("MessageActivity::IOException >> ", e.getMessage());
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
        if (id == R.id.action_settings) {
            Intent i = new Intent(MessageActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetMessages extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            APIHandler handler = new APIHandler();
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            JSONObject jsonObj = new JSONObject();
            parameters.add(new BasicNameValuePair("trainee_id", trainee_id.trim()));
            try {
                jsonObj = handler.sendAPIRequestWithAuth("trainer/messages", handler.GET, token, "", parameters);
            } catch (IOException e) {
                Log.e("MessageActivity::IOException >> ", e.getMessage());
                e.printStackTrace();
            }

            Log.d("Response: ", ">>> " + jsonObj);

            if (jsonObj != null) {
                try {
                    jsonMessages = jsonObj.getJSONArray(TAG_MESSAGE_LIST);
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("cache_messages", jsonMessages.toString());
                    edit.apply();
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
                        String filename = null;
                        if (messageType.equals("audio")) {
                            filename = m.getString("filename");
                        }
                        if (wasSender) {
                            String text = m.getString(TAG_CONTENT);
                            Message addM = new Message(text, true);
                            addM.setType(messageType, filename);
                            addM.setTimestamp(formattedDate.toString());
                            addNewMessage(addM);
                        } else {
                            String text = m.getString(TAG_CONTENT);
                            Message addM = new Message(text, false);
                            addM.setType(messageType, filename);
                            addM.setTimestamp(formattedDate.toString());
                            addNewMessage(addM);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("MessageActivity::JSONException >> ", e.getMessage());
                    e.printStackTrace();
                }

                return true;
            } else {
                Log.e("APIHandler", "no data from specified URL");
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
