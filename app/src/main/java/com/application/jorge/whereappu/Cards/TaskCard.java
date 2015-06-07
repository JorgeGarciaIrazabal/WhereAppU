package com.application.jorge.whereappu.Cards;

import android.content.Context;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;
import com.dexafree.materialList.cards.SimpleCard;

/**
 * Created by Jorge on 01/06/2015.
 */
public class TaskCard extends SimpleCard {
    Task task;
    public TaskCard(Context context, Task task) {
        super(context);
        this.task = task;
    }

    @Override
    public int getLayout() {
        return R.layout.task_card;
    }
}
