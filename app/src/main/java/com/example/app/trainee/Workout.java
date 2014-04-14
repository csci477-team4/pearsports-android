package com.example.app.trainee;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Shay on 3/20/14.
 */
public class Workout implements Serializable {

    private HashMap<String,String> workoutMap;
    private Result result;

    public Workout() {
        result = new Result();
        workoutMap = new HashMap<String, String>(13);
    }

    public HashMap<String,String> getWorkoutMap() {
        return workoutMap;
    }

    public Result getResult() { return result; }

    public boolean isCompleted() {
        return workoutMap.get("status").toString().equals("completed");
    }

    public void printWorkout() {
        Log.d("WORKOUT: ", workoutMap.toString());
    }

    /**
     * Holds the data that is only available if the workout
     * has been completed.
     */
    public class Result implements Serializable {

        public HashMap<String,String> resultMap;

        public Result() {

            resultMap = new HashMap<String, String>(7);
        }

        public void printResult() {
            Log.d("RESULT: ", resultMap.toString());
        }

        public HashMap<String,String> getResultMap() { return resultMap; }

    }

}
