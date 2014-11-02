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

import java.util.HashMap;
import java.util.Map;

import DataFactory.DataHelper;

/**
 * Created by David on 2014/9/9.
 */
public class InitActivity extends Activity {

    private Button oldSystem = null;
    private Button newSystem = null;
    private DataHelper dataHelper=new DataHelper(this);

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
                app_loginSP(dataHelper.OLDSYSTEM,dataHelper.getOLSJWCURL(),null,null);
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

    //完善登录信息
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
                    app_loginSP(dataHelper.NEWSYSTEM,dataHelper.getNEWJWCURL(),username,password);
                    gotoNext();
                } else {
                    Toast.makeText(InitActivity.this, "你是在逗我吗？", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    //更新sharedPreferences: app_login
    private void app_loginSP (String system,String url,String username,String password){
        Map<String,String> data=new HashMap<String, String>();
        data.put(dataHelper.SYSTEM,system);
        data.put(dataHelper.URL,url);
        data.put(dataHelper.USERNAME,username);
        data.put(dataHelper.PASSWORD,password);
        dataHelper.setSharedPreferencesValues(dataHelper.APPLOGIN,data);
    }

    //跳转到下一Activity
    private void gotoNext() {
        Intent intent = new Intent(InitActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
