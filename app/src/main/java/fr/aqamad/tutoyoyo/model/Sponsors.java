package fr.aqamad.tutoyoyo.model;

import android.content.res.Resources;
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
    private static int mfd_navigationid= View.generateViewId();
    private static int yot_navigationid= View.generateViewId();
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
        mfd(mfd_navigationid),
        yot(yot_navigationid),
        frn(frn_navigationid);

        private int key;

        R_ID(int tKey){
            this.key=tKey;
        }

        public int getKey(){
            return this.key;
        }

    }


    private Resources res;

    public Sponsors(Resources res){
        this.res=res;
        int order=0;
        //builder style is prefered
        Sponsor s=new Sponsor()
                .setName(res.getString(R.string.mytutos))
                .setDescription("Here are your tuts lists.")
                .setNavigationId(R_ID.my.getKey())
                .setPreferenceKey(null)
                .setChannelKey(res.getString(R.string.localChannelKey))
                .setLayoutResId(R.layout.header_simple_logo_right)
                .setLogoResId(R.drawable.drawer_header)
                .setMenuitemIconResId(R.drawable.ic_menu_yoyotuts)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(order)
                ;
        this.put(s.navigationId,s);
        order++;
        //yoyoblast
        s=new Sponsor()
                .setName(res.getString(R.string.yoyoblast))
                .setContactNames("Colinn")
                .setDescription("yoyo store and tutorials (apprendre à faire du yoyo)")
                .setNavigationId(R_ID.yyb.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_yoyoblast))
                .setChannelKey(res.getString(R.string.YOYOBLAST_CHANNEL))
                .setCleanVideos(new String[]{" - YoyoBlast"})
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_yoyoblast)
                .setMenuitemIconResId(R.drawable.ic_menu_yoyoblast)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.holo_orange_dark)
                .setOrder(order)
                .setWebsiteURL("http://www.yoyoblast.com");
        this.put(s.navigationId,s);
        order++;
        //yoyoexpert
        s=new Sponsor()
                .setName(res.getString(R.string.yoyoexpert))
                .setContactNames("André Boulay")
                .setDescription("Make the simple amazing")
                .setNavigationId(R_ID.yye.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_yoyoexpert))
                .setChannelKey(res.getString(R.string.YOYOEXPERT_CHANNEL))
                .setIncludePlaylists(new String[]{"PL5fEaQ2mpy1DvDzmmeV0Iz_GABwdzVwGD", "PLD65657CD0CA7EAD4", "PL4CF43DD91C848E5A",
                        "PLFB4661EE4BBCA1ED", "PLC3742039F7C8359F", "PL3B8719FC83AF35DB",
                        "PLF0CEDAB14CEF4A67", "PL8749C6A79DC85F76", "PLA8855EAF38C2B0FA",
                        "PL3718073C10A9A7CA", "PLCA5BA8D19F61DD3D", "PLD136F5AA490BE3C6",
                        "PL592ABBE6C9186E22", "PL2EE39E52C0C72284"
                })
                .setExpandablePlaylists(new String[]{"PL5fEaQ2mpy1DvDzmmeV0Iz_GABwdzVwGD"})
                .setCleanPlaylists(new String[]{"Expert Village Yo Yo Tricks: ",
                        "Expert Village Yo Yo tricks: ",
                        "Expert village Yo Yo Tricks: ",
                        "YoyoExpert ",
                        "How To ",
                        "How to ",
                        "Expert Village: "
                })
                .setCleanVideos(new String[]{"How To Do ",
                        "Learn the ",
                        " Yoyo trick - YoYoExpert Tutorials",
                        "How To ",
                        "How to "})
                .setLayoutResId(R.layout.header_yoyoexpert)
                .setLogoResId(R.drawable.logo_yoyoexpert)
                .setMenuitemIconResId(R.drawable.ic_menu_yoyoexpert)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(order)
                .setWebsiteURL("http://www.yoyoexpert.com");
        this.put(s.navigationId,s);
        order++;
        //CLYW
        s=new Sponsor()
                .setName(res.getString(R.string.clyw))
                .setContactNames("Chris Mikulin & Steve Brown")
                .setDescription("CLYW is managed by Chris Mikulin and Steve Brown, together they work daily to contribute to the future of yoyoing.")
                .setNavigationId(R_ID.cly.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_clyw))
                .setChannelKey(res.getString(R.string.CLYW_CHANNEL))
                .setIncludePlaylists(new String[]{"PLVLLF_sWPwMxR8TQv_FpKZC_W6knxdiln"})
                .setExpandablePlaylists(new String[]{"PLVLLF_sWPwMxR8TQv_FpKZC_W6knxdiln"})
                .setCleanVideos(new String[]{"CLYW Cabin Tutorials - ",
                        "CLYW Cabin Tutorial - ",
                        "Cabin Tutorial - "})
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_clyw)
                .setMenuitemIconResId(R.drawable.ic_menu_clyw)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.white)
                .setForeGroundColor(android.R.color.black)
                .setOrder(order)
                .setWebsiteURL("http://www.clyw.ca");
        this.put(s.navigationId,s);
        order++;
        //YOYOThrower
        s=new Sponsor()
                .setName(res.getString(R.string.yoyothrower))
                .setContactNames("Jeremy McKay")
                .setDescription("Learn to Yoyo")
                .setNavigationId(R_ID.yyt.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_yoyothrower))
                .setChannelKey(res.getString(R.string.YOYOTHROWER_CHANNEL))
                .setIncludePlaylists(new String[]{"PLwMQ2twUtKwIRTisOwx81Au2iXzaWws10", "PLwMQ2twUtKwILBwrAw8fiWwMSLwCPAQtU", "PLwMQ2twUtKwInJNi1gB2fPMzphtLLqKAu",
                        "PLwMQ2twUtKwJUICDNZpWcZVUkrDutPm8w", "PLwMQ2twUtKwIE2dPBnXquZOkuZmqOzS5v", "PLwMQ2twUtKwJSuxDWvLQieUgWAIJJjwEm",
                        "PLwMQ2twUtKwLbAE0ulhlw6inghUX4p2Vh", "PLwMQ2twUtKwJtMLOE8_yZFzP96c21HEFs", "PLwMQ2twUtKwIMoOLSXbPyX8G3dEY1ULaV",
                        "PLwMQ2twUtKwJxW1TnERDstwnr-nfZj-sr", "PLwMQ2twUtKwLKIbFxzRpd86Rwlgg9sFXw", "PLwMQ2twUtKwLOY9fPHepZai81tW9qQ_pF",
                        "PLwMQ2twUtKwKu-XM4pSKmUvCFbWb4E0N8", "PLwMQ2twUtKwKR9GC1a3oTE7UnBb-D8xMe", "PLwMQ2twUtKwIutslagYaloqLSv-Hu35yT"
                })
                .setCleanVideos(new String[]{"Learn to Yo-yo ",
                        "Yoyo Trick Tutorial - ",
                        "Yo-yo Trick Tutorial - ",
                        "Yo-yo Trick Tutorial -",
                        "Yo-yo trick Tutorial - ",
                        "Yo-yo trick tutorial - ",
                        "Yo yo tutorial - ",
                        "Yoyo trick tutorial - ",
                        "Yoyo Trick Tutorial-",
                        "Yoyo trick tutorial-",
                        "Yoyo trick tutorial  ",
                        "yoyo tutorial",
                        "Yo-yo Trick - ",
                        "Yoyo Trick - ",
                        "Yoyo trick - ",
                        "Yoyo Tutorial - ",
                        "Yo-Yo Tutorial - ",
                        "Yoyo Trick Tutorial ",
                        "1a Tutorial - ",
                        "1a Yo-Yo Tutorial - ",
                        "1a Yo-yo Tutorial - ",
                        "1am Tutorial - ",
                        "1A Tutorial - ",
                        "1a tutorial ",
                })
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_yoyothrower)
                .setMenuitemIconResId(R.drawable.ic_menu_yoyothrower)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.holo_blue_light)
                .setOrder(order)
                .setWebsiteURL("http://www.mryoyothrower.com");
        this.put(s.navigationId,s);
        order++;
        //Blackhop
        s=new Sponsor()
                .setName(res.getString(R.string.blackhop))
                .setDescription("Yoyo Lifestyle")
                .setContactNames("Emmanuel Gabriel")
                .setNavigationId(R_ID.bho.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_blackhop))
                .setChannelKey(res.getString(R.string.BLACKHOP_CHANNEL))
                .setIncludePlaylists(new String[]{"PL31896775C716B8B3","PL8C046415EB38C4F4"})
                .setCleanVideos(new String[]{"yoyo-france.net - tuto yoyo - debutant - ",
                        "yoyo-france.net - tuto yoyo - intermédiaire - ",
                        "yoyo-france.net - tuto yoyo - ",
                        "blackhop.com - tuto yoyo intermediaire - ",
                        "blackhop.com - tuto intermediaire - "})
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_blackhop)
                .setMenuitemIconResId(R.drawable.ic_menu_blackhop)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(order)
                .setWebsiteURL("https://www.youtube.com/user/hadoq");
        this.put(s.navigationId,s);
        order++;
        //SPBYY
        s=new Sponsor()
                .setName(res.getString(R.string.spbyys))
                .setContactNames("Ilya Shaposhnikov")
                .setDescription("Saint Petersburg Yo-Yo School shares with YOU the best yo-yo videos and yo-yo trick tutorials")
                .setNavigationId(R_ID.spy.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_spbyys))
                .setChannelKey(res.getString(R.string.SPBYYS_CHANNEL))
                .setIncludePlaylists(new String[]{"PL4nKaZchMa6sEdmNmcowwdXXrBVCUYqdm", "PL4nKaZchMa6tT8Y9xqY3TN6YJ2Njbjflq"})
                .setExpandablePlaylists(new String[]{"PL4nKaZchMa6sEdmNmcowwdXXrBVCUYqdm", "PL4nKaZchMa6tT8Y9xqY3TN6YJ2Njbjflq"})
                .setCleanVideos(new String[]{"SPbYYS Tutorials: ",
                        "YoYoMad1001 x C3yoyodesign x Throw-YoYo Contest - ",
                        "Rethinkyoyo Contest 2012 - ",
                        "Rethinkyoyo Contest 2012 -- ",
                        "TYYT - ",
                        "1a Tutorial - ",
                        "1a Tutorial ",
                        "1a tutorial ",
                        " Tutorial"
                })
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_spbyys)
                .setMenuitemIconResId(R.drawable.ic_menu_spbyys)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(order)
                .setWebsiteURL("https://www.facebook.com/SPbYYS");
        this.put(s.navigationId,s);
        order++;
        //MFD
        s=new Sponsor()
                .setName(res.getString(R.string.monkeyfinger))
                .setContactNames("Ray Smith")
                .setDescription("Customize your own yoyo today!!!")
                .setNavigationId(R_ID.mfd.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_monkeyfinger))
                .setChannelKey(res.getString(R.string.MFD_CHANNEL))
                .setIncludePlaylists(new String[]{"PLQ127so8iTNVeMayoqMyyR9-CxyJHS3r9", "PLQ127so8iTNWMO2lP6x6bkZFaxHyOF8Kr", "PLQ127so8iTNXNsT4PmnjeCxND7VFcpSrr", "PLQ127so8iTNWpEBYkyCsvBzE1V7JMamBa", "PLQ127so8iTNWcqCgwsienEoi5gbL15-AP", "PLQ127so8iTNVQM44aeDzg46UsuFOD-8LN"})
                .setCleanVideos(new String[]{"Learn how to do ",
                        " Yoyo Trick",
                        " yoyo trick",
                        "Learn yoyo sports ladder trick ",
                        "Learn yoyo sport ladder trick ",
                        "Learn yoyo sports ladder ",
                        "Learn yoyo sport ladder ",
                        "Tutorial Easyish - ",
                })
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_monkeyfinger)
                .setMenuitemIconResId(R.drawable.ic_menu_monkeyfinger)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.black)
                .setForeGroundColor(android.R.color.white)
                .setOrder(order)
                .setWebsiteURL("http://www.mfdyoyo.com");
        this.put(s.navigationId,s);
        order++;
        //Yoyotricks
        s=new Sponsor()
                .setName(res.getString(R.string.yoyotricks))
                .setContactNames("Adam D Bottiglia")
                .setDescription("YOYO TRICKS AND YOYO SHOP TO HELP YOU LEARN HOW TO YOYO.")
                .setNavigationId(R_ID.yot.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_yoyotricks))
                .setChannelKey(res.getString(R.string.YOYOTRICKS_CHANNEL))
                .setIncludePlaylists(new String[]{
                        "PLB9C93039FFBD2D24", "PLFupjUECrwwvtI-zQoFsn98hinTufP42j", "PLD8D3F8248D776DE9",
                        "PL86AA7BC1F5526FAD", "PLFC58874788AFC753", "PLFupjUECrwwupL4bsiAfFaXgulJhDbm98",
                        "PLFupjUECrwwsjf3i8gax08GrhNjv6A49M", "PL97F20DB55914530C", "PL2106EF93AD2A6208",
                        "PLF7800866F2CE894F", "PLB7506D64F77C63F1", "PL22839D5E83EFC52D",
                        "PLD9681B4D2A2E3264", "PLFupjUECrwwuly0n-fGyGWYnYxL0nCm94",
                })
