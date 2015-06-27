package com.application.jorge.whereappu.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.activeandroid.ActiveAndroid;
import com.application.jorge.whereappu.Classes.utils;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                /* Setting the alarm here */
                ActiveAndroid.initialize(context);
                ScheduleManager.refreshAllScheduledNotifications(context);
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }
}