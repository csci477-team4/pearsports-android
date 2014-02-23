package com.example.app.trainee;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class TraineeContent {

    /**
     * An array of trainee IDs
     */
    public static List<TraineeItem> TRAINEES = new ArrayList<TraineeItem>();

    /**
     * Maps trainee ID to name
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
        public Stats stats;

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

            stats = new Stats();
        }

        @Override
        public String toString() {
            return name;
        }

        public HashMap<String,String> getInfoMap(){
            return traineeInfo;
        }

        public void printInfo() {
            Log.d("TRAINEE INFO: ", traineeInfo.toString());
        }


        /**
         * ...working on how to best store this... :(
         */
        public class Stats {
            public float distance;
            public float duration;
            public float calories;
            public int workout_count;
            public float distance_km;
            public float distance_mi;
            public String duration_formatted;
            public String object;
            public String object_md5;

            public Stats() { }
        }


    }
}
