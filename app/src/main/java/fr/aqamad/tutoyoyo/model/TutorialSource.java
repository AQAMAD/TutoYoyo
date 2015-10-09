package fr.aqamad.tutoyoyo.model;

import android.app.Application;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by Gregoire on 09/10/2015.
 */
@Table(name="Sources")
public class TutorialSource extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Description")
    public String description;

    @Column(name = "LastRefreshed")
    public Date lastRefreshed;

    public List<TutorialChannel> channels() {
        return getMany(TutorialChannel.class, "Source");
    }

    public static List<TutorialSource> getAll() {
        return new Select()
                .from(TutorialSource.class)
                .orderBy("Name ASC")
                .execute();
    }

    public static void initializeDB(Application app) {
        Configuration dbConfiguration = new Configuration.Builder( app ).setDatabaseName("TutoYoyo.db").create();
        ActiveAndroid.initialize(dbConfiguration);
        //
        Log.d("initDB", "Initialise DB called");
        //Create our tutorial sources
        //First is the "My tuts" source
        TutorialSource myTuts=new TutorialSource();
        myTuts.name="My Tutorials";
        myTuts.description="Add tutorials to these playlists so you can remember them and sync them.";
        myTuts.lastRefreshed=new Date();
        myTuts.save();
        //create channels
        TutorialChannel tmpChannel=new TutorialChannel();
        tmpChannel.source=myTuts;
        tmpChannel.name="Favorites";
        tmpChannel.description="Place your favorites tuts here";
        tmpChannel.mediumThumbnail="http://farm4.static.flickr.com/3269/5837141773_c0d31d8424_o.jpg";
        tmpChannel.defaultThumbnail="http://farm4.static.flickr.com/3269/5837141773_c0d31d8424_o.jpg";
        tmpChannel.highThumbnail="http://farm4.static.flickr.com/3269/5837141773_c0d31d8424_o.jpg";
        tmpChannel.save();
        //create channels
        tmpChannel=new TutorialChannel();
        tmpChannel.source=myTuts;
        tmpChannel.name="Watch Later";
        tmpChannel.description="Place here tutorials to watch later";
        tmpChannel.mediumThumbnail="http://www.eighties.fr/images/stories/Phototheque/divers/Yoyo%20figures%20techniques%20roll%20in%20coca%20cola%20annees%2080%20grand%20format.jpg";
        tmpChannel.defaultThumbnail="http://www.eighties.fr/images/stories/Phototheque/divers/Yoyo%20figures%20techniques%20roll%20in%20coca%20cola%20annees%2080%20grand%20format.jpg";
        tmpChannel.highThumbnail="http://www.eighties.fr/images/stories/Phototheque/divers/Yoyo%20figures%20techniques%20roll%20in%20coca%20cola%20annees%2080%20grand%20format.jpg";
        tmpChannel.save();
        //create channels
        tmpChannel=new TutorialChannel();
        tmpChannel.source=myTuts;
        tmpChannel.name="Shareable";
        tmpChannel.description="Place here tutorials to share with your friends";
        tmpChannel.mediumThumbnail="http://www.monquotidien.fr/media/Images/Video-yoyo-zach.jpg";
        tmpChannel.defaultThumbnail="http://www.monquotidien.fr/media/Images/Video-yoyo-zach.jpg";
        tmpChannel.highThumbnail="http://www.monquotidien.fr/media/Images/Video-yoyo-zach.jpg";
        tmpChannel.save();
        //now create the Yoyoblast channel
        myTuts=new TutorialSource();
        myTuts.name="Yoyoblast(fr)";
        myTuts.description="Yoyo store and Tutorials (Apprendre à faire du yoyo).";
        myTuts.lastRefreshed=null;
        myTuts.save();
        //now create the Yoyoexpert channel
        myTuts=new TutorialSource();
        myTuts.name="YoyoExpert";
        myTuts.description="Make the simple amazing !";
        myTuts.lastRefreshed=null;
        myTuts.save();
        //now create the various channel
        myTuts=new TutorialSource();
        myTuts.name="Other sources";
        myTuts.description="Various sources from all over the world";
        myTuts.lastRefreshed=null;
        myTuts.save();


    }

    public static TutorialSource getByName(String channelId) {
        return new Select()
                .from(TutorialSource.class)
                .where("Name = ?",channelId)
                .orderBy("Name ASC")
                .executeSingle();
    }
}
