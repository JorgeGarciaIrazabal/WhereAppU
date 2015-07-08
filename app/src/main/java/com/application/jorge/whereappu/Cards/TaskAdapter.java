package com.application.jorge.whereappu.Cards;

/**
 * Created by Jorge on 04/07/2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private List<Task> tasks;
    private Context context;


    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }


    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_task, viewGroup, false);
        TaskViewHolder vh = new TaskViewHolder(itemView, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int i) {
        holder.build(getItem(i));
    }


    @Override
    public long getItemId(int i) {
        return getItem(i).ID;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


}