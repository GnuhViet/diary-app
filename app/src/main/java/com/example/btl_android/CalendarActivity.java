package com.example.btl_android;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.CalendarView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class CalendarActivity extends AppCompatActivity {

    private List<Diary> originDiaryList;
    private RecyclerView recyclerView;
    private RealmResults<Diary> realmResultsDiary;
    private Calendar selectedDateInView;

    public static boolean active = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        recyclerView = findViewById(R.id.calendar_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.calendar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.calendar:
                    return true;
                case R.id.about:
                    startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        Realm realm = Realm.getDefaultInstance();
        realmResultsDiary = realm.where(Diary.class).findAll();
        originDiaryList = realm.copyFromRealm(realmResultsDiary);

        CalendarView calendarView = findViewById(R.id.calendarView);

        selectedDateInView = Calendar.getInstance();
        // init load (current date)
        uploadList(Calendar.getInstance());
        // choose date in calendar view
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateInView = Calendar.getInstance();
            selectedDateInView.set(year, month, dayOfMonth);
            uploadList(selectedDateInView);
        });


        realmResultsDiary.addChangeListener(diaries -> {
            if (active) {
                originDiaryList = realm.copyFromRealm(realmResultsDiary);
                uploadList(selectedDateInView);
            }
        });
    }

    private void uploadList(Calendar selected) {
        Calendar diaryDate = Calendar.getInstance();
        List<Diary> diaries = new ArrayList<>();
        for (Diary d : originDiaryList) {
            diaryDate.setTime(Objects.requireNonNull(Diary.getDateTime(d.getDate(), d.getTime())));

            if (selected.get(Calendar.YEAR) == diaryDate.get(Calendar.YEAR)
                    && selected.get(Calendar.MONTH) == diaryDate.get(Calendar.MONTH)
                    && selected.get(Calendar.DAY_OF_MONTH) == diaryDate.get(Calendar.DAY_OF_MONTH)) {
                diaries.add(d);
            }
        }
        if (diaries.size() > 1) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                diaries.sort((o1, o2) -> {
                    Date dt1 = Diary.getDateTime(o1.getDate(), o1.getTime());
                    Date dt2 = Diary.getDateTime(o2.getDate(), o2.getTime());
                    return dt2.compareTo(dt1);
                });
            }
        }

        DiaryAdapter listViewAdapter = new DiaryAdapter(CalendarActivity.this,
                diaries, realmResultsDiary);
        recyclerView.setAdapter(listViewAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}