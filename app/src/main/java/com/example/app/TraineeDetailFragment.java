package com.example.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app.trainee.TraineeContent;

/**
 * A fragment representing a single Trainee detail screen.
 * This fragment is either contained in a {@link TraineeListActivity}
 * in two-pane mode (on tablets) or a {@link TraineeDetailActivity}
 * on handsets.
 */
public class TraineeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy name this fragment is presenting.
     */
    private TraineeContent.TraineeItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TraineeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy name specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load name from a name provider.
            mItem = TraineeContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trainee_detail, container, false);

        // Show the dummy name as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.trainee_detail)).setText(mItem.name);
        }

        return rootView;
    }
}
