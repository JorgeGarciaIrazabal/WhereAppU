package com.application.jorge.whereappu.Classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.activeandroid.ActiveAndroid;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;

/**
 * Created by Jorge on 27/06/2015.
 */
public class NotificationHandler {

    public static final int WAU_NOTIFICATION_ID = 2486;

    public static void showNotification(Context context, long taskId, long creatorId) {
        try {
            Task task = Task.getTaskWithId(taskId);
            utils.log(creatorId);
            User creator = User.getUserByID(creatorId);
            utils.log(creator.Name);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.icon_material_notifications_on)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_launcher))
                            .setContentText(task.Body)
                            .setContentTitle("New task from: " + creator.Name)
                            .setSound(alarmSound);
            Intent resultIntent = new Intent(context, TabsActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(TabsActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(WAU_NOTIFICATION_ID, mBuilder.build());
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public static void cancelAll() {
        NotificationManager mNotificationManager = (NotificationManager) App.context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }


}
