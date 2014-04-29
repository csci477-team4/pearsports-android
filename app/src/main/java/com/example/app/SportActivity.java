package com.example.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SportActivity extends Activity implements AdapterView.OnItemClickListener {

    private String trainee_id;
    private String token;
    private String name;

    private JSONArray sku_list = null;
    private String title;
    private String sku;

    ListView listView;
    List<WorkoutItem> workouts = new ArrayList<WorkoutItem>();
    List<WorkoutItem> plans = new ArrayList<WorkoutItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);

        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");
        token = intent.getStringExtra("token");
        name = intent.getStringExtra("name");

        new GetWorkouts().execute();
        new GetPlans().execute();
    }

    private class GetWorkouts extends AsyncTask<String,Void,List<WorkoutItem>>
    {
        @Override
        protected List<WorkoutItem> doInBackground(String... params) {
            if (isOnline()) {
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("type", "workout"));

                try {
                    JSONObject jsonObj = APIHandler.sendAPIRequestWithAuth("sku_list", APIHandler.GET, token, "", parameters);

                    if (jsonObj != null) {
                        try {
                            sku_list = jsonObj.getJSONArray("sku_list");

                            for (int i = 0; i < sku_list.length(); i++) {
                                JSONObject skuJSON = sku_list.getJSONObject(i);

                                sku = skuJSON.getString("sku");
                                title = skuJSON.getString("title");
                                Drawable d = getDrawable();

                                WorkoutItem wi = new WorkoutItem(d, title, sku);
                                workouts.add(wi);
                            }

                        } catch (JSONException e) {
                            Log.e("TraineeListFragment::GetTraineeList : ", "JSONException: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("APIHandler", "No data from specified URL");
                    }
                } catch (IOException e) {
                    Log.d("TraineeListFragment::GetTraineeList::IOException >> ", e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Log.d("TraineeListFragment::GetTraineeList >> ", "Not online.");
            }
            return workouts;
        }

        @Override
        protected void onPostExecute(final List<WorkoutItem> w) {

            WorkoutAdapter adapter = new WorkoutAdapter(SportActivity.this, w);

            listView = (ListView) findViewById(R.id.list_workouts);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(SportActivity.this);
        }
    }

    private class GetPlans extends AsyncTask<String,Void,List<WorkoutItem>>
    {
        @Override
        protected List<WorkoutItem> doInBackground(String... params) {
            if (isOnline()) {
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("type", "plan"));

                try {
                    JSONObject jsonObj = APIHandler.sendAPIRequestWithAuth("sku_list", APIHandler.GET, token, "", parameters);

                    if (jsonObj != null) {
                        try {
                            sku_list = jsonObj.getJSONArray("sku_list");

                            for (int i = 0; i < sku_list.length(); i++) {
                                JSONObject skuJSON = sku_list.getJSONObject(i);

                                sku = skuJSON.getString("sku");
                                title = skuJSON.getString("title");
                                Drawable d = getDrawable();

                                WorkoutItem wi = new WorkoutItem(d, title, sku);
                                plans.add(wi);
                            }

                        } catch (JSONException e) {
                            Log.e("TraineeListFragment::GetTraineeList : ", "JSONException: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("APIHandler", "No data from specified URL");
                    }
                } catch (IOException e) {
                    Log.d("TraineeListFragment::GetTraineeList::IOException >> ", e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Log.d("TraineeListFragment::GetTraineeList >> ", "Not online.");
            }
            return plans;
        }

        @Override
        protected void onPostExecute(final List<WorkoutItem> w) {

            WorkoutAdapter adapter = new WorkoutAdapter(SportActivity.this, w);

            listView = (ListView) findViewById(R.id.list_plans);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(SportActivity.this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(SportActivity.this, ScheduleWorkoutActivity.class);
        i.putExtra("trainee_id", trainee_id);
        i.putExtra("token", token);
        i.putExtra("name", workouts.get(position).getName());
        i.putExtra("sku", workouts.get(position).getSku());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void onBackPressed() {
        Intent intent = new Intent(SportActivity.this, WorkoutHistoryActivity.class);
        intent.putExtra(TraineeDetailFragment.ARG_ITEM_ID, trainee_id);
        intent.putExtra("trainee_id", trainee_id);
        intent.putExtra("name", name);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, WorkoutHistoryActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.remove("trainee_id");
            edit.apply();

            Intent i = new Intent(SportActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        if (id == R.id.action_settings) {
            Intent i = new Intent(SportActivity.this, SettingsActivity.class);
            i.putExtra("token", token);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public Drawable getDrawable() {
        Random r = new Random();
        int i = r.nextInt(16 - 1) + 1;

        int imageResource;

        switch (i) {
            case 1:
                imageResource = getResources().getIdentifier("drawable/w1", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 2:
                imageResource = getResources().getIdentifier("drawable/w2", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 3:
                imageResource = getResources().getIdentifier("drawable/w3", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 4:
                imageResource = getResources().getIdentifier("drawable/w4", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 5:
                imageResource = getResources().getIdentifier("drawable/w5", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 6:
                imageResource = getResources().getIdentifier("drawable/w6", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 7:
                imageResource = getResources().getIdentifier("drawable/w7", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 8:
                imageResource = getResources().getIdentifier("drawable/w8", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 9:
                imageResource = getResources().getIdentifier("drawable/w9", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 10:
                imageResource = getResources().getIdentifier("drawable/w10", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 11:
                imageResource = getResources().getIdentifier("drawable/w11", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 12:
                imageResource = getResources().getIdentifier("drawable/w12", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 13:
                imageResource = getResources().getIdentifier("drawable/w13", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 14:
                imageResource = getResources().getIdentifier("drawable/w14", null, getPackageName());
                return getResources().getDrawable(imageResource);
            case 15:
                imageResource = getResources().getIdentifier("drawable/w15", null, getPackageName());
                return getResources().getDrawable(imageResource);
        }

        imageResource = getResources().getIdentifier("drawable/w1", null, getPackageName());
        return getResources().getDrawable(imageResource);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected())
            return true;
        SportActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(SportActivity.this, "Device is offline.", Toast.LENGTH_LONG).show();
            }
        });
        return false;
    }
}

