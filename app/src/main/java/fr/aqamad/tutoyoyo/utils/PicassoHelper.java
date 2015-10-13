package fr.aqamad.tutoyoyo.utils;

import android.content.Context;

import com.squareup.picasso.Cache;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Gregoire on 12/10/2015.
 */
public class PicassoHelper {
    // Size in bytes (10 MB)
    private static final long PICASSO_DISK_CACHE_SIZE = 1024 * 1024 * 10;
    private static final int maxSize = 24000;

    private static Picasso mPicasso=null;

    public static Picasso with(Context ctx){

        if (mPicasso==null) {
            // Use OkHttp as downloader
            Downloader downloader = new OkHttpDownloader(ctx.getApplicationContext(),
                    PICASSO_DISK_CACHE_SIZE);

            // Create memory cache
            Cache memoryCache = new LruCache(maxSize);

            mPicasso = new Picasso.Builder(ctx.getApplicationContext())
                    .downloader(downloader).memoryCache(memoryCache).build();

            mPicasso.setIndicatorsEnabled(true);
        }
        //allows to configure with flexibility
        return mPicasso.with(ctx);
    }


}
