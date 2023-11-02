package com.example.message_detection;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Message.class}, version = 2 )
public abstract  class AppDatabase extends RoomDatabase {

    public abstract MessageDao messageDao();

    private static AppDatabase INSTANCE = null;

    public static AppDatabase getDBInstance(Context context){

        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext()
            , AppDatabase.class, "Message.db")
                    .allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
