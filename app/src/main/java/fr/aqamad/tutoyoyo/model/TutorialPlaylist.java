package fr.aqamad.tutoyoyo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

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

    public List<TutorialVideo> videos() {
        return getMany(TutorialVideo.class, "Channel");
    }

    public static TutorialPlaylist getByKey(String keyId) {
        return new Select()
                .from(TutorialPlaylist.class)
                .where("Key = ?",keyId)
                .orderBy("Name ASC")
                .executeSingle();
    }

}
