package fr.aqamad.tutoyoyo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;

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
}
