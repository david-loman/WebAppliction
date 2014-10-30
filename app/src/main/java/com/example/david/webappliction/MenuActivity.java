package com.example.david.webappliction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by David on 2014/8/28.
 */
public class MenuActivity extends Activity {

    private Button commitButton;
    private Button systemButton, userButton;
    private Button diyButton, devButton, helpButton, shareBuuton;
    private Button updataButton;
    private TextView userTextView, systemTextView;

    private int requestCode = 0;
    private String currenSystem = null;
    private String selectSystem = null;
    private String psword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

//        取消EditText默认的焦点
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        commitButton = (Button) findViewById(R.id.sureButton);
        systemButton = (Button) findViewById(R.id.select_system);
        userButton = (Button) findViewById(R.id.user_count);
        diyButton = (Button) findViewById(R.id.diyButton);
        updataButton = (Button) findViewById(R.id.updataButton);
        devButton = (Button) findViewById(R.id.developerButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        shareBuuton = (Button) findViewById(R.id.shareButton);
        userTextView = (TextView) findViewById(R.id.selecd_User);
        systemTextView = (TextView) findViewById(R.id.selecd_System);
        Intent intent = getIntent();
        init(intent);
        showInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        systemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectSystem();
            }
        });

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
                if (checkUpadata()) {
                    updata();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this).setTitle("更新提醒").setMessage("已经是最高版本").setPositiveButton("确定", null).show();
                }
            }
        });

        devButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                developer();
            }
        });

        devButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this)
                        .setTitle("致敬").setMessage("感谢的图书馆A区四楼的那位女生，正是你的存在，让我能够将这个项目坚持下来，谢谢你")
                        .setPositiveButton("Good Luck", null)
                        .show();

                return true;
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help();
            }
        });

        shareBuuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用系统分享功能
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "便捷登录教务处，你也可以做到的，应用下载：http://davidloman.net/about/");
                startActivity(Intent.createChooser(sendIntent, "分享到"));
            }
        });

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userTextView.getText().toString().equals("null")) {
                    AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this).setTitle("错误提示").setMessage("您当前的用户为空，请完善信息！")
                            .setPositiveButton("确定", null)
                            .show();
                } else {
                    go_back();
                }
            }
        });
    }

    private void setSelectSystem() {

        AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this).setTitle("系统修改").setMessage("您默认的系统为:\t" + currenSystem + "\n点击修改按钮将修改系统。")
                .setNegativeButton("取消", null)
                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (selectSystem.equals(StartActivity.OLDSYSTEM)) {
                            selectSystem = StartActivity.NEWSYSTEM;
                            requestCode = 1;
                        } else {
                            selectSystem = StartActivity.OLDSYSTEM;
                            userTextView.setText("");
                            requestCode = 2;
                        }
                        showInfo();
                    }
                }).show();


    }

    private void changeUser() {
        final SharedPreferences sp = getSharedPreferences(StartActivity.INFOAPP, MODE_PRIVATE);
        if (selectSystem.equals(StartActivity.NEWSYSTEM)) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.changeuser_layout, null);
            final EditText user = (EditText) view.findViewById(R.id.usernameEditText);
            final EditText psw = (EditText) view.findViewById(R.id.passwordEditText);
            if (currenSystem.equals(StartActivity.NEWSYSTEM)) {
                String nameS = sp.getString(StartActivity.USERNAME, StartActivity.USERNAME);
                String pswS = sp.getString(StartActivity.PASSWORD, StartActivity.PASSWORD);
                if (nameS.equals(StartActivity.USERNAME)) {
                    nameS = "null";
                }
                user.setText(nameS);
                psw.setText(pswS);
            }
            AlertDialog alertDialog = new AlertDialog.Builder(MenuActivity.this).setTitle("修改用户信息")
                    .setView(view)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String nameValue = user.getText().toString();
                            psword = psw.getText().toString();
                            if (nameValue.length() <= 1 || nameValue == null) {
                                nameValue = "null";
                            }

                            userTextView.setText(nameValue);
                        }
                    })
                    .show();

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MenuActivity.this).setTitle("错误信息")
                    .setMessage("当前系统不支持该功能")
                    .setPositiveButton("确定", null).show();
        }
    }

    private boolean checkUpadata() {
        boolean canUpdata = false;
        SharedPreferences sp = getSharedPreferences(StartActivity.UPDATAAPP, MODE_PRIVATE);
        canUpdata = sp.getBoolean(StartActivity.CANUPDATA, false);
        return canUpdata;
    }

    private void updata() {

        SharedPreferences sp = getSharedPreferences(StartActivity.UPDATAAPP, MODE_PRIVATE);
        final String path = sp.getString(StartActivity.URL, StartActivity.URL);
        String infomation = sp.getString(StartActivity.VERSION, "0.0.0") + "\n"
                + "1. " + sp.getString("one", "Null") + "\n"
                + "2. " + sp.getString("two", "Null") + "\n"
                + "3. " + sp.getString("three", "Null") + "\n\n"
                + "是否更新？";

        AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this).setTitle("更新提醒").setMessage(infomation).setNegativeButton("等等", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse(path);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).show();
    }

    private void go_back() {
        SharedPreferences sp = getSharedPreferences(StartActivity.INFOAPP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (!currenSystem.equals(selectSystem)) {
            editor.putString(StartActivity.SYSTEM, selectSystem);
            if (selectSystem.equals(StartActivity.NEWSYSTEM)) {
                editor.putString(StartActivity.URL, StartActivity.NEWJWCURL);
            } else {
                editor.putString(StartActivity.URL, StartActivity.OLSJWCURL);
            }
        }
        if (selectSystem.equals(StartActivity.NEWSYSTEM)) {
            if (!sp.getString(StartActivity.USERNAME, StartActivity.USERNAME).equals(userTextView.getText().toString())) {
                editor.putString(StartActivity.USERNAME, userTextView.getText().toString());
                editor.putString(StartActivity.PASSWORD, psword);
            }
        }
        editor.commit();
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        setResult(requestCode, intent);
        finish();
    }

    private void showInfo() {

        if (selectSystem.equals(StartActivity.NEWSYSTEM)) {
            SharedPreferences sp = getSharedPreferences(StartActivity.INFOAPP, MODE_PRIVATE);
            String name = sp.getString(StartActivity.USERNAME, StartActivity.USERNAME);
            if (name.equals(StartActivity.USERNAME)) {
                name = "null";
            }
            systemTextView.setText(selectSystem);
            userTextView.setText(name);
        } else {
            systemTextView.setText(selectSystem);
            userTextView.setText("这个系统没有用户");
        }

    }

    private void init(Intent intent) {
        currenSystem = intent.getStringExtra(StartActivity.SYSTEM);
        selectSystem = currenSystem;
    }

    private void help() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.help_layout, null);
        AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this)
                .setTitle("帮助信息").setView(view)
                .setNegativeButton("更多", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("https://github.com/david-loman/WebAppliction/blob/master/%E5%B8%AE%E5%8A%A9%E6%96%87%E6%A1%A3.md");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).setPositiveButton("确定", null).show();
    }

    private void developer() {
        Button weiboButton, zhihuButton, zhuyeButton;
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dev_layout, null);
        weiboButton = (Button) view.findViewById(R.id.weibo);
        zhihuButton = (Button) view.findViewById(R.id.zhihu);
        zhuyeButton = (Button) view.findViewById(R.id.zhuye);

        AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this)
                .setTitle("开发者信息").setView(view).setPositiveButton("确定", null)
                .show();

        weiboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://weibo.com/linxiangpeng1992");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        zhihuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.zhihu.com/people/david-lin-92");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        zhuyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://davidloman.net");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void DIY() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.changeuser_layout, null);
        TextView name = (TextView) view.findViewById(R.id.usernameTextView);
        final TextView url = (TextView) view.findViewById(R.id.passwordTextView);
        final EditText nameValue = (EditText) view.findViewById(R.id.usernameEditText);
        final EditText urlValue = (EditText) view.findViewById(R.id.passwordEditText);
        //改名
        name.setText("网站名称");
        url.setText("网站地址");
        //换面
        nameValue.setHint("请输入网站名称");
        nameValue.setInputType(InputType.TYPE_CLASS_TEXT);
        urlValue.setHint("请输入网站地址");
        urlValue.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        //显示
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("自定义添加")
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        encodeJSONObject(nameValue.getText().toString(), urlValue.getText().toString(), null, null);
                    }
                }).show();

    }

    private void encodeJSONObject(String arg0, String arg1, String arg2, String arg3) {
        StringBuilder stringBuilder = new StringBuilder();
        SharedPreferences sp = getSharedPreferences(StartActivity.APPWEBSITE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        List<Map<String, String>> list = getWebsite();
        Map<String, String> map = new HashMap<String, String>();
        map.put(StartActivity.NAME, arg0);
        map.put(StartActivity.URL, arg1);
        map.put(StartActivity.USERNAME, "null");
        map.put(StartActivity.PASSWORD, "null");
        list.add(map);

        stringBuilder.append("{ \"" + StartActivity.LENTH + "\":" + String.valueOf(list.size()) + ",");
        stringBuilder.append("\"" + StartActivity.WEBSITE + "\"" + ": [ ");
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append("{");
            stringBuilder.append("\"" + StartActivity.NAME + "\":\"" + list.get(i).get(StartActivity.NAME).toString() + "\",");
            stringBuilder.append("\"" + StartActivity.URL + "\":\"" + list.get(i).get(StartActivity.URL).toString() + "\",");
            stringBuilder.append("\"" + StartActivity.USERNAME + "\":\"null\" ,");
            stringBuilder.append("\"" + StartActivity.PASSWORD + "\":\"null\"");
            stringBuilder.append("}");
            if (i != (list.size() - 1)) {
                stringBuilder.append(" , ");
            }
        }
        stringBuilder.append("] }");
        editor.putString(StartActivity.INFOMATION, stringBuilder.toString());
        editor.commit();
    }

    private List<Map<String, String>> getWebsite() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        SharedPreferences sp = getSharedPreferences(StartActivity.APPWEBSITE, MODE_PRIVATE);
        String json = sp.getString(StartActivity.INFOMATION, StartActivity.INFOMATION);
        if (json.equals(StartActivity.INFOMATION)) {
            Toast.makeText(this, "数据加载错误", Toast.LENGTH_SHORT).show();
        } else {

            try {
                JSONObject jsonObject = new JSONObject(json);
                int len = jsonObject.getInt(StartActivity.LENTH);
                JSONArray jsonArray = jsonObject.getJSONArray(StartActivity.WEBSITE);
                for (int i = 0; i < len; i++) {

                    JSONObject childJsonObject = jsonArray.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(StartActivity.NAME, childJsonObject.getString(StartActivity.NAME));
                    map.put(StartActivity.URL, childJsonObject.getString(StartActivity.URL));
                    map.put(StartActivity.USERNAME, childJsonObject.getString(StartActivity.USERNAME));
                    map.put(StartActivity.PASSWORD, childJsonObject.getString(StartActivity.PASSWORD));

                    list.add(map);
                    Log.e("Main", "name: " + map.get(StartActivity.NAME).toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return list;
    }
}
