package com.fromgeoto.nefujwc.webappliction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.util.EncodingUtils;

import java.util.HashMap;

import BaseView.BaseViewActivity;
import DataFactory.JsonHelper;
import DataFactory.UmengString;
import DrawItem.DrawDialog;

public class MainActivity extends BaseViewActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private WebView mWebView;
    private ProgressWheel mProgressWheel;
    private ImageView mImageView;
    private TextView mUserIdTextView, mUserTypeTextView, mUserNameTextView;
    private LinearLayout mHomeLinearLayout, mScoreLinearLayout, mTableLinearLayout, mCourseLinearLayout, mExamLinearLayout,
            mSignupLinearLayout, mAppraiseLinearLayout, mCalendarLinearLayout, mGuestLinearLayout, mSettingsLinearLayout, mHelpLinearLayout;
    private ScrollView mItemScrollView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    
    private final String USERNAME = "USERNAME";
    private final String PASSWORD = "PASSWORD";
    private DrawDialog mDrawDialog = new DrawDialog(this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setmWebView();
        // 补丁
        if (mDataHelper.UPDATATIME.equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPINFO,mDataHelper.UPDATATIME))){
            finish();
        }
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

        showUpdateDialog();

        // 登录
        if (checkNetwork()) {
            doLogin(!mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.URL).equals(mDataHelper.URL));
        } else {
            showNetErrorDialog(getApplicationContext());
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.setInitialScale(39);
                if (checkNetwork()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("website", url);
                    MobclickAgent.onEvent(MainActivity.this, UmengString.VISITOK, map);
                    mWebView.loadUrl(url);
                } else {
                    showNetErrorDialog(getApplicationContext());
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
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITERROR, map);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (mProgressWheel.getVisibility() != View.VISIBLE) {
                    mProgressWheel.setVisibility(View.VISIBLE);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressWheel.getVisibility() == View.VISIBLE) {
                    mProgressWheel.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }
        });
        layoutClick();
    }

    private void doLogin(boolean b) {
        if (b) {
            login();
        } else {
            Toast.makeText(getApplicationContext(), "登录信息出错，通过备用方式登录。", Toast.LENGTH_SHORT).show();
            login(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.USERID), mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.PASSWORD));
        }
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
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && !mWebView.canGoBack()) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            MobclickAgent.onKillProcess(this);
            this.finish();
        }  else if (id == R.id.action_share) {
            MobclickAgent.onEvent(this, UmengString.SHAREAPPLICTION);
            share();
        }
        return super.onOptionsItemSelected(item);
    }

    //访问教务管理系统
    private void login(String username, String password) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(USERNAME).append("=").append(username).append("&").append(PASSWORD).append("=").append(password);
        mWebView.postUrl(mDataHelper.getNEWJWCURL(), EncodingUtils.getBytes(stringBuilder.toString(), "base64"));
        stringBuilder = null;
    }

    private void login() {
        mWebView.loadUrl(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.URL));
    }

    //清除 Cookie
    private void clearCookies() {
        CookieSyncManager.createInstance(MainActivity.this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
    }

    //监听用户变更
    private DialogInterface.OnClickListener changeListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name, psw;
                name = mDrawDialog.getEditText1().getText().toString();
                psw = mDrawDialog.getEditText2().getText().toString();
                if (name == null || name.length() <= 0 || psw == null || psw.length() <= 0) {
                    Toast.makeText(MainActivity.this, "请输入完整的信息！", Toast.LENGTH_SHORT).show();
                    MobclickAgent.onEvent(MainActivity.this, UmengString.LOGINERROR);
                } else {
                    clearCookies();
                    //加载页面
                    login(name, psw);
                    MobclickAgent.onEvent(MainActivity.this, UmengString.GEUSTSESSION);
                }
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

    protected void initView() {
        // 好多。。。
        mImageView = (ImageView) findViewById(R.id.userIconImageView);
        mUserIdTextView = (TextView) findViewById(R.id.infoUserIdTextView);
        mUserTypeTextView = (TextView) findViewById(R.id.infoUserTypeTextView);
        mUserNameTextView = (TextView) findViewById(R.id.infoUserNameTextView);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + "/" + mDataHelper.SAVEFILE));
        mUserNameTextView.setText(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.USERNAME));
        mUserIdTextView.setText(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.USERID));
        mUserTypeTextView.setText(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.USERTYPE));
        mWebView = (WebView) findViewById(R.id.myWebView);
        mProgressWheel = (ProgressWheel) findViewById(R.id.progressWheel);

        // 。。。
        mHomeLinearLayout = (LinearLayout) findViewById(R.id.homeLayout);
        mScoreLinearLayout = (LinearLayout) findViewById(R.id.scoreLayout);
        mTableLinearLayout = (LinearLayout) findViewById(R.id.tableLayout);
        mCourseLinearLayout = (LinearLayout) findViewById(R.id.courseLayout);
        mSignupLinearLayout = (LinearLayout) findViewById(R.id.signupLayout);
        mAppraiseLinearLayout = (LinearLayout) findViewById(R.id.appraiseLayout);
        mCalendarLinearLayout = (LinearLayout) findViewById(R.id.calendarLayout);
        mGuestLinearLayout = (LinearLayout) findViewById(R.id.guestLayout);
        mSettingsLinearLayout = (LinearLayout) findViewById(R.id.settingsLayout);
        mHelpLinearLayout = (LinearLayout) findViewById(R.id.helpLayout);
        mItemScrollView = (ScrollView) findViewById(R.id.drawerItemLayout);
        mExamLinearLayout = (LinearLayout) findViewById(R.id.examLayout);
        mItemScrollView.setVerticalScrollBarEnabled(false);

        // 设置 Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("教务助手");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        // 抽屉效果
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.one, R.string.two) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mItemScrollView.smoothScrollTo(0, 0);
                invalidateOptionsMenu();
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    @Override
    protected void goNextActivity(boolean isComplete) {

    }

    private void layoutClick() {
        mHomeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.URL).equals(mDataHelper.URL)) {
                    mWebView.loadUrl(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.URL));
                    MobclickAgent.onEvent(MainActivity.this, UmengString.VISITHOME);
                } else {
                    Toast.makeText(getApplicationContext(), "数据错误，请重新启动应用", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawer(mItemScrollView);
            }
        });
        mScoreLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("http://jwcnew.nefu.edu.cn/dblydx_jsxsd/kscj/cjcx_query");
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITSCORE);
            }
        });
        mTableLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xskb/xskb_list.do");
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITTABLE);
            }
        });
        mExamLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xsks/xsksap_query?Ves632DSdyV=NEW_XSD_KSBM");
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITEXAM);
            }
        });
        mCourseLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/AccessToXk");
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITCOURSE);
            }
        });
        mSignupLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xsdjks/xsdjks_list");
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITSIANUP);
            }
        });
        mAppraiseLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xspj/xspj_find.do");
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITAPPRAISE);
            }
        });
        mCalendarLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("http://jwcnew.nefu.edu.cn/dblydx_jsxsd/jxzl/jxzl_query?Ves632DSdyV=NEW_XSD_WDZM");
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITCALENDAR);
            }
        });
        mGuestLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawDialog.getInputDialog("切换用户", mDrawDialog.getView(R.layout.changeuser_layout), changeListener());
                mDrawerLayout.closeDrawer(mItemScrollView);
            }
        });
        mSettingsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCookies();

                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.VISITSETTINGS);
            }
        });
        mHelpLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawDialog.getHelpDialog();
                mDrawerLayout.closeDrawer(mItemScrollView);
                MobclickAgent.onEvent(MainActivity.this, UmengString.SHOWHELP);
            }
        });
    }

    // mWebView初始化
    private void setmWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
    }

    private void showUpdateDialog() {
        int count = Integer.parseInt(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT));
        if (count > 0 && count < 4) {
            mDrawDialog.getUpdateDialog(mDataHelper.getUpdataInfo(), mDrawDialog.downloadListener(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.URL)));
        }
        updateCount();
    }

    // 统计更新提示框的次数
    private void updateCount() {
        if (!mDataHelper.VERSIONCODE.equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.VERSIONCODE)) &&
                !getVersionInfo(1).equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.VERSIONCODE)) &&
                !mDataHelper.COUNT.equals(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT))) {
            mDataHelper.setSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT,
                    String.valueOf(Integer.parseInt(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT)) + 1));
        } else {
            mDataHelper.setSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT, String.valueOf(0));
        }
    }

    // type 为 1 时获取 versionCode ,其它时输出 VersionName
    private String getVersionInfo(int type) {
        String reslut = null;
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            reslut = type == 1 ? String.valueOf(pi.versionCode) : pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            reslut = "ERROR";
        }
        return reslut;
    }

}
