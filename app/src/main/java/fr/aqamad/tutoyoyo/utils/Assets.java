package fr.aqamad.tutoyoyo.utils;

import android.app.Activity;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Gregoire on 09/10/2015.
 */
public class Assets {

    public static String loadFileFromAsset(Resources res, String assetName) {
        String json = null;

        try {
            boolean b = Arrays.asList(res.getAssets().list("")).contains(assetName);
            if (b){
                InputStream is = res.getAssets().open(assetName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String loadFileFromAssetFolder(Activity localActivity, String assetFolder, String assetName) {
        String json = null;

        try {
            boolean b = Arrays.asList(localActivity.getResources().getAssets().list(assetFolder)).contains(assetName);
            if (b) {
                InputStream is = localActivity.getAssets().open(assetFolder + "/" + assetName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
