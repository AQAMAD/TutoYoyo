package fr.aqamad.tutoyoyo.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;

import java.util.Collections;
import java.util.Comparator;

import fr.aqamad.tutoyoyo.PrefetchPlaylistActivity;
import fr.aqamad.tutoyoyo.YoutubePlaylistActivity;
import fr.aqamad.tutoyoyo.tasks.GetLocalChannelTask;
import fr.aqamad.tutoyoyo.tasks.GetPrefetchChannelTask;
import fr.aqamad.youtube.YoutubeChannel;
import fr.aqamad.youtube.YoutubePlaylist;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrefetchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class PrefetchFragment extends SourceFragment   {

    @Override
    public void fetchChannel(Handler handler,Activity act) {
        //ici on fetche depuis les assets et on charge la BDD
        new Thread(new GetPrefetchChannelTask(handler,  GetToutubeId(),act )).start();
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

    @Override
    public Class getPlaylistActivityClass() {
        return PrefetchPlaylistActivity.class;
    }

}
