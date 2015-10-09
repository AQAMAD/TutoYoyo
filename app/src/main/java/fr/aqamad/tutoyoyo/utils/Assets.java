package fr.aqamad.tutoyoyo.utils;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Gregoire on 09/10/2015.
 */
public class Assets {

    public static String loadFileFromAsset(Activity localActivity,String assetName) {
        String json = null;
        try {
            InputStream is = localActivity.getAssets().open( assetName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
