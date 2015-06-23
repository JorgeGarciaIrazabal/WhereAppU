package com.application.jorge.whereappu.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.MapsActivity;
import com.application.jorge.whereappu.R;
import com.github.pierry.simpletoast.SimpleToast;
import com.google.android.gms.maps.model.LatLng;

public class PlaceSettingsDialog extends DialogFragment {

    @InjectView(R.id.nameEdit)
    EditText nameEdit;
    @InjectView(R.id.locationSelectedLabel)
    TextView locationSelectedLabel;
    @InjectView(R.id.createButton)
    Button createButton;
    @InjectView(R.id.placeIcon)
    ImageView placeIcon;

    public Place place = null;
    private Uri selectedIcon = utils.getUri(R.drawable.place_home);

    protected boolean answer = false;
    private double latitude = 0;
    private double longitude = 0;
    private int range = 0;

    public interface OnDismissListener {
        void onDismiss(boolean answer);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    protected OnDismissListener onDismissListener = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
        dialog.setContentView(R.layout.place_settings_dialog);
        ButterKnife.inject(this, dialog.getWindow().getDecorView());
        if (place != null) {
            createButton.setText("Update");
            nameEdit.setText(place.Name);
            selectedIcon = place.getIconUri();
            placeIcon.setImageDrawable(place.getIcon());
            latitude = place.Latitude;
            longitude = place.Longitude;
            range = place.Range;
            setLocationLabel();
        }
        return dialog;
    }

    @OnClick(R.id.cancelButton)
    public void onCancel() {
        this.dismiss();
    }

    @OnClick(R.id.createButton)  //only if you use ButterKnife library
    protected void onOkClicked() {
        if (nameEdit.getText().toString().isEmpty())
            SimpleToast.error(getActivity(), "A name is necessary");
        else if (range == 0) {
            SimpleToast.error(getActivity(), "A location is necessary");
        } else {
            if (place == null)//todo: this doesn't work the first time the application is installed
                place = Place.createPlaceOfMine();
            place.Name = nameEdit.getText().toString().trim();
            place.IconURI = selectedIcon.toString();
            place.Longitude = longitude;
            place.Latitude = latitude;
            place.Range = range;
            if (place.write() == -1) {
                alert.soft("Unable to save place, try again later");
            } else {
                answer = true;
                this.dismiss();
            }
        }
    }

    @OnClick(R.id.selectLocationButton)
    protected void openMapsActivity() {
        Intent i = new Intent(getActivity(), MapsActivity.class);
        MapsActivity.setOnSelectListener(new MapsActivity.OnSelectListener() {
            @Override
            public void onSelect(LatLng latLng, int range) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                PlaceSettingsDialog.this.range = range;
                setLocationLabel();
            }
        });
        getActivity().startActivity(i);
    }

    private void setLocationLabel() {
        locationSelectedLabel.setText("Latitude: " + Double.toString(latitude) + "\nLongitude: "
                + Double.toString(longitude) + "\nRange: " + Integer.toString(PlaceSettingsDialog.this.range));
    }

    @OnClick(R.id.placeIcon)
    protected void openIconsDialog() {
        final PlaceIconsDialog placeIconsDialog = new PlaceIconsDialog();
        placeIconsDialog.setOnDismissListener(new PlaceIconsDialog.OnDismissListener() {
            @Override
            public void onDismiss(int selectedIcon) {
                if (selectedIcon != 0) {
                    PlaceSettingsDialog.this.selectedIcon = utils.getUri(selectedIcon);//todo, change to uri when dynamic icons
                    placeIcon.setImageResource(selectedIcon);
                }
            }
        });
        placeIconsDialog.show(getFragmentManager(), "Diag");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null)
            onDismissListener.onDismiss(answer);
    }
}
