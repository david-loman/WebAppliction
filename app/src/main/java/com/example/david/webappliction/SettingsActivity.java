package com.example.david.webappliction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 2014/8/28.
 */
public class SettingsActivity extends Activity {

    private Button commitButton;
    private Button oldSystemButton, newSystemButton, notPswButton, hasPswButton;
    private Button diyButton, devButton, helpButton;
    private EditText userEditText, passEditText;
    private boolean oldSystem = false;
    private boolean hasPassword = false;
    private String usr_name = "0";
    private String usr_pass = "*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //取消EditText默认的焦点
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        commitButton = (Button) findViewById(R.id.sureButton);
        oldSystemButton = (Button) findViewById(R.id.selectOldButton);
        newSystemButton = (Button) findViewById(R.id.selectNewButton);
        hasPswButton = (Button) findViewById(R.id.selectHasPswButton);
        notPswButton = (Button) findViewById(R.id.selectNoPswButton);
        diyButton = (Button) findViewById(R.id.diyButton);
        devButton = (Button) findViewById(R.id.developerButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        userEditText = (EditText) findViewById(R.id.user_count_username);
        passEditText = (EditText) findViewById(R.id.user_count_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        oldSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldSystem = true;
            }
        });

        newSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oldSystem) {
                    oldSystem = false;
                }
            }
        });

        hasPswButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasPassword = true;
            }
        });

        notPswButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPassword) {
                    hasPassword = false;
                }
            }
        });

        diyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "该功能未实现!", Toast.LENGTH_SHORT).show();
            }
        });

        devButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog showMsgDialog = new AlertDialog.Builder(SettingsActivity.this).setTitle("开发着信息").setMessage("应用地址：https://github.com/david-loman/WebAppliction\n联系开发者\nCSDN:http://blog.csdn.net/davidloman\nGitHub:https://github.com/david-loman").setPositiveButton("确定", null).show();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "更多帮助请访问Github下本项目的地址", Toast.LENGTH_LONG).show();
            }
        });

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean done =false;
                if (userEditText.getText().toString().length() > 0 && passEditText.getText().toString().length() > 0){
                    done=true;
                }
                if (!hasPassword && !oldSystem && done) {
                    modifydata();
                    go_back();
                } else if (oldSystem) {
                    SharedPreferences sharedPreferences1 = getSharedPreferences("app", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putString("System_name", "oldJwcURL");
                    editor.putString("URL", "http://jwcweb.nefu.edu.cn/");
                    editor.commit();
                    sharedPreferences1 = null;
                    go_back();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).setTitle("错误提示").setMessage("当前设置无效，请重新设置").setPositiveButton("确定", null).show();
                }

            }
        });
    }

    private void modifydata() {

        SharedPreferences sp = getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("System_name", "postNewJwcURL");
        editor.putString("URL", "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk");
        //不存在新数据则直接更改
        if (sp.getString("System_name", "old").equals("oldJwcURL")) {
            editor.putString("System_name", "postNewJwcURL");
            editor.putString("URL", "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk");
            editor.putString("USERNAME", userEditText.getText().toString());
            editor.putString("PASSWORD", passEditText.getText().toString());
        } else {
            usr_name = sp.getString("USERNAME", "0");
            usr_pass = sp.getString("PASSWORD", "0");
            if (usr_name.equals(userEditText.getText().toString())) {
                Toast.makeText(SettingsActivity.this, "用户名未更改", Toast.LENGTH_SHORT).show();
            }

            editor.putString("USERNAME", userEditText.getText().toString());
            editor.putString("PASSWORD", passEditText.getText().toString());
        }

        editor.commit();
        sp = null;
    }

    private void go_back() {
        Intent intent = new Intent(SettingsActivity.this, MyActivity.class);
        startActivity(intent);
        finish();
    }
}
