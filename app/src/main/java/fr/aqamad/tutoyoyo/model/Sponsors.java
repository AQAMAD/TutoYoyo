package fr.aqamad.tutoyoyo.model;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.fragments.SourceFragment;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class Sponsors extends HashMap<Integer,Sponsor> {


    private Context ctx;

    public Sponsors(Context ctx){
        this.ctx=ctx;
        //builder style is prefered
        Sponsor s=new Sponsor()
                .setName("My tutos")
                .setDescription("Here are your tuts lists.")
                .setNavigationId(R.id.nav_my)
                .setPreferenceKey(null)
                .setChannelKey(ctx.getString(R.string.localChannelKey))
//                .setLayoutResId(R.layout.header_mytuts)
                .setLayoutResId(R.layout.header_simple_logo_right)
                .setLogoResId(R.drawable.drawer_header)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                ;
        this.put(s.navigationId,s);
        //yoyoblast
        s=new Sponsor()
                .setName("Yoyoblast.com")
                .setDescription("yoyo store and tutorials (apprendre à faire du yoyo)")
                .setNavigationId(R.id.nav_yyb)
                .setPreferenceKey(ctx.getString(R.string.chk_pref_yoyoblast))
                .setChannelKey(ctx.getString(R.string.YOYOBLAST_CHANNEL))
//                .setLayoutResId(R.layout.header_yoyoblast)
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_yoyoblast)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.holo_orange_dark)
                .setOrder(1)
                .setWebsiteURL("http://www.yoyoblast.com");
        this.put(s.navigationId,s);
        //yoyoexpert
        s=new Sponsor()
                .setName("YOYOExpert.com")
                .setNavigationId(R.id.nav_yye)
                .setPreferenceKey(ctx.getString(R.string.chk_pref_yoyoexpert))
                .setChannelKey(ctx.getString(R.string.YOYOEXPERT_CHANNEL))
                .setLayoutResId(R.layout.header_yoyoexpert)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(2)
                .setWebsiteURL("http://www.yoyoexpert.com");
        this.put(s.navigationId,s);
        //CLYW
        s=new Sponsor()
                .setName("CLYW")
                .setDescription("CLYW is managed by Chris Mikulin and Steve Brown, together they work daily to contribute to the future of yoyoing.")
                .setNavigationId(R.id.nav_clyw)
                .setPreferenceKey(ctx.getString(R.string.chk_pref_clyw))
                .setChannelKey(ctx.getString(R.string.CLYW_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_clyw)
                .setDisplayasBoxes(false)
                .setExpandablePlaylists(new String[]{ModelConverter.CABIN_TUTORIALS_PLAYLIST})
                .setBackGroundColor(android.R.color.white)
                .setForeGroundColor(android.R.color.black)
                .setOrder(3)
                .setWebsiteURL("http://www.clyw.ca");
        this.put(s.navigationId,s);
        //YOYOThrower
        s=new Sponsor()
                .setName("MrYoyoThrower")
                .setDescription("Learn to Yoyo")
                .setNavigationId(R.id.nav_yyt)
                .setPreferenceKey(ctx.getString(R.string.chk_pref_yoyothrower))
                .setChannelKey(ctx.getString(R.string.YOYOTHROWER_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_yoyothrower)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.holo_blue_light)
                .setOrder(4)
                .setWebsiteURL("http://www.mryoyothrower.com");
        this.put(s.navigationId,s);
        //Blackhop
        s=new Sponsor()
                .setName("Blackhop.com / Yoyo-france.net")
                .setDescription("Yoyo Lifestyle")
                .setNavigationId(R.id.nav_bhop)
                .setPreferenceKey(ctx.getString(R.string.chk_pref_blackhop))
                .setChannelKey(ctx.getString(R.string.BLACKHOP_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_blackhop)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(5)
                .setWebsiteURL("https://www.youtube.com/user/hadoq");
        this.put(s.navigationId,s);
        //SPBYY
        s=new Sponsor()
                .setName("SPbYYS")
                .setDescription("Saint Petersburg Yo-Yo School shares with YOU the best yo-yo videos and yo-yo trick tutorials")
                .setNavigationId(R.id.nav_spyy)
                .setPreferenceKey(ctx.getString(R.string.chk_pref_spbyys))
                .setChannelKey(ctx.getString(R.string.SPBYYS_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_spbyys)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(6)
                .setWebsiteURL("https://www.facebook.com/SPbYYS");
        this.put(s.navigationId,s);
        //Friends
        s=new Sponsor()
                .setName("YoyoTuts Friends")
                .setDescription("These playlists were created by friends, many thanks to them ;-)")
                .setNavigationId(R.id.nav_friends)
                .setPreferenceKey(ctx.getString(R.string.chk_pref_friends))
                .setChannelKey(ctx.getString(R.string.FRIENDS_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_friends)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.white)
                .setForeGroundColor(android.R.color.black)
                .setOrder(7);
        this.put(s.navigationId,s);

        //finally debug.log it to the logcat


    }


    public Map<Integer,SourceFragment> getFragments(){
        Map<Integer,SourceFragment> fragments=new HashMap<>();
        for (Integer key :
                this.keySet()) {
            Sponsor s = this.get(key);
            SourceFragment fragment=SourceFragment.newInstance(s);
            fragments.put(key,fragment);
        }
        return fragments;
    }


}
