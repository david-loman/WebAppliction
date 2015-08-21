package com.fromgeoto.nefujwc.webappliction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataFactory.DataHelper;
import DataFactory.JsonHelper;
import DrawItem.DrawDialog;

/**
 * Created by David on 2014/8/28.
 */
public class MenuActivity extends ActionBarActivity {

    private Button diyButton, devButton, clearButton, updateButton;
    private TextView studentNameTextView, studentTypeTextView;

    private DataHelper dataHelper = new DataHelper(this);
    private DrawDialog drawDialog = new DrawDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.settings);

        diyButton = (Button) findViewById(R.id.diyButton);
        updateButton = (Button) findViewById(R.id.updataButton);
        devButton = (Button) findViewById(R.id.developerButton);
        clearButton = (Button) findViewById(R.id.clearButton);
//        logoutButton = (Button) findViewById(R.id.logoutButton);
        studentNameTextView = (TextView) findViewById(R.id.student_name_value);
        studentTypeTextView = (TextView) findViewById(R.id.student_type_value);
        showInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        diyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DIY();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updata();
            }
        });

        devButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawDialog.getDeeveloperDialog(dataHelper.getMYWEIBO(), dataHelper.getMYZHIHU(), dataHelper.getMYZHUYE());
            }
        });

        devButton.setOnLongClickListener(drawDialog.thansListener());

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataHelper.deleteSharedPreferences(dataHelper.APPWEBSITE);
                Toast.makeText(MenuActivity.this, "网址已清除", Toast.LENGTH_SHORT).show();
            }
        });

//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dataHelper.deleteSharedPreferences(dataHelper.APPACCOUNT);
//                Intent intent = new Intent(MenuActivity.this,StartActivity.class);
//                startActivity(intent);
//                File file = new File(getFilesDir().getAbsolutePath()+"/"+dataHelper.SAVEFILE);
//                if (file.exists()){
//                    file.delete();
//                }
//                System.exit(0);
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void updata() {
        if (Integer.parseInt(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT)) != 0) {
            drawDialog.getUpdateDialog(dataHelper.getUpdataInfo(), drawDialog.downloadListener(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.URL)));
        } else {
            drawDialog.getErrorDialog("更新信息", "当前已是最高版本", null);
        }

    }

    private void showInfo() {
        studentTypeTextView.setText(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.USERTYPE));
        studentNameTextView.setText(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.USERNAME));
    }

    private void DIY() {
        //改名
        drawDialog.getTextView1(R.layout.changeuser_layout).setText("网站名称");
        drawDialog.getTextView2().setText("网站地址");
        //换面
        drawDialog.getEditText1().setHint("请输入网站名称");
        drawDialog.getEditText1().setInputType(InputType.TYPE_CLASS_TEXT);
        drawDialog.getEditText2().setHint("请输入网站地址");
        drawDialog.getEditText2().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        //显示
//        drawDialog.getInputDialog("自定义添加", drawDialog.getView(), addWebsiteListener());
    }

    //添加网站
//    private DialogInterface.OnClickListener addWebsiteListener() {
//        return new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                JsonHelper jsonHelper = new JsonHelper();
//                Map<String, String> map = new HashMap<String, String>();
//                List<Map<String, String>> list = jsonHelper.parseWebsiteJson(dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.MYWEBSITE), dataHelper.MYWEBSITE);
//                if (drawDialog.getEditText1().getText().toString().length() <= 0 || drawDialog.getEditText2().getText().toString().length() <= 0) {
//                    Toast.makeText(MenuActivity.this, "数据无效", Toast.LENGTH_LONG).show();
//                } else {
//                    map.put(jsonHelper.NAME, drawDialog.getEditText1().getText().toString());
//                    map.put(jsonHelper.URL, drawDialog.getEditText2().getText().toString());
//                    map.put(dataHelper.USERNAME, null);
//                    map.put(dataHelper.PASSWORD, null);
//                    list.add(map);
//                    dataHelper.setSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.MYWEBSITE, jsonHelper.convertWebsiteJson(list, dataHelper.MYWEBSITE));
//                }
//            }
//        };
//    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
