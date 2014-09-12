package com.example.david.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 2014/8/28.
 */
public class SettingsActivity extends Activity {

    private Button commitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //取消EditText默认的焦点
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        commitButton=(Button)findViewById(R.id.sureButton);
        Toast.makeText(SettingsActivity.this,"本页面暂时无法使用！若要修改默认账户，可以在\n“管理”——>“应用管理”中选择本应用\n“清除数据”",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingsActivity.this,MyActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
