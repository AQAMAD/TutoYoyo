package fr.aqamad.tutoyoyo.model;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import fr.aqamad.commons.youtube.YoutubeChannel;
import fr.aqamad.commons.youtube.YoutubePlaylist;
import fr.aqamad.commons.youtube.YoutubeThumbnail;
import fr.aqamad.commons.youtube.YoutubeUtils;
import fr.aqamad.commons.youtube.YoutubeVideo;
import fr.aqamad.tutoyoyo.Application;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.utils.Assets;
import fr.aqamad.tutoyoyo.utils.FileOperations;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class ModelConverter {

    public static final String exportDirectory = Environment.getExternalStorageDirectory() + "/yoyotuts/exports/";
    public static final String importDirectory = Environment.getExternalStorageDirectory() + "/yoyotuts/imports/";
    public static final String favoritesFile = "favorites.csv";
    public static final String sharedFile = "shared.csv";
    public static final String watchLaterFile = "watchLater.csv";
    public static final String seenFile = "seen.csv";
    public static final String backupFiles = favoritesFile + "," + sharedFile + "," + watchLaterFile + "," + seenFile;
    private static final int PAGE_SIZE =25;

    public static List<YoutubeVideo> fromModel(List<TutorialVideo> models){
        ArrayList<YoutubeVideo> results=new ArrayList<>();
        for (TutorialVideo vid :
                models) {
            YoutubeVideo nVid = ModelConverter.fromModel(vid);
            results.add(nVid);
        }
        return results;
    }

    public static YoutubeVideo fromModel(TutorialVideo vid) {
        YoutubeVideo newInstance = new YoutubeVideo(vid.key, vid.name, vid.description);
        newInstance.setDefaultThumb(new YoutubeThumbnail(vid.defaultThumbnail, YoutubeUtils.DEFAULT_WIDTH, YoutubeUtils.DEFAULT_HEIGHT));
        newInstance.setMediumThumb(new YoutubeThumbnail(vid.mediumThumbnail, YoutubeUtils.MEDIUM_WIDTH, YoutubeUtils.MEDIUM_HEIGHT));
        newInstance.setHighThumb(new YoutubeThumbnail(vid.highThumbnail, YoutubeUtils.HIGH_WIDTH, YoutubeUtils.HIGH_HEIGHT));
        newInstance.setDuration(vid.duration);
        newInstance.setCaption(vid.caption);
        newInstance.setPublishedAt(vid.publishedAt);
        return newInstance;
    }

    public static void cachePlaylist(YoutubePlaylist playlist,String mChannelID) {
        Log.d("PACP", "Called ModelConverter cachePlaylist for " + playlist.getID() + " aka " + playlist.getTitle());
        TutorialPlaylist tutorialPlaylist = TutorialPlaylist.getByKey(playlist.getID());
        if (tutorialPlaylist == null) {
            Log.d("PACP", "Playlist not found, caching");
            //YoutubeUtils.logPlaylist(playlist);
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
     * done : cacheChannel
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

    public static YoutubeChannel loadChannel(Activity mAct, String channelId, String[] expandPlaylists, String apiKey){

        //will return null if not found
        TutorialSource source=TutorialSource.getByKey(channelId);
        YoutubeChannel channel=null;
        if (source==null){
            Log.d("GCT","not found in database");
            //try to fetch from assets
            //will return null if not found
            String jsonString = Assets.loadFileFromAssetFolder(mAct, "channels", channelId + ".json");
            if (jsonString==null) {
                Log.d("GCT","not found in assets");
                //fetch from youtube
                // Get a httpclient to talk to the internet
                jsonString = YoutubeUtils.getChannelFromApi(channelId,apiKey);
            }
            Log.d("GCT", "got json : " + jsonString);
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
                    YoutubeVideo tVideo = ModelConverter.fromModel(vid);
                    tPlayList.getVideos().add(tVideo);
                }
                channel.getPlaylists().add(tPlayList);
            }
        }
        //part 2, do we have a playlist to expand ?
        //locate the playlist to fill
        if (expandPlaylists!=null){
            Log.d("GCT","asked for expandplaylists");
            for (String playlistKey :
                    expandPlaylists) {
                YoutubePlaylist playlist=channel.getPlaylistFromYoutubeID(playlistKey);
                if (playlist!=null){
                    Log.d("GCT","playlist not found in channel");
                    TutorialPlaylist tchannel= TutorialPlaylist.getByKey(playlistKey);
                    YoutubePlaylist temporary=playlist.clone();
                    if (tchannel!=null){
                        Log.d("GCT","found in database");
                        //iterate and add videos
                        for (int i = 0; i < tchannel.videos().size(); i++) {
                            TutorialVideo video=tchannel.videos().get(i);
                            YoutubeVideo tVideo = ModelConverter.fromModel(video);
                            temporary.getVideos().add(tVideo);
                        }
                    } else {
                        //fill from assets or from youtube
                        String jsonString = Assets.loadFileFromAssetFolder(mAct, "channels/playlists", playlistKey + ".json");
                        if (jsonString==null) {
                            Log.d("GCT","not found in assets");
                            //fetch from youtube
                            // Get a httpclient to talk to the internet
                            jsonString = YoutubeUtils.getPlaylistFromApi(playlistKey, apiKey);
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
                //end
            }
        }
        return channel;
    }

    public static boolean savePersonnalData(Context ctx) {
        boolean result = true;
        //here we want to bacxkup the user personnal data
        //check for directory existance
        FileOperations.ensureDirExists(exportDirectory);
        //first, we need the local lists and we back them up
        TutorialSource localSource = TutorialSource.getByKey(ctx.getString(R.string.LOCAL_CHANNEL));
        //the source should exist but we don't need it, we need the playlists
        TutorialPlaylist watchPlaylist = TutorialPlaylist.getByKey(ctx.getString(R.string.LOCAL_LATER_PLAYLIST));
        try {
            File filename = new File(exportDirectory + watchLaterFile);
            FileOutputStream outputStream = new FileOutputStream(filename);
            for (TutorialVideo vid :
                    watchPlaylist.videos()) {
                outputStream.write(vid.key.getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        TutorialPlaylist favoritesPlaylist = TutorialPlaylist.getByKey(ctx.getString(R.string.LOCAL_FAVORITES_PLAYLIST));
        try {
            File filename = new File(exportDirectory + favoritesFile);
            FileOutputStream outputStream = new FileOutputStream(filename);
            for (TutorialVideo vid :
                    favoritesPlaylist.videos()) {
                outputStream.write(vid.key.getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        TutorialPlaylist sharedPlaylist = TutorialPlaylist.getByKey(ctx.getString(R.string.LOCAL_SOCIAL_PLAYLIST));
        try {
            File filename = new File(exportDirectory + sharedFile);
            FileOutputStream outputStream = new FileOutputStream(filename);
            for (TutorialVideo vid :
                    sharedPlaylist.videos()) {
                outputStream.write(vid.key.getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        //next we backup the view status
        List<TutorialSeenVideo> seenVids = TutorialSeenVideo.getAll();
        try {
            File filename = new File(exportDirectory + seenFile);
            FileOutputStream outputStream = new FileOutputStream(filename);
            for (TutorialSeenVideo vid :
                    seenVids) {
                outputStream.write(vid.key.getBytes());
                outputStream.write("\n".getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
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
            if (playlistID.equals(localActivity.getString(R.string.LOCAL_SOCIAL_PLAYLIST))) {
                Log.d("GPT","local playlist no refetch");
                playlistMustLoad=false;
            } else if (playlistID.equals(localActivity.getString(R.string.LOCAL_FAVORITES_PLAYLIST))) {
                Log.d("GPT","local playlist no refetch");
                playlistMustLoad=false;
            } else if (playlistID.equals(localActivity.getString(R.string.LOCAL_LATER_PLAYLIST))) {
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
            String jsonString = Assets.loadFileFromAssetFolder(localActivity, "channels/playlists", playlistID + ".json");
            if (jsonString==null) {
                Log.d("GPT","not found in assets");
                //fetch from youtube
                // Get a httpclient to talk to the internet
                jsonString = YoutubeUtils.getPlaylistFromApi(playlistID, apiKey);
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
                YoutubeVideo tVideo = ModelConverter.fromModel(video);
                playlist.getVideos().add(tVideo);
            }
        }

        return playlist;
    }

    public static void cleanVideos(YoutubePlaylist playlist, String[] cleanVideos) {
        Iterator<YoutubeVideo> iter= playlist.getVideos().iterator();
        while(iter.hasNext()){
            YoutubeVideo vid=iter.next();
            String title=vid.getTitle();
            //get the ressources for cleaning
            for (String str :
                    cleanVideos) {
                title=(title.replace(str, ""));
            }
            //prettify first letter
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
            vid.setTitle(title);
        }
    }

    public static void prepareChannel(YoutubeChannel channel,Context ctx) {
        //vu et fait : par défaut, on trie les playlists
        Log.d("GPT","Preparing channel " + channel.getID());
        //we need to know the Sponsors
        Sponsors sponsors = Application.getSponsors();
        Sponsor thesp=sponsors.getByChannelKey(channel.getID());
        //condition sur l'ID et les constantes que l'on connait
        if (thesp!=null && thesp.includePlaylists!=null && thesp.expandablePlaylists!=null){
            //standard
            Log.d("GPT","Specific inluded and expanded");
            prepareChannelWithIncludeList(channel, thesp.includePlaylists);
            prepareChannelWithExpandList(channel, thesp.expandablePlaylists);
        } else if (thesp!=null && thesp.includePlaylists!=null){
            //standard
            Log.d("GPT", "Specific for included playlists list specified");
            prepareChannelWithIncludeList(channel, thesp.includePlaylists);
        } else {
            //default
            //Sorting by title
            Log.d("GPT","Generic prepare");
            Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
                @Override
                public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                    return pl1.getTitle().compareTo(pl2.getTitle());
                }
            });
        }
        //channels are redispatched, clean up their names if necessary
        if (thesp!=null && thesp.cleanPlaylists!=null){
            prepareCleanNames(channel,thesp.cleanPlaylists);
        }
    }

    private static void prepareCleanNames(YoutubeChannel channel, String[] cleanPlaylists) {
        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            Log.d("PCN","Before name = " + pl.getTitle());
            //clean the names
            for (String str :
                    cleanPlaylists) {
                pl.setTitle(pl.getTitle().replace(str, ""));
            }
            Log.d("PCN", "After new name = " + pl.getTitle());
        }
    }

    public static void prepareChannelWithIncludeList(YoutubeChannel channel, String[] includeList) {
        //exclude the "Trick videos" playlist
        Iterator<YoutubePlaylist> iter= channel.getPlaylists().iterator();
        final List<String> plslist = Arrays.asList(includeList);
        while(iter.hasNext()){
            YoutubePlaylist pl=iter.next();
            //to handle expanded playlists, check if playlist key contains included playlist radix
            //rather than full equality, given youtube key structure, this is no risk
            boolean contains=false;
            for (String key :
                    includeList) {
                if (pl.getID().startsWith(key)){
                    contains=true;
                }
            }
            if(!contains ){
                iter.remove();
            }
        }
        //Sorting by custom order
        //playlist order
        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                int pl1Index = plslist.indexOf(pl1.getID());
                int pl2Index = plslist.indexOf(pl2.getID());
                return pl1Index - pl2Index;
            }
        });
    }


    public static void prepareChannelWithExpandList(YoutubeChannel channel, String[] expandList) {
        //reparse the cabin tutorials channel to a more manageable size
        for (String expandPlaylist :
                expandList) {
            //we get the playlist
            YoutubePlaylist playlist=channel.getPlaylistFromYoutubeID(expandPlaylist);
            //test first if we have the famous playlist, if not then we will display nothing due to following <code></code>
            //do we have a playlist, if yes, then split it
            if (playlist!=null){
                //remove it from the channel before continuing
                channel.getPlaylists().remove(0);
                //get item count
                //more precise from playlist.getVids
                //int count=playlist.getNumberToFetch();
                int count=playlist.getVideos().size();
                int pages=count / PAGE_SIZE;
                int remainder=count-(pages* PAGE_SIZE);
                if (remainder!=0){
                    pages++;
                }
                //iterate over number of pages and create virtual playlists
                for (int i = 1; i <= pages ; i++) {
                    YoutubePlaylist tpl=playlist.clone();
                    //must create unique ID
                    tpl.setID(tpl.getID() + "(" + i + ")");
                    tpl.setPageNumber(i);
                    if (i==pages){
                        tpl.setNumberToFetch(remainder);
                    }else{
                        tpl.setNumberToFetch(PAGE_SIZE);
                    }
                    int start=((i-1)* PAGE_SIZE);
                    int end=(i* PAGE_SIZE);
                    if (end>=count){
                        end=count;
                    }
                    Log.d("CLYWF.PC", "sublist " + start + "/" + end);
                    List<YoutubeVideo> lst=playlist.getVideos().subList(start, end);
                    for (int j = 0; j < lst.size(); j++) {
                        tpl.getVideos().add(lst.get(j));
                    }
                    //handle date stuff
                    //get first and last video of segment
                    String startDate=lst.get(0).getPublishedAt();
                    String endDate=lst.get(lst.size()-1).getPublishedAt();
                    Log.d("CLYWF.PC", "sublist dates " + startDate + "/" + endDate);
                    //parse dates
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'.000Z'");
                    try {
                        Date startD=sdf.parse(startDate);
                        Date endD=sdf.parse(endDate);
                        sdf=new SimpleDateFormat("MMM yyyy");
                        startDate=sdf.format(startD);
                        endDate=sdf.format(endD);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //here's the new playlist title
                    tpl.setTitle(tpl.getTitle() + " "  + i + " (" + startDate + " - " +  endDate + ")");
                    tpl.setDescription(tpl.getDescription() + "\n Videos from " + startDate + " to " +  endDate + "");
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
        }
    }


    public static boolean restorePersonnalData(Context ctx) {
        boolean result = true;
        //here we want to bacxkup the user personnal data
        FileOperations.ensureDirExists(importDirectory);
        //first, we need the local lists and we back them up
        TutorialSource localSource = TutorialSource.getByKey(ctx.getString(R.string.LOCAL_CHANNEL));
        //the source should exist but we don't need it, we need the playlists
        TutorialPlaylist watchPlaylist = TutorialPlaylist.getByKey(ctx.getString(R.string.LOCAL_LATER_PLAYLIST));
        //before importing, check that file exists
        result = importLocalList(ctx, result, watchPlaylist, watchLaterFile);
        TutorialPlaylist favoritesPlaylist = TutorialPlaylist.getByKey(ctx.getString(R.string.LOCAL_FAVORITES_PLAYLIST));
        result = importLocalList(ctx, result, favoritesPlaylist, favoritesFile);
        TutorialPlaylist sharedPlaylist = TutorialPlaylist.getByKey(ctx.getString(R.string.LOCAL_SOCIAL_PLAYLIST));
        result = importLocalList(ctx, result, sharedPlaylist, sharedFile);
        //next we restore the view status
        List<TutorialSeenVideo> seenVids = TutorialSeenVideo.getAll();
        try {
            File filename = new File(importDirectory + seenFile);
            //check if file exists and is not empty
            if (filename.isFile() && filename.canRead()) {
                //delete existing
                TutorialSource.clearViewed();
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = br.readLine()) != null) {
                    //line holds the key, we need more for lookup info
                    //insert the video in the database
                    TutorialSeenVideo vid = new TutorialSeenVideo();
                    vid.key = line;
                    vid.lastViewed = new Date();
                    vid.timesViewed = 1;
                    vid.save();
                }
                br.close();
            } else {
                Toast.makeText(ctx, "Personal Data File seen.csv Missing from : " + importDirectory + ", restoration cancelled for this element", Toast.LENGTH_SHORT).show();
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static boolean importLocalList(Context ctx, boolean result, TutorialPlaylist playlist, String fileName) {
        try {
            File file = new File(importDirectory + fileName);
            //check if file exists and is not empty
            if (file.isFile() && file.canRead()) {
                Log.d("MC.RPD", fileName + " file ready for import");
                //delete existsing
                TutorialPlaylist.clearVideos(playlist.key);
                Log.d("MC.RPD", playlist.name + "List cleared");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    //line holds the key, we need more for lookup info
                    Log.d("MC.RPD", fileName + " line read : " + line);
                    //find the video in the database
                    List<TutorialVideo> vids = TutorialVideo.getByKey(line);
                    if (vids.size() == 0) {
                        Toast.makeText(ctx, "Database cache couldn't find video, rebuild your cache before importing, restoration cancelled for this element.", Toast.LENGTH_SHORT).show();
                        result = false;
                    } else {
                        TutorialVideo vid = vids.get(0);
                        vid.addToLocal(playlist.key);
                    }
                }
                br.close();
                Log.d("MC.RPD", fileName + " file closed ");
            } else {
                Toast.makeText(ctx, "Personal Data File " + fileName + " Missing from : " + importDirectory + ", restoration cancelled for this element", Toast.LENGTH_SHORT).show();
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
