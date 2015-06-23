package com.application.jorge.whereappu;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.Classes.utils;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity {
    @InjectView(R.id.searchField)
    EditText searchField;
    @InjectView(R.id.radius)
    EditText radius;

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private UiSettings uiSettings;
    private Geocoder geocoder;
    private CameraUpdateFactory cameraUpdateFactory;
    private MarkerOptions marker;
    private LatLngBounds cameraBounds;

    public interface OnSelectListener {
        public void onSelect(LatLng latLng, int range);
    }

    public static void setOnSelectListener(OnSelectListener onSelectListener) {
        MapsActivity.onSelectListener = onSelectListener;
    }

    private static OnSelectListener onSelectListener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        App.context = MapsActivity.this;
        ButterKnife.inject(this);
        geocoder = new Geocoder(this);
        setUpMapIfNeeded();
    }

    @OnEditorAction(R.id.searchField)
    public boolean onSearch(KeyEvent key) {
        String text = searchField.getText().toString();
        try {
            List<Address> addresses = geocoder.getFromLocationName(text, 20, cameraBounds.southwest.latitude, cameraBounds.southwest.longitude, cameraBounds.northeast.latitude, cameraBounds.northeast.longitude);
            map.clear();
            for (Address address : addresses) {
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                map.addMarker(new MarkerOptions()
                        .title("new Place possition")
                        .position(latLng));
            }
            if (addresses.size() == 1)
                animateCameraTo(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()), 16.0f);
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
        App.context = MapsActivity.this;
        setUpMapIfNeeded();
    }

    @OnClick(R.id.selectButton)
    public void onSelect() {
        if (marker == null) {
            alert.soft("Touch to select a place");
            return;
        }
        if (onSelectListener != null)
            onSelectListener.onSelect(marker.getPosition(), Integer.parseInt(radius.getText().toString()));
        finish();
    }

    @OnClick(R.id.cancelButton)
    public void onCancel(){
        finish();
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
        final Geocoder geocoder = new Geocoder(this);
        uiSettings.setCompassEnabled(true);
        cameraBounds = map.getProjection().getVisibleRegion().latLngBounds;
        Location location = utils.getLocation();
        if (location != null)
            animateCameraTo(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f);
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                cameraBounds = map.getProjection().getVisibleRegion().latLngBounds;
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> addresslist = null;
                try {
                    addresslist = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    for (Address address : addresslist) {
                        map.clear();
                        alert.soft("Selected " + address.getAddressLine(0));
                        marker = new MarkerOptions()
                                .title("Place selected")
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        map.addMarker(marker);
                        map.addCircle(new CircleOptions()
                                .center(latLng)
                                .radius(Integer.parseInt(radius.getText().toString())))
                                .setFillColor(getResources().getColor(R.color.map_radius_color));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
