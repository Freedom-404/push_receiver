package com.artemkjv.push_receiver;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.artemkjv.push_receiver.notification.AppContextKeeper;
import com.artemkjv.push_receiver.notification.PushRegistrator;
import com.artemkjv.push_receiver.notification.PushRegistratorFCM;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppContextKeeper.appContext = this;
        PushRegistratorFCM pushRegistratorFCM = new PushRegistratorFCM();
        pushRegistratorFCM.registerForPush(AppContextKeeper.appContext, "92348455022", new PushRegistrator.RegisteredHandler() {
            @Override
            public void complete(String id, int status) {
                Log.e("Log", id);
            }
        });
    }

}
