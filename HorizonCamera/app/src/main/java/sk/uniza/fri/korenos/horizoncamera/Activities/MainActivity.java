package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import sk.uniza.fri.korenos.horizoncamera.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
    }

    public void goToGalleryActivityAction(View view) {
        Intent intent = new Intent(this,BunchGalleryActivity.class);
        intent.putExtra(GalleryActivityTemplate.GALLERY_TYPE_EXTRAS_NAME, GalleryActivityTemplate.BUNCH_GALLERY_CODE);
        startActivity(intent);
    }

    public void goToPhotoActivityAction(View view) {
        Intent intent = new Intent(this,MediaScreenActivity.class);
        intent.putExtra(MediaScreenActivity.MEDIA_NAME_EXTRAS_NAME, MediaScreenActivity.PHOTO_MEDIA_CODE);
        startActivity(intent);
    }

    public void goToVideoActivityAction(View view) {
        Intent intent = new Intent(this,MediaScreenActivity.class);
        intent.putExtra(MediaScreenActivity.MEDIA_NAME_EXTRAS_NAME, MediaScreenActivity.VIDEO_MEDIA_CODE);
        startActivity(intent);
    }

    public void goToNewBunchActivityAction(View view) {
        Intent intent = new Intent(this,NewBunchActivity.class);
        startActivity(intent);
    }

    public void goToSettingsActivityAction(View view) {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }
}