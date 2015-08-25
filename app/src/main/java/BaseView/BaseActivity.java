package BaseView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;

import DataFactory.DataHelper;
import DrawItem.DrawDialog;

/**
 * Created by David on 15-8-22.
 * <li> 基础类，提供基础的网络可访问检查，对话框的显示。
 * <li> 子类在继承时要完善 initView 方法
 * <li>
 */
public abstract class BaseActivity extends ActionBarActivity {

    protected DataHelper mDataHelper;
    protected DrawDialog mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataHelper = new DataHelper(getApplicationContext());
        mDrawerLayout = new DrawDialog(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // 检查网络是否可访问
    protected boolean checkNetwork() {
        ConnectivityManager conn = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        boolean mobile = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
        return (wifi || mobile);
    }

    // 网络不可访问提示对话框
    protected void showNetErrorDialog (Context context){
        new AlertDialog.Builder(context).setTitle("网络连接错误").setMessage("请重新设置网络环境")
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).show();
    }

    // 消息对话框
    protected void showMessageDialog (Context context,String title,String msg,String posString,DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setPositiveButton(posString, onClickListener).show();
    }

    protected abstract void initView();
    protected abstract void goNextActivity(boolean isComplete);
}
