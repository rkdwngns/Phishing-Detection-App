package com.example.message_detection;

import java.util.ArrayList;

public class AdrData {
    private static final AdrData holder = new AdrData();
    public static AdrData getInstance() { return holder; }

    private ArrayList<String> arrayList;

    public void setArrayList(ArrayList<String> list) {
        this.arrayList = list;
    }

    public ArrayList<String> getArrayList() {
        return this.arrayList;
    }
}
