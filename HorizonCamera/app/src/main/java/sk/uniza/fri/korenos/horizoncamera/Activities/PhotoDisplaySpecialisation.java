package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettings;

/**
 * Created by Markos on 11. 11. 2016.
 */

public class PhotoDisplaySpecialisation extends CameraDisplayFragment {

    @Override
    protected void setMediaImages() {
        super.setMediaImages();

        ImageView tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentActionButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp));

        tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentChangeButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_videocam_black_24dp));

        cameraShow.setMedia(true);
    }

    @Override
    protected void mediaFunctions() {
        super.mediaFunctions();

        ImageView actionButton = (ImageView) getView().findViewById(R.id.mediaFragmentActionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeCameraPictureAction();
            }
        });
    }

    public void takeCameraPictureAction(){
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                try {
                    String path = MediaLocationsAndSettings.getPhotoName();
                    if(path == null){
                        Log.e("Error", "Not possible to find proper photo name.");
                        return;
                    }
                    FileOutputStream fos = new FileOutputStream(path);
                    fos.write(bytes);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.e("Error", "File not found: " + e.getMessage());
                    return;
                } catch (IOException e) {
                    Log.e("Error", "Error accessing file: " + e.getMessage());
                    return;
                }
                restartPreview();
            }
        });
    }
}
