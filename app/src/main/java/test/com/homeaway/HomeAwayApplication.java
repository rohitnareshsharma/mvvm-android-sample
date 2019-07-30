package test.com.homeaway;

import android.app.Application;
import android.content.Context;
import com.easyvolley.NetworkClient;

public class HomeAwayApplication extends Application {


    private static HomeAwayApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        NetworkClient.init(this);
    }

    public static Context getAppContext() {
        return INSTANCE.getApplicationContext();
    }

}
