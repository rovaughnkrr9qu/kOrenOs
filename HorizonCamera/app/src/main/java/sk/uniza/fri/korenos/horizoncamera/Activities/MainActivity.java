package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DataOperationServices;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        setBackground();
        setLogo();

        MediaLocationsAndSettingsTimeService.loadSettings(getSharedPreferences(MediaLocationsAndSettingsTimeService.SETTINGS_SAVE_PREFERENCE_NAME, 0));

        DataOperationServices.checkOrCreateDefaultBunch(MediaLocationsAndSettingsTimeService.getBaseLocation(),
                MediaLocationsAndSettingsTimeService.getDefaultBunch(), DatabaseService.getDbInstance(this));
    }

    private void setBackground() {
        RelativeLayout mainActivityBackground = (RelativeLayout) findViewById(R.id.mainActivityBackgraound);
        Drawable image = getResources().getDrawable(R.drawable.main_screen_background_720x1280, null);

        Point resolution = getDisplayResolution();

        Bitmap bitmap = ((BitmapDrawable)image).getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, resolution.x, resolution.y, false);

        mainActivityBackground.setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    private void setLogo(){
        ImageView mainLogoImage = (ImageView) findViewById(R.id.mainLogoImage);
        Drawable image = getResources().getDrawable(R.drawable.horizon_camera_main_logo, null);

        Bitmap bitmap = ((BitmapDrawable)image).getBitmap();

        Point resolution = getDisplayResolution();
        double scaleRate = 1.0*resolution.x/bitmap.getWidth();

        bitmap = Bitmap.createScaledBitmap(bitmap, resolution.x, (int)(bitmap.getHeight()*scaleRate), false);
        mainLogoImage.setImageBitmap(bitmap);
    }

    private Point getDisplayResolution(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
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