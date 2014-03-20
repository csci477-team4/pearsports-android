package com.example.app.trainee;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class TraineeContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * An array of trainee IDs
     */
    public static List<TraineeItem> TRAINEES = new ArrayList<TraineeItem>();

    /**
     * Maps String trainee_id to TraineeItem
     */
    public static Map<String, TraineeItem> TRAINEE_MAP = new HashMap<String, TraineeItem>();

    public static void addItem(TraineeItem item) {
        TRAINEES.add(item);
        TRAINEE_MAP.put(item.id, item);
    }

    /**
     * Manual reset/clear
     */
    public static void resetContent() {
        TRAINEE_MAP.clear();
        TRAINEES.clear();
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class TraineeItem {
        public String id;
        public String name;

        private HashMap<String,String> traineeInfo;
        private Stats stats;

        private ArrayList<Workout> workouts;

        public TraineeItem(String id, String name) {
            this.id = id;
            this.name = name;

            traineeInfo = new HashMap<String, String>(8);
            traineeInfo.put("id", id);
            traineeInfo.put("name", name);
            traineeInfo.put("email", null);
            traineeInfo.put("dob", null);
            traineeInfo.put("gender", null);
            traineeInfo.put("age", null);
            traineeInfo.put("height", null);
            traineeInfo.put("weight", null);
            traineeInfo.put("image",null); // image path

            stats = new Stats();
            workouts = new ArrayList<Workout>();
        }

        @Override
        public String toString() {
            return name;
        }

        public HashMap<String,String> getInfoMap(){
            return traineeInfo;
        }

        public HashMap<String,String> getStatsMap() {
            return stats.getStatsMap();
        }

        public void printInfo() {
            Log.d("TRAINEE INFO: ", traineeInfo.toString());
        }

        public void printStats() {
            Log.d("TRAINEE STATS: ", stats.getStatsMap().toString());
        }

        /**
         * ...working on how to best store this... :(
         *  Strings for now...
         */
        private class Stats {

            private HashMap<String,String> statsMap;

            public Stats() {
                statsMap = new HashMap<String, String>(9);
                statsMap.put("distance",null);
                statsMap.put("duration",null);
                statsMap.put("calories",null);
                statsMap.put("workout_count",null);
                statsMap.put("distance_km",null);
                statsMap.put("distance_mi",null);
                statsMap.put("duration_formatted",null);
                statsMap.put("object",null);
                statsMap.put("object_md5",null);
            }

            public HashMap<String,String> getStatsMap() {
                return statsMap;
            }
        }


    }
}
