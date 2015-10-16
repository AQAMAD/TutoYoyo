package fr.aqamad.youtube;

import java.io.Serializable;

import fr.aqamad.tutoyoyo.model.TutorialVideo;

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

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getPublishedAt() {
        return mPublishedAt;
    }

    public void setPublishedAt(String mPublishedAt) {
        this.mPublishedAt = mPublishedAt;
    }

    private String mDuration;

    private String mPublishedAt;

    private String mCaption;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }


    public YoutubeVideo(String ID, String Title, String Description) {
        this.mID = ID;
        this.mTitle = Title;
        this.mDescription = Description;
    }

    public static YoutubeVideo fromModel(TutorialVideo vid){
        YoutubeVideo newInstance=new YoutubeVideo(vid.key,vid.name,vid.description);
        newInstance.setDefaultThumb(new YoutubeThumbnail(vid.defaultThumbnail, YoutubeUtils.DEFAULT_WIDTH, YoutubeUtils.DEFAULT_HEIGHT));
        newInstance.setMediumThumb(new YoutubeThumbnail(vid.mediumThumbnail, YoutubeUtils.MEDIUM_WIDTH, YoutubeUtils.MEDIUM_HEIGHT));
        newInstance.setHighThumb(new YoutubeThumbnail(vid.highThumbnail, YoutubeUtils.HIGH_WIDTH, YoutubeUtils.HIGH_HEIGHT));
        newInstance.setDuration(vid.duration);
        newInstance.setCaption(vid.caption);
        newInstance.setPublishedAt(vid.publishedAt);
        return newInstance;
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
