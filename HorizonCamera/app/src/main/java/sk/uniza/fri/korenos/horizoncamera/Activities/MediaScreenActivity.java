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

    public static final String VIDEO_MEDIA_CODE = "video";
    public static final String PHOTO_MEDIA_CODE = "photo";

    public static final String MEDIA_NAME_EXTRAS_NAME = "mediaName";

    private String mediaOpened;
    private CameraDisplayFragment activeFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.media_activity_layout);

        Bundle extrasData = getIntent().getExtras();
        mediaOpened = extrasData.getString(MEDIA_NAME_EXTRAS_NAME);

        setFragment();
    }

    public void goToSettingsActivityAction(View view){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    public void goToMainAction(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToGalleryActivityAction(View view){
        /*Intent intent = new Intent(this, GalleryActivityTemplate.class);
        startActivity(intent);*/
    }

    public void changeFragmentAction(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(mediaOpened){
            case PHOTO_MEDIA_CODE:
                activeFragment = new VideoDisplaySpecialisation();
                mediaOpened = VIDEO_MEDIA_CODE;
                break;
            case VIDEO_MEDIA_CODE:
                activeFragment = new PhotoDisplaySpecialisation();
                mediaOpened = PHOTO_MEDIA_CODE;
                break;
        }

        fragmentTransaction.replace(R.id.mediaFragmentPlatform, activeFragment);
        fragmentTransaction.commit();
    }

    private void setFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(mediaOpened){
            case PHOTO_MEDIA_CODE:
                activeFragment = new PhotoDisplaySpecialisation();
                break;
            case VIDEO_MEDIA_CODE:
                activeFragment = new VideoDisplaySpecialisation();
                break;
        }

        fragmentTransaction.replace(R.id.mediaFragmentPlatform, activeFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activeFragment.onDestroy();
    }
}
