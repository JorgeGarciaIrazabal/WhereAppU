package com.application.jorge.whereappu.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.Dialogs.NewTaskDialog;
import com.application.jorge.whereappu.Dialogs.PlaceSettingsDialog;
import com.application.jorge.whereappu.R;
import com.github.alexkolpa.fabtoolbar.FabToolbar;


public class PlacesTab extends android.support.v4.app.Fragment {
    @InjectView(R.id.placesView)
    public GridView placesView;
    @InjectView(R.id.placesToolbar)
    FabToolbar toolbar;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    public static PlacesTab newInstance() {
        PlacesTab fragment = new PlacesTab();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PlacesTab() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.inject(this, v);
        toolbar.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PlaceSettingsDialog placeSettingsDialog = new PlaceSettingsDialog();
                placeSettingsDialog.setOnDismissListener(new PlaceSettingsDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(boolean answer) {
                        alert.soft(answer);
                    }
                });
                placeSettingsDialog.show(getFragmentManager(), "Diag");
                toolbar.hide();
            }
        });
        toolbar.attachToListView(placesView);
        placesView.setAdapter(new PlaceAdapter(getActivity()));
        placesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    public class PlaceAdapter extends BaseAdapter {
        private Context context;
        //private List<Place> places = new ArrayList<>();

        public PlaceAdapter(Context c) {
            //this.places = Place.getMyPlaces();
            context = c;
        }

        public int getCount() {
            return 20;
            //return places.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Button button;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                button = new Button(context);
                button.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                button.setBackgroundResource(0);
            } else {
                button = (Button) convertView;
            }
            button.setCompoundDrawablesWithIntrinsicBounds(null, utils.resize(R.drawable.unknown_contact, 150, 150), null, null);
            button.setPadding(0, 18, 0, 18);
            button.setText("Place");
            return button;
        }
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
