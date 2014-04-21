package com.example.app.trainee;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TraineeContent implements Serializable {

    private static final long serialVersionUID = 1L;

    // An array of trainee IDs
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
    public static void resetTraineeContent() {
        TRAINEE_MAP.clear();
        TRAINEES.clear();
    }

    public static void resetWorkoutContent() {
        for (TraineeItem t : TRAINEES) {
            t.getWorkoutMap().clear();
            t.getWorkouts().clear();
        }
    }

    /**
     * Trainee
     */
    public static class TraineeItem implements Serializable {
        public String id;
        public String name;

        private HashMap<String,String> traineeInfo;
        private Stats stats;

        private ArrayList<Workout> workouts;
        private ArrayList<Workout> weekWorkouts;
        private HashMap<String,Workout> workoutMap;

        public TraineeItem(String id, String name) {
            this.id = id;
            this.name = name;
            workoutMap = new HashMap<String, Workout>();
            traineeInfo = new HashMap<String, String>(8);
            traineeInfo.put("id", id);
            traineeInfo.put("name", name);

            stats = new Stats();
            workouts = new ArrayList<Workout>();
            weekWorkouts = new ArrayList<Workout>();
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

        public HashMap<String,Workout> getWorkoutMap() { return workoutMap; }

        public ArrayList<Workout> getWorkouts() {
            return workouts;
        }

        public ArrayList<Workout> getWeekWorkouts() {
            return weekWorkouts;
        }

        public void printInfo() {
            Log.d("TRAINEE INFO: ", traineeInfo.toString());
        }

        public void printStats() {
            Log.d("TRAINEE STATS: ", stats.getStatsMap().toString());
        }

        /**
         *
         */
        private static class Stats implements Serializable {

            private HashMap<String,String> statsMap;

            public Stats() {
                statsMap = new HashMap<String, String>(9);
            }

            public HashMap<String,String> getStatsMap() {
                return statsMap;
            }
        }


    }
}
