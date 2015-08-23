package NetWork;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 2014/10/31.
 */
public class QucikConnection {

    private Context mContext;

    public QucikConnection(Context context) {
        this.mContext = context;
    }

    public static String getResultString(String url) {
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
                stringBuffer.append(responseCode);
//                Log.e("Qucik_ResultString", "Error on the net");
            }
        } catch (Exception e) {
            stringBuffer = new StringBuffer("ERROR");
//            Log.e("Get ERROR", "TIME_OUT");
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static Map<String, String> getResultMap(String user, String pasw, String urlString) {
        boolean status = false;
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            String tmp = "userAccount=" + user + "&userPassword=" + pasw;
            java.net.URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            // Post 数据
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "utf-8"));
            bufferedWriter.write(tmp);
            bufferedWriter.flush();
            bufferedWriter.close();
            tmp = null;
            // 数据处理
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                int i = 0;
                String tmpLine = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((tmpLine = bufferedReader.readLine()) != null) {
                    // 更新数据
                    if (tmpLine.contains(user) && (i == 0)) {
                        status = true;
                        resultMap.put("username", tmpLine.substring(tmpLine.indexOf("(") + 1, tmpLine.indexOf(")")));
                    }
                    // 数据类型
                    if (status && (i < 3)) {
                        if (i == 1) {
                            resultMap.put("usertype", tmpLine.substring(tmpLine.indexOf("=") + 1, tmpLine.indexOf("+")));
                        } else if (i == 2) {
                            resultMap.put("url", tmpLine.substring(tmpLine.indexOf("=") + 1, tmpLine.indexOf(";")));
                        }
                        i++;
                    }
                    if (i > 3) {
                        tmpLine = null;
                        return resultMap;
                    }
                }
            } else {
                resultMap.put("error", String.valueOf(httpURLConnection.getResponseCode()));
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public static boolean saveImage(File file, String urlString) {
        try {
            Bitmap tmpBitmap = null;
            java.net.URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.setDoInput(true);
            Log.e("QN",file.toString()+" : "+urlString);
            //获取 ICON
            if (httpURLConnection.getResponseCode() == httpURLConnection.HTTP_OK) {
                tmpBitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                //存到内存中
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                tmpBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] tmpByte = byteArrayOutputStream.toByteArray();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                bufferedOutputStream.write(tmpByte);
                bufferedOutputStream.close();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void DownloadApplication(String url) {

    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        boolean mobile = conn.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
        return (wifi || mobile);
    }
}
