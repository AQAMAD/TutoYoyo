package fr.aqamad.tutoyoyo.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import fr.aqamad.tutoyoyo.R;

/**
 * Created by Gregoire on 19/10/2015.
 */
public class HomeFragment extends Fragment {

    public HomeFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_home,container,false);

        //set up hyperlinks from the home fragment
        ImageView v = (ImageView) rootView.findViewById(R.id.imageBHOP);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.youtube.com/user/hadoq"));
                startActivity(intent);
            }
        });
        v = (ImageView) rootView.findViewById(R.id.imageCLYW);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.clyw.ca/"));
                startActivity(intent);
            }
        });
        v = (ImageView) rootView.findViewById(R.id.imageYYB);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.yoyoblast.com/"));
                startActivity(intent);
            }
        });

        v = (ImageView) rootView.findViewById(R.id.imageYYE);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.yoyoexpert.com/"));
                startActivity(intent);
            }
        });
        v = (ImageView) rootView.findViewById(R.id.imageYYT);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.mryoyothrower.com/"));
                startActivity(intent);
            }
        });

        return rootView;
    }
}
