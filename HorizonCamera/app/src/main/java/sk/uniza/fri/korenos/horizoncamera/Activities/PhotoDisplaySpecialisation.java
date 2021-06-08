package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationDemandingActivityInterface;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationService;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.AutomaticModeInterface;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.OrientationDataPackage;

/**
 * Created by Markos on 11. 11. 2016.
 */

public class PhotoDisplaySpecialisation extends CameraDisplayFragment implements OrientationDemandingActivityInterface, AutomaticModeInterface {

    private int successCode = 200;
    private OrientationService orientationService;
    private boolean photoSequence = false;

    @Override
    protected void setMediaImages() {
        super.setMediaImages();

        ImageView tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentActionButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp));

        tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentChangeButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_videocam_black_24dp));

        cameraShow.setMedia(true);

        /*OrientationService os = new OrientationService(this, (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE));
        PanoramaMode pm = new PanoramaMode(this, 36, os, 180, 18);
        pm.startPanoramaSequence();*/
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        orientationService.stopOrientationSensors();
    }

    public void takeCameraPictureAction(){
        if(MediaLocationsAndSettingsTimeService.getSaveAdditionalData()) {
            orientationService = new OrientationService(this, (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE));
            orientationService.startOrientationSensors();
            photoSequence = true;
        }else{
            takePicture();
        }
    }

    private void takePicture(){
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                OrientationDataPackage ODP = orientationService.getActualOrientation();
                orientationService.stopOrientationSensors();

                try {
                    String path = MediaLocationsAndSettingsTimeService.getPhotoName();
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

    @Override
    public void takePicture(double degree) {
        Toast.makeText(getApplicationContext(),degree+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void automaticModeDone() {

    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public int getSuccessRequestCode() {
        return successCode;
    }

    @Override
    public Activity getDemandingActivity() {
        return getActivity();
    }

    @Override
    public void orientationDataReady() {
        if(photoSequence) {
            takePicture();
            photoSequence = false;
        }
    }

    @Override
    public void GPSDataReady() {
    }
}
