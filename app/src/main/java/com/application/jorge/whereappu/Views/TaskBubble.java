package com.application.jorge.whereappu.Views;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jorge on 24/06/2015.
 */
public class TaskBubble extends RelativeLayout {
    @InjectView(R.id.image)
    CircleImageView image;
    @InjectView(R.id.bubbleLayout)
    RelativeLayout bubbleLayout;

    Context context;
    User user;

    public TaskBubble(Context context, User user) {
        super(context);
        this.context = context;
        inflate(this.context, R.layout.view_task_bubble, this);
        ButterKnife.inject(this);
        View target = findViewById(R.id.bubbleLayout);
        try {
            User.getUsersWithActiveComments();
            setUser(user);
            BadgeView badge = new BadgeView(this.context, target);
            badge.setText(String.valueOf(user.getActiveCommentsCount()));
            badge.setBadgeBackgroundColor(this.context.getResources().getColor(R.color.wau_blue));
            badge.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
            badge.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setUser(User user) {
        this.user = user;
        image.setImageDrawable(user.getPhoto());
    }

    @OnClick(R.id.image)
    public void onClick() {
        alert.soft("Clicked");
    }
}