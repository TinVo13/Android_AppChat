package com.example.appchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChatInfoActivity extends AppCompatActivity {
    private ImageButton btnBackChatInfo;
    private TextView xoaBanTv,timTinNhantv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        khoitao();

        btnBackChatInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        xoaBanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatInfoActivity.this)
                        .setCancelable(true)
                        .setTitle("Bạn có chắc muốn xóa bạn với người này?")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("Nguời này không biết đâu :))")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Thôi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        });
    }

    private void khoitao() {
        btnBackChatInfo = findViewById(R.id.btnBackChatInfo);
        xoaBanTv = findViewById(R.id.xoaBanTv);
        timTinNhantv = findViewById(R.id.timTinNhanTv);
    }
}