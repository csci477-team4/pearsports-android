package com.example.app.trainee;

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
        public String email;
        public String dob;
        public String gender;
        public int age;
        public float height; //what metrics are being used for this?
        public float weight;

        public TraineeItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
