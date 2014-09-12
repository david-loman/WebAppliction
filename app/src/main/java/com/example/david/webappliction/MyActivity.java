package com.example.david.webappliction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.util.EncodingUtils;

public class MyActivity extends ActionBarActivity {

    private final int requestCode = 1;
    private String url = null;
    private String urlCode=null;
    private String introUrl=null;
    private WebView webView;
    private ConnectivityManager conn;
    private SharedPreferences sp;
    private boolean wifi;
    private boolean mobile;
    private final String USERNAME="USERNAME";
    private final String PASSWORD="PASSWORD";
    private final String URL="URL";
    private final String OLDSYSTEM="oldJwcURL";
    private final String NEWSYSTEM="postNewJwcURL";
    private final String SYSTEM_NAME="System_name";
    private final String OLDJWCURL="http://jwcweb.nefu.edu.cn/";
    private final String DEFALUTJWCURL="http://jwcnew.nefu.edu.cn/dblydx_jsxsd/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        webView = (WebView) findViewById(R.id.myWebView);

        sp=getSharedPreferences("app",MODE_PRIVATE);
        urlCode=sp.getString(SYSTEM_NAME,"oldSystem");
        url=sp.getString(URL,DEFALUTJWCURL);
        if (urlCode.equals("oldSystem")){
            introUrl=DEFALUTJWCURL;
        }else {
            introUrl=url;
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
            if (urlCode.equals(OLDSYSTEM)){
                webView.loadUrl(url);
            }else if (urlCode.equals(NEWSYSTEM)){
                String name=sp.getString(USERNAME,USERNAME);
                String psw=sp.getString(PASSWORD,PASSWORD);
                StringBuffer stringBuffer=new StringBuffer();
                stringBuffer.append(USERNAME).append("=").append(name).append("&").append(PASSWORD).append("=").append(psw);
                webView.postUrl(url, EncodingUtils.getBytes(stringBuffer.toString(),"base64"));
            }else {
                AlertDialog alertDialog=new AlertDialog.Builder(MyActivity.this).setTitle("错误").setMessage("获取数据出错，请重新启动应用!")
                        .setPositiveButton("是", null).show();
            }
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
                    AlertDialog alertDialog=new AlertDialog.Builder(MyActivity.this).setTitle("提示信息").setMessage("是否要退出应用")
                            .setNegativeButton("取消",null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals(introUrl)|| url.equals(DEFALUTJWCURL)){
                    Toast.makeText(MyActivity.this,"页面加载中",Toast.LENGTH_SHORT).show();
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.equals(introUrl) || url.equals(DEFALUTJWCURL)) {
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
    protected void onDestroy() {
        //清除Cookie信息
        CookieSyncManager.createInstance(MyActivity.this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }else if (keyCode ==KeyEvent.KEYCODE_BACK && !webView.canGoBack()){
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
            Intent intent =new Intent(MyActivity.this,SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_exit) {
            this.finish();
        } else if (id== R.id.action_newJwc){
            url=DEFALUTJWCURL;
            webView.loadUrl(url);
        }else if (id== R.id.action_oldJwc){
            url=OLDJWCURL;
            webView.loadUrl(url);
        }else if(id==R.id.change){

            LayoutInflater inflater =getLayoutInflater();
            View loginView=inflater.inflate(R.layout.changeuser_layout,null);
            final EditText user=(EditText)loginView.findViewById(R.id.usernameEditText);
            final EditText psw=(EditText)loginView.findViewById(R.id.passwordEditText);

            AlertDialog dialog=new AlertDialog.Builder(MyActivity.this).setTitle("请输入学号、密码").setView(loginView).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name, psword;
                    name = user.getText().toString();
                    psword = psw.getText().toString();
                    if (name == null || name.length() <= 0 || psw == null || psw.length() <= 0) {
                        Toast.makeText(MyActivity.this, "请输入完整的信息！", Toast.LENGTH_SHORT).show();
                    } else {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(USERNAME).append("=").append(name).append("&").append(PASSWORD).append("=").append(psword);
                        //清除Cookie信息
                        CookieSyncManager.createInstance(MyActivity.this);
                        CookieSyncManager.getInstance().startSync();
                        CookieManager.getInstance().removeSessionCookie();
                        //加载页面
                        webView.postUrl("http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk", EncodingUtils.getBytes(stringBuffer.toString(), "base64"));
                    }
                }
            }).show();
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
