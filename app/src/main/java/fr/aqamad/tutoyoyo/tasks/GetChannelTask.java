package fr.aqamad.tutoyoyo.tasks;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.MalformedURLException;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.utils.Assets;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeThumbnail;
import fr.aqamad.youtube.YoutubeUtils;
import fr.aqamad.youtube.YoutubeVideo;


/**
 * This is the task that will ask YouTube for a list of videos for a specified user</br>
 * This class implements Runnable meaning it will be ran on its own Thread</br>
 * Because it runs on it's own thread we need to pass in an object that is notified when it has finished
 *
 * @author paul.blundell
 */
public class GetChannelTask implements Runnable {
    // A reference to retrieve the data when this task finishes
    public static final String CHANNEL = "Channel";
    // A handler that will be notified when the task is finished
    private final Handler replyTo;
    // The user we are querying on YouTube for videos
    private final String channelId;
    private final Activity mAct;
    private final String apiKey;
    private final String expandPlaylist;

    /**
     * Don't forget to call run(); to start this task
     * @param replyTo - the handler you want to receive the response when this task has finished
     * @param channelId - the username of who on YouTube you are browsing
     */
    public GetChannelTask(Handler replyTo, String channelId, Activity act) {
        this.replyTo = replyTo;
        this.channelId = channelId;
        this.mAct=act;
        this.apiKey=act.getString(R.string.youtubeapikey);
        expandPlaylist="";
    }

    public GetChannelTask(Handler replyTo, String channelId, String expandPlaylist,Activity act) {
        this.replyTo = replyTo;
        this.channelId = channelId;
        this.mAct=act;
        this.apiKey=act.getString(R.string.youtubeapikey);
        this.expandPlaylist=expandPlaylist;
    }

    @Override
    public void run() {
        try {
            Log.d("GCT","GetChannelTask run called");
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
                    channel.getPlaylists().add(tPlayList);
                }
            }
            //part 2, do we have a playlist to expand ?
            //locate the playlist to fill
            if (!expandPlaylist.equals("")){
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
            Log.d("GCT","Done getting channel, into bundle");
            YoutubeUtils.logChannel(channel);
            // Pack the Library into the bundle to send back to the Activity
            Bundle data = new Bundle();
            data.putSerializable(CHANNEL, channel);

            // Send the Bundle of data (our Library) back to the handler (our Activity)
            Message msg = Message.obtain();
            msg.setData(data);
            replyTo.sendMessage(msg);

            // We don't do any error catching, just nothing will happen if this task falls over
            // an idea would be to reply to the handler with a different message so your Activity can act accordingly
        } catch (Exception e) {
            Log.e("GLCT", "Exception",e);
        }
    }
}