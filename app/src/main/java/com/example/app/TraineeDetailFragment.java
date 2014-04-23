package com.example.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        mItem = TraineeContent.TRAINEE_MAP.get(getArguments().getString(ARG_ITEM_ID));

//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            // Load the dummy name specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load name from a name provider.
//            //mItem is a TraineeItem
//            mItem = TraineeContent.TRAINEE_MAP.get(getArguments().getString(ARG_ITEM_ID));
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trainee_detail, container, false);

        // Show the name as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.trainee_detail_text)).setText(mItem.name);
            ((TextView) rootView.findViewById(R.id.age_text)).setText("Age: " + mItem.getInfoMap().get("age"));
            ((TextView) rootView.findViewById(R.id.height_text)).setText("Height: " + mItem.getInfoMap().get("height"));
            ((TextView) rootView.findViewById(R.id.weight_text)).setText("Weight: " + mItem.getInfoMap().get("weight"));

            ((TextView) rootView.findViewById(R.id.workout_text)).setText(mItem.getStatsMap().get("workout_count") + "\nWorkouts");
            ((TextView) rootView.findViewById(R.id.calories_text)).setText(mItem.getStatsMap().get("calories") + "\nCalories");
            ((TextView) rootView.findViewById(R.id.miles_text)).setText(mItem.getStatsMap().get("distance_mi") + "\nMiles");
            ((TextView) rootView.findViewById(R.id.minutes_text)).setText(mItem.getStatsMap().get("duration_formatted") + "\nTime");

            // dynamically display the trainee image
            ImageView trainee_image = (ImageView) rootView.findViewById(R.id.image_trainee);
            int imageResource = getResources().getIdentifier(mItem.getInfoMap().get("image"), null, getActivity().getPackageName());
            Drawable drawable = getResources().getDrawable(imageResource);
            trainee_image.setImageDrawable(drawable);
        }

        return rootView;
    }




}
