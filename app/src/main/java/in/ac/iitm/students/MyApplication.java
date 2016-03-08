package in.ac.iitm.students;

import android.app.Application;


import java.util.HashMap;

/**
 * Created by arunp on 09-Mar-16.
 */
public class MyApplication extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static android.content.Context getContext() {
        return instance.getApplicationContext();
    }

}