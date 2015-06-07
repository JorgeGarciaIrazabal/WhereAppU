package com.application.jorge.whereappu.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;
import com.github.pierry.simpletoast.SimpleToast;

import java.util.ArrayList;

/**
 * Created by Jorge on 02/06/2015.
 */
public class NewTaskDialog extends DialogFragment {
    @InjectView(R.id.taskComment)
    EditText comment;
    @InjectView(R.id.placesView)
    GridView placesView;
    @InjectView(R.id.receiverPhoto)
    ImageView senderPhoto;
    @InjectView(R.id.sendToLayout)
    LinearLayout sendToLayout;
    public Task task = null;

    private boolean answer = false;

    public interface OnDismissListener{
        void onDismiss(boolean answer);
    }
    public OnDismissListener onDismissListener = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert task != null;

        Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
        dialog.setContentView(R.layout.new_task_dialog);

        ButterKnife.inject(this, dialog.getWindow().getDecorView());
        senderPhoto.setImageDrawable(task.Creator.getPhoto());
        placesView.setAdapter(new PlaceAdapter(getActivity(), new ArrayList<Integer>() {{
            add(1);
            add(2);
        }}));
        placesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        return dialog;
    }

    @OnClick(R.id.cancelButton)
    public void onCancel() {
        this.dismiss();
    }

    @OnClick(R.id.createButton)  //only if you use ButterKnife library
    protected void onOkClicked() {
        if(comment.getText().toString().isEmpty())
            SimpleToast.error(getActivity(), "A comment is necessary");
        else{
            task.Body = comment.getText().toString();
            task.save();
            this.answer = true;
            this.dismiss();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(onDismissListener != null)
            onDismissListener.onDismiss(answer);
    }

    public class PlaceAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Integer> places = new ArrayList<>();

        public PlaceAdapter(Context c, ArrayList<Integer> places) {
            this.places = places;
            context = c;
        }

        public PlaceAdapter(Context c) {
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
            } else {
                button = (Button) convertView;
            }
            button.setCompoundDrawablesWithIntrinsicBounds(null, utils.resize(R.drawable.unknown_contact, 150, 150), null, null);
            button.setPadding(0, 18, 0, 18);
            button.setText("Place");
            return button;
        }
    }

}