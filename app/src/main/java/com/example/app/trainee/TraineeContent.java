package com.example.app.trainee;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TraineeContent implements Serializable {

    private static final long serialVersionUID = 2L;

    private static TraineeContent instance = null;

    /**
     * An array of trainee IDs
     */
    public List<TraineeItem> TRAINEES = new ArrayList<TraineeItem>();

    /**
     * Maps String trainee_id to TraineeItem
     */
    public Map<String, TraineeItem> TRAINEE_MAP = new HashMap<String, TraineeItem>();

    protected TraineeContent() {}

    public static TraineeContent getInstance() {
        if (instance == null) {
            instance = new TraineeContent();
        }
        return instance;
    }

    public void addItem(TraineeItem item) {
        TRAINEES.add(item);
        TRAINEE_MAP.put(item.id, item);
    }

    /**
     * Manual reset/clear
     */
    public void resetTraineeContent() {
        TRAINEE_MAP.clear();
        TRAINEES.clear();
    }

    public void resetWorkoutContent() {
        for (TraineeItem t : TRAINEES) {
            t.getWorkoutMap().clear();
            t.getWorkouts().clear();
        }
    }

    public void printTraineeList() {
        Log.d("TraineeList: ", TRAINEES.toString());
    }

    /**
     * Trainee
     */
    public class TraineeItem implements Serializable {
        public String id;
        public String name;

        private Drawable profile;

        private HashMap<String,String> traineeInfo;
        private Stats stats;

        private ArrayList<Workout> workouts;
        private ArrayList<Workout> weekWorkouts;
        private HashMap<String,Workout> workoutMap;

        private int[] incomplete = new int[7];
        private int[] complete = new int[7];
        private int[] marked_complete = new int[7];
        private int[] scheduled = new int[7];

        public TraineeItem(String id, String name) {
            this.id = id;
            this.name = name;
            workoutMap = new HashMap<String, Workout>();
            traineeInfo = new HashMap<String, String>(8);
            traineeInfo.put("id", id);
            traineeInfo.put("name", name);

            for (int i=0; i<7; i++) {
                incomplete[i] = 0;
                complete[i] = 0;
                marked_complete[i] = 0;
                scheduled[i] = 0;
            }

            stats = new Stats();
            workouts = new ArrayList<Workout>();
            weekWorkouts = new ArrayList<Workout>();
        }

        @Override
        public String toString() {
            return name;
        }

        public Drawable getProfile() { return profile; }

        public void setProfile(Drawable profile) { this.profile = profile; }

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

        public void printWeekWorkouts() { Log.d("TRAINEE WEEK WORKOUTS: ", workouts.toString()); }

        public void printStats() {
            Log.d("TRAINEE STATS: ", stats.getStatsMap().toString());
        }

        public int[] getIncomplete() {
            return incomplete;
        }

        public int[] getComplete() {
            return complete;
        }

        public int[] getMarked_Complete() {
            return marked_complete;
        }

        public int[] getScheduled() {
            return scheduled;
        }

        public void setIncompleteIndex(int pos, int value) { this.incomplete[pos] = value; }

        public void setCompletedIndex(int pos, int value) { this.complete[pos] = value; }

        public void setMarkedIndex(int pos, int value) { this.marked_complete[pos] = value; }

        public void setScheduledIndex(int pos, int value) { this.scheduled[pos] = value; }

        public int getIncompleteIndex(int pos) { return incomplete[pos]; }

        public int getCompletedIndex(int pos) { return complete[pos]; }

        public int getMarkedIndex(int pos) { return marked_complete[pos]; }

        public int getScheduledIndex(int pos) { return scheduled[pos]; }

        /**
         *
         */
        private class Stats implements Serializable {

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
