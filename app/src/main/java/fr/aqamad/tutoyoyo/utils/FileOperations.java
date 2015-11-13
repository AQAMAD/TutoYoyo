package fr.aqamad.tutoyoyo.utils;

import java.io.File;

/**
 * Created by Gregoire on 12/11/2015.
 */
public class FileOperations {

    public static boolean dirExists(String dir_path) {
        boolean ret = false;
        File dir = new File(dir_path);
        if (dir.exists() && dir.isDirectory())
            ret = true;
        return ret;
    }

    public static void ensureDirExists(String dir_path) {
        if (!dirExists(dir_path)) {
            File directory = new File(dir_path);
            directory.mkdirs();
        }
    }

}
