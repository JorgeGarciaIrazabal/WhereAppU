package com.application.jorge.whereappu.WebSocket.ClientHubs;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.ChatActivity;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Classes.NotificationHandler;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.WebSocket.ClientBase;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;

import org.json.JSONObject;

public class Client_TaskHub extends ClientBase {
    public Client_TaskHub(WSHubsApi wsHubsApi) {
        super(wsHubsApi);
    }

    public void newTask(Object taskJson) {
        try {
            utils.log("Receiving task");
            final Task task = Task.getFromJson((JSONObject) taskJson);
            task.__Updated = 1;
            task.State = Task.STATE_ARRIVED;
            task.save();

            if (App.isAppRunning())
                updateTaskView();

            NotificationHandler.showNotification(App.getAppContext());
            this.wsHubsApi.TaskHub.server.syncTask(task);

        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public void taskUpdated(Object taskJson) {
        try {
            utils.log("Updating task");
            final Task task = Task.getFromJson((JSONObject) taskJson);
            task.__Updated = 1;
            task.save();

            if (App.isAppRunning())
                updateTaskView();

            NotificationHandler.showNotification(App.getAppContext());
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public static void updateTaskView() {
        if (App.isAppRunning()) {
            App.getAppActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (App.activeActivity.getClass().equals(TabsActivity.class)) {
                            ((TabsActivity) App.activeActivity).tasksTabFragment.taskListView.refreshTasks();
                            ((TabsActivity) App.activeActivity).contactsTabFragment.refreshContactList();
                        } else if (App.activeActivity.getClass().equals(ChatActivity.class))
                            ((ChatActivity) App.activeActivity).chatList.refreshTasks();
                    } catch (Exception e) {
                        utils.saveExceptionInFolder(e);
                    }
                }
            });
        }
    }
}