package fr.aqamad.youtube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class YoutubeChannel implements Serializable{

    private String mID;

    private String mTitle;

    private String mDescription;

    private List<YoutubePlaylist> mPlaylists;


    public YoutubeChannel(String ID,String Title,String Description) {
        this.mID = ID;
        this.mTitle = Title;
        this.mDescription = Description;
        mPlaylists=new ArrayList<YoutubePlaylist>();
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

    public List<YoutubePlaylist> getPlaylists() {
        return mPlaylists;
    }

    public void setPlaylists(List<YoutubePlaylist> mPlaylists) {
        this.mPlaylists = mPlaylists;
    }

    public YoutubePlaylist getPlaylistFromYoutubeID(String youtubeID){
        Iterator<YoutubePlaylist> iter= mPlaylists.iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            //pikotaku a une playlist en trop, on ne conserve que celles que l'on connait
            if(pl.getID().equals(youtubeID)){
                return pl;
            }
        }
        return null;
    }
    public YoutubePlaylist findByKey(String key){
        for (YoutubePlaylist pls :
                getPlaylists()) {
            if (pls.getID().equals(key)) {
                return pls;
            }
        }
        return null;
    }
}
