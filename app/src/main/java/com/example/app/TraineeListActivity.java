package com.example.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.app.trainee.TraineeContent;


/**
 * An activity representing a list of Trainees. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TraineeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TraineeListFragment} and the item details
 * (if present) is a {@link TraineeDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link TraineeListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class TraineeListActivity extends FragmentActivity
        implements TraineeListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private ProgressDialog progressDialogue;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_list);

        if (findViewById(R.id.trainee_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((TraineeListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.trainee_list))
                    .setActivateOnItemClick(true);
        }

        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        ImageView pic1 = (ImageView) findViewById(R.id.image_trainee);
        pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: this is hardcoded hax to store selected trainee. find a better way.
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit= pref.edit();
                edit.putString("trainee_id", TraineeContent.TRAINEES.get(0).id);
                edit.apply();
                Intent i = new Intent(TraineeListActivity.this, WorkoutHistoryActivity.class);
                startActivity(i);
            }
        });

        ImageView pic2 = (ImageView) findViewById(R.id.image_trainee2);
        pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: this is hardcoded hax to store selected trainee. find a better way.
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit= pref.edit();
                edit.putString("trainee_id", TraineeContent.TRAINEES.get(1).id);
                edit.apply();
                Intent i = new Intent(TraineeListActivity.this, WorkoutHistoryActivity.class);
                startActivity(i);
            }
        });

        ImageView arrow1 = (ImageView) findViewById(R.id.right_arrow1);
        arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TraineeListActivity.this, MessageActivity.class);
                startActivity(i);
            }
        });

        ImageView arrow2 = (ImageView) findViewById(R.id.right_arrow2);
        arrow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TraineeListActivity.this, MessageActivity.class);
                startActivity(i);
            }
        });

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link TraineeListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TraineeDetailFragment.ARG_ITEM_ID, id);
            TraineeDetailFragment fragment = new TraineeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.trainee_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, TraineeDetailActivity.class);
            detailIntent.putExtra(TraineeDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.remove("trainee_id");
            edit.apply();

            Intent i = new Intent(TraineeListActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    protected String getToken() {
        return token;
    }

}
