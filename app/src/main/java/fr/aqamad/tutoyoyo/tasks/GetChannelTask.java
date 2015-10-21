package fr.aqamad.tutoyoyo.tasks;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.MalformedURLException;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.model.ModelConverter;
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
            YoutubeChannel channel= ModelConverter.loadChannel(mAct,channelId,expandPlaylist,apiKey);
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