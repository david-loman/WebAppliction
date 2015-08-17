package BaseView;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;

import DataFactory.DataHelper;
import DataFactory.JsonHelper;
import DrawItem.DrawDialog;

/**
 * Created by linxiangpeng on 15-8-17.
 * About : 基础类
 */

public class BaseActivity extends Activity {

    protected DataHelper mDataHelper = new DataHelper(getApplicationContext());
    protected DrawDialog mDrawerLayout = new DrawDialog(getApplicationContext());
    // 子类调用的时候应该指定对应的布局
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 友盟埋点
        MobclickAgent.onResume(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 友盟埋点
        MobclickAgent.onPause(getApplicationContext());
    }

    // 用于对布局进行包装
    protected void initActionBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
