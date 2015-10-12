package fr.aqamad.youtube;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Created by Gregoire on 09/10/2015.
 */
public class YoutubeUtils {

    public static final int DEFAULT_HEIGHT=90;
    public static final int DEFAULT_WIDTH=120;
    public static final int MEDIUM_HEIGHT=180;
    public static final int MEDIUM_WIDTH=320;
    public static final int HIGH_HEIGHT=360;
    public static final int HIGH_WIDTH=480;


    public static YoutubeChannel channelFromJson(String jsonString){

        YoutubeChannel channel=new YoutubeChannel("","","");

        try{
            JSONObject json = new JSONObject(jsonString);

            // For further information about the syntax of this request and JSON-C
            // see the documentation on YouTube http://code.google.com/apis/youtube/2.0/developers_guide_jsonc.html
            // Get are search result items

            JSONArray jsonPlaylists = json.getJSONArray("items");

            for (int i = 0; i < jsonPlaylists.length(); i++) {
                JSONObject pl = jsonPlaylists.getJSONObject(i);
                JSONObject sn = pl.getJSONObject("snippet");
                JSONObject cd = pl.getJSONObject("contentDetails");
                String id= pl.getString("id");
                String channelId=sn.getString("channelId");
                String title= sn.getString("title");
                String desc= sn.getString("description");
                YoutubePlaylist tPlayList=new YoutubePlaylist(id,title,desc );
                int itemCount=cd.getInt("itemCount");
                tPlayList.setNumberToFetch(itemCount);
                try {
                    //iterate over thumbs now
                    JSONObject jsonThumbs = sn.getJSONObject("thumbnails");
                    JSONObject thumb = jsonThumbs.getJSONObject("default");
                    String url = thumb.getString("url");
                    int height = thumb.getInt("height");
                    int width = thumb.getInt("width");
                    tPlayList.setDefaultThumb(new YoutubeThumbnail(url, width, height));
                    thumb = jsonThumbs.getJSONObject("medium");
                    url = thumb.getString("url");
                    height = thumb.getInt("height");
                    width = thumb.getInt("width");
                    tPlayList.setMediumThumb(new YoutubeThumbnail(url, width, height));
                    thumb = jsonThumbs.getJSONObject("high");
                    url = thumb.getString("url");
                    height = thumb.getInt("height");
                    width = thumb.getInt("width");
                    tPlayList.setHighThumb(new YoutubeThumbnail(url, width, height));
                    try {
                        //standard is optional
                        thumb = jsonThumbs.getJSONObject("standard");
                        url = thumb.getString("url");
                        height = thumb.getInt("height");
                        width = thumb.getInt("width");
                        tPlayList.setStandardThumb(new YoutubeThumbnail(url, width, height));
                    }catch(JSONException e){
                        //can safely ignore
                    };
                }catch (MalformedURLException m){
                    Log.e("YUPT", "malformed thumbnail url for playlist", m);
                }

                channel.getPlaylists().add(tPlayList);

                if (channel.getID().equals("")){
                    title= sn.getString("channelTitle");
                    channel.setID(channelId);
                    channel.setTitle(title);
                }

            }
        }catch(JSONException jsexc){
            //here we only log
            Log.d("YU.CFJ","JSon exception in ChannelFromJson ",jsexc);
            channel=null;
        }
        return channel;
    };

