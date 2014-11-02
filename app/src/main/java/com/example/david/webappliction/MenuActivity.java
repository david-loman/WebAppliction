package com.example.david.webappliction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DataFactory.DataHelper;
import DataFactory.JsonHelper;

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
    private DataHelper dataHelper = new DataHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

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
                if (Integer.parseInt(dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.COUNT)) != 0) {
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
                        if (selectSystem.equals(dataHelper.OLDSYSTEM)) {
                            selectSystem = dataHelper.NEWSYSTEM;
                            requestCode = 1;
                        } else {
                            selectSystem = dataHelper.OLDSYSTEM;
                            userTextView.setText("");
                            requestCode = 2;
                        }
                        showInfo();
                    }
                }).show();


    }

    private void changeUser() {
        if (selectSystem.equals(dataHelper.NEWSYSTEM)) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.changeuser_layout, null);
            final EditText user = (EditText) view.findViewById(R.id.usernameEditText);
            final EditText psw = (EditText) view.findViewById(R.id.passwordEditText);
            if (currenSystem.equals(dataHelper.NEWSYSTEM)) {
                String nameS = dataHelper.getSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.USERNAME);
                String pswS = dataHelper.getSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.PASSWORD);
                if (nameS.equals(dataHelper.USERNAME)) {
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

    private void updata() {

        final String path = dataHelper.getSharedPreferencesValue(dataHelper.APPUPDATA, dataHelper.URL);

        AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this).setTitle("更新提醒").setMessage(dataHelper.getUpdataInfo()).setNegativeButton("等等", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        visit(path);
                    }
                }).show();
    }

    private void go_back() {
        if (!currenSystem.equals(selectSystem)) {
            dataHelper.setSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.SYSTEM, selectSystem);
            if (selectSystem.equals(dataHelper.NEWSYSTEM)) {
                dataHelper.setSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.URL, dataHelper.getNEWJWCURL());
            } else {
                dataHelper.setSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.URL, dataHelper.getOLSJWCURL());
            }
        }
        if (selectSystem.equals(dataHelper.NEWSYSTEM)) {
            if (!(dataHelper.getSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.USERNAME)).equals(userTextView.getText().toString())) {
                dataHelper.setSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.USERNAME, userTextView.getText().toString());
                dataHelper.setSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.PASSWORD, psword);
            }
        }

        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        setResult(requestCode, intent);
        finish();
    }

    private void showInfo() {

        if (selectSystem.equals(dataHelper.NEWSYSTEM)) {
            String name = dataHelper.getSharedPreferencesValue(dataHelper.APPLOGIN, dataHelper.USERNAME);
            if (name.equals(dataHelper.USERNAME)) {
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
        currenSystem = intent.getStringExtra(dataHelper.SYSTEM);
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
        final View view = inflater.inflate(R.layout.dev_layout, null);
        weiboButton = (Button) view.findViewById(R.id.weibo);
        zhihuButton = (Button) view.findViewById(R.id.zhihu);
        zhuyeButton = (Button) view.findViewById(R.id.zhuye);

        AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this)
                .setTitle("开发者信息").setView(view).setPositiveButton("确定", null)
                .show();

        weiboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visit(dataHelper.getMYWEIBO());
            }
        });


        zhihuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visit(dataHelper.getMYZHIHU());
            }
        });

        zhuyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visit(dataHelper.getMYZHUYE());
            }
        });
    }

    //自定义添加网址
    private void DIY() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.changeuser_layout, null);
        TextView name = (TextView) view.findViewById(R.id.usernameTextView);
        final JsonHelper jsonHelper = new JsonHelper();
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
                        Map<String, String> map = new HashMap<String, String>();
                        List<Map<String, String>> list = jsonHelper.parseWebsiteJson(dataHelper.getSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.MYWEBSITE), dataHelper.MYWEBSITE);
                        if (nameValue.getText().toString().length() <= 0 || url.getText().toString().length() <= 0) {
                            Toast.makeText(MenuActivity.this,"数据无效",Toast.LENGTH_LONG).show();
                        }else{
                            map.put(jsonHelper.NAME, nameValue.getText().toString());
                            map.put(jsonHelper.URL, urlValue.getText().toString());
                            map.put(dataHelper.USERNAME, null);
                            map.put(dataHelper.PASSWORD, null);
                            list.add(map);
                            dataHelper.setSharedPreferencesValue(dataHelper.APPWEBSITE, dataHelper.MYWEBSITE, jsonHelper.convertWebsiteJson(list, dataHelper.MYWEBSITE));
                        }
                    }
                }).show();

    }

    //了解我的资讯
    private void visit (String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
