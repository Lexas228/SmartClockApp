package com.example.clock.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.adapter.AlarmItemAdapter;
import com.example.clock.pojo.AlarmItem;
import com.example.clock.receiver.AlarmReceiver;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<AlarmItem> alarmItemList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);

        ImageButton setAlarm = findViewById(R.id.clock_button);
        recyclerView = findViewById(R.id.listView);
        recyclerView.setAdapter(new AlarmItemAdapter(alarmItemList, this, recyclerView, alarmManager));
        ImageButton graphButton = findViewById(R.id.graph_button);

        graphButton.setOnClickListener(view -> {
            Intent switchActivityIntent = new Intent(this, GraphActivity.class);
            startActivity(switchActivityIntent);
        });

        setAlarm.setOnClickListener(v ->{
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Выберите время для будильника")
                    .build();


            materialTimePicker.addOnPositiveButtonClickListener(view-> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());
                    calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    alarmItemList.add(new AlarmItem(calendar, true, pendingIntent));
                    recyclerView.getAdapter().notifyItemInserted(alarmItemList.size() - 1);
            });
            materialTimePicker.show(getSupportFragmentManager(),"tag_picker");
        });

    }


}