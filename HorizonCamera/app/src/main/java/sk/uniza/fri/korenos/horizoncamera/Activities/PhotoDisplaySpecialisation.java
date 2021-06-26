package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.hardware.Camera;
import android.view.View;
import android.widget.ImageView;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;

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

        if(cameraShow != null) {
            cameraShow.setMedia(true);
        }
    }

    private void setLightButton(ImageView button){
        boolean flashMode = false;
        Camera.Parameters param = null;
        if(camera != null){
            param = camera.getParameters();
            flashMode = param.getFlashMode().compareTo(Camera.Parameters.FLASH_MODE_OFF)==0;
        }

        if(flashMode){
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
        super.orientationDataReady();
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