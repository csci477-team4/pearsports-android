package com.example.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.app.trainee.TraineeContent;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    private JSONObject trainees = null;
    private JSONObject trainee_info = null;

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

        TraineeContent.resetContent();
        new GetTraineeList().execute();
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

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(TraineeContent.TRAINEES.get(position).id);
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
        TraineeContent.resetContent();
        new GetTraineeList().execute();
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }


    private class GetTraineeList extends AsyncTask<Void,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params) {
            APIHandler handler = new APIHandler();
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            //parameters.add(new BasicNameValuePair(LoginActivity.EMAIL, LoginActivity.PASSWORD));
            String jsonStr = handler.sendAPIRequestWithAuth("trainee_list", handler.GET, LoginActivity.EMAIL, LoginActivity.PASSWORD);

            Log.d("Response: ", ">>> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    trainees = jsonObj.getJSONObject("trainee_list");

                    for (Iterator<String> keys = trainees.keys(); keys.hasNext();) {
                        String id = keys.next();
                        trainee_info = trainees.getJSONObject(id); // map of trainee info
                        Log.d("Trainee ID: ", "==> " + id);
                        Log.d("Trainee Name: ", "==> " + trainee_info.get("screen_name").toString());
                        TraineeContent.addItem(new TraineeContent.TraineeItem(id, trainee_info.get("screen_name").toString()));
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
            if(success){
                ArrayAdapter<TraineeContent.TraineeItem> adapter = new ArrayAdapter<TraineeContent.TraineeItem>(
                        getActivity(),
                        android.R.layout.simple_list_item_activated_1,
                        android.R.id.text1,
                        TraineeContent.TRAINEES);
                setListAdapter(adapter);
            }
        }

    }
}