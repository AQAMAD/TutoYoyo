package fr.aqamad.tutoyoyo.tasks;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.List;

import fr.aqamad.tutoyoyo.model.TutorialChannel;
import fr.aqamad.tutoyoyo.model.TutorialSource;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeThumbnail;
import fr.aqamad.youtube.YoutubeUtils;


/**
 * This is the task that will ask YouTube for a list of videos for a specified user</br>
 * This class implements Runnable meaning it will be ran on its own Thread</br>
 * Because it runs on it's own thread we need to pass in an object that is notified when it has finished
 *
 * @author paul.blundell
 */
public class GetLocalChannelTask implements Runnable {
    // A reference to retrieve the data when this task finishes
    public static final String CHANNEL = "Channel";
    // A handler that will be notified when the task is finished
    private final Handler replyTo;
    // The user we are querying on YouTube for videos
    private final String channelId;
    private final Activity mAct;

    /**
     * Don't forget to call run(); to start this task
     * @param replyTo - the handler you want to receive the response when this task has finished
     * @param channelId - the username of who on YouTube you are browsing
     */
    public GetLocalChannelTask(Handler replyTo, String channelId,Activity act) {
        this.replyTo = replyTo;
        this.channelId = channelId;
        this.mAct=act;
    }

    @Override
    public void run() {
        try {

            TutorialSource source=TutorialSource.getByKey(channelId);

            YoutubeChannel channel=new YoutubeChannel("",source.name,source.description);

            for (int i = 0; i < source.channels().size(); i++) {
                TutorialChannel playlist=source.channels().get(i);
                YoutubePlaylist tPlayList=new YoutubePlaylist(playlist.key,playlist.name,playlist.description );
                tPlayList.setID(playlist.getId().toString());
                try {
                    tPlayList.setDefaultThumb(new YoutubeThumbnail(playlist.defaultThumbnail, YoutubeUtils.DEFAULT_WIDTH, YoutubeUtils.DEFAULT_HEIGHT));
                    tPlayList.setMediumThumb(new YoutubeThumbnail(playlist.mediumThumbnail, YoutubeUtils.MEDIUM_WIDTH, YoutubeUtils.MEDIUM_HEIGHT));
                    tPlayList.setHighThumb(new YoutubeThumbnail(playlist.highThumbnail, YoutubeUtils.HIGH_WIDTH, YoutubeUtils.HIGH_HEIGHT));
                }catch(MalformedURLException e){
                    Log.d("GLCT","URLException",e);
                }

                channel.getPlaylists().add(tPlayList);

            }

//            Log.d("YUPT","Channel : " + channel.getID());
//            Log.d("YUPT","Title : " + channel.getTitle());
//            Log.d("YUPT","Desc : " + channel.getDescription());
//            Log.d("YUPT","Playlists : " + channel.getPlaylists().size());
//            for (YoutubePlaylist yup :
//                    channel.getPlaylists()) {
//                Log.d("YUPT","Playlist : " + yup.getID());
//                Log.d("YUPT","Playlist title : " + yup.getTitle());
//                Log.d("YUPT","Playlist desc : " + yup.getDescription());
//            }

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