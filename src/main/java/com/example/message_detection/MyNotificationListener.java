package com.example.message_detection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyNotificationListener extends NotificationListenerService {

    private static final String CHANNEL_ID = "10";


    @Override
    public void onNotificationPosted(@NonNull StatusBarNotification sbn) {
        // 알림이 게시될 때마다 호출되며, 여기에 로직을 구현합니다.
        // 카카오톡 com.kakao.talk
        // 삼성 기본 메시지 com.samsung.android.messaging
        Log.d("Notification Details", "앱: "+sbn.getPackageName());
        if (!"com.android.systemui".equals(sbn.getPackageName())&!"android".equals(sbn.getPackageName())) {
            super.onNotificationPosted(sbn);
            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;


            String title = extras.getString(Notification.EXTRA_TITLE);
            title = title.replace("\u2068", "").replace("\u2069", "");
            Pattern pattern = Pattern.compile("새 메시지 (\\d+)개");
            Matcher matcher = pattern.matcher(title);

            // EXTRA_TEXT와 EXTRA_BIG_TEXT 모두 확인합니다.
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
            Log.d("Notification Details", "앱: "
                    + sbn.getPackageName() +
                    ", 제목: " + title +
                    ", 내용: " + text +
                    ", 본문내용: " + bigText);

            Intent intent = new Intent("com.yourdomain.notificationlistener.UPDATE_LIST");
            intent.putExtra("title", title);
            intent.putExtra("text", (String) text);
            intent.putExtra("app",sbn.getPackageName());
            intent.putExtra("type",1);
            if("com.samsung.android.messaging".equals(sbn.getPackageName())||"com.google.android.apps.messaging".equals(sbn.getPackageName())) {
                String category = MsgDT.msg_scan(title, (String) text);
                if(category != null && !matcher.find()) {
                    Log.d("Notification Details", "카테고리: " + category);
                    intent.putExtra("category", category);
                    intent.putExtra("type" , 2);
                }
            }
            sendBroadcast(intent);
        }
    }
}
