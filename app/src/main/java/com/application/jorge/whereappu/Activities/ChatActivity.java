package com.application.jorge.whereappu.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.Dialogs.NewTaskDialog;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.Views.TaskListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ChatActivity extends AppCompatActivity {

    @InjectView(R.id.comment)
    EditText comment;
    @InjectView(R.id.chatLayout)
    LinearLayout chatLayout;

    public TaskListView  chatList;
    public static User receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_chat);
            App.activeActivity = ChatActivity.this;
            ButterKnife.inject(this);
            chatList = new TaskListView(this, receiver, new TaskListView.TaskSelectorFunction() {
                @Override
                public List<Task> getTasks() {
                    try {
                        return Task.getTasksWith(receiver);
                    } catch (Exception e) {
                        utils.saveExceptionInFolder(e);
                    }
                    return null;
                }
            });
            chatLayout.addView(chatList, 0);
            chatList.refreshTasks();
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.activeActivity = ChatActivity.this;
        App.activityResumed();
    }
    @Override
    protected  void onPause(){
        super.onPause();
        App.activityPaused();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.newScheduleTaskButton)
    public void onTimerTaskButton() {
        final NewTaskDialog taskDialog = new NewTaskDialog();
        taskDialog.task = new Task(User.getMySelf(),receiver, "");
        taskDialog.task.Type = Task.TYPE_SCHEDULE;
        taskDialog.onDismissListener = new NewTaskDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean answer) {
                if (answer) {
                    TabsActivity.syncTasks(ChatActivity.this, chatList.refreshRunnable);
                }
            }
        };
        taskDialog.show(getSupportFragmentManager(), "Diag");
    }

    @OnClick(R.id.newLocationTaskButton)
    public void onPlaceTaskButton() {
        final NewTaskDialog taskDialog = new NewTaskDialog();
        taskDialog.task = new Task(User.getMySelf(), receiver, "");
        taskDialog.task.Type = Task.TYPE_PLACE;
        taskDialog.onDismissListener = new NewTaskDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean answer) {
                if (answer) {
                    TabsActivity.syncTasks(ChatActivity.this, chatList.refreshRunnable);
                }
            }
        };
        taskDialog.show(getSupportFragmentManager(), "Diag");
    }

    @OnClick(R.id.commentSender)
    public void sendComment() {
        try {
            String commentString = comment.getText().toString();
            if(!commentString.isEmpty()) {
                Task task = new Task(User.getMySelf(), receiver, commentString);
                task.Type = Task.TYPE_COMMENT;
                task.write();
                TabsActivity.syncTasks(ChatActivity.this, chatList.refreshRunnable);
                comment.setText("");
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }
}
