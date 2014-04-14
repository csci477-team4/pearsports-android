package com.example.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends Activity {

    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final String[] account_settings = new String[]{
                "Reset Password",
                "Available for Trainee Matching"
        };

        ListView myList = (ListView) findViewById(R.id.lv_settings);

        ArrayList<String> listWorkouts = new ArrayList<String>();
        listWorkouts.addAll( Arrays.asList(account_settings) );

        listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, listWorkouts);
        myList.setAdapter(listAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                switch(i) {
                    case 0:
                        //Reset Password API Call
                        Toast.makeText(SettingsActivity.this, "Reset Password", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        //Available for Trainee Matching API Call
                        Toast.makeText(SettingsActivity.this, "Trainee Matching", Toast.LENGTH_LONG).show();
                        break;

                }
            }
        });
    }
}
