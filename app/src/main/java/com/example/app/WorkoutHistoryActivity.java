package com.example.app;

import android.app.Activity;
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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class WorkoutHistoryActivity extends Activity {

    private static final long WEEK_MILLISECONDS = 604800;

    private JSONObject workoutsObject = null;
    private JSONObject resultsObject = null;
    private JSONArray workoutArray = null;
    private JSONArray resultArray = null;
    private String traineeID;
    private String token;
    private TraineeContent.TraineeItem mItem;

    private long weekStartMillis;
    private long weekEndMillis;
    private int currentWeek; // 0 is current week, -1 is last week, 1 is next week

    private String workoutID; // this is for onClick only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_workout_history);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        token = preferences.getString("token",null);
        traineeID = preferences.getString("trainee_id", null);

        mItem = TraineeContent.TRAINEE_MAP.get(traineeID);

        ((TextView) findViewById(R.id.workout_history_trainee_name)).setText(mItem.getInfoMap().get("name"));
        ((TextView) findViewById(R.id.workout_history_email)).setText(mItem.getInfoMap().get("email"));

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

        Button prev = (Button) findViewById(R.id.workout_history_previous_button);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup vg = (ViewGroup) findViewById(R.id.workout_summary_container);
                vg.removeAllViews();
                currentWeek -= 1;
                weekEndMillis = weekStartMillis - 1;
                weekStartMillis -= WEEK_MILLISECONDS;
                new GetWorkoutSchedule().execute(weekStartMillis, weekEndMillis);
            }
        });

        Button next = (Button) findViewById(R.id.workout_history_next_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup vg = (ViewGroup) findViewById(R.id.workout_summary_container);
                vg.removeAllViews();
                currentWeek += 1;
                weekStartMillis = weekEndMillis + 1;
                weekEndMillis += WEEK_MILLISECONDS;
                new GetWorkoutSchedule().execute(weekStartMillis, weekEndMillis);
            }
        });

        DateTime dateTime = new DateTime(); // now
        DateTime weekStart = dateTime.weekOfWeekyear().roundFloorCopy();
        DateTime weekEnd = dateTime.weekOfWeekyear().roundCeilingCopy();
        weekStartMillis = weekStart.getMillis()/1000;
        weekEndMillis = weekEnd.getMillis()/1000;
        currentWeek = 0;
        //Log.d(">>>>> START/END MILLIS: ", "start: " + weekStartMillis + ", end: " + weekEndMillis);
        new GetWorkoutSchedule().execute(weekStartMillis, weekEndMillis);
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


                    // incomplete or marked_complete workouts (no results)
                    for (int i = 0; i < workoutsCount; i++) {
                        JSONObject workoutJSON = workoutArray.getJSONObject(i);

                        // If key not in map, add it
                        if (!mItem.getWorkoutMap().containsKey(workoutJSON.getString("id"))) {
                            Workout workout = new Workout();
                            HashMap<String,String> workoutMap = workout.getWorkoutMap();

                            workoutMap.put("id", workoutJSON.getString("id"));
                            workoutMap.put("title", workoutJSON.getString("title"));
                            workoutMap.put("activity_type", workoutJSON.getString("activity_type"));
                            workoutMap.put("description_short", workoutJSON.getString("description_short"));

                            if (workoutJSON.has("description_html")) {
                                workoutMap.put("description_html", workoutJSON.getString("description_html"));
                            } else {
                                workoutMap.put("description_html", "No description.");
                            }

                            workoutMap.put("duration", workoutJSON.getString("duration"));
                            workoutMap.put("avg_hr", null);
                            workoutMap.put("calories", "0");
                            workoutMap.put("distance", workoutJSON.getString("distance"));
                            workoutMap.put("status", workoutJSON.getString("status"));

                            // parse scheduled_at and completed_at time to human-readable string
                            String scheduled_at = workoutJSON.getString("scheduled_at");
                            String scheduled_at_parsed = ISOStringToString(scheduled_at);
                            workoutMap.put("scheduled_at_iso", scheduled_at);
                            workoutMap.put("scheduled_at_string", scheduled_at_parsed);

                            if (workoutMap.get("status").equals("marked_complete") ||
                                    workoutMap.get("status").equals("completed")) {
                                String completed_at = workoutJSON.getString("completed_at");
                                String completed_at_parsed = ISOStringToString(completed_at);
                                workoutMap.put("completed_at_iso", completed_at);
                                workoutMap.put("completed_at_string", completed_at_parsed);
                            }

                            mItem.getWeekWorkouts().add(workout);
                            mItem.getWorkouts().add(workout);
                            mItem.getWorkoutMap().put(workout.getWorkoutMap().get("id"), workout);

                        } else { // find the workout and update it

                            Workout workout = mItem.getWorkoutMap().get(workoutJSON.getString("id"));
                            HashMap<String,String> workoutMap = workout.getWorkoutMap();

                            workoutMap.put("id", workoutJSON.getString("id"));
                            workoutMap.put("title", workoutJSON.getString("title"));
                            workoutMap.put("activity_type", workoutJSON.getString("activity_type"));
                            workoutMap.put("description_short", workoutJSON.getString("description_short"));

                            if (workoutJSON.has("description_html")) {
                                workoutMap.put("description_html", workoutJSON.getString("description_html"));
                            } else {
                                workoutMap.put("description_html", "No description.");
                            }

                            workoutMap.put("duration", workoutJSON.getString("duration"));
                            workoutMap.put("avg_hr", null);
                            workoutMap.put("calories", "0");
                            workoutMap.put("distance", workoutJSON.getString("distance"));
                            workoutMap.put("status", workoutJSON.getString("status"));

                            // parse scheduled_at and completed_at time to human-readable string
                            String scheduled_at = workoutJSON.getString("scheduled_at");
                            String scheduled_at_parsed = ISOStringToString(scheduled_at);
                            workoutMap.put("scheduled_at_iso", scheduled_at);
                            workoutMap.put("scheduled_at_string", scheduled_at_parsed);

                            if (workoutMap.get("status").equals("marked_complete") ||
                                    workoutMap.get("status").equals("completed")) {
                                String completed_at = workoutJSON.getString("completed_at");
                                String completed_at_parsed = ISOStringToString(completed_at);
                                workoutMap.put("completed_at_iso", completed_at);
                                workoutMap.put("completed_at_string", completed_at_parsed);
                            }

                            mItem.getWeekWorkouts().add(workout);
                        }
                    }

                    // completed workouts with results
                    for (int i = 0; i < resultsCount; i++) {
//                        HashMap<String,String> resultMap = workout.getResult().getResultMap();
                        JSONObject resultJSON = resultArray.getJSONObject(i);
                        JSONObject workoutJSON = resultJSON.getJSONObject("workout");

                        // If key not in map, add it
                        if (!mItem.getWorkoutMap().containsKey(workoutJSON.getString("id"))) {
                            Workout workout = new Workout();
                            HashMap<String,String> workoutMap = workout.getWorkoutMap();

                            workoutMap.put("id", workoutJSON.getString("id"));
                            workoutMap.put("title", workoutJSON.getString("title"));
                            Log.d("WORKOUT TITLE:", workoutMap.get("title"));
                            workoutMap.put("activity_type", workoutJSON.getString("activity_type"));
                            workoutMap.put("description_short", workoutJSON.getString("description_short"));

                            if (workoutJSON.has("description_html")) {
                                workoutMap.put("description_html", workoutJSON.getString("description_html"));
                            } else {
                                workoutMap.put("description_html", "No description.");
                            }

                            workoutMap.put("duration", resultJSON.getString("duration"));
                            workoutMap.put("avg_hr", resultJSON.getString("avg_hr"));
                            workoutMap.put("calories", resultJSON.getString("calories"));
                            workoutMap.put("distance", resultJSON.getString("distance"));
                            workoutMap.put("status", workoutJSON.getString("status"));
                            workoutMap.put("grade", resultJSON.getString("grade"));

                            // parse scheduled_at and completed_at time to human-readable string
                            String scheduled_at = workoutJSON.getString("scheduled_at");
                            String scheduled_at_parsed = ISOStringToString(scheduled_at);
                            workoutMap.put("scheduled_at_iso", scheduled_at);
                            workoutMap.put("scheduled_at_string", scheduled_at_parsed);

                            String completed_at = resultJSON.getString("completed_at");
                            String completed_at_parsed = ISOStringToString(completed_at);
                            workoutMap.put("completed_at_iso", completed_at);
                            workoutMap.put("completed_at_string", completed_at_parsed);

                            mItem.getWeekWorkouts().add(workout);
                            mItem.getWorkouts().add(workout);
                            mItem.getWorkoutMap().put(workout.getWorkoutMap().get("id"), workout);

                        } else { // find the workout and update it

                            Workout workout = mItem.getWorkoutMap().get(workoutJSON.getString("id"));
                            HashMap<String,String> workoutMap = workout.getWorkoutMap();

                            workoutMap.put("id", workoutJSON.getString("id"));
                            workoutMap.put("title", workoutJSON.getString("title"));
                            workoutMap.put("activity_type", workoutJSON.getString("activity_type"));
                            workoutMap.put("description_short", workoutJSON.getString("description_short"));

                            if (workoutJSON.has("description_html")) {
                                workoutMap.put("description_html", workoutJSON.getString("description_html"));
                            } else {
                                workoutMap.put("description_html", "No description.");
                            }

                            workoutMap.put("duration", resultJSON.getString("duration"));
                            workoutMap.put("avg_hr", resultJSON.getString("avg_hr"));
                            workoutMap.put("calories", resultJSON.getString("calories"));
                            workoutMap.put("distance", resultJSON.getString("distance"));
                            workoutMap.put("status", workoutJSON.getString("status"));
                            workoutMap.put("grade", resultJSON.getString("grade"));

                            // parse scheduled_at and completed_at time to human-readable string
                            String scheduled_at = workoutJSON.getString("scheduled_at");
                            String scheduled_at_parsed = ISOStringToString(scheduled_at);
                            workoutMap.put("scheduled_at_iso", scheduled_at);
                            workoutMap.put("scheduled_at_string", scheduled_at_parsed);

                            String completed_at = resultJSON.getString("completed_at");
                            String completed_at_parsed = ISOStringToString(completed_at);
                            workoutMap.put("completed_at_iso", completed_at);
                            workoutMap.put("completed_at_string", completed_at_parsed);

                            mItem.getWeekWorkouts().add(workout);
                        }
                    }

                    // sort by date
                    Collections.sort(mItem.getWorkouts(), new WorkoutComparator());
                    Collections.sort(mItem.getWeekWorkouts(), new WorkoutComparator());

                    // serialize
                    try
                    {
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir() + "trainee_content.txt"))); //Select where you wish to save the file...
                        oos.writeObject(new TraineeContent()); // write the class as an 'object'
                        oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
                        oos.close();// close the stream
                    }
                    catch(Exception ex)
                    {
                        Log.v("Serialization Save Error : ", ex.getMessage());
                        ex.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                try
                {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(getFilesDir() + "trainee_content.txt")));
                    TraineeContent tc = (TraineeContent) ois.readObject();
                }
                catch(Exception ex)
                {
                    Log.v("Serialization Read Error : ",ex.getMessage());
                    ex.printStackTrace();
                }
                Log.e("APIHandler", "No data from specified URL");
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            mItem.getWeekWorkouts().clear();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success) {
                // dynamically load in workout views
                //ArrayList<Workout> workouts = mItem.getWorkouts();
                ArrayList<Workout> workouts = mItem.getWeekWorkouts();
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
                        ((TextView) view.findViewById(R.id.workout_completed_summary_grade)).setText("Grade: " + w.getWorkoutMap().get("grade"));
                        ((TextView) view.findViewById(R.id.workout_completed_summary_hr)).setText("Avg HR: " + w.getWorkoutMap().get("avg_hr"));
                        ((TextView) view.findViewById(R.id.workout_completed_date)).setText("Completed: " + w.getWorkoutMap().get("completed_at_string"));

                        view.setOnClickListener(workoutListener);

                    } else { // incomplete or marked_complete (i.e. no results)
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.workout_incomplete_summary, null);

                        ((TextView) view.findViewById(R.id.workout_incomplete_name)).setText(w.getWorkoutMap().get("title"));
                        ((TextView) view.findViewById(R.id.workout_incomplete_activity_type)).setText(w.getWorkoutMap().get("activity_type"));
                        ((TextView) view.findViewById(R.id.workout_incomplete_date_scheduled)).setText("Scheduled: " + w.getWorkoutMap().get("scheduled_at_string"));
                        ((TextView) view.findViewById(R.id.workout_incomplete_description)).setText(w.getWorkoutMap().get("description_short"));

                        view.setOnClickListener(workoutListener);
                    }
                    parent.addView(view);
                }
            }
        }
    }

    public class WorkoutComparator implements Comparator<Workout> {

        @Override
        public int compare(Workout w1, Workout w2) {
            DateTime dt1 = getDate(w1);
            DateTime dt2 = getDate(w2);
            return dt1.compareTo(dt2);
        }

        private DateTime getDate(Workout w) {
            DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
            if (!w.getWorkoutMap().get("status").equals("incomplete")) { // completed or marked_complete
                return parser.parseDateTime(w.getWorkoutMap().get("completed_at_iso"));
            } else { // incomplete
                if(!w.getWorkoutMap().get("scheduled_at_iso").equals("null")) {
                    return parser.parseDateTime(w.getWorkoutMap().get("scheduled_at_iso"));
                }
                return null;
            }
        }
    }

    /*** TIME HELPER METHODS ***/

    private String ISOStringToString(String iso) {
        if (!iso.equals("null")) {
            DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
            DateTime dt = parser.parseDateTime(iso);
            DateTimeFormatter formatter = DateTimeFormat.shortDateTime();
            //Log.d("ISOStringToString >>", "iso: "+iso+", formatted: " + formatter.print(dt));
            return formatter.print(dt);
        }
        return "null";
    }

}
