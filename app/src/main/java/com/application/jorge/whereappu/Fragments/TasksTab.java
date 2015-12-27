package com.application.jorge.whereappu.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.Dialogs.NewTaskDialog;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.Views.TaskBubble;
import com.application.jorge.whereappu.Views.TaskListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TasksTab extends android.support.v4.app.Fragment {
    @InjectView(R.id.taskLayout)
    LinearLayout taskLayout;

    @InjectView(R.id.bubbleList)
    LinearLayout bubbleList;

    public TaskListView taskListView;

    public static TasksTab newInstance() {
        return new TasksTab();
    }

    public TasksTab() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        ButterKnife.inject(this, v);
        try {
            taskListView = new TaskListView(getActivity(), User.getMySelf(),new TaskListView.TaskSelectorFunction() {
                @Override
                public List<Task> getTasks() {
                    try {
                        constructNewTasksBubbles();
                        return Task.getReceivedTask();
                    } catch (Exception e) {
                        utils.saveExceptionInFolder(e);
                    }
                    return null;
                }
            });
            taskLayout.addView(taskListView, 0);
            taskListView.refreshTasks();
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
        return v;
    }

    private void constructNewTasksBubbles() throws Exception {
        bubbleList.removeAllViews();
        ArrayList<User> users = User.getUsersWithActiveComments();
        for(User user : users) {
            TaskBubble taskBubble = new TaskBubble(getActivity(), user);
            taskBubble.setUser(user);
            bubbleList.addView(taskBubble);
        }
    }

    @OnClick(R.id.addPlaceTaskButton)
     public void onNewPlaceTask(){
        onNewTaskButton(Task.TYPE_PLACE);
    }

    @OnClick(R.id.addScheduleTaskButton)
    public void onNewScheduleTask(){
        onNewTaskButton(Task.TYPE_SCHEDULE);
    }

    public void onNewTaskButton(String type) {
        final NewTaskDialog taskDialog = new NewTaskDialog();
        taskDialog.task = new Task(User.getMySelf(), User.getMySelf(), "");
        taskDialog.task.Type = type;
        taskDialog.onDismissListener = new NewTaskDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean answer) {
                if (answer) {
                    TabsActivity.syncTasks(getActivity(), taskListView.refreshRunnable);
                }
            }
        };
        taskDialog.show(getFragmentManager(), "Diag");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
