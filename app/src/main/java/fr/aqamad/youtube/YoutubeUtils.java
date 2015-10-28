package fr.aqamad.youtube;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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

    public static final int MAX_YOUTUBE_PAGESIZE=50;


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
            //fill playlist pseudo-object with real values for lookups




            JSONArray jsonVideos = json.getJSONArray("items");

            for (int i = 0; i < jsonVideos.length(); i++) {
                JSONObject vid = jsonVideos.getJSONObject(i);
                JSONObject sn = vid.getJSONObject("snippet");
                JSONObject rs = sn.getJSONObject("resourceId");
                String id= rs.getString("videoId");
                String channelId=sn.getString("channelId");
                String title= sn.getString("title");
                String desc= sn.getString("description");
                String pubAt=sn.getString("publishedAt");
                if (sn.has("thumbnails")) {
                    //we don't create vids that have no thumbnails (probably private)
                    YoutubeVideo tVideo=new YoutubeVideo(id,title,desc );
                    tVideo.setPublishedAt(pubAt);
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
    }


    public static YoutubePlaylist addDurationsFromJson(YoutubePlaylist playlist,String jsonString){

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
                JSONObject cd = vid.getJSONObject("contentDetails");
                String id= vid.getString("id");
                String duration=cd.getString("duration");
                String caption=cd.getString("caption");

                YoutubeVideo yVid=playlist.findByKey(id);
                yVid.setDuration(duration);
                yVid.setCaption(caption);

            }
        }catch(JSONException jsexc){
            //here we only log
            Log.d("YU.CFJ","JSon exception in addDurationsFromJson ",jsexc);
            playlist=null;
        }
        return playlist;
    }


    public static String getChannelFromApi(String channelId,String apiKey){
        // Get a httpclient to talk to the internet
        String rurl="https://www.googleapis.com/youtube/v3/playlists?part=id,snippet,contentDetails&maxResults=" + MAX_YOUTUBE_PAGESIZE + "&channelId="+channelId + "&key=" + apiKey;

        HttpRequest req=HttpRequest.get(rurl, true)
                .followRedirects(true);

        // Convert this response into a readable string
        String jsonString = req.body();
        Log.d("YU.GCFA","Req : " + rurl);
        return jsonString;
    }


    public static String getVideosFromApi(String videos,String apiKey){
        // we need some splitting ansd joining operations here as google does not support paging in these
        String[] aVideos= TextUtils.split(videos, ",");
        //get the 50 first
        String[] aBunch= Arrays.copyOfRange(aVideos,0,MAX_YOUTUBE_PAGESIZE-1);
        int position=MAX_YOUTUBE_PAGESIZE;
        String thebunch=TextUtils.join(",",aBunch);
        String rurl="https://www.googleapis.com/youtube/v3/videos?part=contentDetails&maxResults=" + MAX_YOUTUBE_PAGESIZE +  "&id=" + thebunch + "&key=" + apiKey;

        Log.d("YU.GPFA","Req : " + rurl);

        HttpRequest req=HttpRequest.get(rurl, true)
                .followRedirects(true);

        // Convert this response into a readable string
        String jsonString = req.body();
        //test if results complete, if not fetch more
        if (aVideos.length>MAX_YOUTUBE_PAGESIZE)
        while (position<aVideos.length){
            aBunch= Arrays.copyOfRange(aVideos,position,position+MAX_YOUTUBE_PAGESIZE);
            position=position+MAX_YOUTUBE_PAGESIZE;
            thebunch=TextUtils.join(",",aBunch);
            rurl="https://www.googleapis.com/youtube/v3/videos?part=contentDetails&maxResults=" + MAX_YOUTUBE_PAGESIZE + "&id=" + thebunch + "&key=" + apiKey ;
            Log.d("YU.GPFA","Req : " + rurl);
            req=HttpRequest.get(rurl, true)
                    .followRedirects(true);
            String nextJsonString=req.body();
            try{
                //token managed, let's join the items
                JSONObject json2=new JSONObject(nextJsonString);
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

    public static String getPlaylistFromApi(String playlistID,String apiKey){
        // Get a httpclient to talk to the internet
        String rurl="https://www.googleapis.com/youtube/v3/playlistItems?part=id,snippet,contentDetails&maxResults="+MAX_YOUTUBE_PAGESIZE+"&playlistId=" + playlistID + "&key=" + apiKey;

        Log.d("YU.GPFA","Req : " + rurl);

        HttpRequest req=HttpRequest.get(rurl, true)
                .followRedirects(true);

        // Convert this response into a readable string
        String jsonString = req.body();
        //test if results complete, if not fetch more
        String npt="";
        try{
            JSONObject json = new JSONObject(jsonString);
            if (json.has("nextPageToken")){
                npt = json.getString("nextPageToken");
            }else{
                npt = "";
            }
        } catch(JSONException e){
            Log.d("YU.GPFA","JSexc ",e);
        }
        while (!npt.equals("")){
            rurl="https://www.googleapis.com/youtube/v3/playlistItems?part=id,snippet,contentDetails&maxResults="+MAX_YOUTUBE_PAGESIZE+"&playlistId=" + playlistID + "&key=" + apiKey + "&pageToken=" + npt;
            Log.d("YU.GPFA","Req : " + rurl);
            req=HttpRequest.get(rurl, true)
                    .followRedirects(true);
            String nextJsonString=req.body();
            JSONObject json2=null;
            try{
                npt="";
                json2 = new JSONObject(nextJsonString);
                if (json2.has("nextPageToken")){
                    npt = json2.getString("nextPageToken");
                }
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

    public static void PlayYoutubeVideo(String videoId,Context ctx){
        Intent intent = getYoutubeVideoIntent(videoId);
        ctx.startActivity(intent);
    }

    public static void logChannel(YoutubeChannel channel){
        Log.d("YTU","Channel : " + channel.getID());
        Log.d("YTU","Title : " + channel.getTitle());
        Log.d("YTU","Desc : " + channel.getDescription());
        Log.d("YTU", "Playlists : " + channel.getPlaylists().size());
        for (YoutubePlaylist yup :
                channel.getPlaylists()) {
            Log.d("YTU","Playlist : " + yup.getID());
            Log.d("YTU","Playlist title : " + yup.getTitle());
            Log.d("YTU","Playlist desc : " + yup.getDescription());
            Log.d("YTU", "Playlist thumb : " + yup.getHighThumb().getUrl().toString());
            Log.d("YTU","Playlist desc : " + yup.getDescription());
        }
    }

    public static void logPlaylist(YoutubePlaylist playlist){
        Log.d("YTU", "Playlist : " + playlist.getID());
        Log.d("YTU", "Playlist Title : " + playlist.getTitle());
        Log.d("YTU","Playlist Desc : " + playlist.getDescription());
        //Log.d("YTU","Playlist highThumb : " + playlist.getHighThumb().getUrl());
        Log.d("YTU", "Videos : " + playlist.getVideos().size());
        for (YoutubeVideo video :
                playlist.getVideos()) {
            Log.d("YTU", "Video : " + video.getID());
            Log.d("YTU", "Video title : " + video.getTitle());
            Log.d("YTU", "Video desc : " + video.getDescription());
            Log.d("YTU", "Video h thumb : " + video.getHighThumb().getUrl().toString());
//            Log.d("YTU", "Video m thumb : " + video.getMediumThumb().getUrl().toString());
//            Log.d("YTU", "Video d thumb : " + video.getDefaultThumb().getUrl().toString());
            Log.d("YTU", "Video duration : " + video.getDuration());
            Log.d("YTU", "Video caption : " + video.getCaption());
        }
    }


    public static String prettyDuration(String isoDuration){
        if (isoDuration==null){
            return "0:00";
        }
        String result= isoDuration.replaceAll("PT","");
        int mPos=result.indexOf("M");
        //if no M found then return seconds we have
        if (mPos==-1){
            return "0:" + result.replaceAll("S","");
        } else {
            //header stripped, get minutes
            int minutes = Integer.parseInt(result.substring(0,mPos));
            int sPos=result.indexOf("S");
            //if no S found, only minutes count
            if (sPos==-1){
                return minutes + ":00";
            } else {
                int seconds=Integer.parseInt(result.substring(mPos+1,sPos));
                if (seconds<10){
                    return minutes + ":0" + seconds;
                }
                return minutes + ":" + seconds;
            }
        }
    }

    public static int durationInSeconds(String isoDuration){
        if (isoDuration==null){
            return 0;
        }
        String result= isoDuration.replaceAll("PT","");
        int mPos=result.indexOf("M");
        //if no M found then return seconds we have
        if (mPos==-1){
            return Integer.parseInt(result.replaceAll("S",""));
        } else {
            //header stripped, get minutes
            int minutes = Integer.parseInt(result.substring(0,mPos));
            int sPos=result.indexOf("S");
            //if no S found, only minutes count
            if (sPos==-1){
                return minutes*60 ;
            } else {
                int seconds=Integer.parseInt(result.substring(mPos+1,sPos));
                return minutes*60 + seconds;
            }
        }
    }

    public static void ShareYoutubeVideo(String videoId, Context ctx) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, YoutubeUtils.getVideoPlayUrl(videoId));
        intent.setType("text/plain");
        ctx.startActivity(intent);
    }

    public static void ShareYoutubePlaylist(String videoId, Context ctx) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, YoutubeUtils.getVideoPlayUrl(videoId));
        intent.setType("text/plain");
        ctx.startActivity(intent);
    }
}
