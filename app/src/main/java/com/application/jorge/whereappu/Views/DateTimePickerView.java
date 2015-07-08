package com.application.jorge.whereappu.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.R;

import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Jorge on 24/06/2015.
 */
public class DateTimePickerView extends LinearLayout {
    LayoutInflater li;
    @InjectView(R.id.datePicker)
    DatePicker datePicker;
    @InjectView(R.id.timePicker)
    TimePicker timePicker;
    @InjectView(R.id.timeButton)
    Button timeButton;
    @InjectView(R.id.dateButton)
    Button dateButton;

    Context context;
    Task task;

    public DateTimePickerView(Context context, Task task) {
        super(context);
        this.context = context;
        this.task = task;
        li = LayoutInflater.from(this.context);
        inflate(this.context, R.layout.view_date_time_picker, this);
        ButterKnife.inject(this);

        setUpPickers();
        timeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setVisibility(View.VISIBLE);
                datePicker.setVisibility(View.GONE);
            }
        });
        dateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setVisibility(View.GONE);
                datePicker.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpPickers() {
        timePicker.setIs24HourView(true);
        datePicker.setCalendarViewShown(false);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        int year, month, day, hour, min;
        Calendar cal = Calendar.getInstance();
        if (task.isInserted())
            cal.setTime(task.Schedule);
        else
            cal.add(Calendar.HOUR, 1);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                updateTaskAnButtons();
            }
        });
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                updateTaskAnButtons();
            }
        });
        updateTaskAnButtons();
    }

    private void updateTaskAnButtons() {
        task.Schedule = getDate();
        dateButton.setText("date at: " + DateTimeFormater.toDate(task.Schedule));
        timeButton.setText("time at: " + DateTimeFormater.toTime(task.Schedule));
    }

    public Date getDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
        return cal.getTime();
    }
}
