package fr.aqamad.tutoyoyo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.aqamad.commons.youtube.YoutubeUtils;

/**
 * Created by Gregoire on 09/10/2015.
 */
@Table(name="Channels")
public class TutorialPlaylist extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Description")
    public String description;

    @Column(name = "Key")
    public String key;

    @Column(name = "MediumThumbnail")
    public String mediumThumbnail;

    @Column(name = "HighThumbnail")
    public String highThumbnail;

    @Column(name = "DefaultThumbnail")
    public String defaultThumbnail;

    @Column(name = "Source")
    public TutorialSource source;

    @Column(name = "PublishedAt")
    public String publishedAt;

    @Column(name = "FetchedAt")
    public Date fetchedAt;

    public static void clearVideos(String keyId){
        TutorialPlaylist pl=getByKey(keyId);
        new Delete().from(TutorialVideo.class).where("Channel = ?", pl.getId()).execute();
    }

    public static TutorialPlaylist getByKey(String keyId) {
        return new Select()
                .from(TutorialPlaylist.class)
                .where("Key = ?",keyId)
                .orderBy("Name ASC")
                .executeSingle();
    }

    public static List<TutorialPlaylist> getOlderThan(Date refreshDate) {
        return new Select()
                .from(TutorialPlaylist.class)
                .where("FetchedAt is not null and FetchedAt < ?",refreshDate.getTime())
                .orderBy("Name ASC")
                .execute();
    }

    public static boolean isSeen(String mPlayListID) {
        TutorialVideo vid = new Select()
                .from(TutorialVideo.class)
                .where("Key not in (select Key from SeenVideos) and Channel=(select id from Channels where Key=?)", mPlayListID)
                .orderBy("id")
                .executeSingle();
        return (vid == null);
    }

    public static void markSeen(String mPlayListID, boolean state) {
        TutorialPlaylist list = getByKey(mPlayListID);
        for (TutorialVideo vid :
                list.videos()
                ) {
            if (state) {
                //mark as seen
                TutorialSeenVideo.seenThisVideo(vid.key);
            } else {
                //try to delete
                TutorialSeenVideo.unseeThisVideo(vid.key);
            }
        }
    }

    public static boolean isOther(String mPlayListID, String mOtherChannel) {
        TutorialVideo vid = new Select()
                .from(TutorialVideo.class)
                .where("Key not in (select Key from Videos where Channel=(select id from Channels where Key=?)) and Channel=(select id from Channels where Key=?)", mOtherChannel, mPlayListID)
                .orderBy("id")
                .executeSingle();
        return (vid == null);
    }

    public List<TutorialVideo> videos() {
        return getMany(TutorialVideo.class, "Channel");
    }

    public List<String> getVideoUrls(){
        //build array
        ArrayList<String> videoUrls = new ArrayList<String>();
        for (TutorialVideo vid :
                videos()) {
            videoUrls.add(YoutubeUtils.getVideoPlayUrl(vid.key));
        }
        return videoUrls;
    }

    public void markSeen(boolean state) {
        TutorialPlaylist.markSeen(this.key, state);
    }

    public void addToLocal(String localChannelKey) {
        //all videos go to the local video
        for (TutorialVideo vid :
                videos()) {
            vid.addToLocal(localChannelKey);
        }
    }

    public boolean hasVideo(String key) {
        for (TutorialVideo vid :
                videos()) {
            if (vid.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public void removeFromLocal(String localChannelKey) {
        //all videos go to the local video
        for (TutorialVideo vid :
                videos()) {
            vid.removeLocal(localChannelKey);
        }
    }


}
