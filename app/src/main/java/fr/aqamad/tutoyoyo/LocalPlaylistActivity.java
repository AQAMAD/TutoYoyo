package fr.aqamad.tutoyoyo;

import android.os.Handler;

import fr.aqamad.tutoyoyo.base.PlaylistActivity;
import fr.aqamad.tutoyoyo.tasks.GetLocalPlaylistTask;

public class LocalPlaylistActivity extends PlaylistActivity {

    /**
     * This method retrieves the Library of videos from the task and passes them to our ListView
     * @param msg
     */
    @Override
    public void fetchVideos(Handler handler, String playlistId, String apiKey) {
        new Thread(new GetLocalPlaylistTask(handler,  playlistId, apiKey,this)).start();
    }
}
