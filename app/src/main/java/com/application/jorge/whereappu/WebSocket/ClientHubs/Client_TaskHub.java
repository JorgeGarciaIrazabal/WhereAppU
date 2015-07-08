package com.application.jorge.whereappu.WebSocket.ClientHubs;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.ChatActivity;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;

import org.json.JSONObject;

public class Client_TaskHub {
    public static void newTask(Object taskJson) {
        try {
            Task task = Task.getFromJson((JSONObject) taskJson);
            task.__Updated = 1;
            task.save();
            updateTaskView();
            App.wsHubsApi.TaskHub.server.successfullyReceived(task.ID);
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public static void confirmReceived(Object taskId) {
        try {
            Task task = Task.getById(utils.getLong(taskId));
            task.State = Task.STATE_ARRIVED; //todo check if state is lower that arrived
            task.save();
            updateTaskView();

        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public static void updateTaskView() {
        App.getAppActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (App.activeActivity.getClass().equals(TabsActivity.class))
                        ((TabsActivity) App.activeActivity).tasksTabFragment.taskListView.refreshTasks();
                    else if (App.activeActivity.getClass().equals(ChatActivity.class))
                        ((ChatActivity) App.activeActivity).chatList.refreshTasks();
                } catch (Exception e) {
                    utils.saveExceptionInFolder(e);
                }
            }
        });
    }
}