package com.example.app;

import android.app.Application;

import com.ubertesters.sdk.Ubertesters;

/**
 * Created by tcparker on 4/14/14.
 */
public class PearSportsCompanion extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Ubertesters.initialize(this);
    }
}
