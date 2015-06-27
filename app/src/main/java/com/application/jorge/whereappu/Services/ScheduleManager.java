package com.application.jorge.whereappu.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.NotificationHandler;
import com.application.jorge.whereappu.DataBase.Task;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ScheduleManager extends BroadcastReceiver {
    public static final String CREATOR_ID_TAG = "CreatorId";
    private static AlarmManager alarmMgr;
    private static HashMap<Long, PendingIntent> scheduledIntents = new HashMap<>();
    private static final String TASK_BODY_TAG = "taskId";

    @Override
    public void onReceive(Context context, Intent intent) {

        long taskId = intent.getExtras().getLong(TASK_BODY_TAG);
        long creatorId = intent.getExtras().getLong(CREATOR_ID_TAG);
        scheduledIntents.remove(taskId);
        NotificationHandler.showNotification(context, taskId, creatorId);
    }

    public static void setUpScheduleTaskNotification(Context context, Task task, long oldId) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (scheduledIntents.containsKey(oldId))
            alarmMgr.cancel(scheduledIntents.get(oldId));

//        Calendar cal = Calendar.getInstance();
//        cal.setTime(task.Schedule);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 6);

        Intent intent = new Intent(context, ScheduleManager.class);
        intent.putExtra(TASK_BODY_TAG, task.ID);
        intent.putExtra(CREATOR_ID_TAG, task.Creator.ID);
        intent.setAction("" + Math.random());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
        scheduledIntents.put(task.ID, alarmIntent);
    }

    public static void setUpScheduleTaskNotification(Task task) {
        setUpScheduleTaskNotification(task, Long.MIN_VALUE);
    }

    public static void setUpScheduleTaskNotification(Task task, Long oldId) {
        setUpScheduleTaskNotification(App.getAppContext(), task, oldId);
    }

    public static void setUpScheduleTaskNotification(Context context, Task task) {
        setUpScheduleTaskNotification(context, task, Long.MIN_VALUE);
    }

    public static void refreshAllScheduledNotifications(Context context) {
        List<Task> tasks = Task.getScheduledTaskToNotify();
        for (Task task : tasks) {
            setUpScheduleTaskNotification(context, task);
        }
    }
}
