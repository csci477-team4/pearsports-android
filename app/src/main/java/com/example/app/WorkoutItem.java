package com.example.app;

import android.graphics.drawable.Drawable;

/**
 * Created by tcparker on 4/28/14.
 */
public class WorkoutItem {
    private Drawable pic;
    private String name;
    private String sku;
    private String desc;

    public WorkoutItem(Drawable pic, String name, String sku, String desc) {
        this.pic = pic;
        this.name = name;
        this.sku = sku;
        this.desc = desc;
    }
    public Drawable getPic () { return pic; }
    public String getName() {
        return name;
    }
    public String getSku() { return sku; }
    public String getDesc() { return desc; }
}