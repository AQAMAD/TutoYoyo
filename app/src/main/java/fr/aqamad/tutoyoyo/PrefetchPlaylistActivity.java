package fr.aqamad.tutoyoyo;

import android.os.Handler;

import fr.aqamad.tutoyoyo.base.PlaylistActivity;
import fr.aqamad.tutoyoyo.tasks.GetPrefetchPlaylistTask;

public class PrefetchPlaylistActivity extends PlaylistActivity {

    /**
     * This method retrieves the Library of videos from the task and passes them to our ListView
     * @param msg
     */
    @Override
    public void fetchVideos(Handler handler, String playlistId, String apiKey) {
        new Thread(new GetPrefetchPlaylistTask(handler,  playlistId, apiKey,this)).start();
    }
}
