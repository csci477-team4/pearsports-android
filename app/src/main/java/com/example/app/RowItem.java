package com.example.app;

import android.graphics.drawable.Drawable;

/**
 * Created by tcparker on 3/25/14.
 */
public class RowItem {
    private Drawable profile;
    private int rightArrow;
    private String traineeName;
    private int[] incomplete = new int[7];
    private int[] complete = new int[7];
    private int[] marked = new int[7];
    private int[] scheduled = new int[7];

    public RowItem(Drawable profile, int rightArrow, String traineeName, int[] incomplete, int[] complete, int[] marked, int[] scheduled) {
        this.profile = profile;
        this.rightArrow = rightArrow;
        this.traineeName = traineeName;
        this.incomplete = incomplete;
        this.complete = complete;
        this.marked = marked;
        this.scheduled = scheduled;
    }
    public Drawable getProfile () { return profile; }
    public int getRightArrow() {
        return rightArrow;
    }
    public String getTraineeName() {
        return traineeName;
    }

    public int[] getIncomplete() { return incomplete; }
    public int[] getComplete() { return complete; }
    public int[] getMarked() { return marked; }
    public int[] getScheduled() { return scheduled; }
}