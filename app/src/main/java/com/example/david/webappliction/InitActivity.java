package com.example.david.webappliction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by David on 2014/9/9.
 */
public class InitActivity extends Activity {

    private SharedPreferences sp = null;
    private Button oldSystem = null;
    private Button newSystem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        oldSystem = (Button) findViewById(R.id.oldsystem);
        newSystem = (Button) findViewById(R.id.newsystem);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        oldSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences(StartActivity.INFOAPP, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(StartActivity.SYSTEM, StartActivity.OLDSYSTEM);
                editor.putString(StartActivity.URL, StartActivity.OLSJWCURL);
                editor.commit();
                gotoNext();
            }
        });

        newSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });
        super.onResume();

    }

    private void showInputDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.changeuser_layout, null);
        final EditText userEditText = (EditText) view.findViewById(R.id.usernameEditText);
        final EditText passEditText = (EditText) view.findViewById(R.id.passwordEditText);
        AlertDialog dialog = new AlertDialog.Builder(InitActivity.this).setTitle("请输入您的信息").setView(view).setNegativeButton("等等", null).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String username = userEditText.getText().toString();
                String password = passEditText.getText().toString();
                if (username != null && username.length() > 0 && password.length() > 0) {
                    //更新app_info
                    SharedPreferences sharedPreferences = getSharedPreferences(StartActivity.INFOAPP, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(StartActivity.SYSTEM, StartActivity.NEWSYSTEM);
                    editor.putString(StartActivity.URL, StartActivity.NEWJWCURL);
                    editor.putString(StartActivity.USERNAME, username);
                    editor.putString(StartActivity.PASSWORD, password);
                    editor.commit();
                    //打开相关网站
                    gotoNext();
                } else {
                    Toast.makeText(InitActivity.this, "你是在逗我吗？", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    private void gotoNext() {
        Intent intent = new Intent(InitActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
