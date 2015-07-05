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
import com.application.jorge.whereappu.Classes.IconManager;
import com.application.jorge.whereappu.R;

import java.util.ArrayList;

public class PlaceIconsDialog extends DialogFragment {

    @InjectView(R.id.iconsView)
    GridView iconsView;

    protected int selectedIcon = 0;

    public interface OnDismissListener {
        void onDismiss(int selectedIcon);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    protected OnDismissListener onDismissListener = null;

    public class iconsAdapter extends BaseAdapter {
        private Context context;
        public ArrayList<Integer> icons = new ArrayList<>();

        public iconsAdapter(Context c) {
            this.icons = IconManager.GetIcons("place");
            context = c;
        }

        public int getCount() {
            return icons.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(context);
                imageView.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                imageView.setBackgroundResource(0);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(icons.get(position));
            imageView.setPadding(0, 18, 0, 18);
            return imageView;
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
        dialog.setContentView(R.layout.dialog_place_icons);
        ButterKnife.inject(this, dialog.getWindow().getDecorView());
        iconsView.setAdapter(new iconsAdapter(getActivity()));
        iconsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                selectedIcon = ((iconsAdapter)iconsView.getAdapter()).icons.get(position);
                dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null)
            onDismissListener.onDismiss(selectedIcon);
    }
}
