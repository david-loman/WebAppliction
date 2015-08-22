package BaseView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by David on 15-8-22.
 * <li> 基础类，提供基础的网络可访问检查，对话框的显示。
 * <li> 子类在继承时要完善 initView 方法
 * <li>
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected void showNetErrorDialog (String title,String msg){
        new AlertDialog.Builder(getApplicationContext()).setTitle(title).setMessage(msg)
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
    protected void showMessageDialog (String title,String msg,String posString){
        new AlertDialog.Builder(getApplicationContext()).setTitle(title).setMessage(msg)
                .setPositiveButton(posString,null).show();
    }

    // 用户账户对话框
    protected void showInputDialog (){

    }

    // 开发者对话框
    protected void showDeveloperDialog(){

    }

    protected abstract void initView();
    protected abstract void goNextActivity(boolean isComplete);
}
