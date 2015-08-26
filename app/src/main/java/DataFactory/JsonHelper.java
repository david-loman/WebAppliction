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

    private final String INFO = "info";

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
