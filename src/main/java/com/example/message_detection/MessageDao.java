package com.example.message_detection;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM Message")
    List<Message> getAllMessage();

    @Insert
    void insertMessage(Message message);
    /*
    @Delete
    void MessageDelete(Message message);

    @Update
    void MessageUpdate(Message message);
    */

}

