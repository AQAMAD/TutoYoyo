package fr.aqamad.tutoyoyo.model;

import org.parceler.Parcel;

/**
 * Created by Gregoire on 19/10/2015.
 */
@Parcel
public class Sponsor {
    public String name;
    public String contactNames;
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

    public Sponsor setName(String name){
        this.name=name;
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
    };

    public Sponsor setPreferenceKey(String key){
        this.preferenceKey=key;
        return this;
    };

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

