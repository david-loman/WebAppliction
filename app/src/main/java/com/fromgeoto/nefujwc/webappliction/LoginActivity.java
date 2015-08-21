package com.fromgeoto.nefujwc.webappliction;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import BaseView.BaseActivity;


public class LoginActivity extends BaseActivity {

    private ImageView mIconImageView;
    private RelativeLayout mRelativeLayout;
    private TextView mTipTextView;
    private EditText mPasswordEditText;
    private EditText mUsernameEditText;
    private Button mLoginButton;
    private Button mCancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        initView();
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



}
