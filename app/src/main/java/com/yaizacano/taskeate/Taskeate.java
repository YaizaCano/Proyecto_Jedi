package com.yaizacano.taskeate;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class Taskeate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
