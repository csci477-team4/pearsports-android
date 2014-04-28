package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class SportActivity extends Activity {

    private ArrayAdapter<String> listAdapter;
    private String trainee_id;
    private String token;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);

        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");
        token = intent.getStringExtra("token");
        name = intent.getStringExtra("name");

        final String[] workouts = new String[] { "Endurance Ride 73min",
        "Pyramid Indoor Cycle",
        "Fat-Burn 1",
        "Post Run Strength Mini",
        "Interval Advanced Run ",
        "Speed Treadmill",
        "Tempo Treadmill",
        "Fat-Burn 3",
        "Fat-Burn 2",
        "Sprint Triathlon Brick",
        "Iron Triathlon Brick",
        "Functional Strength ",
        "Bike Trainer 1",
        "Endurance Ride",
        "Super SlimTone Bands 3",
        "Robert Reames - Super SlimTone Bands 2",
        "Super SlimTone Gym 3",
        "Time Saver Super Blast 1",
        "Super SlimTone Gym 1",
        "Super SlimTone Gym 2",
        "Time Saver Super Blast 2",
        "Interval Walk Run Climb",
        "Super SlimTone Bands 1",
        "Short Interval Special",
        "Post Cardio Flexibility",
        "Injury Prevention",
        "Lunchtime Power Walk",
        "The 5 min Warmup",
        "Cardio Band Blast 2",
        "Endurance Ride 110min",
        "Endurance Ride 83min",
        "Cardio Band Blast 1",
        "Endurance Ride 95min",
        "Endurance Ride 100min",
        "Endurance Ride 70min",
        "Endurance Ride 75min",
        "Endurance Ride 77min",
        "Endurance + Hill Run",
        "Endurance Ride 90min",
        "Pyramid + Hill Run",
        "Hi-Intensity Interval 1",
        "HIIT 30 Thirties",
        "Tread 'N' Shred Advanced 1",
        "Tread'N'Shred Moderate 1",
        "Functional Strength Circuit",
        "Tred'N'Shred Beginner 1"};

        final String[] sku = new String[] { "CFN030014-00M",
        "CFN01001D-00M",
        "CFN01000E-00M",
        "CFN020024-00M",
        "CFN030006-00M",
        "CFN020003-00M",
        "CFN020004-00M",
        "CFN01000G-00M",
        "CFN01000F-00M",
        "CFN01000Z-00M",
        "CFN01000Y-00M",
        "CFN030005-00M",
        "CFN030007-00M",
        "CFN030008-00M",
        "CFN050040-00M",
        "CFN050041-00M",
        "CFN050032-00M",
        "CFN050014-00M",
        "CFN050030-00M",
        "CFN050031-00M",
        "CFN050015-00M",
        "CFN050016-00M",
        "CFN050036-00M",
        "CFN050017-00M",
        "CFN050011-00M",
        "CFN01008M-00M",
        "CFN090003-00M",
        "CFN090008-00M",
        "CFN090002-00M",
        "CFN030012-00M",
        "CFN030017-00M",
        "CFN090001-00M",
        "CFN030019-00M",
        "CFN030011-00M",
        "CFN030013-00M",
        "CFN030015-00M",
        "CFN030016-00M",
        "CFN030010-00M",
        "CFN030018-00M",
        "CFN030021-00M",
        "CFN01000H-00M",
        "CFN080003-00M",
        "CFN150003-00M",
        "CFN150002-00M",
        "CFN01001C-00M",
        "CFN150001-00M"};

        ListView myList = (ListView) findViewById(R.id.list_workouts);
        ArrayList<String> listWorkouts = new ArrayList<String>();
        listWorkouts.addAll( Arrays.asList(workouts) );
        ArrayList<String> listSku = new ArrayList<String>();
        listSku.addAll( Arrays.asList(sku) );
        listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, listWorkouts);
        myList.setAdapter(listAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                Intent intent = new Intent(SportActivity.this, ScheduleWorkoutActivity.class);
                intent.putExtra("workout_name", workouts[i]);
                intent.putExtra("sku", sku[i]);
                intent.putExtra(TraineeDetailFragment.ARG_ITEM_ID, trainee_id);
                intent.putExtra("trainee_id", trainee_id);
                intent.putExtra("name", name);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
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
}

