package com.example.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app.trainee.TraineeContent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A list fragment representing a list of Trainees. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link TraineeDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class TraineeListFragment extends ListFragment {

    private List<TraineeContent.TraineeItem> listTrainees;
    private String traineeID;
    private TraineeContent traineeContent = TraineeContent.getInstance();

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private JSONObject trainee_list = null;
    private JSONObject trainee_info = null;
    private JSONObject trainee_stats_list = null;
    private JSONObject trainee_stats = null;
    private String token;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TraineeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        token = preferences.getString("token",null);
        traineeID = preferences.getString("trainee_id", null);
        if (isOnline()) {
            Log.d("TraineeListFragment::onCreate >> ", "refresh.");
            refresh();
        }
        // execute?
        listTrainees = traineeContent.TRAINEES;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Add the current trainee to trainee_id in shared preferences
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor edit= pref.edit();
        edit.putString("trainee_id", traineeContent.TRAINEES.get(position).id);
        edit.apply();

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(traineeContent.TRAINEES.get(position).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    /**
     * Manual refresh
     */
    public void refresh() {
        traineeContent.resetTraineeContent();
    }

    public void executeAsyncTasks() {
        new GetTraineeList().execute(token);
        new GetStats().execute();
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private class GetTraineeList extends AsyncTask<String,Void,TraineeContent>
    {
        @Override
        protected TraineeContent doInBackground(String... params) {
            //List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            //parameters.add(new BasicNameValuePair(LoginActivity.EMAIL, LoginActivity.PASSWORD));
            if (isOnline()) {
                try {
                    JSONObject jsonObj = APIHandler.sendAPIRequestWithAuth("trainee_list", APIHandler.GET, token, "");

                    //Log.d("Response: ", ">>> " + jsonObj);

                    if (jsonObj != null) {
                        try {
                            trainee_list = jsonObj.getJSONObject("trainee_list");

                            for (Iterator<String> keys = trainee_list.keys(); keys.hasNext(); ) {
                                String id = keys.next();
                                trainee_info = trainee_list.getJSONObject(id); // map of trainee info
                                TraineeContent.TraineeItem trainee = traineeContent.new TraineeItem(id, trainee_info.get("screen_name").toString());

                                HashMap<String, String> info = trainee.getInfoMap();
                                info.put("email", trainee_info.get("email").toString());
                                info.put("dob", trainee_info.get("dob").toString());
                                info.put("gender", trainee_info.get("gender").toString());
                                info.put("age", trainee_info.get("age").toString());
                                info.put("height", trainee_info.get("height").toString());
                                info.put("weight", trainee_info.get("weight").toString());
                                info.put("notes", trainee_info.get("notes").toString());
                                info.put("photo_url", trainee_info.get("photo_url").toString());

                                if(trainee_info.get("photo_url").toString() != null) {
                                    info.put("image", "drawable/default_prof");
                                }
                                else {
                                    info.put("image", "drawable/default_prof");
                                }

                                traineeContent.addItem(trainee);
                            }
                            writeTraineeContent();
                        } catch (JSONException e) {
                            Log.e("TraineeListFragment::GetTraineeList : ", "JSONException: " + e.getMessage());
                            e.printStackTrace();
                            loadTraineeContent();
                        }
                    } else {
                        Log.e("APIHandler", "No data from specified URL");
                        loadTraineeContent();
                    }
                } catch (IOException e) {
                    Log.d("TraineeListFragment::GetTraineeList::IOException >> ", e.getMessage());
                    e.printStackTrace();
                    loadTraineeContent();
                }
            } else {
                Log.d("TraineeListFragment::GetTraineeList >> ", "Not online.");
                loadTraineeContent();
            }
            return traineeContent;
        }

        @Override
        protected void onPostExecute(final TraineeContent success) {
            //if(success && loadTraineeContent()){
                ViewGroup parent = (ViewGroup) getActivity().findViewById(R.id.trainee_list_container);
                for (TraineeContent.TraineeItem t : listTrainees) {
                    traineeID = t.getInfoMap().get("id");
                    View.OnClickListener traineeListener = new View.OnClickListener() {
                        String id = traineeID;

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity().getBaseContext(), TraineeDetailActivity.class);
                            intent.putExtra("trainee_id", id);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    };
                    View view = null;

                    LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.trainee_row, null);

                    //Drawable trainee = getActivity().getApplicationContext().getResources().getDrawable(R.drawable.trainee_1);

                    ((TextView) view.findViewById(R.id.trainee_name)).setText(t.getInfoMap().get("name"));
                    //((ImageView) view.findViewById(R.id.trainee_pic)).setImageDrawable(trainee);

                    view.setOnClickListener(traineeListener);

                    parent.addView(view);
                }
            //}
        }
    }

    private class GetStats extends AsyncTask<Void,Void,TraineeContent>
    {
        @Override
        protected TraineeContent doInBackground(Void... params) {
            if (isOnline()) {
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("all", "True"));
                try {
                    JSONObject statsJSON = APIHandler.sendAPIRequestWithAuth("stats", APIHandler.GET, token, "", parameters);

                    //Log.d("Response: ", ">>> " + statsJSON);

                    if (statsJSON != null) {
                        try {
                            trainee_stats_list = statsJSON.getJSONObject("trainee_stats_list");

                            for (Iterator<String> keys = trainee_stats_list.keys(); keys.hasNext(); ) {
                                String id = keys.next(); // keys are the trainee_ids
                                trainee_stats = trainee_stats_list.getJSONObject(id); // map of trainee stats
                                TraineeContent.TraineeItem trainee = traineeContent.TRAINEE_MAP.get(id);
                                HashMap<String, String> map = trainee.getStatsMap();
                                for (Iterator<String> iter = trainee_stats.keys(); iter.hasNext(); ) {
                                    String statKey = iter.next();
                                    map.put(statKey, trainee_stats.get(statKey).toString());
                                }
                            }
                            writeTraineeContent();
                        } catch (JSONException e) {
                            Log.e("TraineeListFragment::GetStats : ", "JSONException: " + e.getMessage());
                            e.printStackTrace();
                            loadTraineeContent();
                        }
                    } else {
                        Log.e("APIHandler", "No data from specified URL");
                        loadTraineeContent();
                    }
                } catch (IOException e) {
                    Log.d("TraineeListFragment::GetStats::IOException >> ", e.getMessage());
                    e.printStackTrace();
                    loadTraineeContent();
                }
            } else {
                Log.d("TraineeListFragment::GetStats >> ", "Not online.");
                loadTraineeContent();
            }
            return traineeContent;
        }

        @Override
        protected void onPostExecute(final TraineeContent success) {
            //                ArrayAdapter<TraineeContent.TraineeItem> adapter = new ArrayAdapter<TraineeContent.TraineeItem>(
            //                        getActivity(),
            //                        android.R.layout.simple_list_item_activated_1,
            //                        android.R.id.text1,
            //                        TraineeContent.TRAINEES);
            //                setListAdapter(adapter);
        }
    }

    /*** I/O HELPERS ***/

    private void writeTraineeContent() {
        // serialize TraineeContent
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(getActivity().getFilesDir() + "trainee_content.txt"))); //Select where you wish to save the file...
            oos.writeObject(traineeContent); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
            Log.d("TraineeListFragment >> ", "OOS writeObject, flush, close.");
        } catch (Exception ex) {
            Log.v("Serialization Save Error : ", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean loadTraineeContent() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(getActivity().getFilesDir() + "trainee_content.txt")));
            traineeContent = (TraineeContent) ois.readObject();
            listTrainees = traineeContent.TRAINEES;
            Log.d("TraineeListFragment >> ", "OIS readObject.");
            return true;
        } catch (Exception ex) {
            Log.v("Serialization Read Error : ", ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private boolean isOnline()	{
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected())
            return true;
        return false;
    }

}