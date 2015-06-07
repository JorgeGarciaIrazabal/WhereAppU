package com.application.jorge.whereappu.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.activeandroid.query.Select;
import com.application.jorge.whereappu.Cards.ContactCard;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;
import com.dexafree.materialList.view.MaterialListView;

import java.util.List;


public class ContactsTab extends android.support.v4.app.Fragment {

    @InjectView(R.id.contactList)
    MaterialListView contactList;

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
        List<User> users = new Select().from(User.class).execute();
        for (User user :users) {
            ContactCard card = new ContactCard(getActivity(), user);
            contactList.add(card);
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
