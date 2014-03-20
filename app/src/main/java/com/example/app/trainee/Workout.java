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
//        workoutMap.put("id", null);
//        workoutMap.put("title", null);
//        workoutMap.put("activity_type", null);
//        workoutMap.put("distance", null);
//        workoutMap.put("duration", null);
//        workoutMap.put("description_html", null);
//        workoutMap.put("description_short", null);
//        workoutMap.put("graph_url", null);
//        workoutMap.put("icon_url",null);
//        workoutMap.put("notes",null);
//        workoutMap.put("completed_at",null);
//        workoutMap.put("status",null);
//        workoutMap.put("scheduled_at",null);
    }

    public HashMap<String,String> getWorkoutMap() {
        return workoutMap;
    }

    public Result getResult() { return result; }

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
//            resultMap.put("result_at", null);
//            resultMap.put("avg_hr", null);
//            resultMap.put("calories",null);
//            resultMap.put("grade",null);
//            resultMap.put("coach_notes",null);
//            resultMap.put("hr_data_url",null);
//            resultMap.put("data_url",null);
        }

        public void printResult() {
            Log.d("RESULT: ", resultMap.toString());
        }

        public HashMap<String,String> getResultMap() { return resultMap; }

    }

}
