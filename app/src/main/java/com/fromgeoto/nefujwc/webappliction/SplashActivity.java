package com.fromgeoto.nefujwc.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import BaseView.BaseActivity;
import DataFactory.DataHelper;
import DataFactory.JsonHelper;
import NetWork.QucikConnection;

/**
 * Created by David Lin on 2015/08/17.
 * 该页面是程序的启动页，用于处于初始化操作。
 * 执行逻辑如下：
 * <li>检查用户是否为当天第一次打开应用，是则查看有无新的数据 。
 * <li>检查用户是否为第一次打开应用，是则跳转到登录页 。
 * <li>检查用户是否已经登录，是则跳转到 MainActivity，否则跳到 LoginActivity 。
 */
public class SplashActivity extends BaseActivity {

    private JsonHelper mJsonHelper = new JsonHelper();
    private QucikConnection mQucikConnection;
    private ImageView mImageView;
    private boolean hasLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 删除无用表
        mDataHelper.deleteSharedPreferences("app_login");
        mDataHelper.deleteSharedPreferences("app_website");

        setContentView(R.layout.activity_splash);
        initView();

        // 单日第一次进入，更新数据
        boolean firstCome = (!mDataHelper.getSharedPreferencesValue(mDataHelper.APPINFO, mDataHelper.UPDATATIME).equals(mDataHelper.getTime()) ||
                mDataHelper.UPDATATIME.equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPINFO, mDataHelper.UPDATATIME)));
        if (firstCome) {
            mDataHelper.setSharedPreferencesValue(mDataHelper.APPINFO, mDataHelper.UPDATATIME, mDataHelper.getTime());
            downloadDate();
        } else {
            // 如果有新的欢迎页则加载
            if (Boolean.parseBoolean(mDataHelper.getSharedPreferencesValue(mDataHelper.APPINFO, mDataHelper.WELCOMEIMAGE))) {
                mImageView.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsoluteFile() + mDataHelper.WELCOMEIMAGE));
            }
        }
        updateCount();
        // 用户第一次登录，无需检查帐号信息
        if (mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.PASSWORD).equals(mDataHelper.PASSWORD)) {
            goNextActivity(false);
        } else {
            if (checkNetwork()) {
                checkLogin();
                goNextActivity(hasLogin);
            } else {
                showNetErrorDialog(this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(getApplicationContext());
    }

    protected void initView() {
        mImageView = (ImageView) findViewById(R.id.splashImageView);
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 1){
                // 载入图片
                mImageView.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + mDataHelper.WELCOMEIMAGE));
                mDataHelper.setSharedPreferencesValue(mDataHelper.APPINFO, mDataHelper.WELCOMEIMAGE, String.valueOf(true));
            }
        }
    };

    private boolean checkLogin() {
        hasLogin = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ("ERROR".equals(mQucikConnection.getResultString(mDataHelper.getNEWJWCURL()))) {
                    hasLogin = false;
                    return;
                } else {
                    Map<String, String> data = new HashMap<String, String>();
                    data = mQucikConnection.getResultMap(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.USERID), mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.PASSWORD), mDataHelper.getPOSTURL());
                    if (!data.get(mDataHelper.URL).equals(mDataHelper.URL) && data.get(mDataHelper.URL) != null) {
                        mDataHelper.setSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.USERTYPE, data.get(mDataHelper.USERTYPE));
                        mDataHelper.setSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.URL, data.get(mDataHelper.URL));
                        hasLogin = true;
                        return;
                    }
                    return;
                }
            }
        }).start();
        return hasLogin;
    }

    private void downloadDate() {
        if (checkNetwork()) {
            downloadImage();
            downloadAppInfo();
        } else {
            showNetErrorDialog(this);
        }
    }

    private void downloadAppInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> data = new HashMap<String, String>();
                String result = mQucikConnection.getResultString(mDataHelper.getAPPLICATIONINFOURL());
                mDataHelper.setSharedPreferencesValues(mDataHelper.APPUPDATA, mJsonHelper.parseApplictionJson(result,
                        new String[]{mDataHelper.VERSION, mDataHelper.VERSIONCODE, mDataHelper.URL,
                                mDataHelper.ONE, mDataHelper.TWO, mDataHelper.THREE}));
                updateCount();
                Log.e("135", "Good!");
            }
        });
    }

    private void downloadImage() {
        mDataHelper.setSharedPreferencesValue(mDataHelper.APPINFO, mDataHelper.WELCOMEIMAGE, String.valueOf(false));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resultURL = QucikConnection.getResultString(mDataHelper.getIMAGEINFOURL());
                if (!resultURL.equals(mDataHelper.getDEFAULTIMAGEURL()) &&
                        mQucikConnection.saveImage(new File(getFilesDir().getAbsoluteFile() + "/" + mDataHelper.WELCOMEIMAGE), resultURL)) {
                    Message message = Message.obtain();
                    message.arg1= 1;
                    handler.sendMessage(message);
                }
            }
        }).start();

    }

    private void updateCount() {
        if (getVersionInfo(1).equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.VERSIONCODE)) &&
                !mDataHelper.COUNT.equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT))) {
            mDataHelper.setSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT,
                    String.valueOf(Integer.parseInt(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT)) + 1));
        } else {
            mDataHelper.setSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT, String.valueOf(0));
        }
    }

    protected void goNextActivity(boolean done) {
        final Intent intent = done ? new Intent(this, MainActivity.class) : new Intent(this, LoginActivity.class);
        // 延时执行
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        },1500);
    }

    private String getVersionInfo(int type) {
        String reslut = null;
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            reslut = type == 1 ? String.valueOf(pi.versionCode) : pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            reslut = "ERROR";
        }
        return reslut;
    }

}
