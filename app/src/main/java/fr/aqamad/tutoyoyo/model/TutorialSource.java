package fr.aqamad.tutoyoyo.model;

import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 09/10/2015.
 */
@Table(name="Sources")
public class TutorialSource extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Key")
    public String key;

    @Column(name = "Description")
    public String description;

    @Column(name = "LastRefreshed")
    public Date lastRefreshed;

    public List<TutorialPlaylist> channels() {
        return getMany(TutorialPlaylist.class, "Source");
    }

    public static List<TutorialSource> getAll() {
        return new Select()
                .from(TutorialSource.class)
                .orderBy("Name ASC")
                .execute();
    }

    public static void clearDB() {
        clearViewed();
        new Delete().from(TutorialVideo.class).execute();
        new Delete().from(TutorialPlaylist.class).execute();
        new Delete().from(TutorialSource.class).execute();
    }

    public static void clearViewed() {
        new Delete().from(TutorialSeenVideo.class).execute();
    }
    public static void clearCache(Context app) {
        new Delete().from(TutorialVideo.class).where("Channel in (select Id from Channels where Source in (select Id from Sources where Key <> '" + app.getString(R.string.localChannelKey) + "'))").execute();
        new Delete().from(TutorialPlaylist.class).where("Source in (select Id from Sources where Key <> '" + app.getString(R.string.localChannelKey) + "')").execute();
        new Delete().from(TutorialSource.class).where("Key <> '" + app.getString(R.string.localChannelKey) + "'").execute();
    }


    public static void rebuildDB(Context app){
        clearDB();
        initializeData(app);
    }

    public static void initializeDB(Context app) {
        Configuration dbConfiguration = new Configuration.Builder( app ).setDatabaseName("YoyoTuts.db").create();
        ActiveAndroid.initialize(dbConfiguration);
        //
        Log.d("initDB", "Initialise DB called");
        initializeData(app);

    }

    private static void initializeData(Context app) {
        //test if already present :
        List<TutorialSource> sources =TutorialSource.getAll();
        if (sources.size()==0) {
            //Create our tutorial sources
            //First is the "My tuts" source
            TutorialSource myTuts = new TutorialSource();
            myTuts.name = app.getString(R.string.myTutsTitle);
            myTuts.description = app.getString(R.string.myTutsDesc);
            myTuts.key = app.getString(R.string.localChannelKey);
            myTuts.lastRefreshed = new Date();
            myTuts.save();
            //create channels
            TutorialPlaylist tmpChannel = new TutorialPlaylist();
            tmpChannel.source = myTuts;
            tmpChannel.name = app.getString(R.string.favorites);
            tmpChannel.key = app.getString(R.string.localFavoritesKey);
            tmpChannel.description = app.getString(R.string.favoritesDesc);
            tmpChannel.mediumThumbnail = app.getString(R.string.favoritesThumb);
            tmpChannel.defaultThumbnail = tmpChannel.mediumThumbnail;
            tmpChannel.highThumbnail = tmpChannel.mediumThumbnail;
            tmpChannel.save();
            //create channels
            tmpChannel = new TutorialPlaylist();
            tmpChannel.source = myTuts;
            tmpChannel.name = app.getString(R.string.watchLater);
            tmpChannel.key = app.getString(R.string.localLaterKey);
            tmpChannel.description = app.getString(R.string.watchLaterDesc);
            tmpChannel.mediumThumbnail = app.getString(R.string.watchLaterThumb);
            tmpChannel.defaultThumbnail = tmpChannel.mediumThumbnail;
            tmpChannel.highThumbnail = tmpChannel.mediumThumbnail;
            tmpChannel.save();
            //create channels
            tmpChannel = new TutorialPlaylist();
            tmpChannel.source = myTuts;
            tmpChannel.name = app.getString(R.string.shareable);
            tmpChannel.key = app.getString(R.string.localSocialKey);
            tmpChannel.description = app.getString(R.string.shareableDesc);
            tmpChannel.mediumThumbnail = app.getString(R.string.shareableThumb);
            tmpChannel.defaultThumbnail = tmpChannel.mediumThumbnail;
            tmpChannel.highThumbnail = tmpChannel.mediumThumbnail;
            tmpChannel.save();
        }
    }

    public static TutorialSource getByName(String channelId) {
        return new Select()
                .from(TutorialSource.class)
                .where("Name = ?",channelId)
                .orderBy("Name ASC")
                .executeSingle();
    }

    public static TutorialSource getByKey(String keyId) {
        return new Select()
                .from(TutorialSource.class)
                .where("Key = ?",keyId)
                .orderBy("Name ASC")
                .executeSingle();
    }
}
