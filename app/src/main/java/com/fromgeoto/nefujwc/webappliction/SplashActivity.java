package com.fromgeoto.nefujwc.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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


        setContentView(R.layout.activity_splash);
        mImageView = (ImageView)findViewById(R.id.splashImageView);
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

}
