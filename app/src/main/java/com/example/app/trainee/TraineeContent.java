package com.example.app.trainee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class TraineeContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<TraineeItem> TRAINEES = new ArrayList<TraineeItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, TraineeItem> TRAINEE_MAP = new HashMap<String, TraineeItem>();

    static {
        // Add 3 sample items.
        addItem(new TraineeItem("1", "Name 1"));
        addItem(new TraineeItem("2", "Name 2"));
        addItem(new TraineeItem("3", "Name 3"));
    }

    public static void addItem(TraineeItem item) {
        TRAINEES.add(item);
        TRAINEE_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class TraineeItem {
        public String id;
        public String name;

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
