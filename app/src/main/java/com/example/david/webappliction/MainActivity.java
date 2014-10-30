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

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private final int requestCode = 1;
    private String USERNAME = "USERNAME";
    private String PASSWORD = "PASSWORD";
    private String systemUrl = null;
    private String systemName = null;
    private String visitUrl = null;
    private ConnectivityManager conn;
    private SharedPreferences sp;
    private boolean wifi;
    private boolean mobile;
    private String defalutInfo = "hello";
    private String myURL = "http://davidloman.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //检查更新
        if (checkUpdata()) {
            showUpdataDialog();
        }

        webView = (WebView) findViewById(R.id.myWebView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        sp = getSharedPreferences(StartActivity.INFOAPP, MODE_PRIVATE);
        systemName = sp.getString(StartActivity.SYSTEM, defalutInfo);
        systemUrl = sp.getString(StartActivity.URL, myURL);

        if (systemName.equals(defalutInfo)) {
            Toast.makeText(this, "数据加载错误，请重启应用", Toast.LENGTH_SHORT).show();
        } else {
            visitUrl = systemUrl;
        }

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

        if (cheeckNetWork()) {
            if (systemName.equals(StartActivity.OLDSYSTEM)) {
                webView.loadUrl(visitUrl);
            } else if (systemName.equals(StartActivity.NEWSYSTEM)) {
                String name = sp.getString(StartActivity.USERNAME, StartActivity.USERNAME);
                String psw = sp.getString(StartActivity.PASSWORD, StartActivity.PASSWORD);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(USERNAME).append("=").append(name).append("&").append(PASSWORD).append("=").append(psw);
                webView.postUrl(visitUrl, EncodingUtils.getBytes(stringBuffer.toString(), "base64"));
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).setTitle("错误").setMessage("数据加载错误，请重启应用!")
                        .setPositiveButton("是", null).show();
            }
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
                if (cheeckNetWork()) {
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
        //清除Cookie信息
        CookieSyncManager.createInstance(MainActivity.this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
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
            //清除Cookie信息
            CookieSyncManager.createInstance(MainActivity.this);
            CookieSyncManager.getInstance().startSync();
            CookieManager.getInstance().removeSessionCookie();

            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            intent.putExtra(StartActivity.SYSTEM, systemName);
            startActivityForResult(intent, 1);
        } else if (id == R.id.action_exit) {
            this.finish();
        } else if (id == R.id.action_oldJwc) {
            visitUrl = StartActivity.OLSJWCURL;
            webView.loadUrl(visitUrl);
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
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(USERNAME).append("=").append(name).append("&").append(PASSWORD).append("=").append(psword);
                        //清除Cookie信息
                        CookieSyncManager.createInstance(MainActivity.this);
                        CookieSyncManager.getInstance().startSync();
                        CookieManager.getInstance().removeSessionCookie();
                        //加载页面
                        webView.postUrl(StartActivity.NEWJWCURL, EncodingUtils.getBytes(stringBuffer.toString(), "base64"));
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
                //装填数据
                SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.listitem, new String[]{StartActivity.NAME}, new int[]{R.id.item_website});
                listView.setAdapter(adapter);
                //弹框
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("我的网站").setView(view)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", null)
                        .show();
            }
            //点击有惊喜
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    visitUrl = list.get(position).get("url");
                    webView.loadUrl(visitUrl);
                }
            });

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
            systemName = StartActivity.NEWSYSTEM;
            systemUrl = StartActivity.NEWJWCURL;
        }

        if (resultCode == 2) {
            systemName = StartActivity.OLDSYSTEM;
            systemUrl = StartActivity.OLSJWCURL;
        }
        visitUrl = systemUrl;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("url", visitUrl);
    }

    private boolean cheeckNetWork() {
        conn = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        mobile = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
        return (wifi || mobile);
    }

    private boolean checkUpdata() {

        boolean canUpdata = false;
        SharedPreferences sp = getSharedPreferences(StartActivity.UPDATAAPP, MODE_PRIVATE);
        if (sp.getBoolean(StartActivity.CANUPDATA, false) && (sp.getInt(StartActivity.SHOWINFO, 0) < 3)) {
            canUpdata = true;
        }
        return canUpdata;
    }

    private String getUpdataUrl() {

        String updataUrl = null;
        SharedPreferences sharedPreferences = getSharedPreferences(StartActivity.UPDATAAPP, MODE_PRIVATE);
        updataUrl = sharedPreferences.getString(StartActivity.URL, myURL);
        return updataUrl;
    }

    private void showUpdataDialog() {

        SharedPreferences sp = getSharedPreferences(StartActivity.UPDATAAPP, MODE_PRIVATE);
        String infomation = sp.getString(StartActivity.VERSION, "0.0.0") + "\n"
                + "1. " + sp.getString("one", "Null") + "\n"
                + "2. " + sp.getString("two", "Null") + "\n"
                + "3. " + sp.getString("three", "Null") + "\n\n"
                + "是否更新？";

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("更新提醒").setMessage(infomation).setNegativeButton("等等", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String path = getUpdataUrl();
                        Uri uri = Uri.parse(path);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).show();
    }

    private List<Map<String, String>> getWebsite() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        SharedPreferences sp = getSharedPreferences(StartActivity.APPWEBSITE, MODE_PRIVATE);
        String json = sp.getString(StartActivity.INFOMATION, StartActivity.INFOMATION);
        if (json.equals(StartActivity.INFOMATION)) {
            Toast.makeText(this, "数据加载错误", Toast.LENGTH_SHORT).show();
        } else {

            try {
                JSONObject jsonObject = new JSONObject(json);
                int len = jsonObject.getInt(StartActivity.LENTH);
                JSONArray jsonArray = jsonObject.getJSONArray(StartActivity.WEBSITE);
                for (int i = 0; i < len; i++) {

                    JSONObject childJsonObject = jsonArray.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(StartActivity.NAME, childJsonObject.getString(StartActivity.NAME));
                    map.put(StartActivity.URL, childJsonObject.getString(StartActivity.URL));
                    map.put(StartActivity.USERNAME, childJsonObject.getString(StartActivity.USERNAME));
                    map.put(StartActivity.PASSWORD, childJsonObject.getString(StartActivity.PASSWORD));

                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return list;
    }
}
