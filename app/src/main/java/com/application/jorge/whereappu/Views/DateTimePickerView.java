package com.application.jorge.whereappu.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;
import com.simplicityapks.reminderdatepicker.lib.OnDateSelectedListener;
import com.simplicityapks.reminderdatepicker.lib.ReminderDatePicker;

import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jorge on 24/06/2015.
 */
public class DateTimePickerView extends LinearLayout {
    LayoutInflater li;
    @InjectView(R.id.date_picker)
    ReminderDatePicker datePicker;

    Context context;
    Task task;

    public DateTimePickerView(final Context context, final Task task) {
        super(context);
        this.context = context;
        this.task = task;
        li = LayoutInflater.from(this.context);
        inflate(this.context, R.layout.view_date_time_picker, this);
        ButterKnife.inject(this);

        setUpPickers();
        // setup listener for a date change:
        datePicker.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar date) {
                Toast.makeText(context, "Selected date: " + DateTimeFormater.toDateTime(date.getTime()), Toast.LENGTH_SHORT).show();
                task.Schedule = date.getTime();
            }
        });
    }

    private void setUpPickers() {
        Calendar cal = Calendar.getInstance();
        if (task.isInserted())
            cal.setTime(task.Schedule);
        else
            cal.add(Calendar.HOUR, 1);

        datePicker.setSelectedDate(cal);
    }
}
