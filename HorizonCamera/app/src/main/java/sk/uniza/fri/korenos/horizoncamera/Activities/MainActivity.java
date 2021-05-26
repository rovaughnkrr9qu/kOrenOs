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
        setContentView(R.layout.activity_main);
    }

    public void goToGalleryActivityAction(View view) {
    }

    public void goToPhotoActivityAction(View view) {
        Intent intent = new Intent(this,MediaScreenActivity.class);
        intent.putExtra("mediaName", "photo");
        startActivity(intent);
    }

    public void goToVideoActivityAction(View view) {
        Intent intent = new Intent(this,MediaScreenActivity.class);
        intent.putExtra("mediaName", "video");
        startActivity(intent);
    }

    public void goToNewBunchActivityAction(View view) {
    }

    public void goToSettingsActivityAction(View view) {
    }
}