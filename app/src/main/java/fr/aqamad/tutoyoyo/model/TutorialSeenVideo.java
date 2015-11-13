package fr.aqamad.tutoyoyo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by Gregoire on 09/10/2015.
 * stores videos seen by the user
 */
@Table(name="SeenVideos")
public class TutorialSeenVideo extends Model {

    @Column(name = "Key")
    public String key;

    @Column(name = "lastViewed")
    public Date lastViewed;

    @Column(name = "timesViewed")
    public int timesViewed;

    public static TutorialSeenVideo getByKey(String keyId) {
        return new Select()
                .from(TutorialSeenVideo.class)
                .where("Key = ?",keyId)
                .orderBy("lastViewed Desc")
                .executeSingle();
    }


    public static void seenThisVideo(String keyId) {
        TutorialSeenVideo tsv=new Select()
                .from(TutorialSeenVideo.class)
                .where("Key = ?", keyId)
                .executeSingle();
        if (tsv!=null){
            // update
            tsv.timesViewed++;
            tsv.lastViewed=new Date();
            tsv.save();
        } else {
            //insert
            tsv=new TutorialSeenVideo();
            tsv.key=keyId;
            tsv.timesViewed=1;
            tsv.lastViewed=new Date();
            tsv.save();
        }
    }

    public static void unseeThisVideo(String keyId) {
        new Delete().from(TutorialSeenVideo.class).where("Key = ?", keyId).execute();
    }

    public static void markSeen(String mVideoID, boolean state) {
        if (state) {
            //mark as seen
            seenThisVideo(mVideoID);
        } else {
            //try to delete
            unseeThisVideo(mVideoID);
        }
    }

    public static List<TutorialSeenVideo> getAll() {
        return new Select()
                .from(TutorialSeenVideo.class)
                .execute();
    }

}
