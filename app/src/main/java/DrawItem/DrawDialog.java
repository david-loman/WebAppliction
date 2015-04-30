package DrawItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nefu.david.webappliction.R;

/**
 * Created by David on 2014/10/31.
 */
public class DrawDialog {

    private Context context;
    private String title = null;
    private final String NO = "取消";
    private View view;
    private EditText editText1, editText2;
    private TextView textView1, textView2;
    private ListView listview;
    public final String YES = "确定";
    public final String NetWorkError = "网络连接错误，请检查设置";

    public DrawDialog(Context context) {
        this.context = context;
    }

    public DrawDialog(Context context, String title) {
        this.context = context;
        this.title = title;
    }

    //响应数据输入
    public AlertDialog getInputDialog(String title, View view, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setTitle(title).setView(view).setNegativeButton(NO, null).setPositiveButton(YES, onClickListener).show();
    }

    //响应网络异常
    public AlertDialog getErrorDialog(String title, String msg, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(YES, onClickListener).show();
    }

    //响应选择改变
    public AlertDialog getChangeDialog(String title, String msg, String note, DialogInterface.OnClickListener onClickListener) {
        return new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setNegativeButton(NO, null).setPositiveButton(note, onClickListener).show();
    }

    //响应更新操作
    public AlertDialog getUpdateDialog(String msg, DialogInterface.OnClickListener listener) {
        return new AlertDialog.Builder(context).setTitle("更新信息").setMessage(msg).setNegativeButton(NO, null).setPositiveButton(YES, listener).show();
    }

    //响应帮助操作
    public AlertDialog getHelpDialog() {
        View view = getView(R.layout.help_layout);
        return new AlertDialog.Builder(context)
                .setTitle("帮助信息").setView(view)
                .setNegativeButton("更多", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("https://github.com/david-loman/WebAppliction/blob/master/%E5%B8%AE%E5%8A%A9%E6%96%87%E6%A1%A3.md");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        ((Activity) context).startActivity(intent);
                    }
                }).setPositiveButton(YES, null).show();
    }

    //响应列表操作
    public AlertDialog getListDialog(String title) {

        return new AlertDialog.Builder(context).setTitle(title).setView(view).setNegativeButton(NO, null).show();
    }

    //响应帮助者操作
    public AlertDialog getDeeveloperDialog(final String weibo, final String zhihu, final String zhuye){
        Button weiboButton, zhihuButton, zhuyeButton;
        weiboButton = (Button)getView(R.layout.dev_layout).findViewById(R.id.weibo);
        zhihuButton = (Button)getView().findViewById(R.id.zhihu);
        zhuyeButton = (Button)getView().findViewById(R.id.zhuye);

        weiboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visit(weibo);
            }
        });
        zhihuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visit(zhihu);
            }
        });
        zhuyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visit(zhuye);
            }
        });

        return new AlertDialog.Builder(context)
                .setTitle("开发者信息").setView(view).setPositiveButton("确定", null)
                .show();
    }

    //下载监听
    public DialogInterface.OnClickListener downloadListener(final String url) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                ((Activity) context).startActivity(intent);
            }
        };
    }

    //退出监听
    public DialogInterface.OnClickListener exitListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        };
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EditText getEditText1(int res) {
        return (EditText) getView(res).findViewById(R.id.usernameEditText);
    }

    public EditText getEditText1() {
        return (EditText) view.findViewById(R.id.usernameEditText);
    }

    public EditText getEditText2(int res) {
        return (EditText) getView(res).findViewById(R.id.passwordEditText);
    }

    public EditText getEditText2() {
        return (EditText) view.findViewById(R.id.passwordEditText);
    }

    public TextView getTextView1(int res) {
        return (TextView) getView(res).findViewById(R.id.usernameTextView);
    }

    public TextView getTextView1() {
        return (TextView) view.findViewById(R.id.usernameTextView);
    }

    public TextView getTextView2(int res) {
        return (TextView) getView(res).findViewById(R.id.passwordTextView);
    }

    public TextView getTextView2() {
        return (TextView) view.findViewById(R.id.passwordTextView);
    }

    public ListView getListview() {

        return (ListView) view.findViewById(R.id.website_list);
    }

    public ListView getListview(int res) {

        return (ListView) getView(res).findViewById(R.id.website_list);
    }

    public View getView(int res) {
        Activity thisAcitvity = (Activity) context;
        LayoutInflater layoutInflater = thisAcitvity.getLayoutInflater();
        this.view = layoutInflater.inflate(res, null);
        return view;
    }

    public View getView() {
        return view;
    }

    //祝好
    public View.OnLongClickListener thansListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("致敬").setMessage("感谢的图书馆A区四楼的那位女生，正是你的存在，让我能够将这个项目坚持下来，谢谢你")
                        .setPositiveButton("Good Luck", null)
                        .show();

                return true;
            }
        };
    }

    //了解我的资讯
    private void visit(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ((Activity)context).startActivity(intent);
    }
}
