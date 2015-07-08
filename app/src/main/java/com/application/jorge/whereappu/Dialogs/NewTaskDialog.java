package com.application.jorge.whereappu.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.Views.DateTimePickerView;
import com.application.jorge.whereappu.Views.SelectablePlacesView;
import com.github.pierry.simpletoast.SimpleToast;

/**
 * Created by Jorge on 02/06/2015.
 */
public class NewTaskDialog extends DialogFragment {
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.taskComment)
    EditText comment;
    @InjectView(R.id.receiverPhoto)
    ImageView receiverPhoto;
    @InjectView(R.id.customContainer)
    FrameLayout customContainer;

    @InjectView(R.id.cancelButton)
    Button cancelButton;
    @InjectView(R.id.createButton)
    Button createButton;

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
        dialog.setContentView(R.layout.dialog_new_task);

        ButterKnife.inject(this, dialog.getWindow().getDecorView());
        receiverPhoto.setImageDrawable(task.getReceiver().getPhoto());

        if (task.Type.equals(Task.TYPE_PLACE))
            customContainer.addView(new SelectablePlacesView(getActivity(), task), 0);
        else if (task.Type.equals(Task.TYPE_SCHEDULE))
            customContainer.addView(new DateTimePickerView(getActivity(), task), 0);

        if (task.isInserted()) {
            comment.setText(task.Body);
            title.setVisibility(View.GONE);
            cancelButton.setText("Dismiss");
            createButton.setText("Complete");
            if (task.State >= Task.STATE_COMPLETED) {
                cancelButton.setEnabled(false);
                createButton.setEnabled(false);
            }
        }
        return dialog;
    }

    @OnClick(R.id.cancelButton)
    public void onCancel() {
        if (task.isInserted()) {
            try {
                task.State = Task.STATE_DISMISSED;
                task.write();
            } catch (Exception e) {
                utils.saveExceptionInFolder(e);
            }
            this.answer = true;
        }
        this.dismiss();
    }

    @OnClick(R.id.createButton)
    protected void onOkClicked() {
        if (comment.getText().toString().isEmpty())
            SimpleToast.error(getActivity(), "A comment is necessary");
        else if (task.Type.equals(Task.TYPE_PLACE) && task.LocationId == null) {
            SimpleToast.error(getActivity(), "A location is necessary");
        } else {
            try {
                task.Body = comment.getText().toString();
                if (task.isInserted())
                    task.State = Task.STATE_COMPLETED;
                task.write();
                this.answer = true;
                this.dismiss();
            } catch (Exception e) {
                utils.saveExceptionInFolder(e);
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