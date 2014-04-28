package com.example.app;

import android.graphics.drawable.Drawable;

/**
 * Created by tcparker on 4/28/14.
 */
public class WorkoutItem {
    private Drawable pic;
    private String name;
    private String sku;

    public WorkoutItem(Drawable pic, String name, String sku) {
        this.pic = pic;
        this.name = name;
        this.sku = sku;
    }
    public Drawable getPic () { return pic; }
    public String getName() {
        return name;
    }
    public String getSku() { return sku; }
}