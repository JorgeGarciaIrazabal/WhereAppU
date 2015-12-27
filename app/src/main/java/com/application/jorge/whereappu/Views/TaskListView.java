package com.application.jorge.whereappu.Views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Cards.TaskAdapter;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jorge on 24/06/2015.
 */
public class TaskListView extends LinearLayout {
    LayoutInflater li;
    @InjectView(R.id.taskList)
    RecyclerView taskList;

    Context context;
    User sender;
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    public interface TaskSelectorFunction {
        List<Task> getTasks();
    }

    private TaskSelectorFunction taskSelectorFunction;

    public final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                refreshTasks();
            } catch (Exception e) {
                utils.saveExceptionInFolder(e);
            }
        }
    };

    public TaskListView(final Context context, User sender, TaskSelectorFunction taskSelectorFunction) {
        super(context);
        this.context = context;
        this.sender = sender;
        li = LayoutInflater.from(this.context);
        inflate(this.context, R.layout.view_task_list, this);
        ButterKnife.inject(this);
        taskList.setItemAnimator(new DefaultItemAnimator());
        this.taskSelectorFunction = taskSelectorFunction;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    TabsActivity.syncTasks((Activity) context, refreshRunnable);
                } catch (Exception e) {
                    utils.saveExceptionInFolder(e);
                } finally {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void refreshTasks() throws Exception {
        final List<Task> tasks = taskSelectorFunction.getTasks();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Task task : tasks) {
                    if (task.State < Task.STATE_READ && task.ReceiverId == User.getMySelf().ID && task.State > Task.STATE_CREATED) {
                        try {
                            task.State = Task.STATE_READ;
                            task.write();

                            App.wsHubsApi.TaskHub.server.syncTask(task);
                        } catch (Exception e) {
                            utils.saveExceptionInFolder(e);
                        }
                    }
                }
            }
        }).start();
        taskList.setHasFixedSize(true);
        taskList.setLayoutManager(new LinearLayoutManager(context));
        ((LinearLayoutManager) taskList.getLayoutManager()).setStackFromEnd(true);
        taskList.setAdapter(new TaskAdapter(context, tasks));
    }
}
