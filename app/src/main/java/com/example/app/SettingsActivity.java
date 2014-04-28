package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SettingsActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private ArrayAdapter<String> listAdapter;

    private String token;

    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        ToggleButton tb = (ToggleButton) findViewById(R.id.status);
        if (tb != null) {
            tb.setOnCheckedChangeListener(SettingsActivity.this);
        }

        new GetStatus().execute();

    }

    private class GetStatus extends AsyncTask<Long,Void,Boolean> {

        protected Boolean doInBackground(Long... params) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.status);
            boolean current = tb.isChecked();
            String current_status;
            try {
                JSONObject jsonObj = APIHandler.sendAPIRequestWithAuth("trainer/status", APIHandler.GET, token, "");
                current_status = jsonObj.getJSONObject("trainer").getString("status");

                if (current_status.equals("available"))
                    current = true;
                else if (current_status.equals("not_available"))
                    current = false;
                else {
                    Log.e("SettingsActivity >> ", "Unable to determine if trainer is available or not.");
                    current = false;
                }
            } catch (IOException e) {
                Log.e("SettingsActivity::IOException >> ", e.getMessage());
                e.printStackTrace();
            } catch (JSONException j) {
                Log.e("SettingsActivity::JSONException >> ", j.getMessage());
                j.printStackTrace();
            }
            return current;
        }

        protected void onPostExecute(final Boolean success) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.status);

            if (success)
                tb.setChecked(true);
            else
                tb.setChecked(false);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked) {
            status = "available";
            new SetStatus().execute();
        }
        else {
            status = "not_available";
            new SetStatus().execute();
        }
    }

    private class SetStatus extends AsyncTask<Long,Void,Boolean> {

        protected Boolean doInBackground(Long... params) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.status);
            boolean current = tb.isChecked();

            try {
                JSONObject scheduleJSON = APIHandler.sendAPIRequestWithAuth("trainer/status/" + status, APIHandler.POST, token, "");

                if (scheduleJSON != null) {
                    try {
                        if (scheduleJSON.getString("status").equals(200)) {
                            if (scheduleJSON.getString("message").equals("status is now available"))
                                current = true;
                            else
                                current = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }  catch (IOException e ) {
                Log.e("SettingsActivity::IOException >> ", e.getMessage());
                e.printStackTrace();
            }
            return current;
        }

        protected void onPostExecute(final Boolean success) {
            ToggleButton tb = (ToggleButton) findViewById(R.id.status);

            if (success) {
                Toast.makeText(SettingsActivity.this, "You are available for new trainees.", Toast.LENGTH_SHORT).show();
                tb.setChecked(true);
            }
            else {
                Toast.makeText(SettingsActivity.this, "You are unavailable for new trainees.", Toast.LENGTH_SHORT).show();
                tb.setChecked(false);
            }
        }
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
            NavUtils.navigateUpTo(this, new Intent(this, WorkoutHistoryActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.remove("trainee_id");
            edit.apply();

            Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
