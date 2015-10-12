package fr.aqamad.tutoyoyo.tasks;


import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import fr.aqamad.tutoyoyo.model.TutorialChannel;
import fr.aqamad.tutoyoyo.model.TutorialSource;
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
public class GetLocalPlaylistTask implements Runnable {
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
    public GetLocalPlaylistTask(Handler replyTo, String playlistID, String apiKey, Activity act) {
        this.replyTo = replyTo;
        this.playlistID = playlistID;
        this.apiKey=apiKey;
        this.localActivity=act;
    }

    @Override
    public void run() {
        try {
            // Get a httpclient to talk to the internet

            TutorialChannel channel=TutorialChannel.load(TutorialChannel.class,Long.parseLong(playlistID));

            YoutubePlaylist playlist= new YoutubePlaylist(channel.getId().toString(),channel.name,channel.description);
            //iterate and add videos

            for (int i = 0; i < channel.videos().size(); i++) {
                TutorialVideo video=channel.videos().get(i);
                YoutubeVideo tVideo=new YoutubeVideo(video.key,video.name,video.description );
                try {
                    tVideo.setDefaultThumb(new YoutubeThumbnail(video.defaultThumbnail, YoutubeUtils.DEFAULT_WIDTH, YoutubeUtils.DEFAULT_HEIGHT));
                    tVideo.setMediumThumb(new YoutubeThumbnail(video.mediumThumbnail, YoutubeUtils.MEDIUM_WIDTH, YoutubeUtils.MEDIUM_HEIGHT));
                    tVideo.setHighThumb(new YoutubeThumbnail(video.highThumbnail, YoutubeUtils.HIGH_WIDTH, YoutubeUtils.HIGH_HEIGHT));
                }catch(MalformedURLException e){
                    Log.d("GLCT","URLException",e);
                }

                playlist.getVideos().add(tVideo);

            }
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
            Log.e("Feck", "JSExc",e);
        }
    }
}