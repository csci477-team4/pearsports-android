package com.example.app.trainee;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Shay on 3/20/14.
 */
public class Workout implements Serializable {

    private HashMap<String,String> workoutMap;

    public Workout() {
        workoutMap = new HashMap<String, String>(13);
    }

    public HashMap<String,String> getWorkoutMap() {
        return workoutMap;
    }

    public boolean isCompleted() {
        return workoutMap.get("status").toString().equals("completed");
    }

    public void printWorkout() {
        Log.d("WORKOUT: ", workoutMap.toString());
    }

}
