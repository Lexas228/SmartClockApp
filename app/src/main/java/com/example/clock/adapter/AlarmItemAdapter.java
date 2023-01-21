package com.example.clock.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activity.MainActivity;
import com.example.clock.pojo.AlarmItem;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmItemAdapter extends RecyclerView.Adapter<AlarmItemAdapter.ViewHolder>{
    private final List<AlarmItem> alarmItemList;
    private final MainActivity mainActivity;

    private final RecyclerView recyclerView;
    private final AlarmManager alarmManager;

    public AlarmItemAdapter(List<AlarmItem> alarmItemList, MainActivity mainActivity, RecyclerView recyclerView, AlarmManager alarmManager) {
        this.alarmItemList = alarmItemList;
        this.mainActivity = mainActivity;
        this.recyclerView = recyclerView;
        this.alarmManager = alarmManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmItem alarmItem = alarmItemList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.time.setText(sdf.format(alarmItem.getTime().getTime()));
        holder.isOn.setChecked(alarmItem.isOn());
        holder.isOn.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                long time = (alarmItem.getTime().getTimeInMillis() - (alarmItem.getTime().getTimeInMillis() % 60000));
                if (System.currentTimeMillis() > time) {
                    // setting time as AM and PM
                    if (Calendar.AM_PM == 0)
                        time = time + (1000 * 60 * 60 * 12);
                    else
                        time = time + (1000 * 60 * 60 * 24);
                }
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, alarmItem.getPendingIntent());
            }else{
                alarmManager.cancel(alarmItem.getPendingIntent());
            }
        });
        holder.itemView.setOnClickListener(v -> {
            String[] split = holder.time.getText().toString().split(":");
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(Integer.parseInt(split[0]))
                    .setMinute(Integer.parseInt(split[1]))
                    .setTitleText("Выберите время для будильника")
                    .build();
            materialTimePicker.addOnPositiveButtonClickListener(some->{
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                calendar.set(Calendar.MINUTE,materialTimePicker.getMinute());
                calendar.set(Calendar.HOUR_OF_DAY,materialTimePicker.getHour());
                holder.time.setText(sdf.format(calendar.getTime()));
            });
            materialTimePicker.show(mainActivity.getSupportFragmentManager(), "tag_picker");
        });

        holder.itemView.setOnLongClickListener(view -> {
            int index = recyclerView.getChildAdapterPosition(view);
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    alarmManager.cancel(alarmItemList.get(index).getPendingIntent());
                    alarmItemList.remove(index);
                    recyclerView.getAdapter().notifyItemRemoved(index);

                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Delete?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return alarmItemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        final TextView time;
        final CheckBox isOn;

        AlarmItem alarmItem;
        ViewHolder(View view){
            super(view);
            time = view.findViewById(R.id.time);
            isOn = view.findViewById(R.id.isOn);
        }

    }
}
