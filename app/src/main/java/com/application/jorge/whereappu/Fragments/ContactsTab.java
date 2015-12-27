package com.application.jorge.whereappu.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Cards.ContactAdapter;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ContactsTab extends android.support.v4.app.Fragment {
    @InjectView(R.id.contactList)
    RecyclerView contactList;
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    EditText searchField;
    ContactAdapter contactAdapter;

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
            searchField = ((TabsActivity) getActivity()).searchField;
            searchField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        refreshContactList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        TabsActivity.syncPhoneNumbers(getActivity());
                        TabsActivity.syncContactsFromPhoneBook();
                    } catch (Exception e) {
                        utils.saveExceptionInFolder(e);
                    } finally {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
            List<User> users = User.getAll(User.class);
            contactList.setHasFixedSize(true);
            contactList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            contactAdapter = new ContactAdapter(this.getActivity(), users);
            contactList.setAdapter(contactAdapter);
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

    public void refreshContactList() throws Exception {
        List<User> users;
        if (searchField.getText().toString().isEmpty())
            users = User.getAll(User.class);
        else
            users = User.where(User.class, "Name like '%" + searchField.getText().toString() + "%'");
        contactAdapter = new ContactAdapter(this.getActivity(), users);
        contactList.setAdapter(contactAdapter);
    }
}
