package com.example.app;

import android.content.Intent;
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
     * The name this fragment is presenting.
     */
    private TraineeContent.TraineeItem mItem;

    /**
     * The trainer's token authentication.
     */
    private String token;

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
            //mItem is a TraineeItem
            mItem = TraineeContent.TRAINEE_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trainee_detail, container, false);

        // Show the name as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.trainee_detail_text)).setText(mItem.name);
        }

        rootView.findViewById(R.id.detail_fragment_text_activity_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MessageActivity.class);
                i.putExtra("trainee_id",mItem.id);
                i.putExtra("name", mItem.name);
                startActivity(i);
            }
        });

        rootView.findViewById(R.id.detail_fragment_audio_activity_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Change SplashScreenActivity
                Intent i = new Intent(getActivity(), SplashScreenActivity.class);
                i.putExtra("trainee_id",mItem.id);
                i.putExtra("name", mItem.name);
                startActivity(i);
            }
        });

        rootView.findViewById(R.id.detail_fragment_workout_activity_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Change MainActivity
                Intent i = new Intent(getActivity(), MainActivity.class);
                i.putExtra("trainee_id",mItem.id);
                i.putExtra("name", mItem.name);
                startActivity(i);
            }
        });
        return rootView;
    }




}
