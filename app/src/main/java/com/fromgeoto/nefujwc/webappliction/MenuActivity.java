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

    private Button commitButton;
    private Button userButton;
    private Button diyButton, devButton, helpButton, clearBuuton;
    private Button updataButton;
    private TextView userTextView, systemTextView;

    private int requestCode = 0;
    private String psword = null;
    private DataHelper dataHelper = new DataHelper(this);
    private DrawDialog drawDialog = new DrawDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.settings);

        commitButton = (Button) findViewById(R.id.sureButton);
        userButton = (Button) findViewById(R.id.user_count);
        diyButton = (Button) findViewById(R.id.diyButton);
        updataButton = (Button) findViewById(R.id.updataButton);
        devButton = (Button) findViewById(R.id.developerButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        clearBuuton = (Button) findViewById(R.id.clearButton);
        userTextView = (TextView) findViewById(R.id.selecd_User);
        systemTextView = (TextView) findViewById(R.id.selecd_System);
        showInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUser();
            }
        });

        diyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DIY();
            }
        });

        updataButton.setOnClickListener(new View.OnClickListener() {
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

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help();
            }
        });

        clearBuuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataHelper.deleteSharedPreferences(dataHelper.APPWEBSITE);
                Toast.makeText(MenuActivity.this, "网址已清除", Toast.LENGTH_SHORT).show();
            }
        });

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userTextView.getText().toString().equals("null")) {
                    drawDialog.getErrorDialog("错误提示", "您当前的用户为空，请完善信息！", null);
                } else {
                    go_back();
                }
            }
        });
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void changeUser() {
        String nameS = dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.USERID);
        String pswS = dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.PASSWORD);
        if (nameS.equals(dataHelper.USERID)) {
            nameS = "null";
        }
        drawDialog.getEditText1(R.layout.changeuser_layout).setText(nameS);
        drawDialog.getEditText2().setText(pswS);
        drawDialog.getInputDialog("修改用户", drawDialog.getView(), changeUerListener());

    }

    private void updata() {
        if (Integer.parseInt(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT)) != 0) {
            drawDialog.getUpdateDialog(dataHelper.getUpdataInfo(), drawDialog.downloadListener(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.URL)));
        } else {
            drawDialog.getErrorDialog("更新信息", "当前已是最高版本", null);
        }

    }

    private void go_back() {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        setResult(requestCode, intent);
        finish();
    }

    private void showInfo() {
            systemTextView.setText(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT,dataHelper.USERTYPE));
            userTextView.setText(dataHelper.getSharedPreferencesValue(dataHelper.APPACCOUNT, dataHelper.USERNAME));
    }

    private void help() {
        drawDialog.getHelpDialog();
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
        drawDialog.getInputDialog("自定义添加", drawDialog.getView(), addWebsiteListener());
    }
    //监听用户修改
    private DialogInterface.OnClickListener changeUerListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nameValue = drawDialog.getEditText1().getText().toString();
                psword = drawDialog.getEditText2().getText().toString();
                if (nameValue.length() <= 1 || nameValue == null) {
                    nameValue = "null";
                }
                userTextView.setText(nameValue);
            }
        };
    }

    //添加网站
    private DialogInterface.OnClickListener addWebsiteListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JsonHelper jsonHelper = new JsonHelper();
                Map<String, String> map = new HashMap<String, String>();
                List<Map<String, String>> list = jsonHelper.parseWebsiteJson(dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.MYWEBSITE), dataHelper.MYWEBSITE);
                if (drawDialog.getEditText1().getText().toString().length() <= 0 || drawDialog.getEditText2().getText().toString().length() <= 0) {
                    Toast.makeText(MenuActivity.this, "数据无效", Toast.LENGTH_LONG).show();
                } else {
                    map.put(jsonHelper.NAME, drawDialog.getEditText1().getText().toString());
                    map.put(jsonHelper.URL, drawDialog.getEditText2().getText().toString());
                    map.put(dataHelper.USERNAME, null);
                    map.put(dataHelper.PASSWORD, null);
                    list.add(map);
                    dataHelper.setSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.MYWEBSITE, jsonHelper.convertWebsiteJson(list, dataHelper.MYWEBSITE));
                }
            }
        };
    }

    private void initStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
