package com.application.jorge.whereappu.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.jorge.whereappu.Activities.ChatActivity;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jorge on 01/06/2015.
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {
    @InjectView(R.id.nameEdit)
    TextView name;
    @InjectView(R.id.lastMessage)
    TextView lastMessage;
    @InjectView(R.id.photo)
    ImageView photo;
    @InjectView(R.id.statusIcon)
    ImageView statusIcon;


    public User contact;
    public Context context;

    public ContactViewHolder(View v, Context context) {
        super(v);
        ButterKnife.inject(this, v);
        this.context = context;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.receiver = contact;
                Intent intent = new Intent(ContactViewHolder.this.context, ChatActivity.class);
                ContactViewHolder.this.context.startActivity(intent);
            }
        });
    }

    public void build(User contact) {
        try {
            this.contact = contact;
            name.setText(contact.Name);
            Task task = contact.getLastTask();

            lastMessage.setText(task == null ? "--no message--" : task.Body);
            statusIcon.setImageResource(task != null ? task.getTaskStateIcon() : 0);
            if(task != null && task.isTaskToNotify())
                lastMessage.setTypeface(null, Typeface.BOLD);
            photo.setImageDrawable(contact.getPhoto());
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }
}
