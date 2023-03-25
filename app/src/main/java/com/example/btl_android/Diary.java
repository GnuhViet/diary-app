package com.example.btl_android;

import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Diary extends RealmObject {
    @PrimaryKey
    private int id;
    private String title;
    private String content;
    private String date;
    private String time;
    private String imageUri;

    @Ignore
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public Diary() {
        imageUri = null;
    }

    public Diary(Diary d) {
        this.id = d.id;
        this.title = d.title;
        this.content = d.content;
        this.date = d.date;
        this.time = d.time;
        this.imageUri = d.imageUri;
    }

    public static Date getDateTime(String date, String time) {
        try {
            return dateFormat.parse(date + " " + time);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bundle toBundle(Diary diary) {
        Bundle b = new Bundle();
        b.putInt("id", diary.getId());
        b.putString("title", diary.getTitle());
        b.putString("content", diary.getContent());
        b.putString("date", diary.getDate());
        b.putString("time", diary.getTime());
        b.putString("imageUri", diary.getImageUri());
        return b;
    }

    public static Diary fromBundle(Bundle b) {
        Diary d = new Diary();
        d.setId(b.getInt("id"));
        d.setTitle(b.getString("title"));
        d.setContent(b.getString("content"));
        d.setDate(b.getString("date"));
        d.setTime(b.getString("time"));
        d.setImageUri(b.getString("imageUri"));
        return d;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
