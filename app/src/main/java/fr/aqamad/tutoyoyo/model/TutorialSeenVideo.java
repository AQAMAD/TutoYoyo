package fr.aqamad.tutoyoyo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Gregoire on 09/10/2015.
 * stores videos seen by the user
 */
@Table(name="SeenVideos")
public class TutorialSeenVideo extends Model {

    @Column(name = "Key")
    public String key;

    public static TutorialSeenVideo getByKey(String keyId) {
        return new Select()
                .from(TutorialSeenVideo.class)
                .where("Key = ?",keyId)
                .orderBy("Name ASC")
                .executeSingle();
    }
}
