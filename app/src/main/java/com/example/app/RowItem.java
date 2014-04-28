package com.example.app;

/**
 * Created by tcparker on 3/25/14.
 */
public class RowItem {
    private int traineeId;
    private int rightArrow;
    private String traineeName;
    private int[] incomplete = new int[7];
    private int[] complete = new int[7];
    private int[] marked = new int[7];
    private int[] scheduled = new int[7];

    public RowItem(int traineeId, int rightArrow, String traineeName, int[] incomplete, int[] complete, int[] marked, int[] scheduled) {
        this.traineeId = traineeId;
        this.rightArrow = rightArrow;
        this.traineeName = traineeName;
        this.incomplete = incomplete;
        this.complete = complete;
        this.marked = marked;
        this.scheduled = scheduled;
    }
    public int getTraineeId() {
        return traineeId;
    }
    public void setTraineeId(int traineeId) {
        this.traineeId = traineeId;
    }
    public int getRightArrow() {
        return rightArrow;
    }
    public void setRigthArrow(int rightArrow) {
        this.rightArrow = rightArrow;
    }
    public String getTraineeName() {
        return traineeName;
    }
    public void setTraineeName(String traineeName) {
        this.traineeName = traineeName;
    }
    public int[] getIncomplete() { return incomplete; }
    public int[] getComplete() { return complete; }
    public int[] getMarked() { return marked; }
    public int[] getScheduled() { return scheduled; }
}