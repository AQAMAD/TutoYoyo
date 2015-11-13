package fr.aqamad.tutoyoyo.utils;

import org.json.JSONArray;

/**
 * Created by Gregoire on 12/11/2015.
 */
public class Json {
    public static String[] getStringArray(JSONArray jsonArray) {
        String[] stringArray = null;
        int length = jsonArray.length();
        if (jsonArray != null) {
            stringArray = new String[length];
            for (int i = 0; i < length; i++) {
                stringArray[i] = jsonArray.optString(i);
            }
        }
        return stringArray;
    }
}
