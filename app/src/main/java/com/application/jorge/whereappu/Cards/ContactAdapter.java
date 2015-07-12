package com.application.jorge.whereappu.Cards;

/**
 * Created by Jorge on 04/07/2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<User> contacts;
    private Context context;


    public ContactAdapter(Context context, List<User> data) {
        this.context = context;
        this.contacts = data;
    }


    public User getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact, viewGroup, false);
        ContactViewHolder vh = new ContactViewHolder(itemView, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int i) {
        holder.build(getItem(i));
    }


    @Override
    public long getItemId(int i) {
        return getItem(i).ID;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

}