package fr.aqamad.tutoyoyo.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Gregoire on 28/10/2015.
 */
public class IntentHelper {

    public static void sendMail(Activity act,Uri mailto, String string, String string2) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, mailto);
        intent.putExtra(Intent.EXTRA_SUBJECT, string);
        intent.putExtra(Intent.EXTRA_TEXT, string2);
        if (intent.resolveActivity(act.getPackageManager()) != null) {
            act.startActivity(intent);
        }
    }
}
