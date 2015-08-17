package com.fromgeoto.nefujwc.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import DataFactory.DataHelper;
import DataFactory.JsonHelper;
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

        // 如果第一次使用直接跳登录
        if(!isFirstIntro()){
            goNextActivity(false);
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


}
