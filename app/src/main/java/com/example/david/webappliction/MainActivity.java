package com.example.david.webappliction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataFactory.DataHelper;
import DataFactory.JsonHelper;
import NetWork.QucikConnection;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private final String USERNAME = "USERNAME";
    private final String PASSWORD = "PASSWORD";
    private String systemName = null;
    private DataHelper dataHelper = new DataHelper(this);
    private JsonHelper jsonHelper = new JsonHelper();
    private QucikConnection qucikConnection=new QucikConnection(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //检查更新
        if (checkUpdata()) {
            dataHelper.deleteSharedPreferences("app_info");
            showUpdataDialog();

        }

        webView = (WebView) findViewById(R.id.myWebView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        systemName = dataHelper.getSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.SYSTEM);

        if (systemName.equals(dataHelper.SYSTEM)) {
            Toast.makeText(this, "数据加载错误，请重启应用", Toast.LENGTH_SHORT).show();
        }

        //webView初始化
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (qucikConnection.checkNetwork()) {
            login(systemName,dataHelper.getSharedPreferencesValue(dataHelper.APPLOGIN,dataHelper.USERNAME),dataHelper.getSharedPreferencesValue(dataHelper.APPLOGIN,dataHelper.PASSWORD));
        } else {
            AlertDialog checkNetWork = new AlertDialog.Builder(MainActivity.this).setTitle("连接错误")
                    .setMessage("网络未连接请检查网络后访问").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.setInitialScale(39);
                if (qucikConnection.checkNetwork()) {
                    webView.loadUrl(url);
                } else {
                    AlertDialog checkNetWork = new AlertDialog.Builder(MainActivity.this).setTitle("连接错误")
                            .setMessage("网络未连接请检查网络后访问").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();

                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "连接错误，请检查该链接是否可用", Toast.LENGTH_SHORT).show();
                if (view.canGoBack()) {
                    view.goBack();
                } else {
                    view.loadUrl("http://www.baidu.com/");
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).setTitle("提示信息").setMessage("是否要退出应用")
                            .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (progressBar.getVisibility() != View.VISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearCookies();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && !webView.canGoBack()) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            clearCookies();

            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            intent.putExtra(dataHelper.SYSTEM, systemName);
            startActivityForResult(intent, 1);
        } else if (id == R.id.action_exit) {
            this.finish();
        } else if (id == R.id.action_oldJwc) {
            login(dataHelper.OLDSYSTEM,null,null);
        } else if (id == R.id.change) {

            LayoutInflater inflater = getLayoutInflater();
            View loginView = inflater.inflate(R.layout.changeuser_layout, null);
            final EditText user = (EditText) loginView.findViewById(R.id.usernameEditText);
            final EditText psw = (EditText) loginView.findViewById(R.id.passwordEditText);

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("请输入学号、密码").setView(loginView).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name, psword;
                    name = user.getText().toString();
                    psword = psw.getText().toString();
                    if (name == null || name.length() <= 0 || psw == null || psw.length() <= 0) {
                        Toast.makeText(MainActivity.this, "请输入完整的信息！", Toast.LENGTH_SHORT).show();
                    } else {
                        clearCookies();
                        //加载页面
                        login(dataHelper.NEWSYSTEM,name,psword);
                    }
                }
            }).show();
        } else if (id == R.id.action_website) {
            //获取ListView
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.websitelist, null);
            ListView listView = (ListView) view.findViewById(R.id.website_list);
            //演示专用数据
            final List<Map<String, String>> list = getWebsite();
//            Map<String,String> map=new HashMap<String, String>();
//
//            map.put("name","林大教务处");
//            map.put("url","http://jwc.nefu.edu.cn/");
//            map.put("usename","null");
//            map.put("password","null");
//
//            list.add(map);
            if (list == null) {
                Toast.makeText(this, "数据加载错误", Toast.LENGTH_SHORT).show();
            } else {
                String url=null;
                //装填数据
                SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.listitem, new String[]{jsonHelper.NAME}, new int[]{R.id.item_website});
                listView.setAdapter(adapter);
                //弹框
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("我的网站").setView(view)
                        .setNegativeButton("取消", null)
                        .show();

                //点击有惊喜
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.cancel();
                        webView.loadUrl(list.get(position).get(dataHelper.URL));
                    }
                });
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode > 0) {
            Toast.makeText(this, "设置已更改", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "设置未更改", Toast.LENGTH_SHORT).show();
        }

        if (resultCode == 1) {
            systemName = dataHelper.NEWSYSTEM;
        }

        if (resultCode == 2) {
            systemName = dataHelper.OLDSYSTEM;
        }
    }

    //是否可以更新
    private boolean checkUpdata() {
        boolean canUpdata = false;
        int count = Integer.parseInt(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA,dataHelper.COUNT));
        if (count > 0 && count < 4) {
            canUpdata = true;
        }
        return canUpdata;
    }

    //负责提醒更新
    private void showUpdataDialog() {

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("更新提醒").setMessage(dataHelper.getUpdataInfo()).setNegativeButton("等等", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //需要更换成新的下载管理方式
                        String path = dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.URL);
                        Uri uri = Uri.parse(path);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).show();
        //更新 count值
        int tmp=Integer.parseInt(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA,dataHelper.COUNT));
        tmp++;
        dataHelper.setSharedPreferencesValue(dataHelper.APPUPDATA,dataHelper.COUNT,String.valueOf(tmp));
    }

    //访问教务管理系统
    private void login (String system,String username,String password){
        if (system.equals(dataHelper.OLDSYSTEM)){
            webView.loadUrl(dataHelper.getOLSJWCURL());
        }else {
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(USERNAME).append("=").append(username).append("&").append(PASSWORD).append("=").append(password);
            webView.postUrl(dataHelper.getNEWJWCURL(), EncodingUtils.getBytes(stringBuilder.toString(), "base64"));
            stringBuilder=null;
        }
    }

    //清除 Cookie
    private void clearCookies (){
        CookieSyncManager.createInstance(MainActivity.this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
    }

    private List<Map<String, String>> getWebsite() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE,dataHelper.DEFAULTWEBSITE).equals(dataHelper.DEFAULTWEBSITE)) {
            Toast.makeText(this, "数据加载错误", Toast.LENGTH_SHORT).show();
        } else{
            List<Map<String,String>> tmp=jsonHelper.parseWebsiteJson(dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE,dataHelper.DEFAULTWEBSITE),dataHelper.DEFAULTWEBSITE);
            for (int i=0;i<tmp.size();i++){
                list.add(tmp.get(i));
            }
            if (!dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE,dataHelper.MYWEBSITE).equals(dataHelper.MYWEBSITE)){
                tmp=jsonHelper.parseWebsiteJson(dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE,dataHelper.MYWEBSITE),dataHelper.MYWEBSITE);
                for (int i=0;i<tmp.size();i++){
                    list.add(tmp.get(i));
                }
            }
        }
        return list;
    }
}
