package com.fromgeoto.nefujwc.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import DataFactory.DataHelper;

/**
 * Created by David on 2014/9/9.
 */
public class InitActivity extends Activity {

    private EditText usernameEditText , passwordEditText;
    private Button loginButton , resetButton;
    private ImageView iconView;
    private DataHelper dataHelper=new DataHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
                passwordEditText.setText("");
            }
        });
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    //登录
    private void login (){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (checkLogin(username,password)) {
            app_loginSP(dataHelper.NEWSYSTEM,dataHelper.getNEWJWCURL(),username,password);
            downloadIcon (username);
            gotoNext();
        } else {
            Toast.makeText(InitActivity.this, "无法登录，请检查用户名与密码", Toast.LENGTH_SHORT).show();
            MobclickAgent.onEvent(InitActivity.this,"login_error");
        }
        username=null;
        password=null;
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

    private boolean checkLogin (String user,String pasw){

        return false;
    }

    private void downloadIcon (String user){

    }

    private void initView(){
        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        passwordEditText= (EditText)findViewById(R.id.passwordEditText);
        loginButton =(Button)findViewById(R.id.loginButton);
        loginButton.setText("登录");
        resetButton=(Button)findViewById(R.id.cancelButton);
        resetButton.setText("重置");
        iconView = (ImageView)findViewById(R.id.iconImageView);
        iconView.setVisibility(View.INVISIBLE);
    }
}
