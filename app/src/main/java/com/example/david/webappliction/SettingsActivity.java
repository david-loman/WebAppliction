package com.example.david.webappliction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    private Button nefuButton;
    private final int resultCode =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        nefuButton =(Button)findViewById(R.id.nefu_com);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

        nefuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this,"功能尚未实现！^_^|||",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
//                intent.putExtra("URL","http://www.nefu.edu.cn/");
                setResult(resultCode,intent);
                finish();
            }
        });
    }
}
