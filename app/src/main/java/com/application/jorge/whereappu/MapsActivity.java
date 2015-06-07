package com.application.jorge.whereappu;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnEditorAction;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.MainActivity;
import com.application.jorge.whereappu.Classes.alert;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity {
    @InjectView(R.id.searchField)
    EditText searchField;

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private UiSettings uiSettings;
    private Geocoder geocoder;

    private MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.inject(this);
        geocoder = new Geocoder(this);
        setUpMapIfNeeded();
    }

    @OnEditorAction(R.id.searchField)
    public boolean onSearch(KeyEvent key) {
        String text = searchField.getText().toString();
        try {
            List<Address> addresses = geocoder.getFromLocationName(text, 1);
            Address address;
            if (addresses.size() > 0) {
                if (marker != null)
                    marker.visible(false);
                marker = new MarkerOptions()
                        .position(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()))
                        .title("Hello world");
            }

        } catch (IOException e) {
            alert.soft("Unable to search in address: " + text);
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        App.context = MapsActivity.this;
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        uiSettings = map.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMyLocationEnabled(true);
        Geocoder geocoder = new Geocoder(this);
        uiSettings.setCompassEnabled(true);

    }
}
