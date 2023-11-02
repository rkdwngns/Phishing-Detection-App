package com.example.message_detection;

// GlobalStore.java
// Adapter 전역관리

public class GlobalStore {
    private static MsgAdapter msgAdapter;

    public static void setMsgAdapter(MsgAdapter adapter) {
        msgAdapter = adapter;
    }

    public static MsgAdapter getMsgAdpater() {
        return msgAdapter;
    }
}




