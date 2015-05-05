package com.fromgeoto.nefujwc.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Map;

import DataFactory.DataHelper;
import NetWork.QucikConnection;

/**
 * Created by David on 2014/9/9.
 */
public class InitActivity extends Activity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, resetButton;
    private ImageView iconView;
    private DataHelper dataHelper = new DataHelper(this);
    private File mFile = null;
    private boolean mIsAccount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        initFile();
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
        MobclickAgent.onResume(this);
        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if ((usernameEditText.getText().toString().length() == 8) || (usernameEditText.getText().toString().length() == 10)) {
                        deleteIcon();
                        downloadIcon(usernameEditText.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "请输入正确的学号", Toast.LENGTH_SHORT).show();
                        initInput();
                    }
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录验证
                if (mIsAccount) {
                    checkLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "请输入正确的学号", Toast.LENGTH_SHORT).show();
                    initInput();
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInput();
                mIsAccount = false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what > 0) {
                iconView.setImageBitmap(BitmapFactory.decodeFile(mFile.getAbsolutePath()));
                iconView.setVisibility(View.VISIBLE);
                gotoNext();
                return true;
            } else {
                Toast.makeText(InitActivity.this, "无法登录，请检查用户名与密码", Toast.LENGTH_SHORT).show();
                MobclickAgent.onEvent(InitActivity.this, "login_error");
                return false;
            }
        }
    });

    //跳转到下一Activity
    private void gotoNext() {
        Intent intent = new Intent(InitActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //下载图像
    private void downloadIcon(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (QucikConnection.saveImage(mFile, dataHelper.getICONURL() + userId + ".JPG")) {
                    mIsAccount = true;
                } else {
                    initInput();
                }
            }
        }).start();
    }

    //登录验证
    private void checkLogin(final String userID, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                Map<String, String> map = QucikConnection.getResultMap(userID, password, dataHelper.getPOSTURL());
                Log.e("Init-T-84", map.get(dataHelper.USERNAME) + " :" + map.get(dataHelper.USERTYPE) + " , " + map.get(dataHelper.URL));
                if ((map.get(dataHelper.USERNAME) != null) && (!dataHelper.USERNAME.equals(map.get(dataHelper.USERNAME)))) {
                    map.put(dataHelper.USERID, userID);
                    map.put(dataHelper.PASSWORD, password);
                    dataHelper.setSharedPreferencesValues(dataHelper.APPACCOUNT, map);
                    msg.what = 2;
                    handler.sendMessage(msg);
                } else {
                    deleteIcon();
                    msg.what = -1;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void initView() {
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setText("登录");
        resetButton = (Button) findViewById(R.id.cancelButton);
        resetButton.setText("重置");
        iconView = (ImageView) findViewById(R.id.iconImageView);
        iconView.setVisibility(View.INVISIBLE);
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void deleteIcon() {
        if (mFile.exists()) {
            mFile.delete();
        }
    }

    private void initFile() {
        try {
            String filePath = getFilesDir().getAbsolutePath() + "/" + dataHelper.SAVEFILE;
            mFile = new File(filePath);
            if (!mFile.exists()) {
                mFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initInput() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        iconView.setVisibility(View.INVISIBLE);
    }
}
