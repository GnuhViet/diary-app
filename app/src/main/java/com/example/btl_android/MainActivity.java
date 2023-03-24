package com.example.btl_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Realm.init(getApplicationContext());
//        Realm.deleteRealm(Realm.getDefaultConfiguration());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Diary> diaryLst = realm.where(Diary.class).findAll();

        List<Diary> copyList = realm.copyFromRealm(diaryLst);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            copyList.sort((o1, o2) -> {
                Date dt1 = Diary.getDateTime(o1.getDate(), o1.getTime());
                Date dt2 = Diary.getDateTime(o2.getDate(), o2.getTime());
                return dt1.compareTo(dt2);
            });
        }

        Collections.reverse(diaryLst);
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, copyList, diaryLst);
        recyclerView.setAdapter(myAdapter);
        /////////
        //end init
        /////////


        MaterialButton addBtn = findViewById(R.id.add_diary_btn);
        addBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, DiaryActivity.class));  //Khi bấm thêm mới sẽ mở sang DỉayActivity
        });

        diaryLst.addChangeListener(diaries -> {
            List<Diary> c = realm.copyFromRealm(diaryLst);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                c.sort((o1, o2) -> {
                    Date dt1 = Diary.getDateTime(o1.getDate(), o1.getTime());
                    Date dt2 = Diary.getDateTime(o2.getDate(), o2.getTime());
                    return dt1.compareTo(dt2);
                });
            }

            Collections.reverse(c);

            recyclerView.setAdapter(new MyAdapter(MainActivity.this, c, diaryLst));
        });
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 100) {
            Toast.makeText(this, "Until you grant the permission,...",
                    Toast.LENGTH_SHORT).show();
        }
    }
}