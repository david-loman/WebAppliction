package com.fromgeoto.nefujwc.webappliction;

import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import BaseView.BaseViewActivity;
import DataFactory.UmengString;

/**
 * Created by David on 2014/8/28.
 */
public class SettingsActivity extends BaseViewActivity {

    private Button mDevButton, mClearButton, mUpdateButton, mSuggestionButton;
    private TextView mStudentNameTextView, mStudentTypeTextView;

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

        mSuggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent internt = new Intent(Intent.ACTION_SENDTO);
                internt.setData(Uri.parse("mailto:790955623@qq.com"));
                internt.putExtra(Intent.EXTRA_SUBJECT,"反馈");
                startActivity(internt);
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updata();
            }
        });

        mDevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.getDeeveloperDialog(mDataHelper.getMYWEIBO(), mDataHelper.getMYZHIHU(), mDataHelper.getMYZHUYE());
            }
        });

        mDevButton.setOnLongClickListener(mDrawerLayout.thansListener());

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataHelper.deleteSharedPreferences(mDataHelper.APPINFO);
                mDataHelper.deleteSharedPreferences(mDataHelper.APPACCOUNT);
                goNextActivity(false);
                Toast.makeText(SettingsActivity.this, "账户信息删除完成，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void updata() {
        MobclickAgent.onEvent(SettingsActivity.this, UmengString.CHECKUPDATA);
        if (Integer.parseInt(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.COUNT)) != 0) {
            mDrawerLayout.getUpdateDialog(mDataHelper.getUpdataInfo(), mDrawerLayout.downloadListener(mDataHelper.getSharedPreferencesValue(mDataHelper.APPUPDATA, mDataHelper.URL)));
        } else {
            mDrawerLayout.getErrorDialog("更新信息", "当前已是最高版本", null);
        }
    }

    private void showInfo() {
        mStudentTypeTextView.setText(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.USERTYPE));
        mStudentNameTextView.setText(mDataHelper.getSharedPreferencesValue(mDataHelper.APPACCOUNT, mDataHelper.USERNAME));
    }

    @Override
    protected void initView() {
        mUpdateButton = (Button) findViewById(R.id.updataButton);
        mDevButton = (Button) findViewById(R.id.developerButton);
        mClearButton = (Button) findViewById(R.id.clearButton);
        mSuggestionButton=(Button)findViewById(R.id.suggestionButton);
        mStudentNameTextView = (TextView) findViewById(R.id.student_name_value);
        mStudentTypeTextView = (TextView) findViewById(R.id.student_type_value);
    }

    @Override
    protected void goNextActivity(boolean isComplete) {
        Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
