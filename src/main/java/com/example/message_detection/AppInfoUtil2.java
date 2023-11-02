package com.example.message_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppInfoUtil2 extends AppCompatActivity {
    private NotiAdapter notiAdapter;

    private static MsgAdapter msgAdapter = GlobalStore.getMsgAdpater();
    private ArrayList<MessageData> allNotifications;
    private String title;
    private String text;
    private ListView notilistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appinfoutil2);

        allNotifications = msgAdapter.getMessageList();
        String AppPackage = getIntent().getStringExtra("app");
        title = getIntent().getStringExtra("title");
        text = getIntent().getStringExtra("text");

        Log.d("AppInfoUtil2", "앱: " + AppPackage);
        Log.d("AppInfoUtil2", "내용 : " + text);
        Log.d("AppInfoUtil2", "제목  : " + title);


        //앱 아이콘
        Drawable appIcon = getAppIcon(this, AppPackage);
        ImageView icon = (ImageView) findViewById(R.id.appicon);
        icon.setImageDrawable(appIcon);

        //앱 이름
        String AppName = getAppName(getApplicationContext(),AppPackage);
        TextView name = (TextView) findViewById(R.id.appname);
        name.setText(AppName);

        //앱 버전
        String AppVer = getVersion(getApplicationContext(),AppPackage);
        TextView ver = (TextView) findViewById(R.id.appversion);
        ver.setText(AppVer);

        Log.d("AppInfo", "앱 이름 : "+ AppName );

        notilistView = findViewById(R.id.NotiList);
        notiAdapter = new NotiAdapter(this, allNotifications);
        notilistView.setAdapter(notiAdapter);

        // 새로운 알림을 가져와 어댑터를 업데이트합니다.
        ArrayList<MessageData> newNotifications = getNewNotificationsFromApp(AppPackage);
        notiAdapter.updateAdapter(newNotifications);

    }

    private ArrayList<MessageData> getNewNotificationsFromApp(String appPackage) {
        Log.d( "getNewNotificationsFromApp: ","메소드 진입");
        ArrayList<MessageData> newNotifications = new ArrayList<>();
        // 이전에 저장한 값들을 유지하기 위해 allNotifications 리스트를 사용합니다.
        for (MessageData data : allNotifications) {
            // 해당 앱에 대한 알림만 필터링하여 newNotifications에 추가합니다.
            if (data.getPackage().equals(appPackage)) {
                Log.d( "getNewNotificationsFromApp: ",appPackage + data);
                newNotifications.add(data);
            }
        }
        return newNotifications;
    }

    //해당 앱 이름 가져오기
    public String getAppName(Context context, String packageName) {
        String appName = getIntent().getStringExtra("app"); ;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo i = pm.getPackageInfo(packageName, 0);
            appName = i.applicationInfo.loadLabel(pm) + "";
        } catch(PackageManager.NameNotFoundException e) { }
        return appName;
    }

    //버전명 가져오기
    public String getVersion(Context context, String packageName) {
        String versionName = getIntent().getStringExtra("app");
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = pInfo.versionName + "";
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("AppInfo", "버전 코드 : " +versionName);
        return versionName;
    }

    //아이콘 가져오기
    public Drawable getAppIcon(Context context, String packageName) {
        Drawable appIcon = Drawable.createFromPath(getIntent().getStringExtra("app"));
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            appIcon = appInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appIcon;
    }

}
