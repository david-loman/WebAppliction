package NetWork;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by David on 2014/10/31.
 */
public class QucikConnection {

    private Context context;

    public QucikConnection (Context context){
        this.context=context;
    }

    public String getResultString(String url) {
        StringBuffer stringBuffer = new StringBuffer();
        try {

            java.net.URL Url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();

                byte[] buff = new byte[1024];
                int len = 0;

                while ((len = inputStream.read(buff)) != -1) {
                    String s = new String(buff);
                    stringBuffer.append(s);
                }

                inputStream.close();
                url = null;
            } else {
                Log.e("Qucik_ResultString", "Error on the net");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

    public void DownloadApplication (String url){

    }

    public boolean checkNetwork(){
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        boolean mobile = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
        return (wifi || mobile);
    }
}
