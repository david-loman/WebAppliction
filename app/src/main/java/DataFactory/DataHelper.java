package DataFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 2014/10/26.
 */
public class DataHelper {
    //网络请求数据
    private final String NEWJWCURL = "http://jwcnew.nefu.edu.cn/dblydx_jsxsd/xk/LoginToXk";
    private final String OLSJWCURL = "http://jwcweb.nefu.edu.cn/";
    private final String MYWEIBO = "";
    private final String MYZHIHU ="";
    private final String MYZHUYE ="";
    private final String UPDATAINFOURL="";
    private final String APPLICATIONINFOURL="";
    private final String DATAINFOURL ="";
    //普通数据
    public final String OLDSYSTEM = "旧教务系统";
    public final String NEWSYSTEM = "新教务系统";
    //SharedPrefence名称
    public final String APPUPDATA = "app_updata";
    public final String APPLOGIN = "app_login";
    public final String APPWEBSITE ="app_website";
    //SharedPrefence属性
    public final String VERSION = "version";
    public final String COUNT = "count";
    public final String ONE ="one";
    public final String TWO ="two";
    public final String THREE ="three";
    public final String SYSTEM = "system";
    public final String URL = "url";
    public final String FUNCTION="function";
    public final String USERNAME = "username";
    public final String PASSWORD = "password";
    public final String DEFAULTWEBSITE="defaultwebsite";
    public final String MYWEBSITE="mywebsite";
    //私有属性
    private Context context;
    private SharedPreferences sharedPreferences=null;

    public DataHelper (Context context){
        this.context=context;
    }

    public void setSharedPreferencesValue (String sharedPreferencesName,String sharedPreferencesKey,String sharedPreferencesValue) {
        //获得编辑状态
        sharedPreferences=context.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(sharedPreferencesKey,sharedPreferencesValue);
        editor.commit();
        editor=null;
        sharedPreferences=null;
    }

    public void setSharedPreferencesValues (String sharedPreferencesName,HashMap<String,String> data){
        //获得编辑状态
        sharedPreferences=context.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        //编辑
        if (sharedPreferencesName.equals(APPUPDATA)){
            editor.putString(VERSION,data.get(VERSION));
            editor.putString(URL,data.get(URL));
            editor.putString(ONE,data.get(ONE));
            editor.putString(TWO,data.get(TWO));
            editor.putString(THREE,data.get(THREE));
        }else if (sharedPreferencesName.equals(APPLOGIN)){
            editor.putString(SYSTEM,data.get(SYSTEM));
            editor.putString(URL,data.get(URL));
            editor.putString(USERNAME,data.get(USERNAME));
            editor.putString(PASSWORD,data.get(PASSWORD));
        }else if (sharedPreferencesName.equals(APPWEBSITE)){
            editor.putString(DEFAULTWEBSITE,data.get(DEFAULTWEBSITE));
            editor.putString(MYWEBSITE,data.get(MYWEBSITE));
        }else{
            editor.clear();
        }
        editor.commit();
        editor=null;
        sharedPreferences=null;
    }

    public String getSharedPreferencesValue (String sharedPreferencesName,String sharedPreferencesKey){
        sharedPreferences=context.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
        String value=sharedPreferences.getString(sharedPreferencesKey,sharedPreferencesKey);
        sharedPreferences=null;
        return value;
    }

    public Map<String,String> getSharedPreferencesValues (String sharedPreferencesName){
        Map<String,String> data=new HashMap<String, String>();
        sharedPreferences=context.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
        if (sharedPreferencesName.equals(APPUPDATA)){
            data.put(VERSION,sharedPreferences.getString(VERSION,VERSION));
            data.put(URL,sharedPreferences.getString(URL,URL));
            data.put(ONE,sharedPreferences.getString(ONE,ONE));
            data.put(TWO,sharedPreferences.getString(TWO,TWO));
            data.put(THREE,sharedPreferences.getString(THREE,THREE));
            data.put(COUNT,sharedPreferences.getString(COUNT,COUNT));
        }else if (sharedPreferencesName.equals(APPLOGIN)){
            data.put(SYSTEM,sharedPreferences.getString(SYSTEM,SYSTEM));
            data.put(URL,sharedPreferences.getString(URL,URL));
            data.put(USERNAME,sharedPreferences.getString(USERNAME,USERNAME));
            data.put(PASSWORD,sharedPreferences.getString(PASSWORD,PASSWORD));
        }else if (sharedPreferencesName.equals(APPWEBSITE)){
            data.put(DEFAULTWEBSITE,sharedPreferences.getString(DEFAULTWEBSITE,DEFAULTWEBSITE));
            data.put(MYWEBSITE,sharedPreferences.getString(MYWEBSITE,MYWEBSITE));
        }else{
            data=null;
        }
        sharedPreferences=null;
        return data;
    }

    public String getNEWJWCURL (){
        return NEWJWCURL;
    }

    public String getOLSJWCURL() {
        return OLSJWCURL;
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

    public String getUPDATAINFOURL() {
        return UPDATAINFOURL;
    }

    public String getAPPLICATIONINFOURL() {
        return APPLICATIONINFOURL;
    }

    public String getDATAINFOURL() {
        return DATAINFOURL;
    }
}
