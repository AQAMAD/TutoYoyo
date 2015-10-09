package fr.aqamad.tutoyoyo.tasks;


import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import java.util.Arrays;
import java.util.List;

import fr.aqamad.tutoyoyo.utils.Assets;
import fr.aqamad.youtube.YoutubePlaylist;
import fr.aqamad.youtube.YoutubeUtils;


/**
 * This is the task that will ask YouTube for a list of videos for a specified user</br>
 * This class implements Runnable meaning it will be ran on its own Thread</br>
 * Because it runs on it's own thread we need to pass in an object that is notified when it has finished
 *
 * @author paul.blundell
 */
public class GetPrefetchPlaylistTask implements Runnable {
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
    public GetPrefetchPlaylistTask(Handler replyTo, String playlistID, String apiKey,Activity act) {
        this.replyTo = replyTo;
        this.playlistID = playlistID;
        this.apiKey=apiKey;
        this.localActivity=act;
    }

    @Override
    public void run() {
        try {
            // Get a httpclient to talk to the internet
            String jsonString;
            String rurl="https://www.googleapis.com/youtube/v3/playlistItems?part=id,snippet,contentDetails&maxResults=50&playlistId=" + playlistID + "&key=" + apiKey;
            //test if local asset exists
            AssetManager am = localActivity.getAssets();
            Log.d("PFPT","Assets " + am.list("").toString());
            int foundId=-1;
            String[] amlist=am.list("");
            for (int i = 0; i <amlist.length; i++) {
                Log.d("PFPT","Assets (" +i+ ")" + amlist[i]);
                if (amlist[i].equals(playlistID + ".json")){
                    Log.d("PFPT","found (" +i+ ")" + playlistID + ".json");
                    foundId=i;
                }
            }
            if (foundId!=-1){
                //load from assets
                jsonString= Assets.loadFileFromAsset(localActivity, playlistID + ".json");
                Log.d("PFPT","From Assets");
            }else{
                //load from youtube
                HttpRequest req=HttpRequest.get(rurl, true)
                        .followRedirects(true);
                // Convert this response into a readable string
                jsonString = req.body();
                Log.d("PFPT","Req " + rurl);
            }

            //Log.d("YUVT","Got Json" + jsonString);
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
            Log.e("Feck", "JSExc",e);
        }
    }
}