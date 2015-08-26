package com.fromgeoto.nefujwc.webappliction;

import android.content.Intent;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import BaseView.BaseViewActivity;
import DataFactory.DataHelper;
import DataFactory.UmengString;
import DrawItem.DrawDialog;

/**
 * Created by David on 2014/8/28.
 */
public class MenuActivity extends BaseViewActivity {

    private Button devButton, clearButton, updateButton;
    private TextView studentNameTextView, studentTypeTextView;

    private DataHelper dataHelper = new DataHelper(this);
    private DrawDialog drawDialog = new DrawDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initView();
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
                dataHelper.deleteSharedPreferences(dataHelper.APPINFO);
                dataHelper.deleteSharedPreferences(dataHelper.APPACCOUNT);
                goNextActivity(false);
                Toast.makeText(MenuActivity.this, "账户信息删除完成，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void updata() {
        MobclickAgent.onEvent(MenuActivity.this, UmengString.CHECKUPDATA);
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

    @Override
    protected void initView() {
        updateButton = (Button) findViewById(R.id.updataButton);
        devButton = (Button) findViewById(R.id.developerButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        studentNameTextView = (TextView) findViewById(R.id.student_name_value);
        studentTypeTextView = (TextView) findViewById(R.id.student_type_value);
    }

    @Override
    protected void goNextActivity(boolean isComplete) {
        Intent intent = new Intent(MenuActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
