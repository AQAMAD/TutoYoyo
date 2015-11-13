package fr.aqamad.tutoyoyo.model;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import fr.aqamad.tutoyoyo.utils.Json;
import fr.aqamad.tutoyoyo.utils.UI;

/**
 * Created by Gregoire on 19/10/2015.
 */
@Parcel
public class Sponsor {
    public String name;
    public String contactNames;
    public String language;
    public String description;
    public int navigationId;
    public String preferenceKey;
    public String channelKey;
    public String[] expandablePlaylists;
    public String[] includePlaylists;
    public String[] excludePlaylists;
    public String[] cleanPlaylists;
    public String[] cleanVideos;
    public int layoutResId;
    public int foreGroundColor;
    public int backGroundColor;
    public boolean displayasBoxes=false;
    public int order=0;
    public String websiteURL;
    public int logoResId;
    public int menuitemIconResId;
    public int nameResId;
    public int descriptionResId;

    public Sponsor(){

    }

    public static String keyFromJson(JSONObject jsonSponsor) throws JSONException {
        if (jsonSponsor.has("channelKey")) {
            return jsonSponsor.getString("channelKey");
        }
        return null;
    }

    public static Sponsor fromJson(Context ctx, JSONObject jsonSponsor, int navId) throws JSONException {
        Sponsor sp = new Sponsor();
        Resources res = ctx.getResources();
        sp.setNavigationId(navId);
        if (jsonSponsor.has("name")) {
            sp.setName(jsonSponsor.getString("name"));
        }
        if (jsonSponsor.has("description")) {
            sp.setDescription(jsonSponsor.getString("description"));
        }
        if (jsonSponsor.has("language")) {
            sp.setLanguage(jsonSponsor.getString("language"));
        }
        if (jsonSponsor.has("contactNames")) {
            sp.setContactNames(jsonSponsor.getString("contactNames"));
        }
        if (jsonSponsor.has("websiteURL")) {
            sp.setWebsiteURL(jsonSponsor.getString("websiteURL"));
        }
        if (jsonSponsor.has("channelKey")) {
            sp.setChannelKey(jsonSponsor.getString("channelKey"));
        }
        if (jsonSponsor.has("noPreferenceKey")) {
            if (!jsonSponsor.getBoolean("noPreferenceKey")) {
                //generate pref key
                sp.setPreferenceKey("prefsForSponsorFromJson" + navId);
            }
        }
        if (jsonSponsor.has("headerLayout")) {
            String resName = jsonSponsor.getString("headerLayout");
            int resId = res.getIdentifier(resName, "layout", ctx.getPackageName());
            sp.setLayoutResId(resId);
        }
        if (jsonSponsor.has("logoResource")) {
            String resName = jsonSponsor.getString("logoResource");
            int resId = res.getIdentifier(resName, "drawable", ctx.getPackageName());
            sp.setLogoResId(resId);
        }
        if (jsonSponsor.has("iconResource")) {
            String resName = jsonSponsor.getString("iconResource");
            int resId = res.getIdentifier(resName, "drawable", ctx.getPackageName());
            sp.setMenuitemIconResId(resId);
        }
        if (jsonSponsor.has("listItems")) {
            if (!jsonSponsor.getString("listItems").equals("box")) {
                sp.setDisplayasBoxes(false);
            } else {
                sp.setDisplayasBoxes(true);
            }
        }
        if (jsonSponsor.has("backgroundColor")) {
            String colorName = jsonSponsor.getString("backgroundColor");
            sp.setBackGroundColor(UI.getColorFromName(colorName));
        }
        if (jsonSponsor.has("foregroundColor")) {
            String colorName = jsonSponsor.getString("foregroundColor");
            sp.setForeGroundColor(UI.getColorFromName(colorName));
        }
        if (jsonSponsor.has("includePlaylists")) {
            JSONArray playlists = jsonSponsor.getJSONArray("includePlaylists");
            sp.setIncludePlaylists(Json.getStringArray(playlists));
        }
        if (jsonSponsor.has("expandPlaylists")) {
            JSONArray playlists = jsonSponsor.getJSONArray("expandPlaylists");
            sp.setExpandablePlaylists(Json.getStringArray(playlists));
        }
        if (jsonSponsor.has("cleanPlaylists")) {
            JSONArray playlists = jsonSponsor.getJSONArray("cleanPlaylists");
            sp.setCleanPlaylists(Json.getStringArray(playlists));
        }
        if (jsonSponsor.has("cleanVideos")) {
            JSONArray playlists = jsonSponsor.getJSONArray("cleanVideos");
            sp.setCleanVideos(Json.getStringArray(playlists));
        }
        return sp;
    }

    public Sponsor setName(String name){
        this.name=name;
        return this;
    }

    public Sponsor setLanguage(String language) {
        this.language = language;
        return this;
    }

    public Sponsor setIncludePlaylists(String[] includePlaylists) {
        this.includePlaylists = includePlaylists;
        return this;
    }

    public Sponsor setCleanPlaylists(String[] cleanPlaylists) {
        this.cleanPlaylists = cleanPlaylists;
        return this;
    }

    public Sponsor setCleanVideos(String[] cleanVideos) {
        this.cleanVideos = cleanVideos;
        return this;
    }

    public Sponsor setContactNames(String contactNames) {
        this.contactNames = contactNames;
        return this;
    }

    public Sponsor setNavigationId(int id){
        this.navigationId=id;
        return this;
    }

    public Sponsor setPreferenceKey(String key){
        this.preferenceKey=key;
        return this;
    }

    public Sponsor setChannelKey(String channelKey) {
        this.channelKey = channelKey;
        return this;
    }

    public Sponsor setLayoutResId(int layoutResId) {
        this.layoutResId = layoutResId;
        return this;
    }

    public Sponsor setDisplayasBoxes(boolean displayasBoxes) {
        this.displayasBoxes = displayasBoxes;
        return this;
    }

    public Sponsor setExpandablePlaylists(String[] expandablePlaylists) {
        this.expandablePlaylists = expandablePlaylists;
        return this;
    }

    public Sponsor setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
        return this;
    }

    public Sponsor setForeGroundColor(int foreGroundColor) {
        this.foreGroundColor = foreGroundColor;
        return this;
    }

    public Sponsor setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
        return this;
    }

    public Sponsor setOrder(int order) {
        this.order = order;
        return this;
    }

    public Sponsor setDescription(String description) {
        this.description = description;
        return this;
    }

    public Sponsor setDescriptionResId(int descriptionResId) {
        this.descriptionResId = descriptionResId;
        return this;
    }

    public Sponsor setLogoResId(int logoResId) {
        this.logoResId = logoResId;
        return this;
    }

    public Sponsor setMenuitemIconResId(int menuitemIconResId) {
        this.menuitemIconResId = menuitemIconResId;
        return this;
    }

    public Sponsor setNameResId(int nameResId) {
        this.nameResId = nameResId;
        return this;
    }


}

