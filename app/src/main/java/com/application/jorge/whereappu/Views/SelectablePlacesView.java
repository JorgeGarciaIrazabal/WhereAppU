package com.application.jorge.whereappu.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jorge on 24/06/2015.
 */
public class SelectablePlacesView extends LinearLayout {
    LayoutInflater li;
    @InjectView(R.id.placesView)
    GridView placesView;
    Context context;
    Task task;

    public SelectablePlacesView(Context context, Task task) {
        super(context);
        this.context = context;
        this.task = task;
        li = LayoutInflater.from(this.context);
        inflate(this.context, R.layout.view_selectable_places_grid, this);
        ButterKnife.inject(this);

        placesView.setAdapter(new PlaceAdapter(this.context, Place.getPlacesFrom(task.Creator)));
        placesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(SelectablePlacesView.this.context, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class PlaceAdapter extends BaseAdapter {
        private Context context;
        private List<Place> places = new ArrayList<>();
        private Button selectedButton = null;

        public PlaceAdapter(Context c, List<Place> places) {
            this.places = places;
            context = c;
        }

        public int getCount() {
            return places.size();
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
                button.setPadding(0, 18, 0, 18);
            } else {
                button = (Button) convertView;
            }
            final Place place = places.get(position);
            button.setCompoundDrawablesWithIntrinsicBounds(null, utils.resize(place.getIcon(), 150, 150), null, null);
            button.setText(place.Name);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedButton != null)
                        selectedButton.setBackgroundResource(0);
                    selectedButton = (Button) view;
                    selectedButton.setBackgroundResource(R.drawable.background_button_rectangle);
                    task.Location = place;
                    task.Type = Task.TYPE_PLACE;
                }
            });
            return button;
        }
    }

}
