package com.application.jorge.whereappu.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.Views.DateTimePickerView;
import com.application.jorge.whereappu.Views.SelectablePlacesView;
import com.github.pierry.simpletoast.SimpleToast;

/**
 * Created by Jorge on 02/06/2015.
 */
public class NewTaskDialog extends DialogFragment {
    @InjectView(R.id.taskComment)
    EditText comment;
    @InjectView(R.id.receiverPhoto)
    ImageView receiverPhoto;
    @InjectView(R.id.customContainer)
    LinearLayout customContainer;

    public Task task = null;
    private boolean answer = false;

    public interface OnDismissListener {
        void onDismiss(boolean answer);
    }

    public OnDismissListener onDismissListener = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert task != null;

        Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
        dialog.setContentView(R.layout.new_task_dialog);

        ButterKnife.inject(this, dialog.getWindow().getDecorView());
        receiverPhoto.setImageDrawable(task.Creator.getPhoto());

        if(task.Type.equals(Task.TYPE_PLACE))
            customContainer.addView(new SelectablePlacesView(getActivity(), task), 0);
        else if(task.Type.equals(Task.TYPE_SCHEDULE))
            customContainer.addView(new DateTimePickerView(getActivity(), task), 0);
        return dialog;
    }

    @OnClick(R.id.cancelButton)
    public void onCancel() {
        this.dismiss();
    }

    @OnClick(R.id.createButton)
    protected void onOkClicked() {
        if (comment.getText().toString().isEmpty())
            SimpleToast.error(getActivity(), "A comment is necessary");
        else if(task.Type.equals(Task.TYPE_PLACE) && task.Location == null){
            SimpleToast.error(getActivity(), "A location is necessary");
        }
        else {
            try {
                task.Body = comment.getText().toString();
                task.write();
                this.answer = true;
                this.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null)
            onDismissListener.onDismiss(answer);
    }



}