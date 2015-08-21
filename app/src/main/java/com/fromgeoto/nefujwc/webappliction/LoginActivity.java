package com.fromgeoto.nefujwc.webappliction;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Map;

import BaseView.BaseActivity;
import NetWork.QucikConnection;


public class LoginActivity extends BaseActivity {

    private ImageView mIconImageView;
    private RelativeLayout mRelativeLayout;
    private TextView mTipTextView;
    private EditText mPasswordEditText,mUsernameEditText;
    private Button mLoginButton,mCancelButton;
    private InputMethodManager mInputMethodManager;
    private QucikConnection mQucikConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        initView();
        mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mQucikConnection = new QucikConnection(this);
    }

    private void initView() {
        mIconImageView = (ImageView) findViewById(R.id.iconImageView);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.checkLayout);
        mTipTextView = (TextView)findViewById(R.id.tipTextView);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);

        mRelativeLayout.setVisibility(View.INVISIBLE);
        mTipTextView.setVisibility(View.INVISIBLE);
        mLoginButton.setText("登录");
        mLoginButton.setInputType(InputType.TYPE_CLASS_NUMBER);
        mCancelButton.setText("取消");
        mLoginButton.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUsernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (mUsernameEditText.getText().toString().length() == 8 || mUsernameEditText.getText().toString().length() == 10) {
                        downloadIcon(mUsernameEditText.getText().toString());
                    } else {
                        initInput();
                    }
                }
            }
        });
        
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRelativeLayout.setVisibility(View.VISIBLE);
                if (mPasswordEditText.getText().toString().length() > 8) {
                    checkLogin(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(),"输入的密码有误",Toast.LENGTH_LONG).show();
                    mPasswordEditText.setText("");
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initInput();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what > 0){
                mRelativeLayout.setVisibility(View.INVISIBLE);
                mTipTextView.setVisibility(View.VISIBLE);
                gotoMainActivity();
            }else {
                Toast.makeText(getApplicationContext(),"输入的密码有误",Toast.LENGTH_LONG).show();
                mPasswordEditText.setText("");
            }
        }
    };
    
    private void  checkLogin(final String uid, final String psw){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                Map<String, String> map = QucikConnection.getResultMap(uid, psw, mDataHelper.getPOSTURL());
                if ((map.get(mDataHelper.USERNAME) != null) && (!mDataHelper.USERNAME.equals(map.get(mDataHelper.USERNAME)))) {
                    map.put(mDataHelper.USERID, uid);
                    map.put(mDataHelper.PASSWORD, psw);
                    mDataHelper.setSharedPreferencesValues(mDataHelper.APPACCOUNT, map);
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                    msg.what = -1;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    
    private void downloadIcon (final String uid){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (QucikConnection.saveImage(new File(getFilesDir().getAbsolutePath() + "/" + mDataHelper.SAVEFILE), mDataHelper.getICONURL() + uid + ".JPG")) {
                    mIconImageView.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + "/" + mDataHelper.SAVEFILE));
                } else {
                    Toast.makeText(LoginActivity.this, "This Account maybe has some problem !", Toast.LENGTH_LONG).show();
                    initInput();
                }
            }
        });
    }

    private void initInput() {
        mRelativeLayout.setVisibility(View.INVISIBLE);
        mIconImageView.setImageResource(R.drawable.owl);
        mIconImageView.setVisibility(View.VISIBLE);
        //关闭软键盘 （万恶的软键盘）
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        mPasswordEditText.setText("");
        mUsernameEditText.setText("");
    }

    private void gotoMainActivity(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