//                .setCleanVideos(new String[]{"Learn how to do ",
//                        " Yoyo Trick",
//                        " yoyo trick",
//                        "Learn yoyo sports ladder trick ",
//                        "Learn yoyo sport ladder trick ",
//                        "Learn yoyo sports ladder ",
//                        "Learn yoyo sport ladder ",
//                        "Tutorial Easyish - ",
//                })
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_yotricks)
                .setMenuitemIconResId(R.drawable.ic_menu_yotricks)
                .setDisplayasBoxes(true)
                .setBackGroundColor(android.R.color.white)
                .setForeGroundColor(android.R.color.black)
                .setOrder(order)
                .setWebsiteURL("http://www.yoyotricks.com");
        this.put(s.navigationId,s);
        order++;

        //Friends
        s=new Sponsor()
                .setName(res.getString(R.string.other_friends))
                .setContactNames("Jonathan Daniel Saelens")
                .setDescription("These playlists were created by friends, many thanks to them ;-)")
                .setNavigationId(R_ID.frn.getKey())
                .setPreferenceKey(res.getString(R.string.chk_pref_friends))
                .setChannelKey(res.getString(R.string.FRIENDS_CHANNEL))
                .setLayoutResId(R.layout.header_simple_logo_left)
                .setLogoResId(R.drawable.logo_friends)
                .setMenuitemIconResId(R.drawable.ic_menu_friends)
                .setDisplayasBoxes(false)
                .setBackGroundColor(android.R.color.white)
                .setForeGroundColor(android.R.color.black)
                .setOrder(order);
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


    public Sponsor getByChannelKey(String channelKey){
        for (Sponsor s :
             values()) {
            if (s.channelKey.equals(channelKey)){
                return s;
            }
        }
        return null;
    }
}
