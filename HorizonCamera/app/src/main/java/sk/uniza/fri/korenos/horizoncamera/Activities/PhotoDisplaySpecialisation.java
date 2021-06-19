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
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationDemandingActivityInterface;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationService;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.AutomaticModeInterface;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.MediaDataSaver;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.OrientationDataPackage;

/**
 * Created by Markos on 11. 11. 2016.
 */

public class PhotoDisplaySpecialisation extends CameraDisplayFragment{

    private boolean photoSequence = false;

    @Override
    protected void setMediaImages() {
        super.setMediaImages();

        ImageView tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentActionButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp));

        tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentChangeButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_videocam_black_24dp));

        cameraShow.setMedia(true);
    }

    private void setLightButton(ImageView button){
        Camera.Parameters param = camera.getParameters();
        if(param.getFlashMode().compareTo(Camera.Parameters.FLASH_MODE_OFF)==0){
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_black_24dp));
        }else{
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_black_24dp));
        }
    }

    @Override
    protected void mediaFunctions() {
        super.mediaFunctions();

        ImageView lightButton = (ImageView) getActivity().findViewById(R.id.mediaFragmentLightButton);
        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnLight();
            }
        });
        setLightButton(lightButton);

        ImageView actionButton = (ImageView) getView().findViewById(R.id.mediaFragmentActionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeCameraPictureAction();
            }
        });
    }

    public void takeCameraPictureAction(){
        if(MediaLocationsAndSettingsTimeService.getSaveAdditionalData()) {
            startOrientationService();
            photoSequence = true;
        }else{
            takePicture();
        }
    }

    @Override
    public void orientationDataReady() {
        if(photoSequence) {
            photoSequence = false;
            takePicture();
            stopOrientationService(false);
        }
    }

    private void turnOnLight(){
        ImageView button = (ImageView) getActivity().findViewById(R.id.mediaFragmentLightButton);
        Camera.Parameters param = camera.getParameters();
        if(param.getFlashMode().compareTo(Camera.Parameters.FLASH_MODE_OFF)==0){
            param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_black_24dp));
        }else{
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_black_24dp));
        }
        camera.setParameters(param);
    }
}