package com.application.jorge.whereappu.Cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;
import com.dexafree.materialList.model.CardItemView;

/**
 * Created by Jorge on 01/06/2015.
 */
public class TaskItemView extends CardItemView<TaskCard> {
    TextView senderName;
    TextView body;
    ImageView photo;
    ImageView statusIcon;
    ImageView typeIcon;
    // Default constructors
    public TaskItemView(Context context) {
        super(context);
    }

    public TaskItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(TaskCard card) {
        senderName = (TextView)findViewById(R.id.senderName);
        senderName.setText(card.task.Creator.Name);
        body = (TextView)findViewById(R.id.body);
        body.setText(card.task.Body);
        photo = (ImageView) findViewById(R.id.senderPhoto);
        photo.setImageDrawable(card.task.Creator.getPhoto());
        typeIcon = (ImageView) findViewById(R.id.typeIcon);
        if(card.task.Type.equals(Task.TYPE_PLACE))
            typeIcon.setImageDrawable(card.task.Location.getIcon());
        else if (card.task.Type.equals(Task.TYPE_SCHEDULE))
            typeIcon.setImageResource(R.drawable.icon_material_timer);
        statusIcon = (ImageView) findViewById(R.id.statusIcon);
    }
}
