package com.example.app;

/**
 * Created by tcparker on 3/25/14.
 */
public class RowItem {
    private int traineeId;
    private int rightArrow;
    private String traineeName;

    public RowItem(int traineeId, int rightArrow, String traineeName) {
        this.traineeId = traineeId;
        this.rightArrow = rightArrow;
        this.traineeName = traineeName;
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
}