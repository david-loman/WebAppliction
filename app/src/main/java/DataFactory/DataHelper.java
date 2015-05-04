package DataFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 2014/10/26.
 */
public class DataHelper {
    //网络请求数据
    private final String ICONURL = "http://bkjwgl.nefu.edu.cn/dblydx//uploadfile/studentphoto/pic/";
    private final String POSTURL = "http://jwc.nefu.edu.cn/JwdlAction.do";
    private final String NEWJWCURL = "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk";
    private final String MYWEIBO = "http://weibo.com/linxiangpeng1992";
    private final String MYZHIHU = "http://www.zhihu.com/people/david-lin-92";
    private final String MYZHUYE = "http://davidloman.net";
    private final String UPDATASTATUSURL = "http://jwcglxt.qiniudn.com/updataStatus";
    private final String APPLICATIONINFOURL = "http://jwcglxt.qiniudn.com/applictionInfo";
    private final String DATAINFOURL = "http://jwcglxt.qiniudn.com/dataInfo";
    //SharedPrefence名称
    public final String APPACCOUNT = "app_account";
    public final String APPUPDATA = "app_updata";
    public final String APPINFO = "app_info";
    public final String APPWEBSITE = "app_website";
    //SharedPrefence属性
    public final String VERSION = "version";
    public final String COUNT = "count";
    public final String ONE = "one";
    public final String TWO = "two";
    public final String THREE = "three";
    public final String URL = "url";
    public final String USERID = "userid";
    public final String USERTYPE = "usertype";
    public final String USERNAME = "username";
    public final String PASSWORD = "password";
    public final String UPDATATIME = "updatetime";
    public final String DEFAULTWEBSITE = "defaultwebsite";
    public final String MYWEBSITE = "mywebsite";
    //私有属性
    private Context context;
    private SharedPreferences sharedPreferences = null;

    public DataHelper(Context context) {
        this.context = context;
    }

    public void setSharedPreferencesValue(String sharedPreferencesName, String sharedPreferencesKey, String sharedPreferencesValue) {
        //获得编辑状态
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPreferencesKey, sharedPreferencesValue);
        editor.commit();
        editor = null;
        sharedPreferences = null;
    }

    public void setSharedPreferencesValues(String sharedPreferencesName, Map<String, String> data) {
        //获得编辑状态
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //编辑
        if (sharedPreferencesName.equals(APPUPDATA)) {
            editor.putString(VERSION, data.get(VERSION));
            editor.putString(URL, data.get(URL));
            editor.putString(ONE, data.get(ONE));
            editor.putString(TWO, data.get(TWO));
            editor.putString(THREE, data.get(THREE));
        } else if (sharedPreferencesName.equals(APPACCOUNT)) {
            editor.putString(URL, data.get(URL));
            editor.putString(USERID, data.get(USERID));
            editor.putString(USERTYPE, data.get(USERTYPE));
            editor.putString(USERNAME, data.get(USERNAME));
            editor.putString(PASSWORD, data.get(PASSWORD));
        } else if (sharedPreferencesName.equals(APPWEBSITE)) {
            editor.putString(DEFAULTWEBSITE, data.get(DEFAULTWEBSITE));
            editor.putString(MYWEBSITE, data.get(MYWEBSITE));
        } else {
            editor.clear();
        }
        editor.commit();
        editor = null;
        sharedPreferences = null;
    }

    public String getSharedPreferencesValue(String sharedPreferencesName, String sharedPreferencesKey) {
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(sharedPreferencesKey, sharedPreferencesKey);
        if (value.equals(COUNT)) {
            value = String.valueOf(0);
        }
        if (value.equals(MYWEBSITE)) {
            value = "{ \"len\" : 0 , \"mywebsite\" :[]  }";
        }
        sharedPreferences = null;
        return value;
    }

    public Map<String, String> getSharedPreferencesValues(String sharedPreferencesName) {
        Map<String, String> data = new HashMap<String, String>();
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        if (sharedPreferencesName.equals(APPUPDATA)) {
            data.put(VERSION, sharedPreferences.getString(VERSION, VERSION));
            data.put(URL, sharedPreferences.getString(URL, URL));
            data.put(ONE, sharedPreferences.getString(ONE, ONE));
            data.put(TWO, sharedPreferences.getString(TWO, TWO));
            data.put(THREE, sharedPreferences.getString(THREE, THREE));
            data.put(COUNT, sharedPreferences.getString(COUNT, COUNT));
        } else if (sharedPreferencesName.equals(APPACCOUNT)) {
            data.put(USERID, sharedPreferences.getString(USERID, USERID));
            data.put(USERTYPE, sharedPreferences.getString(USERTYPE, USERTYPE));
            data.put(URL, sharedPreferences.getString(URL, URL));
            data.put(USERNAME, sharedPreferences.getString(USERNAME, USERNAME));
            data.put(PASSWORD, sharedPreferences.getString(PASSWORD, PASSWORD));
        } else if (sharedPreferencesName.equals(APPWEBSITE)) {
            data.put(DEFAULTWEBSITE, sharedPreferences.getString(DEFAULTWEBSITE, DEFAULTWEBSITE));
            data.put(MYWEBSITE, sharedPreferences.getString(MYWEBSITE, MYWEBSITE));
        } else {
            data = null;
        }
        sharedPreferences = null;
        return data;
    }

    public void deleteSharedPreferences(String sharedPreferencesName) {
        sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        editor = null;
        sharedPreferences = null;
    }

    public String getNEWJWCURL() {
        return NEWJWCURL;
    }

    public String getMYWEIBO() {
        return MYWEIBO;
    }

    public String getMYZHIHU() {
        return MYZHIHU;
    }

    public String getMYZHUYE() {
        return MYZHUYE;
    }

    public String getUPDATASTATUSURL() {
        return UPDATASTATUSURL;
    }

    public String getAPPLICATIONINFOURL() {
        return APPLICATIONINFOURL;
    }

    public String getDATAINFOURL() {
        return DATAINFOURL;
    }

    public String getUpdataInfo() {

        String information = getSharedPreferencesValue(APPUPDATA, VERSION) + "\n"
                + "1. " + getSharedPreferencesValue(APPUPDATA, ONE) + "\n"
                + "2. " + getSharedPreferencesValue(APPUPDATA, TWO) + "\n"
                + "3. " + getSharedPreferencesValue(APPUPDATA, THREE) + "\n\n"
                + "是否更新？";
        return information;
    }
}
