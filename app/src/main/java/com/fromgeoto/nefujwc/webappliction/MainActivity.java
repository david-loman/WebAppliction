package com.fromgeoto.nefujwc.webappliction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.util.EncodingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataFactory.DataHelper;
import DataFactory.JsonHelper;
import DrawItem.DrawDialog;
import NetWork.QucikConnection;

public class MainActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private WebView webView;
    private ProgressWheel progressWheel;
    private ImageView mImageView ;
    private TextView userIdTextView,userTypeTextView,userNameTextView;
    private final String USERNAME = "USERNAME";
    private final String PASSWORD = "PASSWORD";
    private DataHelper dataHelper = new DataHelper(this);
    private JsonHelper jsonHelper = new JsonHelper();
    private DrawDialog drawDialog = new DrawDialog(this);
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_my);
        initView();
        //检查更新
        if (checkUpdata()) {
            dataHelper.deleteSharedPreferences("app_info");
            showUpdataDialog();
        }

        webView = (WebView) findViewById(R.id.myWebView);
        progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);

        //webView初始化
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        Log.e("URL-69", dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.URL));
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
        MobclickAgent.onResume(this);
        if (QucikConnection.checkNetwork(getApplicationContext())) {
            if (!dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.URL).equals(dataHelper.URL)) {
                login();
            } else {
                Toast.makeText(getApplicationContext(), "登录信息出错，通过备用方式登录。", Toast.LENGTH_SHORT).show();
                login(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.USERID), dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.PASSWORD));
            }
        } else {
            drawDialog.getErrorDialog("网络错误", drawDialog.NetWorkError, drawDialog.exitListener());
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.setInitialScale(39);
                if (QucikConnection.checkNetwork(getApplicationContext())) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("website", url);
                    MobclickAgent.onEvent(MainActivity.this, "visit_ok", map);
                    webView.loadUrl(url);
                } else {
                    drawDialog.getErrorDialog("网络错误", drawDialog.NetWorkError, drawDialog.exitListener());
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(MainActivity.this, "连接错误，请检查该链接是否可用", Toast.LENGTH_SHORT).show();
                view.loadUrl("http://www.baidu.com/");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("errorCode", String.valueOf(errorCode));
                map.put("description", description);
                MobclickAgent.onEvent(MainActivity.this, "visit_error", map);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (progressWheel.getVisibility() != View.VISIBLE) {
                    progressWheel.setVisibility(View.VISIBLE);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressWheel.getVisibility() == View.VISIBLE) {
                    progressWheel.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
            startActivityForResult(intent, 1);
        } else if (id == R.id.action_exit) {
            MobclickAgent.onKillProcess(this);
            this.finish();
        } else if (id == R.id.change) {
            drawDialog.getInputDialog("切换用户", drawDialog.getView(R.layout.changeuser_layout), changeListener());
        } else if (id == R.id.action_website) {
            //获取数据
            final List<Map<String, String>> list = getWebsite();
            if (list == null) {
                Toast.makeText(this, "数据加载错误", Toast.LENGTH_SHORT).show();
            } else {
                //装填数据
                SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.listitem, new String[]{jsonHelper.NAME}, new int[]{R.id.item_website});
                drawDialog.getListview(R.layout.websitelist).setAdapter(adapter);

                AlertDialog dialog = drawDialog.getListDialog("我的网站");
                //点击有惊喜
                drawDialog.getListview().setOnItemClickListener(selectListener(dialog, list));
                //长按删除
                drawDialog.getListview().setOnItemLongClickListener(deleteListener(list));
            }
        } else if (id == R.id.action_share) {
            MobclickAgent.onEvent(this, "share_appliction");
            share();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    //是否可以更新
    private boolean checkUpdata() {
        boolean canUpdata = false;
        int count = Integer.parseInt(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT));
        if (count > 0 && count < 4) {
            canUpdata = true;
        }
        return canUpdata;
    }

    //负责提醒更新
    private void showUpdataDialog() {

        drawDialog.getUpdateDialog(dataHelper.getUpdataInfo(), drawDialog.downloadListener(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.URL)));
        //更新 count值
        int tmp = Integer.parseInt(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT));
        tmp++;
        dataHelper.setSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT, String.valueOf(tmp));
    }

    //访问教务管理系统
    private void login(String username, String password) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(USERNAME).append("=").append(username).append("&").append(PASSWORD).append("=").append(password);
        webView.postUrl(dataHelper.getNEWJWCURL(), EncodingUtils.getBytes(stringBuilder.toString(), "base64"));
        stringBuilder = null;
    }

    private void login() {
        webView.loadUrl(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.URL));
    }

    //清除 Cookie
    private void clearCookies() {
        CookieSyncManager.createInstance(MainActivity.this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
    }

    //获取网站列表
    private List<Map<String, String>> getWebsite() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.DEFAULTWEBSITE).equals(dataHelper.DEFAULTWEBSITE)) {
            Toast.makeText(this, "数据加载错误", Toast.LENGTH_SHORT).show();
        } else {
            List<Map<String, String>> tmp = jsonHelper.parseWebsiteJson(dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.DEFAULTWEBSITE), dataHelper.DEFAULTWEBSITE);
            for (int i = 0; i < tmp.size(); i++) {
                list.add(tmp.get(i));
            }
            if (!dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.MYWEBSITE).equals(dataHelper.MYWEBSITE)) {
                tmp = jsonHelper.parseWebsiteJson(dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.MYWEBSITE), dataHelper.MYWEBSITE);
                for (int i = 0; i < tmp.size(); i++) {
                    list.add(tmp.get(i));
                }
            }
        }
        return list;
    }

    //监听用户变更
    private DialogInterface.OnClickListener changeListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name, psw;
                name = drawDialog.getEditText1().getText().toString();
                psw = drawDialog.getEditText2().getText().toString();
                if (name == null || name.length() <= 0 || psw == null || psw.length() <= 0) {
                    Toast.makeText(MainActivity.this, "请输入完整的信息！", Toast.LENGTH_SHORT).show();
                    MobclickAgent.onEvent(MainActivity.this, "login_error");
                } else {
                    clearCookies();
                    //加载页面
                    login(name, psw);
                    MobclickAgent.onEvent(MainActivity.this, "geust_session");
                }
            }
        };
    }

    //监听列表选择
    private AdapterView.OnItemClickListener selectListener(final AlertDialog dialog, final List<Map<String, String>> list) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.cancel();
                webView.loadUrl(list.get(position).get(dataHelper.URL));
            }
        };
    }

    //监听类表删除
    private AdapterView.OnItemLongClickListener deleteListener(final List<Map<String, String>> list) {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        };
    }

    private void share() {
        //调用系统分享功能
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "便捷登录教务处，你也可以做到的，应用下载：http://davidloman.net/project/jwcglxt/");
        startActivity(Intent.createChooser(sendIntent, "分享到"));
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void initView (){
        //layout
        mImageView = (ImageView)findViewById(R.id.userIconImageView);
        userIdTextView =(TextView)findViewById(R.id.infoUserIdTextView);
        userTypeTextView =(TextView)findViewById(R.id.infoUserTypeTextView);
        userNameTextView =(TextView)findViewById(R.id.infoUserNameTextView);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsolutePath()+"/"+dataHelper.SAVEFILE));
        userNameTextView.setText(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT,dataHelper.USERNAME));
        userIdTextView.setText(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT,dataHelper.USERID));
        userTypeTextView.setText(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT,dataHelper.USERTYPE));
        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("教务助手");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.one,R.string.two){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

}
