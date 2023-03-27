package com.example.btl_android;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class DiaryActivity extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    private EditText selectDay;
    private EditText selectTime;
    private EditText title_input;
    private EditText description_input;
    private ImageView imageView;
    private FloatingActionButton addImageBtn;
    private Uri selectedImage;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final float IMAGE_VIEW_HEIGHT = 500;
    private boolean isShow = false;

    private int diaryEditId = -1;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diary);
        getSupportActionBar().hide();

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        title_input = findViewById(R.id.title_input);
        description_input = findViewById(R.id.description_input);
        selectDay = findViewById(R.id.selectDay);
        selectTime = findViewById(R.id.selectTime);
        imageView = findViewById(R.id.img_input);
        addImageBtn = findViewById(R.id.add_image_button);

        addImageBtn.setOnClickListener(v -> {
            if (isShow) {
                isShow = false;
                selectedImage = null;
                toggleImageView();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 3);
        });

        findViewById(R.id.saveBtn).setOnClickListener(v -> {

            String title = title_input.getText().toString();
            String content = description_input.getText().toString();
            String date = selectDay.getText().toString();
            String time = selectTime.getText().toString();
            String imageUri = null;
            if (selectedImage != null) {
                imageUri = selectedImage.toString();
            }

            if ("".equals(title.trim()) || "".equals(content.trim())) {
                Toast.makeText(getApplicationContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle b = getIntent().getExtras();
            if (b != null) {
                if (b.getBoolean("isFromCalendarActivity")) {
                    CalendarActivity.active = true;
                }
            }


            // increment index
            Number currentIdNum = realm.where(Diary.class).max("id");
            int nextId;
            if(currentIdNum == null) {
                nextId = 1;
            } else {
                nextId = currentIdNum.intValue() + 1;
            }

            realm.beginTransaction();
//            Diary diary = realm.createObject(Diary.class, nextId);
            Diary diary = new Diary();
            if (diaryEditId != -1) {
                diary.setId(diaryEditId);
            } else {
                diary.setId(nextId);
            }
            diary.setTitle(title);
            diary.setContent(content);
            diary.setDate(date);
            diary.setTime(time);
            diary.setImageUri(imageUri);
            realm.copyToRealmOrUpdate(diary);
            realm.commitTransaction();

            Toast.makeText(getApplicationContext(), "Đã lưu lại", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.cancelBtn).setOnClickListener(v -> {
            finish();
        });


        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);

            selectDay.setText(dateFormat.format(myCalendar.getTime()));
        };
        TimePickerDialog.OnTimeSetListener time = (view, hour, min) -> {
            myCalendar.set(Calendar.HOUR, hour);
            myCalendar.set(Calendar.MINUTE, min);

            selectTime.setText(timeFormat.format(myCalendar.getTime()));
        };
        selectDay.setOnClickListener(view -> new DatePickerDialog(DiaryActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        selectTime.setOnClickListener(v -> new TimePickerDialog(DiaryActivity.this, time, myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE), true).show());


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Diary b = Diary.fromBundle(bundle);
            diaryEditId = b.getId();
            title_input.setText(b.getTitle());
            description_input.setText(b.getContent());
            selectDay.setText(b.getDate());
            selectTime.setText(b.getTime());
            if (b.getImageUri() != null) {
                selectedImage = Uri.parse(b.getImageUri());
                imageView.setImageURI(selectedImage);
                isShow = true;
                toggleImageView();
            }
        } else {
            selectDay.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            selectTime.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        }

    }

    private void toggleImageView() {
        if (isShow) {
            imageView.setVisibility(View.VISIBLE);
            addImageBtn.setImageResource(R.drawable.ic_remove_image);
            // start chon anh
        } else {
            imageView.setVisibility(View.INVISIBLE);
            addImageBtn.setImageResource(R.drawable.ic_add_image);
        }

        float val = IMAGE_VIEW_HEIGHT;
        if (!isShow) {
            val = 0;
        }
        imageView.animate().translationY(-val);
        title_input.animate().translationY(val);
        description_input.animate().translationY(val);
        selectDay.animate().translationY(val);
        selectTime.animate().translationY(val);
        imageView.animate().translationY(val);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            isShow = true;
            selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
            toggleImageView();
        } else {
            isShow = false;
        }
    }
}