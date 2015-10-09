package fr.aqamad.tutoyoyo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Gregoire on 09/10/2015.
 */
@Table(name="Videos")
public class TutorialVideo extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Description")
    public String description;

    @Column(name = "YoutubeId")
    public String youtubeId;

    @Column(name = "Channel")
    public TutorialChannel channel;

    @Column(name = "MediumThumbnail")
    public String mediumThumbnail;

    @Column(name = "HighThumbnail")
    public String highThumbnail;

    @Column(name = "DefaultThumbnail")
    public String defaultThumbnail;
}
