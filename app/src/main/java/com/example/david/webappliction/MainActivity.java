package com.example.david.webappliction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;

/**
 * Created by David on 2014/9/9.
 */
public class MainActivity extends Activity {

    private SharedPreferences sp = null;
    private Button oldSystem = null;
    private Button newSystem = null;
    private Button sureButton = null;
    private TextView defalutSystem = null;
    private EditText username = null;
    private EditText password = null;
    private String url = null;
    private String usernameValue = null;
    private String passwordValue = null;
    private String URLCODE = "noClick";
    private boolean isOK = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("app", MODE_PRIVATE);
        if (sp.getString("System_name", "old").equals("old")) {
            setContentView(R.layout.activity_init);
            oldSystem = (Button) findViewById(R.id.oldsystem);
            newSystem = (Button) findViewById(R.id.newsystem);
            defalutSystem = (TextView) findViewById(R.id.show_result);
            username = (EditText) findViewById(R.id.usrname);
            password = (EditText) findViewById(R.id.password);
            sureButton = (Button) findViewById(R.id.sure);

            //取消EditText默认的焦点
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            url = "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk";
            URLCODE="postNewJwcURL";
            defalutSystem.setText("新教务系统");
        } else {
            Intent intent = new Intent(MainActivity.this, MyActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        oldSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "http://jwcweb.nefu.edu.cn/";
                URLCODE = "oldJwcURL";
                defalutSystem.setText("旧教务系统");
            }
        });

        newSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk";
                URLCODE = "postNewJwcURL";
                defalutSystem.setText("新教务系统");
            }
        });

        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameValue = username.getText().toString();
                passwordValue = password.getText().toString();
                if (URLCODE.equals("postNewJwcURL") && (usernameValue == null || usernameValue.length() <= 0 || password == null || password.length() <= 0)) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("设置信息错误").setMessage("请输入学号与密码")
                            .setPositiveButton("确认", null).show();
                    isOK = false;
                } else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("System_name", URLCODE);
                    editor.putString("URL", url);
                    if (URLCODE.equals("postNewJwcURL")) {

                        editor.putString("USERNAME", usernameValue);
                        editor.putString("PASSWORD", passwordValue);
                    } else {
                        editor.putString("USERNAME", "0");
                        editor.putString("PASSWORD", "0");
                    }
                    editor.commit();
                    isOK = true;
                }
                if (isOK) {
                    Intent intent = new Intent(MainActivity.this, MyActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

}
