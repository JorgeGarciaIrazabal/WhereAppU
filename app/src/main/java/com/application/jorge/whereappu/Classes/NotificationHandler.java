package com.application.jorge.whereappu.Classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.DataBase.DataBaseManager;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;

import java.util.List;

/**
 * Created by Jorge on 27/06/2015.
 */
public class NotificationHandler {

    public static final int WAU_NOTIFICATION_ID = 2486;

    public static void showNotification(Context context) {
        try {
            App.setContextIfNull(context);
            App.db = new DataBaseManager(context);
            List<Task> tasks = Task.getTaskToNotify();
            Bitmap largeIcon;
            int smallIcon = R.drawable.ic_stat_wau;
            String contextText, title;
            if (tasks.size() == 1) {
                Task task = tasks.get(0);
                largeIcon = utils.getBitmap(task.getCreator().getPhoto());
                title = "New task from: " + task.getCreator().Name;
                contextText = task.Body;
            }else if(tasks.size()>1){
                largeIcon = utils.getBitmap(R.drawable.app_launcher);
                title = "Tasks to review: " + tasks.size();
                contextText = title;
            }
            else return;
            Notify(context, title, contextText, largeIcon, smallIcon);
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    private static void Notify(Context context, String title, String contextText, Bitmap largeIcon, int smallIcon) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(smallIcon)
                        .setLargeIcon(largeIcon)
                        .setContentText(contextText)
                        .setContentTitle(title)
                        .setSound(alarmSound);
        Intent resultIntent = new Intent(context, TabsActivity.class);
        //task.Notified = 1;

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
    }

    public static void cancelAll() {
        NotificationManager mNotificationManager = (NotificationManager) App.activeActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }


}
