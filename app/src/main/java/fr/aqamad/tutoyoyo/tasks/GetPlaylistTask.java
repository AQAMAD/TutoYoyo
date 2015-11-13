package fr.aqamad.tutoyoyo.tasks;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fr.aqamad.commons.youtube.YoutubePlaylist;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.ModelConverter;


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
            YoutubePlaylist playlist= ModelConverter.loadPlaylist(localActivity,playlistID,apiKey);

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