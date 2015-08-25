package com.fromgeoto.nefujwc.webappliction;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Map;

import BaseView.BaseViewActivity;
import NetWork.QucikConnection;

public class LoginActivity extends BaseViewActivity {

    private ImageView mIconImageView;
    private RelativeLayout mRelativeLayout;
    private TextView mTipTextView;
    private EditText mPasswordEditText, mUsernameEditText;
    private Button mLoginButton, mCancelButton;
    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        initView();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    protected void initView() {
        mIconImageView = (ImageView) findViewById(R.id.iconImageView);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.checkLayout);
        mTipTextView = (TextView) findViewById(R.id.tipTextView);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);

        mRelativeLayout.setVisibility(View.INVISIBLE);
        mTipTextView.setVisibility(View.INVISIBLE);
        mLoginButton.setText("登录");
        mCancelButton.setText("取消");
        // 监听EditorAction
        mUsernameEditText.setOnEditorActionListener(checkAccountListener);
        mPasswordEditText.setOnEditorActionListener(checkAccountListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // 如果帐号检查没有通过
                int lenth = mUsernameEditText.getText().toString().length();
                if (b && lenth != 8 && lenth != 10) {
                    showUserNameError();
                }
                // 如果帐号检查通过
                if (b && (lenth == 8 || lenth == 10)) {
                    downloadIcon(mUsernameEditText.getText().toString());
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogin();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initInput();
                resetFocus();
            }
        });
        MobclickAgent.onResume(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(getApplicationContext());
    }

    /**
     * 多线程信息处理。
     * 1： 帐号验证通过
     * -1： 密码出错
     * 2： 用户名检测通过
     * 0： 用户名检测未通过
     */
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mRelativeLayout.setVisibility(View.INVISIBLE);
                mTipTextView.setVisibility(View.VISIBLE);
                goNextActivity(true);
            } else if (msg.what == 2) {
                mIconImageView.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + "/" + mDataHelper.SAVEFILE));
            } else {
                if (msg.what == 0) {
                    showUserNameError();
                } else {
                    showPassWordError();
                }
            }
        }
    };

    private void checkLogin(final String uid, final String psw) {
        // 关闭输入法
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        if (checkNetwork()) {
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
        } else {
            showNetErrorDialog(this);
        }
    }

    private void downloadIcon(final String uid) {
        if (checkNetwork()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = Message.obtain();
                    if (QucikConnection.saveImage(new File(getFilesDir().getAbsolutePath() + "/" + mDataHelper.SAVEFILE), mDataHelper.getICONURL() + uid + ".JPG")) {
                        message.what = 2;
                    } else {
                        message.what = 0;
                    }
                    handler.sendMessage(message);
                }
            }).start();
        } else {
            showNetErrorDialog(this);
        }
    }

    private void initInput() {
        resetView();
        //关闭软键盘 （万恶的软键盘）
        if (mInputMethodManager.isActive()) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        mPasswordEditText.setText("");
        mUsernameEditText.setText("");
    }

    private void resetView() {
        mRelativeLayout.setVisibility(View.INVISIBLE);
        mIconImageView.setImageResource(R.drawable.owl);
        mIconImageView.setVisibility(View.VISIBLE);
    }

    private void resetFocus() {
        mPasswordEditText.clearFocus();
        mUsernameEditText.setText("");
        mUsernameEditText.requestFocus();
    }

    private void showUserNameError() {
        Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_LONG).show();
        resetView();
        resetFocus();
    }

    private void showPassWordError() {
        Toast.makeText(getApplicationContext(), "输入的密码有误", Toast.LENGTH_LONG).show();
        mPasswordEditText.setText("");
        mRelativeLayout.setVisibility(View.INVISIBLE);
    }

    // 监听键盘里的用户操作
    private TextView.OnEditorActionListener checkAccountListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_NEXT) {
                int lenth = mUsernameEditText.getText().toString().length();
                if (lenth == 8 || lenth == 10) {
                    downloadIcon(mUsernameEditText.getText().toString());
                    return false;
                } else {
                    mUsernameEditText.setText("");
                    return true;
                }
            } else if (i == EditorInfo.IME_ACTION_DONE) {
                return startLogin();
            }
            return false;
        }
    };

    private boolean startLogin() {
        mRelativeLayout.setVisibility(View.VISIBLE);
        if (mPasswordEditText.getText().toString().length() >= 8 && mUsernameEditText.getText().toString().length() > 0) {
            checkLogin(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString());
            return false;
        } else {
            showPassWordError();
            return true;
        }
    }

    protected void goNextActivity(boolean isComplete) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
