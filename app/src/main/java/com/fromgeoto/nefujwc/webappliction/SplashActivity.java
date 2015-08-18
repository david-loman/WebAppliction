package com.fromgeoto.nefujwc.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import DataFactory.DataHelper;
import DataFactory.JsonHelper;
import NetWork.QucikConnection;

/**
 *  Created by David on 2015/08/17.
 *  该页面是程序的启动页，用于处于初始化操作。
 *  执行逻辑如下：
 *  <li>检查用户是否为第一次打开应用，是则从网络加载数据</li>
 *  <li>检查用户是否为当天第一次打开应用，是则查看有无新的数据</li>
 *  <li>检查用户是否已经登录，是则跳转到 MainActivity，否则跳到 LoginActivity</li>
 */
public class SplashActivity extends Activity {

    private DataHelper mDataHelper = new DataHelper(getApplicationContext());
    private JsonHelper mJsonHelper = new JsonHelper();
    private QucikConnection mQucikConnection = new QucikConnection(getApplicationContext());
    private ImageView mImageView;
    private boolean mNewFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 删除无用表
        mDataHelper.deleteSharedPreferences("app_login");
        mDataHelper.deleteSharedPreferences(mDataHelper.APPWEBSITE);

        setContentView(R.layout.activity_splash);
        mImageView = (ImageView)findViewById(R.id.splashImageView);

        // 数据加载
        String lastTime = mDataHelper.getSharedPreferencesValue(mDataHelper.APPINFO,mDataHelper.UPDATATIME);
        boolean firstCome = lastTime.equals(mDataHelper.UPDATATIME);
        if (firstCome){
            downloadDate();
        }
        chechAccount();
        checkLogin();
        // 如果第一次使用直接跳登录
        if(!isFirstIntro()){
            goNextActivity(false);
        }
    }

    private void downloadDate() {
        downloadImage();
        downloadAppInfo();
    }

    private void downloadImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String resultURL = QucikConnection.getResultString(mDataHelper.getIMAGEINFOURL());
                if (!resultURL.equals(mDataHelper.getDEFAULTIMAGEURL())){
                    mQucikConnection.saveImage(,resultURL);
                }
                mImageView
            }
        });
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

    private void goNextActivity(boolean done) {
        Intent intent = done ? new Intent(this, MainActivity.class) : new Intent(this, InitActivity.class);
        startActivity(intent);
        this.finish();
    }

    private boolean isFirstIntro (){
        boolean firstIntro = false;
        String version = mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA,mDataHelper.VERSION);
        if (version.equals(mDataHelper.VERSION)){
            // 更新
            firstIntro = true;
            updateAppUpdata(getVersionInfo(0),getVersionInfo(1));
        }
        return firstIntro;
    }

    private void updateAppUpdata (String vs,String vc){
        if (!vs.equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.VERSION))){
            mDataHelper.setSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.VERSION, vs);
        }
        if (!vc.equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.VERSIONCODE))){
            mDataHelper.setSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.VERSIONCODE, vc);
        }
    }

    private String getVersionInfo (int type){
        String reslut = null;
        try{
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(),0);
            reslut = type == 1 ? String.valueOf(pi.versionCode) : pi.versionName;
        }catch (Exception e){
            e.printStackTrace();
            reslut = "ERROR";
        }
        return reslut;
    }

    private void updateImage (){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String lastTime = mDataHelper.getSharedPreferencesValue(mDataHelper.APPINFO,)
            }
        });
    }
}
