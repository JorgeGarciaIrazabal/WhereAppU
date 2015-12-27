package com.application.jorge.whereappu.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.Dialogs.PlaceSettingsDialog;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.Views.DetailedPlace;
import com.github.alexkolpa.fabtoolbar.FabToolbar;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PlacesTab extends android.support.v4.app.Fragment {
    @InjectView(R.id.placesView)
    public GridView placesView;
    @InjectView(R.id.placesToolbar)
    FabToolbar toolbar;
    private PlaceSettingsDialog.OnDismissListener onDialogDismissListener;

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
        onDialogDismissListener = new PlaceSettingsDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean answer) {
                if (answer) {
                    try {
                        refreshView();
                    } catch (Exception e) {
                        utils.saveExceptionInFolder(e);
                    }
                }
            }
        };
    }

    public void refreshView() throws Exception {
        PlaceAdapter placesAddapter = ((PlaceAdapter) placesView.getAdapter());
        placesAddapter.places = Place.getMyPlaces();
        placesAddapter.notifyDataSetChanged();
        TabsActivity.syncPlaces(getActivity());
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
                OpenPlaceSettingsDialog(null);
                toolbar.hide();
            }
        });
        toolbar.attachToListView(placesView);
        placesView.setAdapter(new PlaceAdapter(getActivity()));
        placesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                OpenPlaceSettingsDialog((Place)(((GridView) parent).getAdapter().getItem(position)));
            }
        });

        return v;
    }

    private void OpenPlaceSettingsDialog(Place place) {
        final PlaceSettingsDialog placeSettingsDialog = new PlaceSettingsDialog();
        if (place != null)
            placeSettingsDialog.place = place;
        placeSettingsDialog.setOnDismissListener(onDialogDismissListener);
        placeSettingsDialog.show(getFragmentManager(), "Diag");
    }

    public class PlaceAdapter extends BaseAdapter {
        private Context context;
        public List<Place> places = new ArrayList<>();

        public PlaceAdapter(Context c) {
            try {
                this.places = Place.getMyPlaces();
                context = c;
            } catch (Exception e) {
                utils.saveExceptionInFolder(e);
            }
        }

        public int getCount() {
            return places.size();
        }

        public Object getItem(int position) {
            return places.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            DetailedPlace detailedPlace;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                detailedPlace = new DetailedPlace(context, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            refreshView();
                        } catch (Exception e) {
                            utils.saveExceptionInFolder(e);
                        }
                    }
                });
                detailedPlace.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                detailedPlace.setPadding(0, 18, 0, 18);
            } else {
                detailedPlace = (DetailedPlace) convertView;
            }
            final Place place = places.get(position);
            detailedPlace.setPlace(place);
            return detailedPlace;
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
