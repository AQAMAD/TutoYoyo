package fr.aqamad.tutoyoyo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

/**
 * Created by Gregoire on 12/10/2015.
 */
public class PicassoHelper {
    // Size in bytes (50 MB)
    private static final long PICASSO_DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private static final int maxSize = 10000;

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

    public static RequestCreator loadWeborDrawable(Context ctx,String source){
        RequestCreator  res;
        if (!source.startsWith("http")){
            //localURI, use URI
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ctx.getPackageName()+"/drawable/" + source);
            //load image with picasso
            res=PicassoHelper.with(ctx).load(uri);
        }else{
            //load image with picasso
            res=PicassoHelper.with(ctx).load(source);
        }
        return res;
    }

    public static Transformation getRoundedCornersTranform(){
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.DKGRAY)
                .borderWidthDp(2)
                .cornerRadiusDp(10)
                .oval(false)
                .build();
        return transformation;
    }

    public static Transformation getRoundedCornersTranform(int color){
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(color)
                .borderWidthDp(1)
                .cornerRadiusDp(10)
                .oval(false)
                .build();
        return transformation;
    }

}
