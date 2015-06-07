package com.application.jorge.whereappu;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnEditorAction;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.MainActivity;
import com.application.jorge.whereappu.Classes.alert;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
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
    private CameraUpdateFactory cameraUpdateFactory;
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
        searchField.setText("");
        try {
            List<Address> addresses = geocoder.getFromLocationName(text, 1);
            Address address;
            if (addresses.size() > 0) {
                LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                if(marker == null)
                    marker = new MarkerOptions()
                            .title("Hello world");
                marker.visible(true);
                marker.position(latLng);
                map.addMarker(marker);
                animateCameraTo(latLng, 16.0f);
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

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

    public void animateCameraTo(final LatLng latLng, final float zoom) {
        CameraPosition camPosition = map.getCameraPosition();
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        if (!((Math.floor(camPosition.target.latitude * 100) / 100) == (Math.floor(lat * 100) / 100) && (Math.floor(camPosition.target.longitude * 100) / 100) == (Math.floor(lng * 100) / 100))) {
            uiSettings.setScrollGesturesEnabled(false);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    map.getUiSettings().setScrollGesturesEnabled(true);

                }
                @Override
                public void onCancel() {
                    map.getUiSettings().setAllGesturesEnabled(true);

                }
            });
        }

    }

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
