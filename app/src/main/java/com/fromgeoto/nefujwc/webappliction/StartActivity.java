package com.fromgeoto.nefujwc.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;

import DataFactory.DataHelper;
import DataFactory.JsonHelper;
import NetWork.QucikConnection;

/**
 * Created by David on 2014/10/9.
 */
public class StartActivity extends Activity implements Runnable {

    private DataHelper dataHelper = new DataHelper(this);
    private JsonHelper jsonHelper = new JsonHelper();
    private ImageView addImageView;
    private boolean mDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //清理旧数据
        dataHelper.deleteSharedPreferences("app_login");
        //检查可否登录
        checkLogin();
        //界面绘制
        setContentView(R.layout.layout);
        addImageView = (ImageView) findViewById(R.id.welcome);
        //获取数据
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    android.os.Handler handler = new android.os.Handler() {
        boolean update = false;
        boolean website = false;
        boolean next = false;

        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                String jsonString = msg.obj.toString();
                if (jsonString != null && jsonString.length() > 0) {
                    //更新 app_updata
                    app_updataSP(jsonString);
                    update = true;
                }
            } else if (msg.arg1 == 2) {
                String jsonString = msg.obj.toString();
                if (jsonString != null && jsonString.length() > 0) {
                    //更新app_website
                    app_websiteSP(jsonString);
                    website = true;
                }
            } else {
                next = true;
            }
            //防止因为没有更新而卡在登录界面
            if (!dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.VERSION).equals(dataHelper.VERSION)) {
                update = true;
            }
            if (!dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.DEFAULTWEBSITE).equals(dataHelper.DEFAULTWEBSITE)) {
                website = true;
            }
            if (dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT,dataHelper.USERID).equals(dataHelper.USERID)){
                next=true;
            }
            Log.e("TAG-S",String.valueOf(update)+" : "+String.valueOf(website)+" : "+String.valueOf(next));
            while (update && website && next) {
                gotoNext(mDone);
                next = false;
            }
        }

    };

    @Override
    public void run() {
        Message msg = Message.obtain();
        Map<String, String> map = QucikConnection.getResultMap(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.USERID), dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.PASSWORD), dataHelper.getPOSTURL());
        if ((map.get(dataHelper.URL)!= null)&&(!dataHelper.URL.equals(map.get(dataHelper.URL)))) {
            mDone = true;
            dataHelper.setSharedPreferencesValue(dataHelper.APPACCOUNT,dataHelper.USERTYPE,map.get(dataHelper.USERTYPE));
            dataHelper.setSharedPreferencesValue(dataHelper.APPACCOUNT,dataHelper.URL,map.get(dataHelper.URL));
            msg.arg1 = 0;
            handler.sendMessage(msg);
        } else {
            msg.arg1 = -1;
            handler.sendMessage(msg);
        }
        return;
    }

    private class DownloadThread extends Thread {

        private QucikConnection qucikConnection = new QucikConnection(StartActivity.this);
        String result = null;

        @Override
        public void run() {
            //获取更新信息
            if (QucikConnection.checkNetwork(getApplicationContext())) {
                result = qucikConnection.getResultString(dataHelper.getUPDATASTATUSURL());
            }
            boolean[] updataStatus = jsonHelper.parseUpdataJson(result);
            //如果应用需要更新或者未初始化
            result = null;
            if (updataStatus[0] || dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.VERSION).equals(dataHelper.VERSION)) {
                if (QucikConnection.checkNetwork(getApplicationContext())) {
                    result = qucikConnection.getResultString(dataHelper.getAPPLICATIONINFOURL());
                    //一旦有新的更新，将count归零
                    dataHelper.setSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT, String.valueOf(0));
                }
                if (result != null) {
                    sendMsg(1, result);
                }
            }
            //如果列表需要更新或者未初始化
            result = null;
            if (updataStatus[1] || dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.DEFAULTWEBSITE).equals(dataHelper.DEFAULTWEBSITE)) {
                if (QucikConnection.checkNetwork(getApplicationContext())) {
                    result = qucikConnection.getResultString(dataHelper.getDATAINFOURL());
                }
                if (result != null) {
                    sendMsg(2, result);
                }
            }
        }
    }

    //更新sharedPreferences: app_updata
    private void app_updataSP(String jsonString) {

        try {

            String versionName = dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.VERSION);
            //存储到 SharedPreferences中
            dataHelper.setSharedPreferencesValues(dataHelper.APPUPDATA, jsonHelper.parseApplictionJson(jsonString, new String[]{dataHelper.VERSION
                    , dataHelper.URL
                    , dataHelper.ONE, dataHelper.TWO, dataHelper.THREE}));
            //获取版本信息
            if (!versionName.equals(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.VERSION))) {
                dataHelper.setSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT, String.valueOf(1));
            }
            versionName = dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.VERSION);
            //判断可否升级
            if (versionName.equals(getVersionName())) {
                dataHelper.setSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT, String.valueOf(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //更新sharedPreferences: app_website
    private void app_websiteSP(String jsonString) {
        dataHelper.setSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.DEFAULTWEBSITE, jsonString);
    }

    //从网络中获取应用信息
    private void getData() {
        DownloadThread thread = new DownloadThread();
        thread.start();
    }

    //获取版本信息
    private String getVersionName() {
        String versionName = null;
        try {
            //获取本应用版本名称
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    //发送消息
    private void sendMsg(int status, String text) {
        Message message = Message.obtain();
        message.arg1 = status;
        if (status != 0) {
            message.obj = text;
        }
        handler.sendMessage(message);
    }

    //跳转到下一Activity
    private void gotoNext(boolean done) {
        Intent intent = null;
        if (done) {
            intent = new Intent(StartActivity.this, MainActivity.class);
        } else {
            intent = new Intent(StartActivity.this, InitActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void checkLogin() {
        if (!dataHelper.USERID.equals(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.USERID))) {
            new Thread(this).start();
        }
    }

}
