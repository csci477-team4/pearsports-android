package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Iterator;
import java.util.List;

public class WorkoutHistoryActivity extends Activity {

    private JSONObject workoutsObject = null;
    private JSONObject resultsObject = null;
    private JSONArray workoutArray = null;
    private JSONArray resultArray = null;
    private String traineeID;
    private String token;
    private TraineeContent.TraineeItem mItem;

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
         new GetWorkoutSchedule().execute((long)1394345840, (long)1395469040);
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
            return true;
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
                        for (Iterator<String> keys = workoutJSON.keys(); keys.hasNext();) {
                            String key = keys.next();
                            workoutMap.put(key, workoutJSON.getString(key));
                        }
                        mItem.getWorkouts().add(workout);
                        //workout.printWorkout();
                    }

                    // completed workouts
                    for (int i = 0; i < resultsCount; i++) {
                        Workout workout = new Workout();
                        HashMap<String,String> workoutMap = workout.getWorkoutMap();
                        HashMap<String,String> resultMap = workout.getResult().getResultMap();
                        JSONObject resultJSON = resultArray.getJSONObject(i);
                        JSONObject workoutJSON = resultJSON.getJSONObject("workout");
                        for (Iterator<String> keys = workoutJSON.keys(); keys.hasNext();) {
                            String key = keys.next();
                            workoutMap.put(key, workoutJSON.getString(key));
                        }
                        for (Iterator<String> keys = resultJSON.keys(); keys.hasNext();) {
                            String key = keys.next();
                            resultMap.put(key, resultJSON.getString(key));
                        }
                        mItem.getWorkouts().add(workout);
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

        }
    }

}
