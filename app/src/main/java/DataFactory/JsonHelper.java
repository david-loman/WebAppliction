package DataFactory;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 2014/10/31.
 */
public class JsonHelper {

    private final String LENTH = "len";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String APPLICTION = "application";
    private final String DATA = "data";
    private final String INFO = "info";

    public final String NAME = "name";
    public final String URL = "url";

    public boolean[] parseUpdataJson(String jsonString) {
        boolean[] result = new boolean[5];

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            result[0] = jsonObject.getBoolean(APPLICTION);
            result[1] = jsonObject.getBoolean(DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, String> parseApplictionJson(String jsonString, String[] jsonKey) {
        Map<String, String> data = new HashMap<String, String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            data.put(jsonKey[0], jsonObject.getString(jsonKey[0]));
            data.put(jsonKey[1], jsonObject.getString(jsonKey[1]));
            JSONObject childJsonObject = jsonObject.getJSONObject(INFO);
            data.put(jsonKey[2], childJsonObject.getString(jsonKey[2]));
            data.put(jsonKey[3], childJsonObject.getString(jsonKey[3]));
            data.put(jsonKey[4], childJsonObject.getString(jsonKey[4]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public List<Map<String, String>> parseWebsiteJson(String jsonString, String parent) {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            int len = jsonObject.getInt(LENTH);
            JSONArray jsonArray = jsonObject.getJSONArray(parent);
            for (int i = 0; i < len; i++) {
                JSONObject childJsonObject = jsonArray.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put(NAME, childJsonObject.getString(NAME));
                map.put(URL, childJsonObject.getString(URL));
                map.put(USERNAME, childJsonObject.getString(USERNAME));
                map.put(PASSWORD, childJsonObject.getString(PASSWORD));
                data.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public String convertWebsiteJson(List<Map<String, String>> data, String parent) {
        StringBuilder stringBuilder = new StringBuilder("{");
        int len = data.size();
        stringBuilder.append("\"" + LENTH + "\": " + len + ",");
        stringBuilder.append("\"" + parent + "\": " + " [");
        for (int i = 0; i < len; i++) {
            stringBuilder.append("{ \"" + NAME + "\": \"" + data.get(i).get(NAME) + "\" ,");
            stringBuilder.append("\"" + URL + "\": \"" + data.get(i).get(URL) + "\" ,");
            stringBuilder.append("\"" + USERNAME + "\": \"" + data.get(i).get(USERNAME) + "\" ,");
            stringBuilder.append("\"" + PASSWORD + "\": \"" + data.get(i).get(PASSWORD) + "\" }");
            if (i != (len - 1)) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]  }");
        return stringBuilder.toString();
    }

}
