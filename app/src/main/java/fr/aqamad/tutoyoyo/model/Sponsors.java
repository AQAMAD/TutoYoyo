package fr.aqamad.tutoyoyo.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.fragments.SourceFragment;
import fr.aqamad.tutoyoyo.utils.Assets;
import fr.aqamad.tutoyoyo.utils.UI;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class Sponsors extends HashMap<Integer,Sponsor> {

    private static int group_id = UI.getViewID();

    private static int my_navigationid = UI.getViewID();

    //a static map of generated ids vs keys
    private static Map<String, Integer> navigationIds = new HashMap<String, Integer>();

    private static int frn_navigationid = UI.getViewID();

    private Resources res;

    private Sponsors() {

    }

    public Sponsors(Context ctx) {
        this.res = ctx.getResources();
        int order=0;
        //builder style is prefered
        Sponsor s=new Sponsor()
                .setName(res.getString(R.string.mytutos))
                .setDescription("Here are your tuts lists.")
                .setNavigationId(R_ID.my.getKey())
                .setPreferenceKey(null)
                .setChannelKey(res.getString(R.string.LOCAL_CHANNEL))
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
        //here we load the assets sponsors
        try {
            order = loadAssetsSponsors(ctx, this, order);
        } catch (JSONException j) {
            j.printStackTrace();
            //throw new Exception("invalid json format for sponsors",j);
        }
        Log.d("S.LFJ", "Loaded json assets : neworder is " + order);
        //Friends
        s=new Sponsor()
                .setName(res.getString(R.string.other_friends))
                .setContactNames("Jonathan Daniel Saelens")
                .setLanguage("en")
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

    private int loadAssetsSponsors(Context ctx, Sponsors sponsors, int order) throws JSONException {
        //here we need a few things :
        Resources res = ctx.getResources();
        //a json string extracted from the asset file we're looking for
        String jsonString = Assets.loadFileFromAsset(res, "sponsors.json");
        //a method to deserialize this json
        JSONObject json = new JSONObject(jsonString);
        JSONArray jsonSponsors = json.getJSONArray("sponsors");
        for (int i = 0; i < jsonSponsors.length(); i++) {
            //first we obtain an object from it
            JSONObject jsonSponsor = jsonSponsors.getJSONObject(i);
            //next we check if an id has been generated
            //get the key
            String sponsorKey = Sponsor.keyFromJson(jsonSponsor);
            int navID = 0;
            if (navigationIds.containsKey(sponsorKey)) {
                navID = navigationIds.get(sponsorKey);
            } else {
                navID = UI.getViewID();
                navigationIds.put(sponsorKey, navID);
            }
            Sponsor newSponsor = Sponsor.fromJson(ctx, jsonSponsor, navID);
            newSponsor.setOrder(order);
            order++;
            Log.d("S.LFJ", "Loaded from json asset : " + newSponsor.name + " at order " + newSponsor.order);
            //sponsor is ready, apply same treatment as others
            this.put(newSponsor.navigationId, newSponsor);
        }
        return order;
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

    public Sponsors getCopy() {
        Sponsors result = new Sponsors();
        result.putAll(this);
        return result;
    }


    public enum R_ID {

        group(group_id),
        my(my_navigationid),
        frn(frn_navigationid);

        private int key;

        R_ID(int tKey) {
            this.key = tKey;
        }

        public int getKey() {
            return this.key;
        }

    }
}
