package com.example.app;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.app.trainee.TraineeContent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TraineeListActivity extends Activity implements OnItemClickListener {

    public static final String[] names = new String[] { "John",
            "Daniel", "Shay", "Marc", "Vahe", "Tanya", "Poojan", "Garima" };

    public static final Integer arrows = R.drawable.right_arrow;

    public static final Integer[] trainees = { R.drawable.trainee_1,
            R.drawable.trainee_2, R.drawable.trainee_3, R.drawable.trainee_4,
            R.drawable.trainee_5, R.drawable.trainee_6, R.drawable.trainee_7,
            R.drawable.trainee_8};

    ListView listView;
    List<RowItem> rowItems;

    private TraineeContent traineeContent = TraineeContent.getInstance();
    private JSONObject trainee_list = null;
    private JSONObject trainee_info = null;
    private JSONObject trainee_stats_list = null;
    private JSONObject trainee_stats = null;
    private List<TraineeContent.TraineeItem> listTrainees;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = preferences.getString("token",null);
        loadTraineeContent();
        listTrainees = traineeContent.TRAINEES;
        if (isOnline()) {
            Log.d("TraineeListActivity::onCreate >> ", "refresh.");
            refresh();
        }
    }

    public void refresh() {
        traineeContent.resetTraineeContent();
        new GetTraineeList().execute(token);
        new GetStats().execute();
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

            Intent i = new Intent(TraineeListActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetTraineeList extends AsyncTask<String,Void,TraineeContent>
    {
        @Override
        protected TraineeContent doInBackground(String... params) {
            if (isOnline()) {
                //List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                //parameters.add(new BasicNameValuePair(LoginActivity.EMAIL, LoginActivity.PASSWORD));
                try {
                    JSONObject jsonObj = APIHandler.sendAPIRequestWithAuth("trainee_list", APIHandler.GET, token, "");

                    //Log.d("Response: ", ">>> " + jsonObj);

                    if (jsonObj != null) {
                        try {
                            trainee_list = jsonObj.getJSONObject("trainee_list");

                            for (Iterator<String> keys = trainee_list.keys(); keys.hasNext(); ) {
                                String id = keys.next();
                                trainee_info = trainee_list.getJSONObject(id); // map of trainee info
                                TraineeContent.TraineeItem trainee = traineeContent.new TraineeItem(id, trainee_info.get("screen_name").toString());

                                HashMap<String, String> info = trainee.getInfoMap();
                                info.put("email", trainee_info.get("email").toString());
                                info.put("dob", trainee_info.get("dob").toString());
                                info.put("gender", trainee_info.get("gender").toString());
                                info.put("age", trainee_info.get("age").toString());
                                info.put("height", trainee_info.get("height").toString());
                                info.put("weight", trainee_info.get("weight").toString());
                                info.put("notes", trainee_info.get("notes").toString());
                                info.put("photo_url", trainee_info.get("photo_url").toString());

                                // TODO: change this - hardcoded.
                                if (trainee.name.equals("KR")) {
                                    info.put("image", "drawable/trainee_1");
                                } else if (trainee.name.equals("Jamie")) {
                                    info.put("image", "drawable/trainee_2");
                                } else if (trainee.name.equals("Joe R")) {
                                    info.put("image", "drawable/trainee_3");
                                } else if (trainee.name.equals("eric")) {
                                    info.put("image", "drawable/trainee_4");
                                }

                                traineeContent.addItem(trainee);
                            }
                            writeTraineeContent();
                        } catch (JSONException e) {
                            Log.e("TraineeListActivity::GetTraineeList : ", "JSONException: " + e.getMessage());
                            e.printStackTrace();
                            loadTraineeContent();
                        }
                    } else {
                        Log.e("APIHandler", "No data from specified URL");
                        loadTraineeContent();
                    }
                } catch (IOException e) {
                    Log.d("TraineeListActivity::GetTraineeList::IOException >> ", e.getMessage());
                    e.printStackTrace();
                    loadTraineeContent();
                }
            } else {
                Log.d("TraineeListActivity::GetTraineeList >> ", "Not online.");
                loadTraineeContent();
            }
            return traineeContent;
        }

        @Override
        protected void onPostExecute(final TraineeContent tc) {
            rowItems = new ArrayList<RowItem>();
            Log.d("TraineeListActivity::onPostExecute >> ", "printing trainee list.");
            traineeContent = tc;
            listTrainees = traineeContent.TRAINEES;
            traineeContent.printTraineeList();
            for (int i = 0; i < listTrainees.size(); i++) {
                RowItem item = new RowItem(trainees[i], arrows, listTrainees.get(i).getInfoMap().get("name"));
                rowItems.add(item);
            }

            CustomBaseAdapter adapter = new CustomBaseAdapter(TraineeListActivity.this, rowItems);

            listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(TraineeListActivity.this);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // Click listeners are in CustomBaseAdapter
    }

    private class GetStats extends AsyncTask<Void,Void,TraineeContent>
    {
        @Override
        protected TraineeContent doInBackground(Void... params) {
            if (isOnline()) {
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("all", "True"));
                try {
                    JSONObject statsJSON = APIHandler.sendAPIRequestWithAuth("stats", APIHandler.GET, token, "", parameters);

                    //Log.d("Response: ", ">>> " + statsJSON);

                    if (statsJSON != null) {
                        try {
                            trainee_stats_list = statsJSON.getJSONObject("trainee_stats_list");

                            for (Iterator<String> keys = trainee_stats_list.keys(); keys.hasNext(); ) {
                                String id = keys.next(); // keys are the trainee_ids
                                trainee_stats = trainee_stats_list.getJSONObject(id); // map of trainee stats
                                TraineeContent.TraineeItem trainee = traineeContent.TRAINEE_MAP.get(id);
                                HashMap<String, String> map = trainee.getStatsMap();
                                for (Iterator<String> iter = trainee_stats.keys(); iter.hasNext(); ) {
                                    String statKey = iter.next();
                                    map.put(statKey, trainee_stats.get(statKey).toString());
                                }
                            }
                            writeTraineeContent();
                        } catch (JSONException e) {
                            Log.e("TraineeListActivity::GetStats : ", "JSONException: " + e.getMessage());
                            e.printStackTrace();
                            loadTraineeContent();
                        }
                    } else {
                        Log.e("APIHandler", "No data from specified URL");
                        loadTraineeContent();
                    }
                } catch (IOException e) {
                    Log.d("TraineeListActivity::GetStats::IOException >> ", e.getMessage());
                    e.printStackTrace();
                    loadTraineeContent();
                }
            } else {
                Log.d("TraineeListActivity::GetStats >> ", "Not online.");
                loadTraineeContent();
            }
            return traineeContent;
        }

        @Override
        protected void onPostExecute(final TraineeContent tc) {

        }
    }

    /*** I/O HELPERS ***/

    private void writeTraineeContent() {
        // serialize TraineeContent
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir() + "trainee_content.txt"))); //Select where you wish to save the file...
            oos.writeObject(traineeContent); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
            Log.d("TraineeListActivity >> ", "OOS writeObject, flush, close.");
        } catch (Exception ex) {
            Log.v("Serialization Save Error : ", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean loadTraineeContent() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(getFilesDir() + "trainee_content.txt")));
            traineeContent = (TraineeContent) ois.readObject();
            listTrainees = traineeContent.TRAINEES;
            Log.d("TraineeListActivity >> ", "OIS readObject.");
            traineeContent.printTraineeList();
            return true;
        } catch (Exception ex) {
            Log.v("Serialization Read Error : ", ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private boolean isOnline()	{
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected())
            return true;
        return false;
    }

}
