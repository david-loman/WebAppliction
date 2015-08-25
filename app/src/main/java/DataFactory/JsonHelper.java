package DataFactory;

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

    public Map<String,String> parseImageJson(String jsonString){
        Map<String,String> map = new HashMap<>();
        return map;
    }

    public Map<String, String> parseApplictionJson(String jsonString, String[] jsonKey) {
        Map<String, String> data = new HashMap<String, String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            data.put(jsonKey[0], jsonObject.getString(jsonKey[0]));
            data.put(jsonKey[1], jsonObject.getString(jsonKey[1]));
            data.put(jsonKey[2],jsonObject.getString(jsonKey[2]));
            JSONObject childJsonObject = jsonObject.getJSONObject(INFO);
            data.put(jsonKey[3], childJsonObject.getString(jsonKey[3]));
            data.put(jsonKey[4], childJsonObject.getString(jsonKey[4]));
            data.put(jsonKey[5], childJsonObject.getString(jsonKey[5]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
