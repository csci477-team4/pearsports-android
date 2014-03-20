package com.example.app.trainee;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Shay on 3/20/14.
 */
public class Workout implements Serializable {

    public String id;
    public String title;
    public String activityType;
    public String distance;
    public String duration;
    public String descriptionHTML;
    public String descriptionShort;
    public String graphURL;
    public String iconURL;
    public String notes;
    public String completed_at;
    public String status;
    public String scheduledAt;

    public HashMap<String,String> workoutMap;
    public Result result;

    public Workout() {
        workoutMap = new HashMap<String, String>(13);
        result = new Result();
    }

    /**
     * Holds the data that is only available if the workout
     * has been completed.
     */
    public class Result implements Serializable {

        public String resultAt;
        public String averageHR;
        public String calories;
        public String grade;
        public String coachNotes;
        public String hrDataURL;
        public String dataURL;

        public HashMap<String,String> resultMap;

        public Result() {
            resultMap = new HashMap<String, String>(7);
        }

    }

}
