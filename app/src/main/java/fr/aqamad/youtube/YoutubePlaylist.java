package fr.aqamad.youtube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class YoutubePlaylist implements Serializable {

    private YoutubeThumbnail mDefaultThumb;
    private YoutubeThumbnail mStandardThumb;
    private YoutubeThumbnail mMediumThumb;
    private YoutubeThumbnail mHighThumb;
    private String mID;

    private String mTitle;

    private String mDescription;

    public int getNumberToFetch() {
        return mNumberToFetch;
    }

    public void setNumberToFetch(int mNumberToFetch) {
        this.mNumberToFetch = mNumberToFetch;
    }

    private int mNumberToFetch=0;

    public int getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(int mPageNumber) {
        this.mPageNumber = mPageNumber;
    }

    private int mPageNumber=0;

    private List<YoutubeVideo> mVideos;

    public YoutubePlaylist(String ID,String Title,String Description) {
        this.mID = ID;
        this.mTitle = Title;
        this.mDescription = Description;
        mVideos=new ArrayList<YoutubeVideo>();
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

    public List<YoutubeVideo> getVideos() {
        return mVideos;
    }

    public void setVideos(List<YoutubeVideo> mVideos) {
        this.mVideos= mVideos;
    }

    public YoutubePlaylist clone(){
        YoutubePlaylist theClone=new YoutubePlaylist(this.mID,this.mTitle,this.mDescription);
        theClone.setDefaultThumb(this.getDefaultThumb());
        theClone.setStandardThumb(this.getStandardThumb());
        theClone.setMediumThumb(this.getMediumThumb());
        theClone.setHighThumb(this.getHighThumb());
        theClone.setNumberToFetch(this.getNumberToFetch());
        //theClone.setVideos(this.getVideos());
        return theClone;
    }


}
