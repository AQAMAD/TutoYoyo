package fr.aqamad.tutoyoyo.tasks;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
public class GetYouTubePlaylistTask implements Runnable {
    // A reference to retrieve the data when this task finishes
    public static final String PLAYLIST = "fr.aqamad.youtube.playlist";
    // A handler that will be notified when the task is finished
    private final Handler replyTo;
    // The user we are querying on YouTube for videos
    private final String playlistID;
    // The api key for this app
    private final String apiKey;
    /**
     * Don't forget to call run(); to start this task
     * @param replyTo - the handler you want to receive the response when this task has finished
     * @param playlistID - the username of who on YouTube you are browsing
     */
    public GetYouTubePlaylistTask(Handler replyTo, String playlistID, String apiKey) {
        this.replyTo = replyTo;
        this.playlistID = playlistID;
        this.apiKey=apiKey;
    }

    @Override
    public void run() {
        try {
            // Get a httpclient to talk to the internet
            String jsonString = YoutubeUtils.getPlaylistFromApi(playlistID, apiKey);
            // Create a JSON object that we can use from the String
            YoutubePlaylist playlist= YoutubeUtils.playlistFromJson(jsonString);

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
            Log.e("GYPT", "JSExc",e);
        }
    }
}