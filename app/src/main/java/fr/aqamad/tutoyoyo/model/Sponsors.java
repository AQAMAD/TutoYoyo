package fr.aqamad.tutoyoyo.model;

import android.content.Context;

import java.util.HashMap;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 19/10/2015.
 */
public abstract class Sponsors extends HashMap<Integer,Sponsor> {


    private Context ctx;

    public Sponsors(Context ctx){
        this.ctx=ctx;
        Sponsor s=new Sponsor("Yoyoblast.com",
                                R.id.nav_yyb,
                                ctx.getString(R.string.chk_pref_yoyoblast),
                                ctx.getString(R.string.YOYOBLAST_CHANNEL),
                                R.layout.header_yoyoblast,
                                android.R.color.black,
                                android.R.color.holo_orange_dark,
                                "http://www.yoyoblast.com"
                                );
        this.put(R.id.nav_yyb,s);

    }

}
