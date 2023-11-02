package com.example.message_detection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class Msgactivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String text = getIntent().getStringExtra("text");
        String category = getIntent().getStringExtra("category");

        // 나머지 코드...
        TextView msgbox = (TextView) findViewById(R.id.msg_text);
        msgbox.setText(text);
        TextView nbrbox = (TextView) findViewById(R.id.phone_nbr);
        nbrbox.setText(phoneNumber);
        TextView ctgbox = (TextView) findViewById(R.id.category);
        ctgbox.setText(category);


        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티를 종료하여 이전 액티비티로 돌아감
            }
        });

        Button msgButton = findViewById(R.id.message_app);
        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("smsto:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                /*
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
                startActivity(intent);
                */
            }
        });
    }
}
