package com.application.jorge.whereappu.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.NotificationHandler;
import com.application.jorge.whereappu.Classes.utils;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                App.setContextIfNull(context);
                utils.log("Booted successfully");

                /*Intent servIntent = new Intent(activeActivity, MessageService.class);
                servIntent.setAction("StartForeground");
                activeActivity.startService(servIntent);*/

                ScheduleManager.refreshAllScheduledNotifications(context);
                utils.log("successfully inserted schedule alarms");
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }
}