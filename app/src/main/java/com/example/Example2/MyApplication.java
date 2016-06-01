package com.example.Example2;

import android.app.Application;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alex-lenovi on 5/29/2016.
 */
public class MyApplication extends Application {
    static Context app_context;
    static MyApplication app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        app_context = this;
    }
    public static Context getAppContext(){
        return app_context ;
    }
    public MyApplication getInstance(){
        return app;
    }
    public static String getFileStringFromRes (String file_name, String folder_name){
        try {
            InputStream in_s = app.getResources().openRawResource(app.getResources().getIdentifier(file_name, folder_name, app.getPackageName()));
            byte[] b = new byte[0];
            b = new byte[in_s.available()];
            in_s.read(b);
            return  new String(b);
        } catch (IOException e) {

            e.printStackTrace();
            return "";
        }
    }
}
