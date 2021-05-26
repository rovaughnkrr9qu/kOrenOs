package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.CameraView;

/**
 * Created by Markos on 10. 11. 2016.
 */

public class CameraDisplayFragment extends Fragment {

    protected Camera camera = null;
    protected CameraView cameraShow;
    private FrameLayout displayArea;
    private CameraSides cameraActiveSide;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.media_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!checkCameraDevice(getActivity().getApplicationContext())){
            Toast.makeText(getActivity().getApplicationContext(), "There is not camera device.", Toast.LENGTH_SHORT).show();
            Log.e("Error", "There is not camera device.");
            return;
        }

        displayArea = (FrameLayout) getView().findViewById(R.id.mediaFragmentDisplaySurface);

        cameraActiveSide = CameraSides.back;
        startPreview(findCameraCode(cameraActiveSide));

        setMediaImages();

        ImageView imageButton = (ImageView) getActivity().findViewById(R.id.mediaFragmentLightButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnLight();
            }
        });
        setLightButton(imageButton);

        imageButton = (ImageView) getActivity().findViewById(R.id.mediaFragmentChangeCameraButton);
        if(setCameraChangeOption(imageButton)){
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeCameraSide();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraClose();
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

    private void changeCameraSide(){
        ImageView imageButton = (ImageView) getActivity().findViewById(R.id.mediaFragmentChangeCameraButton);
        switch (cameraActiveSide){
            case back:
                cameraActiveSide = CameraSides.front;
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_rear_black_24dp));
                break;
            case front:
                cameraActiveSide = CameraSides.back;
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_front_black_24dp));
                break;
        }
        startPreview(findCameraCode(cameraActiveSide));
    }

    private void setLightButton(ImageView button){
        Camera.Parameters param = camera.getParameters();
        if(param.getFlashMode().compareTo(Camera.Parameters.FLASH_MODE_OFF)==0){
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_black_24dp));
        }else{
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_black_24dp));
        }
    }

    private boolean setCameraChangeOption(ImageView button){
        if(Camera.getNumberOfCameras()<2){
            Drawable icon = getResources().getDrawable(R.drawable.ic_camera_front_black_24dp);
            icon.setAlpha(135);
            button.setImageDrawable(icon);
            return false;
        }else{
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_front_black_24dp));
            return true;
        }
    }

    private int findCameraCode(CameraSides cameraCode){
        switch (cameraCode){
            case back:
                return 0;
            case front:
                return 1;
            default: return 0;
        }
    }

    private boolean checkCameraDevice(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }else{
            return false;
        }
    }

    private boolean cameraOpen(int cameraID) {
        if (camera != null) {
            cameraClose();
        }
        try {
            camera = Camera.open(cameraID);
            return true;
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "Failed to open Camera.");
            e.printStackTrace();
            return false;
        }
    }

    private void cameraClose(){
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void startPreview(int cameraID){
        cameraClose();
        if(!cameraOpen(cameraID)){
            return;
        }
        cameraShow = new CameraView(getActivity().getApplicationContext(), camera, true);
        displayArea.removeAllViews();
        displayArea.addView(cameraShow);
    }

    protected void restartPreview(){
        startPreview(findCameraCode(cameraActiveSide));
    }

    protected void setMediaImages() {
        Toolbar upToolbar = (Toolbar) getView().findViewById(R.id.mediaFragmentUpToolbar);
        upToolbar.getBackground().setAlpha(100);

        Toolbar downToolbar = (Toolbar) getView().findViewById(R.id.mediaFragmentDownToolbar);
        downToolbar.getBackground().setAlpha(100);

        ImageView tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentMainButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));

        tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentSettingsButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings_black_24dp));

        tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentGalleryButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_portrait_black_24dp));
    }

    private enum CameraSides {
        back, front
    }
}