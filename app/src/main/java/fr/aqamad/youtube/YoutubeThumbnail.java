package fr.aqamad.youtube;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Gregoire on 07/10/2015.
 */
public class YoutubeThumbnail implements Serializable {

    private URL mUrl;
    private int mWidth;
    private int mHeight;

    public URL getUrl() {
        return mUrl;
    }

    public void setUrl(URL mUrl) {
        this.mUrl = mUrl;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public YoutubeThumbnail(URL mUrl, int mWidth, int mHeight) {
        this.mUrl = mUrl;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public YoutubeThumbnail(String mUrl, int mWidth, int mHeight) throws MalformedURLException {
        this.mUrl = new URL(mUrl);
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }
}
