package com.application.jorge.whereappu.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.R;
import com.github.pierry.simpletoast.SimpleToast;

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

    protected boolean answer = false;

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
            placeIcon.setImageDrawable(place.getIcon());
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

        else {
            answer = true;
            this.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null)
            onDismissListener.onDismiss(answer);
    }
}
