package com.application.jorge.whereappu.Cards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;

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

    public TaskViewHolder(View v, Context context) {
        super(v);
        ButterKnife.inject(this, v);
        this.context = context;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.soft("clicked on: " + task);
            }
        });
    }

    public void build(Task task) {
        User creator = task.getCreator();
        senderName.setText(creator.Name);
        body.setText(task.Body);
        photo.setImageDrawable(creator.getPhoto());
        if (task.Type.equals(Task.TYPE_PLACE))
            typeIcon.setImageDrawable(task.getLocationId().getIcon());
        else if (task.Type.equals(Task.TYPE_SCHEDULE))
            typeIcon.setImageResource(R.drawable.icon_material_timer);
        else
            typeIcon.setImageResource(0);

        if(task.State.equals(Task.STATE_CREATED))
            statusIcon.setImageResource(R.drawable.icon_material_cloud_upload);
        else if(task.State.equals(Task.STATE_UPLOADED))
            statusIcon.setImageResource(R.drawable.icon_material_done);
        else if(task.State.equals(Task.STATE_ARRIVED))
            statusIcon.setImageResource(R.drawable.icon_material_done_all);
        else if(task.State.equals(Task.STATE_READ))
            statusIcon.setImageResource(R.drawable.circle_ok);
        else if(task.State.equals(Task.STATE_COMPLETED))
            statusIcon.setImageResource(R.drawable.ok_hand);
        else if(task.State.equals(Task.STATE_DISMISSED))
            statusIcon.setImageResource(R.drawable.icon_material_do_not_disturb);

        createOnDate.setText("Created on: " + DateTimeFormater.toDateTime(task.CreatedOn));
    }
}
