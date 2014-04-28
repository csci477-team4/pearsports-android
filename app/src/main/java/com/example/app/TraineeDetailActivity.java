package com.example.app;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An activity representing a single Trainee detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TraineeListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link TraineeDetailFragment}.
 */
public class TraineeDetailActivity extends FragmentActivity {

    private String trainee_id;
    private String name;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_detail);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TraineeDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(TraineeDetailFragment.ARG_ITEM_ID));
            TraineeDetailFragment fragment = new TraineeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_trainee_detail_layout, fragment)
                    .commit();
        }

        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");
        name = intent.getStringExtra("name");
        token = intent.getStringExtra("token");
        this.setTitle(name);


        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                int tabPos = tab.getPosition();
                switch (tabPos) {
                    case 0:
                        Intent w = new Intent(TraineeDetailActivity.this, WorkoutHistoryActivity.class);
                        w.putExtra(TraineeDetailFragment.ARG_ITEM_ID, trainee_id);
                        w.putExtra("trainee_id", trainee_id);
                        w.putExtra("name", name);
                        startActivity(w);
                        break;
                    case 1:
                        Intent m = new Intent(TraineeDetailActivity.this, MessageActivity.class);
                        m.putExtra(TraineeDetailFragment.ARG_ITEM_ID, trainee_id);
                        m.putExtra("trainee_id", trainee_id);
                        m.putExtra("name", name);
                        startActivity(m);
                        break;
                    case 2:
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
                .setIcon(R.drawable.ic_action_go_to_today)
                .setTabListener(tabListener), 0, false);

        actionBar.addTab(actionBar.newTab()
                .setText("Messages")
                .setIcon(R.drawable.ic_action_chat)
                .setTabListener(tabListener), 1, false);

        actionBar.addTab(actionBar.newTab()
                .setText("Contact")
                .setIcon(R.drawable.ic_action_person)
                .setTabListener(tabListener), 2, true);
}

    public void onBackPressed() {
        Intent intent = new Intent(TraineeDetailActivity.this, TraineeListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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

            Intent i = new Intent(TraineeDetailActivity.this, LoginActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_settings) {
            Intent i = new Intent(TraineeDetailActivity.this, SettingsActivity.class);
            i.putExtra("token", token);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
