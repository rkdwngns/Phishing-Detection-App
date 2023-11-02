package com.example.message_detection;

import android.util.Log;

public class DatabaseTask implements Runnable {
    private AppDatabase db;
    private String title;
    private String text;
    private int type;
    private String category;

    private String Package;

    public DatabaseTask(AppDatabase db, String title, String text,String category,int type,String Package) {
        this.db = db;
        this.title = title;
        this.text = text;
        this.category = category;
        this.type = type;
        this.Package = Package;
        }

    @Override
    public void run() {
        db.messageDao().insertMessage(new Message(title, text, category ,type,Package));
        Log.d("DBInsert","문자추가!");
    }
}

