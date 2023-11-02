package com.example.message_detection;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    Context context;
    private ContentObserver contactsObserver;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 3;
    private ArrayList<MessageData> msgList;
    private MsgAdapter msgAdapter;
    private BroadcastReceiver updateListReceiver;

    private AppDatabase MessageDB = null;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        MessageDB = AppDatabase.getDBInstance(this);
        /* 툴바 삭제
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        */
        // 권한 확인 로직 변경해야함
        onCheckContactsPermission();

        if (!checkNotificationPermission()) {
            showPermissionDialog();
        } else if (!isNotificationServiceEnabled()) {
            showNotificationAccessSettings();
        }

        setupListView();
        updateListReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                String category = intent.getStringExtra("category");
                int type = intent.getIntExtra("type",1);
                String Package = intent.getStringExtra("app");
                Log.d("updateListReceiver","app"+Package);
                if(type == 2){
                    msgAdapter.addMessage(new MessageData(title, text, category,type,Package));
                    msgAdapter.updateAdapter();
                    Log.d("MessageData","text"+text);
                    // DB에 메시지 추가
                    DatabaseTask task = new DatabaseTask(MessageDB, title, text, category,type,Package);
                    Thread t = new Thread(task);
                    t.start();
                }else if(type == 1){
                    //알림 받는 코드
                    msgAdapter.addMessage(new MessageData(title, text,type,Package));
                    msgAdapter.updateAdapter();
                    intent.putExtra("app",Package);
                    Log.d("Notification Details", "앱: " + Package);
                    Log.d("Messagedata","text"+title);
                }
            }
        };
        registerReceiver(updateListReceiver, new IntentFilter("com.yourdomain.notificationlistener.UPDATE_LIST"));

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (updateListReceiver != null) {
            unregisterReceiver(updateListReceiver);
            updateListReceiver = null;
        }
        if (contactsObserver != null) {
            getContentResolver().unregisterContentObserver(contactsObserver);
            contactsObserver = null;
        }
        AppDatabase.destroyInstance();
    }

    private void setListData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<List<Message>> future = executor.submit(new Callable<List<Message>>() {
            @Override
            public List<Message> call() throws Exception {
                // 데이터베이스에서 모든 메시지 가져오기
                return MessageDB.messageDao().getAllMessage();
            }
        });

        executor.shutdown();  // ExecutorService 종료

        try {
            List<Message> messages = future.get();
            for (Message message : messages) {
                msgList.add(new MessageData(message.phoneNumber,message.msgText,message.category,message.type,message.Package));
            }
            // 어댑터 업데이트 (메인 스레드에서 실행되어야 함)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgAdapter.updateAdapter();
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }



    private void setupListView() {
        msgList = new ArrayList<MessageData>();

        ListView listView = findViewById(R.id.list_view);
        msgAdapter = new MsgAdapter(this,msgList); // assign the adapter to the global variable

        listView.setAdapter(msgAdapter);
        // MainActivity.java 에서 어댑터 설정 후:
        GlobalStore.setMsgAdapter(msgAdapter);

        listView.setOnItemClickListener((parent, v, position, id) -> { //리스트
            int type = msgAdapter.getItem(position).getType();
            String phoneNumber = msgAdapter.getItem(position).getTitle();
            String text = msgAdapter.getItem(position).getText();
            String category = msgAdapter.getItem(position).getCategory();
            String AppPackage = msgAdapter.getItem(position).getPackage();
            String title = msgAdapter.getItem(position).getTitle();

            Log.d("cliplist", "앱: " + AppPackage);
            Intent intent = null;   // SMGActivity 시작

            if(type == 1) {
                intent = new Intent(this, AppInfoUtil2.class);
                intent.putExtra("app", AppPackage);
                intent.putExtra("text",text);
                intent.putExtra("title",title);
            } else if (type == 2) {
                intent = new Intent(this, Msgactivity.class);
                intent.putExtra("phoneNumber", phoneNumber);  // phoneNumber 값 전달
                intent.putExtra("text", text);
                intent.putExtra("category", category);
            }
            startActivity(intent);
        });

        setListData();
    }
    private void contact(){
        contactsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                // Code to handle contact change (added, deleted, or updated)
                Log.d("Contacts", "Contact data has changed.");
                GetContact();
            }
        };

        getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI,
                true,
                contactsObserver);
    }


    private boolean checkNotificationPermission() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("알림 권한 필요")
                .setMessage("앱의 기능을 사용하기 위해선 알림 권한이 필요합니다. 설정 화면으로 이동하시겠습니까?")
                .setPositiveButton("설정하기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestNotificationPermission();
                        /*
                        if (!checkNotificationPermission()) {
                            showPermissionDialog();
                        }
                        */
                        if (!isNotificationServiceEnabled()) {
                            showNotificationAccessSettings();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // "필요한 권한이 부족합니다." 메시지 표시
                        Toast.makeText(getApplicationContext(), "필요한 권한이 부족합니다.", Toast.LENGTH_SHORT).show();

                        // 앱 종료
                        //finishAffinity();
                    }
                })
                .setIcon(R.drawable.ic_launcher_foreground)
                .show();
    }

    private void requestNotificationPermission() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

        //for Android 5-7
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);

        // for Android 8 and above
        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

        startActivity(intent);
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void openNotificationAccessSettings() {

        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }

    private void showNotificationAccessSettings(){
        new AlertDialog.Builder(this)
                .setTitle("알림 접근 권한 필요")
                .setMessage("피싱문자탐지 기능을 이용하기 위해서는 알림 접근 권한이 필요합니다.")
                .setPositiveButton("설정하기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openNotificationAccessSettings();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // "필요한 권한이 부족합니다." 메시지 표시
                        Toast.makeText(getApplicationContext(), "필요한 권한이 부족합니다.", Toast.LENGTH_SHORT).show();

                        // 앱 종료
                        //finishAffinity();
                    }
                })
                .show();
    }

    private void GetContact(){
        // 주소록 연락처 배열
        ArrayList<String> AdrbookList = new ArrayList<String>();
        ContentResolver resolver = context.getContentResolver();
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                ,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                ,  ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor cursor = resolver.query(phoneUri, projection, null, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    int nameIndex = cursor.getColumnIndex(projection[1]);
                    int numberIndex = cursor.getColumnIndex(projection[2]);
                    String name = cursor.getString(nameIndex);
                    String number = cursor.getString(numberIndex);
                    number = number.replace("-","");
                    Log.d("GetContact", "이름 : " + name + " 번호 : " + number);
                    AdrbookList.add(name);
                }while(cursor.moveToNext());
            }
            AdrData.getInstance().setArrayList(AdrbookList);
        }
        // 데이터 계열은 반드시 닫아줘야 한다.
        cursor.close();

        // 주소록을 허용했을때만 받아옴 언제 업데이트 처음킬때 받아오지못해 첫실행때 앱이죽음
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                GetContact();

            }else {
                //Toast.makeText(this, "문자 탐지를 위해서는 주소록 접근 권한이 필요합니다", Toast.LENGTH_LONG).show();
            }
        }

    }


    private void onCheckContactsPermission(){
        boolean permissionDenied =  checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED;

        if(permissionDenied){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }else{
            GetContact();
        }

    }



    // 현재 앱이 알림 접근 권한허용 확인
   private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName(); // 현재 앱의 패키지 이름
        final String flat = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners"); // 시스템 설정 값
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
