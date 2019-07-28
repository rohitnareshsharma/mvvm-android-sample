package test.com.homeaway;

import android.app.Application;
import android.content.Context;
import com.easyvolley.NetworkClient;

public class HomeAwayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkClient.init(this);
    }

}
