package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleWorkoutActivity extends Activity {

    private String token;
    private String traineeID;
    private String name;
    protected String sku;
    protected long epoch_start;
    protected long epoch_end;

    private CheckBox sun, mon, tue, wed, thu, fri, sat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_workout);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = preferences.getString("token",null);

        Bundle b = getIntent().getExtras();
        traineeID = b.getString("trainee_id");
        sku = b.getString("sku");    //sku ID of workout
        name = b.getString("name");

        ((TextView) findViewById(R.id.workout_name)).setText(name);

        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePickerStart);

        Button pic1 = (Button) findViewById(R.id.schedule_workout);
        pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve date and time from pickers
                int day   = datePicker.getDayOfMonth(); //date 1-31
                int month = datePicker.getMonth() + 1;  //month 1-12
                int year  = datePicker.getYear();       //YYYY

                // Time conversion to Epoch
                String date_str = new StringBuilder().append(month).append(' ').append(day).append(' ').append(year).toString();

                SimpleDateFormat df = new SimpleDateFormat("MM dd yyyy");
                Date date = null;
                try {
                    date = df.parse(date_str);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                epoch_start = date.getTime();
                epoch_end = epoch_start + 1800000;
                //TODO: epoch_end assumes workout is 30min long, should get duration


                sun = (CheckBox) findViewById(R.id.checkSun);
                mon = (CheckBox) findViewById(R.id.checkMon);
                tue = (CheckBox) findViewById(R.id.checkTue);
                wed = (CheckBox) findViewById(R.id.checkWed);
                thu = (CheckBox) findViewById(R.id.checkThu);
                fri = (CheckBox) findViewById(R.id.checkFri);
                sat = (CheckBox) findViewById(R.id.checkSat);

                List<Integer> list_days = new ArrayList<Integer>();

                if(sun.isChecked())
                    list_days.add(0);
                if(mon.isChecked())
                    list_days.add(1);
                if(tue.isChecked())
                    list_days.add(2);
                if(wed.isChecked())
                    list_days.add(3);
                if(thu.isChecked())
                    list_days.add(4);
                if(fri.isChecked())
                    list_days.add(5);
                if(sat.isChecked())
                    list_days.add(6);

                if(list_days.size() >= 2) {
                    new SendPlan().execute(list_days);
                }
                else {
                    new SendWorkout().execute();
                }

                // Switch back to trainee's workout history
                Intent i = new Intent(ScheduleWorkoutActivity.this, TraineeListActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(ScheduleWorkoutActivity.this, SportActivity.class);
        intent.putExtra(TraineeDetailFragment.ARG_ITEM_ID, traineeID);
        intent.putExtra("trainee_id", traineeID);
        intent.putExtra("name", name);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(ScheduleWorkoutActivity.this, SettingsActivity.class);
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
            NavUtils.navigateUpTo(this, new Intent(this, SportActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.remove("trainee_id");
            edit.apply();

            Intent i = new Intent(ScheduleWorkoutActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private class SendWorkout extends AsyncTask<Long,Void,Boolean> {

        protected Boolean doInBackground(Long... params) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            parameters.add(new BasicNameValuePair("trainee_id", traineeID));
            //parameters.add(new BasicNameValuePair("sku", sku));
            parameters.add(new BasicNameValuePair("start", String.valueOf(epoch_end)));
            //parameters.add(new BasicNameValuePair("end", String.valueOf(epoch_end)));

            try {
                JSONObject scheduleJSON = APIHandler.sendAPIRequestWithAuth("workout" + "/" + sku, APIHandler.POST, token, "", parameters);

                if (scheduleJSON != null) {
                    try {
                        if (scheduleJSON.getString("object").equals("error"))
                            return false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } catch (IOException e ) {
                Log.e("ScheduleWorkoutActivity::IOException >> ", e.getMessage());
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(final Boolean success) {

            if (success)
                Toast.makeText(ScheduleWorkoutActivity.this, "Workout successfully sent to trainee.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(ScheduleWorkoutActivity.this, "Unable to send workout to trainee.", Toast.LENGTH_LONG).show();

        }
    }

    private class SendPlan extends AsyncTask<List<Integer>,Void,Boolean> {

        protected Boolean doInBackground(List<Integer>... params) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("trainee_id", traineeID));
            parameters.add(new BasicNameValuePair("start", String.valueOf(epoch_end)));

            String days = "[";
            for (int i = 0; i < params[0].size(); i++) {
                if(i == 0)
                    days = days + params[0].get(i) + ", ";
                else if (i == 1)
                    days = days + params[0].get(i);
                else
                    days = days + ", " + params[0].get(i);
            }
            days = days + "]";
            Log.e("***SCHEDULE***", days);
            parameters.add(new BasicNameValuePair("weekdays", days));

            try {
                JSONObject scheduleJSON = APIHandler.sendAPIRequestWithAuth("plan" + "/" + sku, APIHandler.POST, token, "", parameters);

                if (scheduleJSON != null) {
                    try {
                        if (scheduleJSON.getString("object").equals("error"))
                            return false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } catch (IOException e ) {
                Log.e("ScheduleWorkoutActivity::IOException >> ", e.getMessage());
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(final Boolean success) {

            if (success)
                Toast.makeText(ScheduleWorkoutActivity.this, "Plan successfully sent to trainee.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(ScheduleWorkoutActivity.this, "Unable to send plan to trainee.", Toast.LENGTH_LONG).show();

        }


    }

}