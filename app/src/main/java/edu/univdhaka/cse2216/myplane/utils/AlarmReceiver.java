package edu.univdhaka.cse2216.myplane.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.univdhaka.cse2216.myplane.activities.MainActivity;



public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                NotificationScheduler.setReminder(context, AlarmReceiver.class);
                return;
            }
        }
        Log.d(TAG, "onReceive: ");
        //Trigger the notification
        NotificationScheduler.showNotification(context, MainActivity.class,
                "It's time to hurry", "You have an upcoming task withing a minute!");
    }
}


