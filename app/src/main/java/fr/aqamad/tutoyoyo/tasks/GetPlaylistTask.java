package fr.aqamad.tutoyoyo.tasks;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.MalformedURLException;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.TutorialPlaylist;
import fr.aqamad.tutoyoyo.model.TutorialVideo;
import fr.aqamad.tutoyoyo.utils.Assets;
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
public class GetPlaylistTask implements Runnable {
    // A reference to retrieve the data when this task finishes
    public static final String PLAYLIST = "fr.aqamad.youtube.playlist";
    // A handler that will be notified when the task is finished
    private final Handler replyTo;
    // The user we are querying on YouTube for videos
    private final String playlistID;
    private final String apiKey;
    private final Activity localActivity;
    /**
     * Don't forget to call run(); to start this task
     * @param replyTo - the handler you want to receive the response when this task has finished
     * @param playlistID - the username of who on YouTube you are browsing
     */
    public GetPlaylistTask(Handler replyTo, String playlistID, Activity act) {
        this.replyTo = replyTo;
        this.playlistID = playlistID;
        this.apiKey=act.getString(R.string.youtubeapikey);
        this.localActivity=act;
    }

    @Override
    public void run() {
        try {
            // Get from database
            Log.d("GPT","GetPlayListTask run called for " + playlistID);
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
                Log.d("GPT","enrich with duration");
                jsonString = YoutubeUtils.getVideosFromApi(playlist.getVideoIds(), apiKey);
                YoutubeUtils.addDurationsFromJson(playlist,jsonString);
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

            Log.d("GPT","Done, put bundle");
//            YoutubeUtils.logPlaylist(playlist);
            // Pack the Library into the bundle to send back to the Activity
            Bundle data = new Bundle();
            data.putSerializable(PLAYLIST, playlist);

            // Send the Bundle of data (our Library) back to the handler (our Activity)
            Message msg = Message.obtain();
            msg.setData(data);
            replyTo.sendMessage(msg);
            // We don't do any error catching, just nothing will happen if this task falls over
            // an idea would be to reply to the handler with a different message so your Activity can act accordingly
        } catch (Exception e) {
            Log.e("GPT", "Exception during GetPlayListTask",e);
            e.printStackTrace();
        }
    }
}