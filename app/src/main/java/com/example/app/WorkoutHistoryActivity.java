package com.example.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.trainee.TraineeContent;
import com.example.app.trainee.Workout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutHistoryActivity extends Activity {

    private JSONObject workoutsObject = null;
    private JSONObject resultsObject = null;
    private JSONArray workoutArray = null;
    private JSONArray resultArray = null;
    private String traineeID;
    private String token;
    private String name;
    private String trainee_id;
    private TraineeContent.TraineeItem mItem;

    private String workoutID; // this is for onClick only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = preferences.getString("token",null);
        traineeID = preferences.getString("trainee_id", null);
        mItem = TraineeContent.TRAINEE_MAP.get(traineeID);

        setContentView(R.layout.activity_workout_history);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        ((TextView) findViewById(R.id.workout_history_trainee_name)).setText(mItem.getInfoMap().get("name"));

        ImageView trainee_image = (ImageView) findViewById(R.id.image_trainee);
        int imageResource = getResources().getIdentifier(mItem.getInfoMap().get("image"), null, getPackageName());
        Drawable drawable = getResources().getDrawable(imageResource);
        trainee_image.setImageDrawable(drawable);

        Button b = (Button) findViewById(R.id.schedule_workout);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WorkoutHistoryActivity.this, SportActivity.class);
                startActivity(i);
                finish();
            }
        });

        // TODO: specify start/stop timestamp parameters for API call
        /* defaulting to 3/9/2014 (1394345840) to 3/22/2014 (1395469040) interval */
        //TraineeContent.resetTraineeContent();
        new GetWorkoutSchedule().execute((long)1394345840, (long)1395469040);

        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");
        name = intent.getStringExtra("name");
        this.setTitle(name);

        SharedPreferences.Editor edit = preferences.edit();
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
                        break;
                    case 1:
                        Intent m = new Intent(WorkoutHistoryActivity.this, MessageActivity.class);
                        m.putExtra(TraineeDetailFragment.ARG_ITEM_ID, trainee_id);
                        m.putExtra("trainee_id", trainee_id);
                        m.putExtra("name", name);
                        startActivity(m);
                        break;
                    case 2:
                        Intent t = new Intent(WorkoutHistoryActivity.this, TraineeDetailActivity.class);
                        t.putExtra(TraineeDetailFragment.ARG_ITEM_ID, trainee_id);
                        t.putExtra("trainee_id", trainee_id);
                        t.putExtra("name", name);
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
                .setTabListener(tabListener), 0, true);

        actionBar.addTab(actionBar.newTab()
                .setText("Messages")
                .setTabListener(tabListener), 1, false);

        actionBar.addTab(actionBar.newTab()
                .setText("Contact")
                .setTabListener(tabListener), 2, false);
    }

    public void onBackPressed() {
        Intent intent = new Intent(WorkoutHistoryActivity.this, TraineeListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workout_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(WorkoutHistoryActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, TraineeListActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.remove("trainee_id");
            edit.apply();

            Intent i = new Intent(WorkoutHistoryActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetWorkoutSchedule extends AsyncTask<Long,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Long... params) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //Log.d("Workout Schedule: ", "trainee_id: " + preferences.getString("trainee_id", ""));
            parameters.add(new BasicNameValuePair("trainee_id",preferences.getString("trainee_id", "")));
            JSONObject scheduleJSON = APIHandler.sendAPIRequestWithAuth("workout_schedule" +
                    params[0].toString() + "/" + params[1].toString(), APIHandler.GET, token, "", parameters);

            //Log.d("Workout Schedule Response: ", ">>> " + scheduleJSON);

            if (scheduleJSON != null) {
                try {
                    workoutsObject = scheduleJSON.getJSONObject("workout_data").getJSONObject("workouts");
                    int workoutsCount = workoutsObject.getInt("count");
                    workoutArray = workoutsObject.getJSONArray("data");

                    resultsObject = scheduleJSON.getJSONObject("workout_data").getJSONObject("results");
                    int resultsCount = resultsObject.getInt("count");
                    resultArray = resultsObject.getJSONArray("data");


                    // incomplete workouts
                    for (int i = 0; i < workoutsCount; i++) {
                        Workout workout = new Workout();
                        HashMap<String,String> workoutMap = workout.getWorkoutMap();
                        JSONObject workoutJSON = workoutArray.getJSONObject(i);

                        workoutMap.put("id", workoutJSON.getString("id"));
                        workoutMap.put("title", workoutJSON.getString("title"));
                        workoutMap.put("activity_type", workoutJSON.getString("activity_type"));
                        workoutMap.put("description_short", workoutJSON.getString("description_short"));
                        workoutMap.put("duration", workoutJSON.getString("duration"));
                        workoutMap.put("avg_hr", null);
                        workoutMap.put("calories", "0");
                        workoutMap.put("distance", workoutJSON.getString("distance"));
                        workoutMap.put("scheduled_at", workoutJSON.getString("scheduled_at"));
                        workoutMap.put("status", workoutJSON.getString("status"));

//                        for (Iterator<String> keys = workoutJSON.keys(); keys.hasNext();) {
//                            String key = keys.next();
//                            workoutMap.put(key, workoutJSON.getString(key));
//                        }
                        mItem.getWorkouts().add(workout);
                        mItem.getWorkoutMap().put(workout.getWorkoutMap().get("id"), workout);
                        //workout.printWorkout();
                    }

                    // completed workouts
                    for (int i = 0; i < resultsCount; i++) {
                        Workout workout = new Workout();
                        HashMap<String,String> workoutMap = workout.getWorkoutMap();
//                        HashMap<String,String> resultMap = workout.getResult().getResultMap();
                        JSONObject resultJSON = resultArray.getJSONObject(i);
                        JSONObject workoutJSON = resultJSON.getJSONObject("workout");

                        workoutMap.put("id", workoutJSON.getString("id"));
                        workoutMap.put("title", workoutJSON.getString("title"));
                        workoutMap.put("activity_type", workoutJSON.getString("activity_type"));
                        workoutMap.put("description_short", workoutJSON.getString("description_short"));
                        workoutMap.put("duration", resultJSON.getString("duration"));
                        workoutMap.put("avg_hr", resultJSON.getString("avg_hr"));
                        workoutMap.put("calories", resultJSON.getString("calories"));
                        workoutMap.put("distance", resultJSON.getString("distance"));
                        workoutMap.put("scheduled_at", workoutJSON.getString("scheduled_at"));
                        workoutMap.put("status", workoutJSON.getString("status"));

//                        for (Iterator<String> keys = workoutJSON.keys(); keys.hasNext();) {
//                            String key = keys.next();
//                            workoutMap.put(key, workoutJSON.getString(key));
//                        }
//                        for (Iterator<String> keys = resultJSON.keys(); keys.hasNext();) {
//                            String key = keys.next();
//                            resultMap.put(key, resultJSON.getString(key));
//                        }

                        mItem.getWorkouts().add(workout);
                        mItem.getWorkoutMap().put(workout.getWorkoutMap().get("id"), workout);
                        //workout.printWorkout();
                        //workout.getResult().printResult();
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
            if(success) {
                // dynamically load in workout views
                ArrayList<Workout> workouts = mItem.getWorkouts();
                ViewGroup parent = (ViewGroup) findViewById(R.id.workout_summary_container);
                for (Workout w : workouts) {
                    workoutID = w.getWorkoutMap().get("id");
                    View.OnClickListener workoutListener = new View.OnClickListener() {
                        String id = workoutID;
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(WorkoutHistoryActivity.this,WorkoutDetailActivity.class);
                            intent.putExtra("workout_id", id);
                            startActivity(intent);
                        }
                    };
                    View view = null;
                    if (w.isCompleted()) {

                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.workout_completed_summary, null);

                        ((TextView) view.findViewById(R.id.workout_completed_name)).setText(w.getWorkoutMap().get("title"));
                        ((TextView) view.findViewById(R.id.workout_completed_activity_type)).setText(w.getWorkoutMap().get("activity_type"));

                        view.setOnClickListener(workoutListener);

                    } else {
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.workout_incomplete_summary, null);

                        ((TextView) view.findViewById(R.id.workout_incomplete_name)).setText(w.getWorkoutMap().get("title"));
                        ((TextView) view.findViewById(R.id.workout_incomplete_activity_type)).setText(w.getWorkoutMap().get("activity_type"));
                        ((TextView) view.findViewById(R.id.workout_incomplete_date_scheduled)).setText("Date Scheduled: " + w.getWorkoutMap().get("scheduled_at"));
                        ((TextView) view.findViewById(R.id.workout_incomplete_description)).setText(w.getWorkoutMap().get("description_short"));

                        view.setOnClickListener(workoutListener);
                    }
                    parent.addView(view);
                }
            }
        }
    }

}
