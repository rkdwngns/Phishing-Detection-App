package com.example.message_detection;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "phoneNumber")
    public String phoneNumber;

    @ColumnInfo(name = "msgText")
    public String msgText;

    @ColumnInfo(name = "category")
    public String category;
    @ColumnInfo(name = "type")
    public int type;

    @ColumnInfo(name = "Package")
    public String Package;

    public Message() {}

    public Message(String phoneNumber, String text,String category,int type,String Package) {
        this.phoneNumber = phoneNumber;
        this.msgText = text;
        this.category = category;
        this.type = type;
        this.Package = Package;
    }
}
