package com.example.civiv;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Log.d("MyApp", "Firebase inicializado correctamente");
    }

}
