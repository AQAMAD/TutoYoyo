package fr.aqamad.youtube;

import java.io.Serializable;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class YoutubeVideo implements Serializable {

    private YoutubeThumbnail mDefaultThumb;
    private YoutubeThumbnail mStandardThumb;
    private YoutubeThumbnail mMediumThumb;
    private YoutubeThumbnail mHighThumb;
    private String mID;

    private String mTitle;

    private String mDescription;


    public YoutubeVideo(String ID, String Title, String Description) {
        this.mID = ID;
        this.mTitle = Title;
        this.mDescription = Description;
    }

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }


    public YoutubeThumbnail getDefaultThumb() {
        return mDefaultThumb;
    }

    public void setDefaultThumb(YoutubeThumbnail mDefaultThumb) {
        this.mDefaultThumb = mDefaultThumb;
    }

    public YoutubeThumbnail getStandardThumb() {
        return mStandardThumb;
    }

    public void setStandardThumb(YoutubeThumbnail mStandardThumb) {
        this.mStandardThumb = mStandardThumb;
    }

    public YoutubeThumbnail getMediumThumb() {
        return mMediumThumb;
    }

    public void setMediumThumb(YoutubeThumbnail mMediumThumb) {
        this.mMediumThumb = mMediumThumb;
    }

    public YoutubeThumbnail getHighThumb() {
        return mHighThumb;
    }

    public void setHighThumb(YoutubeThumbnail mHighThumb) {
        this.mHighThumb = mHighThumb;
    }


}
