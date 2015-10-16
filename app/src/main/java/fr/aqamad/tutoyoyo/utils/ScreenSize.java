package fr.aqamad.tutoyoyo.utils;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by Gregoire on 16/10/2015.
 */
public class ScreenSize {

    private float dpWidth;
    private float dpHeight;
    private int pWidth;
    private int pHeight;

    public ScreenSize(Activity act){
        Display display=act.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics=new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density=act.getResources().getDisplayMetrics().density;
        pHeight=outMetrics.heightPixels;
        pWidth=outMetrics.widthPixels;
        dpHeight=pHeight/density;
        dpWidth=pWidth/density;
    }

    public float getDpWidth(){return dpWidth;}
    public float getDpHeight(){return dpHeight;}

    public int getWidth(){return pWidth;}
    public int getHeight(){return pHeight;}


}