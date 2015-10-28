package fr.aqamad.tutoyoyo.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Gregoire on 16/10/2015.
 */
public class ScreenSize {

    private float dpWidth;
    private float dpHeight;
    private int pWidth;
    private int pHeight;

    public float getDensity() {
        return density;
    }

    private float density;

    public ScreenSize(Context ctx){
        //Display display=act.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics=ctx.getResources().getDisplayMetrics();
        //display.getMetrics(outMetrics);
        density=ctx.getResources().getDisplayMetrics().density;
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