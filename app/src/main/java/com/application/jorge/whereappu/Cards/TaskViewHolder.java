package com.application.jorge.whereappu.Cards;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.Dialogs.NewTaskDialog;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.WebSocket.ClientHubs.Client_TaskHub;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jorge on 01/06/2015.
 */
public class TaskViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.senderName)
    TextView senderName;
    @InjectView(R.id.body)
    TextView body;
    @InjectView(R.id.senderPhoto)
    ImageView photo;
    @InjectView(R.id.statusIcon)
    ImageView statusIcon;
    @InjectView(R.id.typeIcon)
    ImageView typeIcon;
    @InjectView(R.id.createdOnDate)
    TextView createOnDate;

    Task task;
    Context context;
    View view;

    public TaskViewHolder(View v, final Context context) {
        super(v);
        ButterKnife.inject(this, v);
        this.view = v;
        this.context = context;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NewTaskDialog taskDialog = new NewTaskDialog();
                NewTaskDialog.task = task;
                taskDialog.onDismissListener = new NewTaskDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(boolean answer) {
                        if (answer) {
                            alert.soft("successfully updated");
                            Client_TaskHub.updateTaskView();
                            //TabsActivity.syncTasks(context, taskListView.refreshRunnable);
                        }
                    }
                };
                taskDialog.show(((android.support.v4.app.FragmentActivity) context).getSupportFragmentManager(), "Diag");
            }
        });

        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(context, typeIcon);
        droppyBuilder.fromMenu(R.menu.droppy)
                .setOnClick(new DroppyClickCallbackInterface() {
                    @Override
                    public void call(View v, int id) {
                    }
                })
                .build();
    }

    public void build(Task task) {
        this.task = task;
        User creator = task.getCreator();
        senderName.setText(creator.Name);
        body.setText(task.Body);
        photo.setImageDrawable(creator.getPhoto());
        switch (task.Type) {
            case Task.TYPE_PLACE:
                typeIcon.setImageDrawable(task.getLocation().getIcon());
                break;
            case Task.TYPE_SCHEDULE:
                typeIcon.setImageResource(R.drawable.icon_material_timer);
                break;
            default:
                typeIcon.setImageResource(0);
                break;
        }

        statusIcon.setImageResource(task.getTaskStateIcon());

        if (task.State >= Task.STATE_COMPLETED) {
            view.setBackgroundResource(R.drawable.wau_card_completed);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                view.setElevation(context.getResources().getDimension(R.dimen.card_elevation));
        }else{
            view.setBackgroundResource(R.drawable.wau_card);
        }

        createOnDate.setText("Created on: " + DateTimeFormater.toDateTime(task.CreatedOn));
    }
}
