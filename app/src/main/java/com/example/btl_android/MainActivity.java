package com.example.btl_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private DiaryAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home:
                    return true;
                case R.id.calendar:
                    startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.about:
                    startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }

            return false;
        });

        Realm.init(getApplicationContext());
//        Realm.deleteRealm(Realm.getDefaultConfiguration());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Diary> realmResultsDiary = realm.where(Diary.class).findAll();

        List<Diary> copyList = realm.copyFromRealm(realmResultsDiary);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            copyList.sort((o1, o2) -> {
                Date dt1 = Diary.getDateTime(o1.getDate(), o1.getTime());
                Date dt2 = Diary.getDateTime(o2.getDate(), o2.getTime());
                return dt2.compareTo(dt1);
            });
        }

        listViewAdapter = new DiaryAdapter(MainActivity.this, copyList, realmResultsDiary);
        recyclerView.setAdapter(listViewAdapter);
        /////////
        //end init
        /////////

        findViewById(R.id.add_diary_btn).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, DiaryActivity.class));  //Khi bấm thêm mới sẽ mở sang DỉayActivity
        });

        realmResultsDiary.addChangeListener(diaries -> {
            List<Diary> c = realm.copyFromRealm(realmResultsDiary);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                c.sort((o1, o2) -> {
                    Date dt1 = Diary.getDateTime(o1.getDate(), o1.getTime());
                    Date dt2 = Diary.getDateTime(o2.getDate(), o2.getTime());
                    return dt2.compareTo(dt1);
                });
            }
            listViewAdapter = new DiaryAdapter(MainActivity.this, c, realmResultsDiary);

            recyclerView.setAdapter(listViewAdapter);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_button);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE); // make the search bar full with
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listViewAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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