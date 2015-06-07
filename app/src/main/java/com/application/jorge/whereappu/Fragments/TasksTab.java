package com.application.jorge.whereappu.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.activeandroid.query.Select;
import com.application.jorge.whereappu.Activities.MainActivity;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Cards.TaskCard;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.Dialogs.NewTaskDialog;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.WebSocket.FunctionResult;
import com.application.jorge.whereappu.WebSocket.WSServer;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.github.alexkolpa.fabtoolbar.FabToolbar;
import it.sephiroth.android.library.floatingmenu.FloatingActionItem;
import it.sephiroth.android.library.floatingmenu.FloatingActionMenu;
import net.steamcrafted.loadtoast.LoadToast;
import org.json.JSONException;

import java.util.List;

public class TasksTab extends android.support.v4.app.Fragment {
    @InjectView(R.id.taskList)
    MaterialListView taskList;
    @InjectView(R.id.taskToolbar)
    FabToolbar toolbar;
    @InjectView(R.id.placeTaskButton)
    Button placeTaskButton;
    @InjectView(R.id.timerTaskButton)
    Button timerTaskButton;

    public static TasksTab newInstance() {
        return new TasksTab();
    }

    public TasksTab() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        ButterKnife.inject(this, v);
        toolbar.attachToRecyclerView(taskList);
        refreshTasks();

        taskList.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {
                toolbar.hide();
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {
                toolbar.hide();
            }
        });
        return v;
    }

    private void refreshTasks() {
        taskList.clear();
        User mySelf = User.getMySelf();
        if (mySelf != null) {
            List<Task> tasks = new Select().from(Task.class).where("ReceiverID = ?", mySelf.getId()).execute();
            for (Task task : tasks) {
                TaskCard card = new TaskCard(getActivity(), task);
                taskList.add(card);
            }
        }
    }


    @OnClick(R.id.timerTaskButton)
    public void onTimerTaskButton() {
        toolbar.hide();
        final NewTaskDialog taskDialog = new NewTaskDialog();
        taskDialog.task = new Task(User.getMySelf(), User.getMySelf(), "");
        taskDialog.onDismissListener = new NewTaskDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean answer) {
                if (answer) {
                    TabsActivity.syncTasks(getActivity());
                    refreshTasks();
                }
            }
        };
        taskDialog.show(getFragmentManager(), "Diag");
    }

    @OnClick(R.id.placeTaskButton)
    public void onPlaceTaskButton() {
        toolbar.hide();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
