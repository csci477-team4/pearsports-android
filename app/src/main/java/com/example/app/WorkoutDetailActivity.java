package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.trainee.TraineeContent;
import com.example.app.trainee.Workout;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Created by Shay on 3/20/14.
 */
public class WorkoutDetailActivity extends Activity {

    private String traineeID;
    private TraineeContent.TraineeItem mItem;
    private TraineeContent traineeContent = TraineeContent.getInstance();
    private String workoutID;
    private String token;
    private Workout workout; // the current workout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        traineeID = preferences.getString("trainee_id", null);

        Log.d("WorkoutDetailActivity::onCreate >> ", "Loading TraineeContent.");
        loadTraineeContent();
        mItem = traineeContent.TRAINEE_MAP.get(traineeID);
        workoutID = getIntent().getStringExtra("workout_id");
        token = getIntent().getStringExtra("token");
        workout = mItem.getWorkoutMap().get(workoutID);

        if (workout.getWorkoutMap().get("status").equals("completed")) {
            setContentView(R.layout.activity_workout_detail_completed);
        } else {
            setContentView(R.layout.activity_workout_detail_incomplete);
        }

        ((TextView) findViewById(R.id.workout_detail_name)).setText(workout.getWorkoutMap().get("title"));
        ((TextView) findViewById(R.id.workout_detail_activity_type)).setText(workout.getWorkoutMap().get("activity_type"));
        ((TextView) findViewById(R.id.workout_detail_description_short)).setText(workout.getWorkoutMap().get("description_short"));

        if (workout.getWorkoutMap().get("status").equals("completed")) {
            ((TextView) findViewById(R.id.workout_detail_completed_grade)).setText("Grade: " + workout.getWorkoutMap().get("grade"));
            ((TextView) findViewById(R.id.workout_detail_duration)).setText("Duration: " + workout.getWorkoutMap().get("duration"));
            ((TextView) findViewById(R.id.workout_detail_hr)).setText("Average HR: " + workout.getWorkoutMap().get("avg_hr"));
            ((TextView) findViewById(R.id.workout_detail_calories)).setText("Calories: " + workout.getWorkoutMap().get("calories"));
            ((TextView) findViewById(R.id.workout_detail_distance)).setText("Distance: " + workout.getWorkoutMap().get("distance"));
            ((TextView) findViewById(R.id.workout_detail_complete_description)).setText(Html.fromHtml(workout.getWorkoutMap().get("description_html")));
        } else {
            ((TextView) findViewById(R.id.workout_detail_incomplete_description)).setText(Html.fromHtml(workout.getWorkoutMap().get("description_html")));
        }

        ImageView trainee_image = (ImageView) findViewById(R.id.workout_detail_activity_icon);
        trainee_image.setImageDrawable(mItem.getProfile());
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
            Intent i = new Intent(WorkoutDetailActivity.this, SettingsActivity.class);
            i.putExtra("token", token);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            NavUtils.navigateUpTo(this, new Intent(this, WorkoutHistoryActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.remove("trainee_id");
            edit.apply();

            Intent i = new Intent(WorkoutDetailActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean loadTraineeContent() {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(getFilesDir() + "trainee_workouts.txt")));
            traineeContent = (TraineeContent) ois.readObject();
            mItem = traineeContent.TRAINEE_MAP.get(traineeID);
            Log.d("WorkoutDetailActivity >> ", "OIS readObject.");
            mItem.printWeekWorkouts();
            return true;
        }
        catch(Exception ex)
        {
            Log.v("WorkoutDetailActivity >> Serialization Read Error : ",ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}
