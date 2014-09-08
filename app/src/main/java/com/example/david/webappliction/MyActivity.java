package com.example.david.webappliction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.apache.http.util.EncodingUtils;

import java.security.KeyException;


public class MyActivity extends ActionBarActivity {

    private final int requestCode = 1;
    private String url = null;
    private WebView webView;
    private ConnectivityManager conn;
    private boolean wifi;
    private boolean mobile;
    private final String name="USERNAME";
    private final String password="PASSWORD";
    private final String oldJwcURL = "http://jwcweb.nefu.edu.cn/";
    private final String newJwcURL="http://jwcnew.nefu.edu.cn/dblydx_jsxsd/";
    private final String postNewJwcURL ="http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        webView = (WebView) findViewById(R.id.myWebView);

        if (savedInstanceState != null) {
            url = savedInstanceState.getString("url");
        } else {
            url = newJwcURL;
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

//            String postData="USERNAME=20112790&PASSWORD=20112790";
//            webView.postUrl(url, EncodingUtils.getBytes(postData, "base64"));
            webView.loadUrl(url);
        } else {
            AlertDialog checkNetWork = new AlertDialog.Builder(MyActivity.this).setTitle("连接错误")
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
                    AlertDialog checkNetWork = new AlertDialog.Builder(MyActivity.this).setTitle("连接错误")
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
                Toast.makeText(MyActivity.this,"连接错误，请检查该链接是否可用",Toast.LENGTH_SHORT).show();
                if (view.canGoBack()){
                    view.goBack();
                }else {
                    view.loadUrl("http://www.baidu.com/");
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals(oldJwcURL)|| url.equals(newJwcURL)){
                    Toast.makeText(MyActivity.this,"页面加载中",Toast.LENGTH_SHORT).show();
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.equals(oldJwcURL) || url.equals(newJwcURL)) {
                    Toast.makeText(MyActivity.this, "页面加载完成", Toast.LENGTH_SHORT).show();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
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
            Toast.makeText(getBaseContext(),"目前尚不支持",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_exit) {
            this.finish();
        } else if (id== R.id.action_newJwc){
            url=newJwcURL;
            webView.loadUrl(url);
        }else if (id== R.id.action_oldJwc){
            url=oldJwcURL;
            webView.loadUrl(url);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            url = data.getStringExtra("URL");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("url", url);
    }

    private boolean cheeckNetWork() {
        conn = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        mobile = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
        return (wifi || mobile);
    }
}
