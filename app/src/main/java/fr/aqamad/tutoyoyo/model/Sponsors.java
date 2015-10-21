package fr.aqamad.tutoyoyo.model;

import java.util.HashMap;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 19/10/2015.
 */
public abstract class Sponsors extends HashMap<Integer,Sponsor> {
    public Sponsors(){
        Sponsor s=new Sponsor("Yoyoblast.com",
                                "yoyo store and tutorials (Apprendre comment faire du yoyo)",
                                R.drawable.logo_yoyoblast_big,
                                android.R.color.black,
                                android.R.color.holo_orange_dark,
                                "http://www.yoyoblast.com");
        this.put(R.id.nav_yyb,s);

    }

}
