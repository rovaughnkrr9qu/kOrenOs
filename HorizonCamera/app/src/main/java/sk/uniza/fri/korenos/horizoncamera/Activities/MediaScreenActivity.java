package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import sk.uniza.fri.korenos.horizoncamera.R;

/**
 * Created by Markos on 10. 11. 2016.
 */

public class MediaScreenActivity extends FragmentActivity {

    private mediaType mediaOpened;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.media_activity_layout);

        Bundle extrasData = getIntent().getExtras();
        findMediaType(extrasData.getString("mediaName"));

        setFragment();
    }

    public void goToSettingsActivityAction(View view){
        /*Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);*/
    }

    public void goToMainAction(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToGalleryActivityAction(View view){
        /*Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);*/
    }

    public void mediaMainAction(View view){
    }

    public void changeFragmentAction(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(mediaOpened){
            case photo:
                fragmentTransaction.replace(R.id.mediaFragmentPlatform, new VideoDisplaySpecialisation());
                mediaOpened = mediaType.video;
                break;
            case video:
                fragmentTransaction.replace(R.id.mediaFragmentPlatform, new PhotoDisplaySpecialisation());
                mediaOpened = mediaType.photo;
                break;
        }
        fragmentTransaction.commit();
    }

    private void setFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(mediaOpened){
            case photo:
                fragmentTransaction.replace(R.id.mediaFragmentPlatform, new PhotoDisplaySpecialisation());
                break;
            case video:
                fragmentTransaction.replace(R.id.mediaFragmentPlatform, new VideoDisplaySpecialisation());
                break;
        }
        fragmentTransaction.commit();
    }

    private void findMediaType(String type){
        switch (type){
            case "photo":
                mediaOpened = mediaType.photo;
                break;
            case "video":
                mediaOpened = mediaType.video;
                break;
        }
    }

    private enum mediaType {
        photo, video
    }
}
