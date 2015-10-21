package fr.aqamad.tutoyoyo.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.utils.Assets;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeThumbnail;
import fr.aqamad.youtube.YoutubeUtils;
import fr.aqamad.youtube.YoutubeVideo;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class ModelConverter {

    private static final String BHOP_MEDIUM_PLAYLIST = "PL8C046415EB38C4F4";
    private static final String BHOP_BEGINNER_PLAYLIST = "PL31896775C716B8B3";
    private static final String YYT_TRICK_VIDEOS_PLAYLIST = "PLwMQ2twUtKwLoyRtFpECxUSQ6hoRT2j7a";
    public static final String CABIN_TUTORIALS_PLAYLIST="PLVLLF_sWPwMxR8TQv_FpKZC_W6knxdiln";
    private static final int CLYW_PAGE_SIZE=25;


    public static void cachePlaylist(YoutubePlaylist playlist,String mChannelID) {
        Log.d("PACP", "Called ModelConverter cachePlaylist for " + playlist.getID() + " aka " + playlist.getTitle());
        TutorialPlaylist tutorialPlaylist = TutorialPlaylist.getByKey(playlist.getID());
        if (tutorialPlaylist == null) {
            Log.d("PACP","Playlist not found, caching");
            YoutubeUtils.logPlaylist(playlist);
            //loading source
            TutorialSource source = TutorialSource.getByKey(mChannelID);
            //we did a lookup, store in the database to avoid reparsing json
            tutorialPlaylist = new TutorialPlaylist();

            tutorialPlaylist.name = playlist.getTitle();
            tutorialPlaylist.description = playlist.getDescription();
            tutorialPlaylist.fetchedAt = new Date();
            tutorialPlaylist.publishedAt=playlist.getPublishedAt();
            if (playlist.getDefaultThumb()!=null){
                tutorialPlaylist.defaultThumbnail=playlist.getDefaultThumb().getUrl().toString();
            }
            if (playlist.getMediumThumb()!=null){
                tutorialPlaylist.mediumThumbnail=playlist.getMediumThumb().getUrl().toString();
            }
            tutorialPlaylist.highThumbnail=playlist.getHighThumb().getUrl().toString();
            tutorialPlaylist.source= source;///problem, how to get the source...
            tutorialPlaylist.key = playlist.getID();
            tutorialPlaylist.save();
            Log.d("PACP", "Playlist cached");
            //now iterate to store playlists
            Log.d("PACP", "Caching videos");
            //if the playlists have videos, we store them too
            for (YoutubeVideo vid :
                    playlist.getVideos()  ) {
                //repeat the deal
                TutorialVideo vi = new TutorialVideo();
                Log.d("PACP", "Caching video " + vid.getID() + " aka " + vid.getTitle());
                vi.channel = tutorialPlaylist;
                vi.key = vid.getID();
                vi.name = vid.getTitle();
                vi.description = vid.getDescription();
                vi.duration=vid.getDuration();
                vi.publishedAt = vid.getPublishedAt();
                vi.defaultThumbnail = vid.getDefaultThumb().getUrl().toString();
                vi.mediumThumbnail = vid.getMediumThumb().getUrl().toString();
                vi.highThumbnail = vid.getHighThumb().getUrl().toString();
                vi.caption=vid.getCaption();
                vi.save();
                Log.d("PACP", "Video " + vi.name + " cached");
            }
        }else{
            if (tutorialPlaylist.videos().size()==0){
                Log.d("PACP", "remote playlist exists but empty, cache content only");
                for (YoutubeVideo vid :
                        playlist.getVideos()  ) {
                    //repeat the deal
                    TutorialVideo vi = new TutorialVideo();
                    Log.d("PACP", "Caching video " + vid.getID() + " aka " + vid.getTitle());
                    vi.channel = tutorialPlaylist;
                    vi.key = vid.getID();
                    vi.name = vid.getTitle();
                    vi.description = vid.getDescription();
                    vi.duration=vid.getDuration();
                    vi.publishedAt = vid.getPublishedAt();
                    vi.defaultThumbnail = vid.getDefaultThumb().getUrl();
                    vi.mediumThumbnail = vid.getMediumThumb().getUrl();
                    vi.highThumbnail = vid.getHighThumb().getUrl();
                    vi.caption=vid.getCaption();
                    vi.save();
                    Log.d("PACP", "Video " + vi.name + " cached");
                }
            }
            Log.d("PACP", "Playlist " + tutorialPlaylist.name + " found");
        }
    }

    /**
     * TODO : cacheChannel
     */
    public static void cacheChannel(YoutubeChannel channel) {
        Log.d("SFCC", "Called SourceFragment cacheChannel for " + channel.getID() + " aka " + channel.getTitle());
        TutorialSource source = TutorialSource.getByKey(channel.getID());
        if (source == null) {
            Log.d("SFCC","Channel not found, caching");
            //we did a lookup, store in the database to avoid reparsing json
            source = new TutorialSource();
            source.name = channel.getTitle();
            source.description = channel.getDescription();
            source.lastRefreshed = new Date();
            source.key = channel.getID();
            source.save();
            Log.d("SFCC", "Channel cached");
            //now iterate to store playlists
            Log.d("SFCC", "Caching playlists");
            for (YoutubePlaylist pl :
                    channel.getPlaylists()) {
                TutorialPlaylist ch = new TutorialPlaylist();
                Log.d("SFCC", "Caching playlist" + pl.getID() + " aka " + pl.getTitle());
                ch.source = source;
                ch.key = pl.getID();
                ch.name = pl.getTitle();
                ch.description = pl.getDescription();
                ch.fetchedAt = new Date();
                ch.publishedAt = pl.getPublishedAt();
                ch.defaultThumbnail = pl.getDefaultThumb().getUrl();
                ch.mediumThumbnail = pl.getMediumThumb().getUrl();
                ch.highThumbnail = pl.getHighThumb().getUrl();
                ch.save();
                Log.d("SFCC", "Playlist " + ch.name + " cached");
                Log.d("SFCC", "Caching videos");
                //if the playlists have videos, we store them too
                for (YoutubeVideo vid :
                        pl.getVideos()  ) {
                    //repeat the deal
                    TutorialVideo vi = new TutorialVideo();
                    Log.d("SFCC", "Caching video " + vid.getID() + " aka " + vid.getTitle());
                    vi.channel = ch;
                    vi.key = vid.getID();
                    vi.name = vid.getTitle();
                    vi.description = vid.getDescription();
                    vi.duration=vid.getDuration();
                    vi.publishedAt = vid.getPublishedAt();
                    vi.defaultThumbnail = vid.getDefaultThumb().getUrl();
                    vi.mediumThumbnail = vid.getMediumThumb().getUrl();
                    vi.highThumbnail = vid.getHighThumb().getUrl();
                    vi.caption=vid.getCaption();
                    vi.save();
                    Log.d("SFCC", "Video " + vi.name + " cached");
                }
            }

        }else{
            Log.d("SFCC", "Source " + source.name + " found");
        }
    }


    /**
     * Here is how you load a channel
     */

    public static YoutubeChannel loadChannel(Activity mAct, String channelId, String expandPlaylist, String apiKey){

        //will return null if not found
        TutorialSource source=TutorialSource.getByKey(channelId);
        YoutubeChannel channel=null;
        if (source==null){
            Log.d("GCT","not found in database");
            //try to fetch from assets
            //will return null if not found
            String jsonString= Assets.loadFileFromAsset(mAct, channelId + ".json");
            if (jsonString==null) {
                Log.d("GCT","not found in assets");
                //fetch from youtube
                // Get a httpclient to talk to the internet
                jsonString = YoutubeUtils.getChannelFromApi(channelId,apiKey);
            }
            channel= YoutubeUtils.channelFromJson(jsonString);
        } else {
            Log.d("GCT","found in database");
            channel = new YoutubeChannel(source.key, source.name, source.description);
            for (int i = 0; i < source.channels().size(); i++) {
                TutorialPlaylist playlist = source.channels().get(i);
                YoutubePlaylist tPlayList = new YoutubePlaylist(playlist.key, playlist.name, playlist.description);
                tPlayList.setID(playlist.key);
                tPlayList.setDefaultThumb(new YoutubeThumbnail(playlist.defaultThumbnail, YoutubeUtils.DEFAULT_WIDTH, YoutubeUtils.DEFAULT_HEIGHT));
                tPlayList.setMediumThumb(new YoutubeThumbnail(playlist.mediumThumbnail, YoutubeUtils.MEDIUM_WIDTH, YoutubeUtils.MEDIUM_HEIGHT));
                tPlayList.setHighThumb(new YoutubeThumbnail(playlist.highThumbnail, YoutubeUtils.HIGH_WIDTH, YoutubeUtils.HIGH_HEIGHT));
                //check if we can load videos at the same time
                for (TutorialVideo vid :
                        playlist.videos()) {
                    YoutubeVideo tVideo=YoutubeVideo.fromModel(vid);
                    tPlayList.getVideos().add(tVideo);
                }
                channel.getPlaylists().add(tPlayList);
            }
        }
        //part 2, do we have a playlist to expand ?
        //locate the playlist to fill
        if (expandPlaylist!=null && !expandPlaylist.equals("")){
            Log.d("GCT","asked for expandplaylist");
            YoutubePlaylist playlist=channel.getPlaylistFromYoutubeID(expandPlaylist);
            if (playlist!=null){
                Log.d("GCT","playlist not found in channel");
                TutorialPlaylist tchannel= TutorialPlaylist.getByKey(expandPlaylist);
                YoutubePlaylist temporary=playlist.clone();
                if (tchannel!=null){
                    Log.d("GCT","found in database");
                    //iterate and add videos
                    for (int i = 0; i < tchannel.videos().size(); i++) {
                        TutorialVideo video=tchannel.videos().get(i);
                        YoutubeVideo tVideo=YoutubeVideo.fromModel(video);
                        temporary.getVideos().add(tVideo);
                    }
                } else {
                    //fill from assets or from youtube
                    String jsonString= Assets.loadFileFromAsset(mAct, expandPlaylist + ".json");
                    if (jsonString==null) {
                        Log.d("GCT","not found in assets");
                        //fetch from youtube
                        // Get a httpclient to talk to the internet
                        jsonString = YoutubeUtils.getPlaylistFromApi(expandPlaylist, apiKey);
                    }
                    temporary=YoutubeUtils.playlistFromJson(jsonString);
                    Log.d("GCT","enrich with duration");
                    jsonString = YoutubeUtils.getVideosFromApi(temporary.getVideoIds(), apiKey);
                    YoutubeUtils.addDurationsFromJson(temporary,jsonString);

                }
                //fill this playlist from either one of the sources
                //copy videos from temp to playlist
                playlist.getVideos().addAll(temporary.getVideos());
            }
        }

        return channel;
    }

    public static YoutubePlaylist loadPlaylist(Activity localActivity,String playlistID, String apiKey){

        TutorialPlaylist channel= TutorialPlaylist.getByKey(playlistID);
        YoutubePlaylist playlist;
        //complex test here, if channel exists, is not local and no videos
        //if channel exists
        boolean playlistExists= (channel!=null) ;
        boolean playlistMustLoad=!playlistExists;
        if (playlistExists){
            Log.d("GPT","found in db");
            //test if local
            if (playlistID.equals(localActivity.getString(R.string.localSocialKey))){
                Log.d("GPT","local playlist no refetch");
                playlistMustLoad=false;
            }else if (playlistID.equals(localActivity.getString(R.string.localFavoritesKey))){
                Log.d("GPT","local playlist no refetch");
                playlistMustLoad=false;
            }else if (playlistID.equals(localActivity.getString(R.string.localLaterKey))){
                Log.d("GPT","local playlist no refetch");
                playlistMustLoad=false;
            } else if (channel.videos().size()==0){
                Log.d("GPT","remote playlist exists but empty, refetch");
                playlistMustLoad=true;
            }
        }
        //now we know if playlist exists
        if (playlistMustLoad){
            Log.d("GPT","not found in db or needs fetching");
            //try to fetch from assets
            //will return null if not found
            String jsonString= Assets.loadFileFromAsset(localActivity, playlistID + ".json");
            if (jsonString==null) {
                Log.d("GPT","not found in assets");
                //fetch from youtube
                // Get a httpclient to talk to the internet
                jsonString = YoutubeUtils.getPlaylistFromApi(playlistID,apiKey);
            }
            playlist= YoutubeUtils.playlistFromJson(jsonString);
            Log.d("GPT", "enrich with duration");
            jsonString = YoutubeUtils.getVideosFromApi(playlist.getVideoIds(), apiKey);
            YoutubeUtils.addDurationsFromJson(playlist, jsonString);
        } else{
            Log.d("GPT", "found in db : " + channel.name);
            playlist= new YoutubePlaylist(channel.key,channel.name,channel.description);
            //iterate and add videos
            for (int i = 0; i < channel.videos().size(); i++) {
                Log.d("GPT","exploding videos " + i);
                TutorialVideo video=channel.videos().get(i);
                YoutubeVideo tVideo=YoutubeVideo.fromModel(video);
                playlist.getVideos().add(tVideo);
            }
        }

        return playlist;
    }

    public static void cleanPlaylist(YoutubePlaylist playlist) {
        Iterator<YoutubeVideo> iter= playlist.getVideos().iterator();
        while(iter.hasNext()){
            YoutubeVideo vid=iter.next();
            String title=vid.getTitle();
            //get the ressources for cleaning
            title=(title.replace(" - YoyoBlast", ""))
                    .replace("CLYW Cabin Tutorials - ", "")
                    .replace("CLYW Cabin Tutorial - ", "")
                    .replace("Cabin Tutorial - ","")
                    .replace("Learn to Yo-yo ", "")
                    .replace("yoyo-france.net - tuto yoyo - debutant - ", "")
                    .replace("yoyo-france.net - tuto yoyo - intermédiaire - ", "")
                    .replace("yoyo-france.net - tuto yoyo - ", "")
                    .replace("blackhop.com - tuto yoyo intermediaire - ", "")
                    .replace("blackhop.com - tuto intermediaire - ", "")
                    .replace("Yoyo Trick Tutorial - ", "")
                    .replace("Yoyo Trick Tutorial-", "")
                    .replace("Yoyo trick tutorial-", "")
                    .replace("yoyo tutorial", "")
                    .replace("Yoyo Tutorial - ", "")
                    .replace("Yoyo Trick Tutorial ", "")
                    .replace("1a Tutorial - ", "")
                    .replace("1a Tutorial ", "")
                    .replace("1a tutorial ", "")
                    .replace("4a Tutorial - ", "4a - ");
            //prettify first letter
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            vid.setTitle(title);
        }
    }

    public static void prepareChannel(YoutubeChannel channel,Context ctx) {
        //todo : par défaut, on trie les playlists
        Log.d("GPT","Preparing channel " + channel.getID());
        //condition sur l'ID et les constantes que l'on connait
        if (channel.getID().equals(ctx.getString(R.string.YOYOEXPERT_CHANNEL))){
            //non-standard
            Log.d("GPT","Specific YoyoExpert");
            prepareYYEChannel(channel);
        } else if (channel.getID().equals(ctx.getString(R.string.CLYW_CHANNEL))){
            //standard
            prepareClywChannel(channel);
        } else if (channel.getID().equals(ctx.getString(R.string.YOYOTHROWER_CHANNEL))){
            //standard
            prepareYYTChannel(channel);
        } else if (channel.getID().equals(ctx.getString(R.string.BLACKHOP_CHANNEL))){
            //standard
            prepareBlackhopChannel(channel);
        } else {
            //default
            //Sorting by title
            Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
                @Override
                public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                    return pl1.getTitle().compareTo(pl2.getTitle());
                }
            });
        }
    }

    public static void prepareYYEChannel(YoutubeChannel channel) {

        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            Log.d("PYYE","YoyoExpert name = " + pl.getTitle());
            //clean the names
            pl.setTitle(pl.getTitle().replace("Expert Village Yo Yo Tricks: ",""));
            pl.setTitle(pl.getTitle().replace("Expert Village Yo Yo tricks: ",""));
            pl.setTitle(pl.getTitle().replace("Expert village Yo Yo Tricks: ",""));
            pl.setTitle(pl.getTitle().replace("Expert Village: ",""));
            Log.d("PYYE", "YoyoExpert new name = " + pl.getTitle());
            //pikotaku a une playlist en trop, on ne conserve que celles que l'on connait
            if(pl.getID().equals("LLjt9-TsmzQl1P9Vpt8sHYCA")){
                iter.remove();
            }
            if(pl.getID().equals("FLjt9-TsmzQl1P9Vpt8sHYCA")){
                iter.remove();
            }

        }

        //Sorting by custom order
        //playlist order
        String[] plsOrder={"YoyoExpert 2015 Tutorials","Yo-Yo Techniques for the Beginner","Basic","Yo Yo Maintenance",
                "Intermediate","Advanced (Part 1)","Advanced (Part 2)",
                "Expert (Part 1)","Expert (Part 2)","Master",
                "Looping","Offstring","Counterweight",
                "Double hand looping (2A)"};
        final List<String> plsList= Arrays.asList(plsOrder);


        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                int pl1Index=plsList.indexOf(pl1.getTitle());
                int pl2Index=plsList.indexOf(pl2.getTitle());
                return pl1Index-pl2Index;
            }
        });


    }

    public static void prepareClywChannel(YoutubeChannel channel) {
        //reparse the cabin tutorials channel to a more manageable size
        YoutubePlaylist playlist=channel.getPlaylistFromYoutubeID(CABIN_TUTORIALS_PLAYLIST);
        //test first if we have the famous playlist, if not then we will display nothing due to following <code></code>
        if (playlist==null){
            //not found, either error or already parsed
            //so nothing to do and exit there
        }else{
            playlist=null;
            Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
            while(iter.hasNext()){
                YoutubePlaylist pl=iter.next();
                //clean the names
                //CLYW n'a qu'une playlist qui nous intéresse
                if(!pl.getID().equals(CABIN_TUTORIALS_PLAYLIST)){
                    iter.remove();
                }else{
                    playlist=pl;
                }
            }
            //do we have a playlist, if yes, then split it
            if (playlist!=null){
                //remove it from the channel before continuing
                channel.getPlaylists().remove(0);
                //get item count
                //more precise from playlist.getVids
                //int count=playlist.getNumberToFetch();
                int count=playlist.getVideos().size();
                int pages=count / CLYW_PAGE_SIZE;
                int remainder=count-(pages*CLYW_PAGE_SIZE);
                if (remainder!=0){
                    pages++;
                }
                //iterate over number of pages and create virtual playlists
                for (int i = 1; i <= pages ; i++) {
                    YoutubePlaylist tpl=playlist.clone();
                    //must create unique ID
                    tpl.setID(tpl.getID()+"(" + i + ")");
                    tpl.setTitle(tpl.getTitle() + " (" + i + ")");
                    tpl.setPageNumber(i);
                    if (i==pages){
                        tpl.setNumberToFetch(remainder);
                    }else{
                        tpl.setNumberToFetch(CLYW_PAGE_SIZE);
                    }
                    int start=((i-1)*CLYW_PAGE_SIZE);
                    int end=(i*CLYW_PAGE_SIZE)-1;
                    if (end>=count){
                        end=count;
                    }
                    Log.d("CLYWF.PC", "sublist " + start + "/" + end);
                    List<YoutubeVideo> lst=playlist.getVideos().subList(start, end);
                    for (int j = 0; j < lst.size(); j++) {
                        tpl.getVideos().add(lst.get(j));
                    }
                    //playlist thumbs should point to one of the vids, so choose at random
                    Random rand = new Random();
                    int  n = rand.nextInt(tpl.getVideos().size()-1);
                    YoutubeVideo rndV=tpl.getVideos().get(n);
                    tpl.setDefaultThumb(rndV.getDefaultThumb());
                    tpl.setStandardThumb(rndV.getStandardThumb());
                    tpl.setMediumThumb(rndV.getMediumThumb());
                    tpl.setHighThumb(rndV.getHighThumb());
                    channel.getPlaylists().add(tpl);
                }
            }

            //Sorting by title
            Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
                @Override
                public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                    return pl1.getTitle().compareTo(pl2.getTitle());
                }
            });

        }

    }



    public static void prepareBlackhopChannel(YoutubeChannel channel) {
        //exclude the "Trick videos" playlist
        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            if(!(pl.getID().equals(BHOP_BEGINNER_PLAYLIST) || pl.getID().equals(BHOP_MEDIUM_PLAYLIST)) ){
                iter.remove();
            }
        }

        //Sorting by custom order
        //playlist order
        String[] plsOrder={"Tutos yoyo débutant","Tutos yoyo intermédiaire"};
        final List<String> plsList= Arrays.asList(plsOrder);


        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                int pl1Index = plsList.indexOf(pl1.getTitle());
                int pl2Index = plsList.indexOf(pl2.getTitle());
                return pl1Index - pl2Index;
            }
        });

    }

    public static void prepareYYTChannel(YoutubeChannel channel ) {
        //exclude the "Trick videos" playlist
        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            if(pl.getID().equals(YYT_TRICK_VIDEOS_PLAYLIST)){
                iter.remove();
            }
        }

        //Sorting by custom order
        //playlist order
        String[] plsOrder={"Beginner Tricks","Sport Ladder","Quick and Easy tricks",
                "Mounts","Binds, Snap starts and string tension","Snap Start Tutorial Series",
                "Difficulty 1","Difficulty 2","Difficulty 3",
                "Difficulty 4","Difficulty 5","4a/Offstring",
                "Repeaters","How to Build a Combo","Pun Tutorials"};
        final List<String> plsList= Arrays.asList(plsOrder);


        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                int pl1Index = plsList.indexOf(pl1.getTitle());
                int pl2Index = plsList.indexOf(pl2.getTitle());
                return pl1Index - pl2Index;
            }
        });

    }


    public static String getExpandPlayList(String channelId, Context ctx) {
        if (channelId.equals(ctx.getString(R.string.CLYW_CHANNEL))){
            return CABIN_TUTORIALS_PLAYLIST;
        }else{
            return null;
        }
    }
}
