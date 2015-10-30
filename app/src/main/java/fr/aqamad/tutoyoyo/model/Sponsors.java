package fr.aqamad.tutoyoyo.model;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.fragments.SourceFragment;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class Sponsors extends HashMap<Integer,Sponsor> {

    private static int group_id= View.generateViewId();

    private static int my_navigationid= View.generateViewId();
    private static int yyb_navigationid= View.generateViewId();
    private static int yye_navigationid= View.generateViewId();
    private static int cly_navigationid= View.generateViewId();
    private static int yyt_navigationid= View.generateViewId();
    private static int bho_navigationid= View.generateViewId();
    private static int spy_navigationid= View.generateViewId();
    private static int frn_navigationid= View.generateViewId();

    public static enum R_ID {

        group(group_id),
        my(my_navigationid),
        yyb(yyb_navigationid),
        yye(yye_navigationid),
        cly(cly_navigationid),
        yyt(yyt_navigationid),
        bho(bho_navigationid),
        spy(spy_navigationid),
        frn(frn_navigationid);

        private int key;

        R_ID(int tKey){
            this.key=tKey;
        }

        public int getKey(){
            return this.key;
        }

    }


    private Context ctx;

    public Sponsors(Context ctx){
        this.ctx=ctx;
        //builder style is prefered
        Sponsor s=new Sponsor()
                .setName(ctx.getString(R.string.mytutos))
                .setDescription("Here are your tuts lists.")
//                .setNavigationId(R.id.nav_my)
                .setNavigationId(R_ID.my.getKey())
                .setPreferenceKey(null)
                .setChannelKey(ctx.getString(R.string.localChannelKey))
                .setLayoutResId(R.layout.header_simple_logo_right)
                .setLogoResId(R.drawable.drawer_header)
                .setMenuitemIconResId(R.drawable.ic_menu_yoyotuts)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                ;
        this.put(s.navigationId,s);
        //yoyoblast
        s=new Sponsor()
                .setName(ctx.getString(R.string.yoyoblast))
                .setContactNames("Colinn")
                .setDescription("yoyo store and tutorials (apprendre à faire du yoyo)")
//                .setNavigationId(R.id.nav_yyb)
                .setNavigationId(R_ID.yyb.getKey())
                .setPreferenceKey(ctx.getString(R.string.chk_pref_yoyoblast))
                .setChannelKey(ctx.getString(R.string.YOYOBLAST_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_yoyoblast)
                .setMenuitemIconResId(R.drawable.ic_menu_yoyoblast)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.holo_orange_dark)
                .setOrder(1)
                .setWebsiteURL("http://www.yoyoblast.com");
        this.put(s.navigationId,s);
        //yoyoexpert
        s=new Sponsor()
                .setName(ctx.getString(R.string.yoyoexpert))
                .setContactNames("André Boulay")
                .setDescription("Make the simple amazing")
//                .setNavigationId(R.id.nav_yye)
                .setNavigationId(R_ID.yye.getKey())
                .setPreferenceKey(ctx.getString(R.string.chk_pref_yoyoexpert))
                .setChannelKey(ctx.getString(R.string.YOYOEXPERT_CHANNEL))
                .setLayoutResId(R.layout.header_yoyoexpert)
                .setLogoResId(R.drawable.logo_yoyoexpert)
                .setMenuitemIconResId(R.drawable.ic_menu_yoyoexpert)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(2)
                .setWebsiteURL("http://www.yoyoexpert.com");
        this.put(s.navigationId,s);
        //CLYW
        s=new Sponsor()
                .setName(ctx.getString(R.string.clyw))
                .setContactNames("Chris Mikulin & Steve Brown")
                .setDescription("CLYW is managed by Chris Mikulin and Steve Brown, together they work daily to contribute to the future of yoyoing.")
//                .setNavigationId(R.id.nav_clyw)
                .setNavigationId(R_ID.cly.getKey())
                .setPreferenceKey(ctx.getString(R.string.chk_pref_clyw))
                .setChannelKey(ctx.getString(R.string.CLYW_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_clyw)
                .setMenuitemIconResId(R.drawable.ic_menu_clyw)
                .setDisplayasBoxes(false)
                .setExpandablePlaylists(new String[]{ModelConverter.CABIN_TUTORIALS_PLAYLIST})
                .setBackGroundColor(android.R.color.white)
                .setForeGroundColor(android.R.color.black)
                .setOrder(3)
                .setWebsiteURL("http://www.clyw.ca");
        this.put(s.navigationId,s);
        //YOYOThrower
        s=new Sponsor()
                .setName(ctx.getString(R.string.yoyothrower))
                .setContactNames("Jeremy McKay")
                .setDescription("Learn to Yoyo")
//                .setNavigationId(R.id.nav_yyt)
                .setNavigationId(R_ID.yyt.getKey())
                .setPreferenceKey(ctx.getString(R.string.chk_pref_yoyothrower))
                .setChannelKey(ctx.getString(R.string.YOYOTHROWER_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_yoyothrower)
                .setMenuitemIconResId(R.drawable.ic_menu_yoyothrower)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.holo_blue_light)
                .setOrder(4)
                .setWebsiteURL("http://www.mryoyothrower.com");
        this.put(s.navigationId,s);
        //Blackhop
        s=new Sponsor()
                .setName(ctx.getString(R.string.blackhop))
                .setDescription("Yoyo Lifestyle")
                .setContactNames("Emmanuel Gabriel")
//                .setNavigationId(R.id.nav_bhop)
                .setNavigationId(R_ID.bho.getKey())
                .setPreferenceKey(ctx.getString(R.string.chk_pref_blackhop))
                .setChannelKey(ctx.getString(R.string.BLACKHOP_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_blackhop)
                .setMenuitemIconResId(R.drawable.ic_menu_blackhop)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(5)
                .setWebsiteURL("https://www.youtube.com/user/hadoq");
        this.put(s.navigationId,s);
        //SPBYY
        s=new Sponsor()
                .setName(ctx.getString(R.string.spbyys))
                .setContactNames("Ilya Shaposhnikov")
                .setDescription("Saint Petersburg Yo-Yo School shares with YOU the best yo-yo videos and yo-yo trick tutorials")
//                .setNavigationId(R.id.nav_spyy)
                .setNavigationId(R_ID.spy.getKey())
                .setPreferenceKey(ctx.getString(R.string.chk_pref_spbyys))
                .setChannelKey(ctx.getString(R.string.SPBYYS_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_spbyys)
                .setMenuitemIconResId(R.drawable.ic_menu_spbyys)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(6)
                .setWebsiteURL("https://www.facebook.com/SPbYYS");
        this.put(s.navigationId,s);
        //Friends
        s=new Sponsor()
                .setName(ctx.getString(R.string.other_friends))
                .setContactNames("Jonathan Daniel Saelens")
                .setDescription("These playlists were created by friends, many thanks to them ;-)")
//                .setNavigationId(R.id.nav_friends)
                .setNavigationId(R_ID.frn.getKey())
                .setPreferenceKey(ctx.getString(R.string.chk_pref_friends))
                .setChannelKey(ctx.getString(R.string.FRIENDS_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_friends)
                .setMenuitemIconResId(R.drawable.ic_menu_friends)
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


    // not yet sorted
    public List<Sponsor> getOrdered() {
        List<Sponsor> sponsorsByOrder = new ArrayList<Sponsor>(values());

        Collections.sort(sponsorsByOrder, new Comparator<Sponsor>() {

            public int compare(Sponsor o1, Sponsor o2) {
                return o1.order - o2.order;
            }
        }
        );
        return sponsorsByOrder;
    }
}
