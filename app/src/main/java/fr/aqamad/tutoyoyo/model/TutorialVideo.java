package fr.aqamad.tutoyoyo.model;

import android.database.Cursor;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Gregoire on 09/10/2015.
 */
@Table(name="Videos")
public class TutorialVideo extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Description")
    public String description;

    @Column(name = "Key")
    public String key;

    @Column(name = "Channel")
    public TutorialPlaylist channel;

    @Column(name = "MediumThumbnail")
    public String mediumThumbnail;

    @Column(name = "HighThumbnail")
    public String highThumbnail;

    @Column(name = "DefaultThumbnail")
    public String defaultThumbnail;

    @Column(name = "Duration")
    public String duration;

    @Column(name = "PublishedAt")
    public String publishedAt;

    @Column(name = "Caption")
    public String caption;

    public static List<TutorialVideo> getByKey(String keyId) {
        return new Select()
                .from(TutorialVideo.class)
                .where("Key = ?",keyId)
                .orderBy("Name ASC")
                .execute();
    }

    public static int countAll() {
        Cursor c = ActiveAndroid.getDatabase().rawQuery("SELECT COUNT(*) as total FROM " + new TutorialVideo().getTableName(), null);
        c.moveToFirst();
        int total = c.getInt(c.getColumnIndex("total"));
        c.close();
        return total;
    }

    public static TutorialVideo getRandom() {
        return new Select().from(TutorialVideo.class).orderBy("RANDOM()").executeSingle();
    }

    private String getTableName() {
        return "Videos";
    }
}
