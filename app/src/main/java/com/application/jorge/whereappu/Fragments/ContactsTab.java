package com.application.jorge.whereappu.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.jorge.whereappu.Cards.ContactAdapter;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ContactsTab extends android.support.v4.app.Fragment {

    @InjectView(R.id.contactList)
    RecyclerView contactList;

    public static ContactsTab newInstance() {
        ContactsTab fragment = new ContactsTab();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ContactsTab() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.inject(this, v);
        try {
            List<User> users = User.getAll(User.class);
            contactList.setHasFixedSize(true);
            contactList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            contactList.setAdapter(new ContactAdapter(this.getActivity(), users));
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
