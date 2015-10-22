package fr.aqamad.tutoyoyo.model;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class Sponsor {
    public String name;
    public int navigationId;
    public String preferenceKey;
    public String channelKey;
    public int layoutResId;
    public int foreGroundColor;
    public int backGroundColor;
    public String websiteURL;

    public Sponsor(String name, int navigationId, String preferenceKey, String channelKey, int layoutResId, int foreGroundColor, int backGroundColor, String websiteURL) {
        this.name = name;
        this.navigationId = navigationId;
        this.preferenceKey = preferenceKey;
        this.channelKey = channelKey;
        this.layoutResId = layoutResId;
        this.foreGroundColor = foreGroundColor;
        this.backGroundColor = backGroundColor;
        this.websiteURL = websiteURL;
    }


}
