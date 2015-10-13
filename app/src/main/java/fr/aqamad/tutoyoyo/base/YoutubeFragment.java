package fr.aqamad.tutoyoyo.base;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;

import fr.aqamad.tutoyoyo.R;
import fr.aqamad.tutoyoyo.YoutubePlaylistActivity;
import fr.aqamad.tutoyoyo.tasks.GetYouTubeChannelTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;



public abstract class YoutubeFragment extends SourceFragment   {

    public YoutubeFragment() {
    }


    @Override
    public void fetchChannel(Handler handler,Activity act) {
        new Thread(new GetYouTubeChannelTask(handler,  getChannelId() , act.getString(R.string.youtubeapikey))).start();
    }

    @Override
    public Class getPlaylistActivityClass() {
        return YoutubePlaylistActivity.class;
    }

    @Override
    public void prepareChannel() {
        //todo : par défaut, on trie les playlists
        YoutubeChannel channel=this.getChannel();
        //Sorting by title
        Collections.sort(channel.getPlaylists(), new Comparator<YoutubePlaylist>() {
            @Override
            public int compare(YoutubePlaylist pl1, YoutubePlaylist pl2) {
                return pl1.getTitle().compareTo(pl2.getTitle());
            }
        });
    }



}
