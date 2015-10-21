package fr.aqamad.tutoyoyo.model;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class Sponsor {
    public String name;
    public String catchPhrase;
    public int foreGroundColor;
    public int backGroundColor;
    public int logoResId;
    public String websiteURL;


    public Sponsor(String name, String catchPhrase, int logo, int backgroundResId, int forGroundResId, String websiteUrl) {
        this.name=name;
        this.catchPhrase=catchPhrase;
        this.logoResId=logo;
        this.backGroundColor=backgroundResId;
        this.foreGroundColor=forGroundResId;
        this.websiteURL=websiteUrl;
    }

    public View.OnClickListener getNavigateListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(websiteURL));
                v.getContext().startActivity(intent);
            }
        };
    }






}
