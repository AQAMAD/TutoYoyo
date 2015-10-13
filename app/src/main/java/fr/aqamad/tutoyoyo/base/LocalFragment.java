package fr.aqamad.tutoyoyo.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;

import fr.aqamad.tutoyoyo.LocalPlaylistActivity;
import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.YoutubePlaylistActivity;
import fr.aqamad.tutoyoyo.tasks.GetLocalChannelTask;
import fr.aqamad.tutoyoyo.tasks.GetYouTubeChannelTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;



public abstract class LocalFragment extends SourceFragment   {

    public LocalFragment() {
        Log.d("LF", "LocalFragment constructor" );
    }

    @Override
    public void fetchChannel(Handler handler,Activity act) {
        new Thread(new GetLocalChannelTask(handler,  getChannelId(),act )).start();
        Log.d("LF", "LocalFragment fetchChannel");
    }

    @Override
    public void prepareChannel() {
        //par défaut, on trie les playlists
        YoutubeChannel channel=this.getChannel();
        //Sorting by title
        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                return pl1.getTitle().compareTo(pl2.getTitle());
            }
        });
        Log.d("LF", "LocalFragment prepareChannel");
    }

    @Override
    public Class getPlaylistActivityClass() {
        Log.d("LF", "LocalFragment getPlaylistActivityClass");
        return LocalPlaylistActivity.class;
    }

}