    public static YoutubePlaylist playlistFromJson(String jsonString){

        YoutubePlaylist playlist=new YoutubePlaylist("","","");

        try{
            JSONObject json = new JSONObject(jsonString);

            // For further information about the syntax of this request and JSON-C
            // see the documentation on YouTube http://code.google.com/apis/youtube/2.0/developers_guide_jsonc.html

            // For further information about the syntax of this request and JSON-C
            // see the documentation on YouTube http://code.google.com/apis/youtube/2.0/developers_guide_jsonc.html
            // Get are search result items

            JSONArray jsonVideos = json.getJSONArray("items");

            for (int i = 0; i < jsonVideos.length(); i++) {
                JSONObject vid = jsonVideos.getJSONObject(i);
                JSONObject sn = vid.getJSONObject("snippet");
                JSONObject rs = sn.getJSONObject("resourceId");
                String id= rs.getString("videoId");
                String channelId=sn.getString("channelId");
                String title= sn.getString("title");
                String desc= sn.getString("description");
                if (sn.has("thumbnails")) {
                    //we don't create vids that have no thumbnails (probably private)
                    YoutubeVideo tVideo=new YoutubeVideo(id,title,desc );
                    try {
                        //iterate over thumbs now
                        JSONObject jsonThumbs = sn.getJSONObject("thumbnails");
                        JSONObject thumb = jsonThumbs.getJSONObject("default");
                        String url = thumb.getString("url");
                        int height = thumb.getInt("height");
                        int width = thumb.getInt("width");
                        tVideo.setDefaultThumb(new YoutubeThumbnail(url, width, height));
                        thumb = jsonThumbs.getJSONObject("medium");
                        url = thumb.getString("url");
                        height = thumb.getInt("height");
                        width = thumb.getInt("width");
                        tVideo.setMediumThumb(new YoutubeThumbnail(url, width, height));
                        thumb = jsonThumbs.getJSONObject("high");
                        url = thumb.getString("url");
                        height = thumb.getInt("height");
                        width = thumb.getInt("width");
                        tVideo.setHighThumb(new YoutubeThumbnail(url, width, height));
                        try {
                            //standard is optional
                            thumb = jsonThumbs.getJSONObject("standard");
                            url = thumb.getString("url");
                            height = thumb.getInt("height");
                            width = thumb.getInt("width");
                            tVideo.setStandardThumb(new YoutubeThumbnail(url, width, height));
                        } catch (JSONException e) {
                            //can safely ignore
                        }
                        ;
                    } catch (MalformedURLException m) {
                        Log.e("YUPT", "malformed thumbnail url for playlist", m);
                    }
                    playlist.getVideos().add(tVideo);
                }

                if (playlist.getID().equals("")){
                    title= sn.getString("channelTitle");
                    playlist.setID(channelId);
                    playlist.setTitle(title);
                }

            }
        }catch(JSONException jsexc){
            //here we only log
            Log.d("YU.CFJ","JSon exception in PlaylistFromJson ",jsexc);
            playlist=null;
        }
        return playlist;
    };


    public static String getChannelFromApi(String channelId,String apiKey){
        // Get a httpclient to talk to the internet
        String rurl="https://www.googleapis.com/youtube/v3/playlists?part=id,snippet,contentDetails&maxResults=50&channelId="+channelId + "&key=" + apiKey;

        HttpRequest req=HttpRequest.get(rurl, true)
                .followRedirects(true);

        // Convert this response into a readable string
        String jsonString = req.body();
        Log.d("YU.GCFA","Req : " + rurl);
        return jsonString;
    }


    public static String getPlaylistFromApi(String playlistID,String apiKey){
        // Get a httpclient to talk to the internet
        String rurl="https://www.googleapis.com/youtube/v3/playlistItems?part=id,snippet,contentDetails&maxResults=50&playlistId=" + playlistID + "&key=" + apiKey;

        Log.d("YU.GPFA","Req : " + rurl);

        HttpRequest req=HttpRequest.get(rurl, true)
                .followRedirects(true);

        // Convert this response into a readable string
        String jsonString = req.body();
        //test if results complete, if not fetch more
        String npt="";
        try{
            JSONObject json = new JSONObject(jsonString);
            npt = json.getString("nextPageToken");
        } catch(JSONException e){
            Log.d("YU.GPFA","JSexc ",e);
        }
        while (!npt.equals("")){
            rurl="https://www.googleapis.com/youtube/v3/playlistItems?part=id,snippet,contentDetails&maxResults=50&playlistId=" + playlistID + "&key=" + apiKey + "&pageToken=" + npt;
            Log.d("YU.GPFA","Req : " + rurl);
            req=HttpRequest.get(rurl, true)
                    .followRedirects(true);
            String nextJsonString=req.body();
            JSONObject json2=null;
            try{
                npt="";
                json2 = new JSONObject(nextJsonString);
                npt = json2.getString("nextPageToken");
            } catch(JSONException e){
                Log.d("YU.GPFA","JSexc 1 ",e);
            }
            try{
                //token managed, let's join the items
                JSONObject json = new JSONObject(jsonString);
                JSONArray jsonVideos = json.getJSONArray("items");
                JSONArray jsonVideos2 = json2.getJSONArray("items");
                for (int i = 0; i < jsonVideos2.length(); i++) {
                    jsonVideos.put(jsonVideos2.getJSONObject(i));
                }
                jsonString= json.toString();
            } catch(JSONException e){
                Log.d("YU.GPFA","JSexc 2 ",e);
            }
        }
        return jsonString;
    }

    public static String getVideoPlayUrl(String videoId){
        return "http://www.youtube.com/watch?v=" + videoId;
    }

    public static Intent getYoutubeVideoIntent(String videoId){
        return new Intent(Intent.ACTION_VIEW, Uri.parse(getVideoPlayUrl(videoId)));
    }

    public static void PlayYoutubeVideo(String videoId,Activity currentActivity){
        Intent intent = getYoutubeVideoIntent(videoId);
        currentActivity.startActivity(intent);
    }

}
