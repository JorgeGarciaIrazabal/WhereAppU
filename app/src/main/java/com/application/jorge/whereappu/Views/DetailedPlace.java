package com.application.jorge.whereappu.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.Dialogs.PlaceSettingsDialog;
import com.application.jorge.whereappu.R;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.simplicityapks.reminderdatepicker.lib.OnDateSelectedListener;
import com.simplicityapks.reminderdatepicker.lib.ReminderDatePicker;

import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jorge on 24/06/2015.
 */
public class DetailedPlace extends RelativeLayout {
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.placeIcon)
    ImageView placeIcon;
    @InjectView(R.id.stateIcon)
    ImageView stateIcon;
    @InjectView(R.id.typeIcon)
    ImageView typeIcon;
    @InjectView(R.id.deleteButton)
    ImageView deleteButton;

    Context context;
    Place place;
    Runnable afterDeleteCallback;

    public DetailedPlace(final Context context, Runnable afterDelete) {
        super(context);
        this.context = context;
        afterDeleteCallback = afterDelete;
        inflate(this.context, R.layout.view_detailed_place, this);
        ButterKnife.inject(this);
    }

    public void setPlace(Place place) {
        this.place = place;
        placeIcon.setImageDrawable(place.getIcon());
        if (place.__Updated == 0)
            stateIcon.setImageResource(R.drawable.icon_material_cloud_upload);
        else
            stateIcon.setImageResource(R.drawable.icon_material_done);

        name.setText(place.Name);

        if (place.Type.equals(Place.TYPE_PUBLIC))
            typeIcon.setImageResource(R.drawable.ic_share_128);
        else if (place.Type.equals(Place.TYPE_MANDATORY)) {
            typeIcon.setImageResource(R.drawable.ic_share_128);
            deleteButton.setVisibility(View.GONE);
        } else if (place.Type.equals(Place.TYPE_PRIVATE))
            typeIcon.setImageResource(R.drawable.wau_private);

        this.deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(context);
                dialog.withTitle("Are you sure?")
                        .withMessage("Do you want to delete the place?")
                        .withIcon(getResources().getDrawable(android.R.drawable.stat_sys_warning))
                        .withDuration(200)
                        .withEffect(Effectstype.Slidetop)
                        .withButton1Text("Delete")
                        .withButton2Text("Cancel")
                        .isCancelableOnTouchOutside(true)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DetailedPlace.this.place.DeletedOn = new Date();
                                try {
                                    DetailedPlace.this.place.write();
                                    afterDeleteCallback.run();
                                } catch (Exception e) {
                                    utils.saveExceptionInFolder(e);
                                } finally {
                                    dialog.dismiss();
                                }
                            }
                        })
                .setButton2Click(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
}