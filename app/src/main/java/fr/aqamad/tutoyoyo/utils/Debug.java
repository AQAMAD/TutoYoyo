package fr.aqamad.tutoyoyo.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 27/10/2015.
 */
public class Debug {
//    public static final int debugRefresh = 31;
    public static final int debugRefresh = 0;

    public static void sendBugReport(Activity act){
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/plain");
        //add email address
        intent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{act.getString(R.string.mailto_address)});
        intent.putExtra(Intent.EXTRA_SUBJECT, act.getString(R.string.app_name) + " [Bug Report] ");
        //and bodytext
        //above code does not work, below code works but gets a warning in the logcat
        intent.putExtra(Intent.EXTRA_TEXT, act.getString(R.string.mailto_bugmessage));
        //get the db filename here
        String dbName="YoyoTuts.db";
        //backup the database
        File backupDB = null;
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + act.getApplicationContext().getPackageName()
                        + "//databases//" + dbName + "";
                File currentDB = new File(data, currentDBPath);
                backupDB = new File(sd, dbName);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                else
                {
                    Log.d("MA.RB", "Database not found at path : " + currentDBPath);
                }
            } else
            {
                Log.d("MA.RB","Unwritable sd : " + sd.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //log the db path
        Log.d("MA.RB","Backup DatabasePath was : " + backupDB.getAbsolutePath() );
        //now get the logcat
        File filename = new File(Environment.getExternalStorageDirectory()+"/mylog.log");
        try {
            filename.createNewFile();
            String cmd = "logcat -d -f"+filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //logically logcat was created
        Log.d("MA.RB","Logfile Path was : " + filename.getAbsolutePath() );
        //prepare array
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        uris.add(Uri.fromFile(backupDB));
        uris.add(Uri.fromFile(filename));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        if (intent.resolveActivity(act.getPackageManager()) != null) {
            act.startActivity(intent);
        }
    }

}
