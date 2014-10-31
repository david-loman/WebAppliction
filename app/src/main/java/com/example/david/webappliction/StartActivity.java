package com.example.david.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import DataFactory.DataHelper;

/**
 * Created by David on 2014/10/9.
 */
public class StartActivity extends Activity {

    public static String NEWJWCURL = "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk";
    public static String OLSJWCURL = "http://jwcweb.nefu.edu.cn/";
    public static String OLDSYSTEM = "旧教务系统";
    public static String NEWSYSTEM = "新教务系统";
    public static String SYSTEM = "system";
    public static String VERSION = "version";
    public static String INFOMATION = "info";
    public static String SHOWINFO = "showUpdataInfo";
    public static String URL = "url";
    public static String UPDATAAPP = "app_updata";
    public static String INFOAPP = "app_info";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static String CANUPDATA ="canUpdata";
    public static String APPWEBSITE ="app_website";
    public static String LENTH ="len";
    public static String WEBSITE="website";
    public static String NAME="name";
    public DataHelper dataHelper=new DataHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        //预载自定义网站
        initWebsite();
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
    }

    android.os.Handler handler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {

                String jsonString = msg.obj.toString();
                if (jsonString != null && jsonString.length() > 0) {
                    //如果更新app_updata不成功则重新更新
                    boolean flag = false;
                    while (!flag) {
                        flag = updata_appSP(jsonString);
                    }
                } else {
                    //获取jsonString
                    getData();
                }
            }else {
                gotoNext();
            }
        }

    };

    private class DownloadThread extends Thread {

        private String url = new String("http://jwcglxt.qiniudn.com/updataInfo");
        private boolean hasgetInfo = false;
        private StringBuffer stringBuffer = new StringBuffer();
        private int count = 0;

        @Override
        public void run() {
            while (!hasgetInfo) {

                try {

                    java.net.URL Url = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = connection.getInputStream();

                        byte[] buff = new byte[1024];
                        int len = 0;

                        while ((len = inputStream.read(buff)) != -1) {
                            String s = new String(buff);
                            stringBuffer.append(s);
                        }

                        inputStream.close();
                        url = null;
                    } else {
                        Toast.makeText(StartActivity.this, "网络连接有问题", Toast.LENGTH_SHORT).show();
                    }

                    //返回从网络获得的版本信息
                    Message message = Message.obtain();
                    message.arg1 = 1;
                    message.obj = stringBuffer.toString();
                    handler.sendMessage(message);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (stringBuffer != null) {
                    hasgetInfo = true;
                }
                stringBuffer = null;

            }

            while (count <= 2) {
                count++;
                try {
                    DownloadThread.sleep(750);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Message message=Message.obtain();
            message.arg1=0;
            handler.sendMessage(message);

        }
    }

    private boolean cheeckNetWork() {

        ConnectivityManager conn = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        boolean mobile = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
        return (wifi || mobile);
    }

    //更新sharedPreferences: app_updata
    private boolean updata_appSP(String jsonString) {
        boolean hasDone = false;

        try {

            String versionName =null;
            SharedPreferences sharedPreferences = getSharedPreferences(UPDATAAPP, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject childObject = jsonObject.getJSONObject(INFOMATION);
            versionName=jsonObject.getString(VERSION);
            dataHelper.setSharedPreferencesValue(UPDATAAPP,URL,jsonObject.getString(URL));
            dataHelper.setSharedPreferencesValue(UPDATAAPP,dataHelper.ONE,childObject.getString(dataHelper.ONE));
            dataHelper.setSharedPreferencesValue(UPDATAAPP,dataHelper.TWO,childObject.getString(dataHelper.TWO));
            dataHelper.setSharedPreferencesValue(UPDATAAPP,dataHelper.THREE,childObject.getString(dataHelper.THREE));
//            editor.putString(URL, jsonObject.getString(URL));
//            editor.putString("one", childObject.getString("one"));
//            editor.putString("two", childObject.getString("two"));
//            editor.putString("three", childObject.getString("three"));

            //获取本应用版本名称
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            String app_version = info.versionName;
            //判断可否升级
            if (app_version.equals(versionName)){
                editor.putBoolean(CANUPDATA,false);
            }else{
                editor.putBoolean(CANUPDATA,true);
                if ( !versionName.equals(sharedPreferences.getString(VERSION,VERSION)) ) {
                    editor.putInt(SHOWINFO, 0);
                }else {
                    editor.putInt(SHOWINFO,sharedPreferences.getInt(SHOWINFO,0)+1);
                }
            }
            editor.putString(StartActivity.VERSION,versionName);
            //提交编辑
            editor.commit();
            hasDone = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasDone;
    }

    private void getData() {
        DownloadThread thread = new DownloadThread();
        thread.start();
    }

    private boolean checkAppInfo() {
        boolean hasInit = false;

        SharedPreferences sharedPreferences = getSharedPreferences(INFOAPP, MODE_PRIVATE);
        String system = sharedPreferences.getString(SYSTEM, "hello");

        if (!system.equals("hello")) {
            hasInit = true;
        } else {
            hasInit = false;
        }

        return hasInit;
    }

    private void gotoNext() {
        Intent intent = null;
        if (checkAppInfo()) {
            intent = new Intent(StartActivity.this, MainActivity.class);
        } else {
            intent = new Intent(StartActivity.this, InitActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void initWebsite (){
        SharedPreferences sp=getSharedPreferences(APPWEBSITE,MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        //判断数据是否输入
        if (sp.getString(INFOMATION,INFOMATION).equals(INFOMATION)){
            String initData="{ \"len\" : 3 , \"website\" : [ {\"name\": \"东北林业大学\",\"url\":\"http://www.nefu.edu.cn/\" ,\"username\":\"null\" ,\"password\": \"null\"} ,{\"name\": \"林大教务处\",\"url\":\"http://jwc.nefu.edu.cn/\" ,\"username\":\"null\" ,\"password\": \"null\"} ,{\"name\": \"百度一下\",\"url\":\"http://www.baidu.com\" ,\"username\":\"null\" ,\"password\": \"null\"}  ] }";
            editor.putString(INFOMATION,initData);
            editor.commit();
        }

        editor=null;
        sp=null;
    }

}
