package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationDemandingActivityInterface;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationService;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.AutomaticModeInterface;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.CameraView;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.DataVisitorInterface;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.MediaDataSaver;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.OrientationDataPackage;

/**
 * Created by Markos on 10. 11. 2016.
 */

public class CameraDisplayFragment extends Fragment implements OrientationDemandingActivityInterface, DataVisitorInterface {

    public static final String BUNCH_NAME_EXTRAS_NAME = "bunchName";

    protected Camera camera = null;
    protected CameraView cameraShow;
    protected CameraSides cameraActiveSide;

    private FrameLayout displayArea;

    private int successCode = 200;

    private int zoomLevel = 0;
    private int zoomStep = 5;

    protected String bunchName;

    protected OrientationService orientationService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.media_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle extrasData = getArguments();
        if(extrasData != null) {
            String bunch = extrasData.getString(BUNCH_NAME_EXTRAS_NAME);
            if (bunch != null) {
                bunchName = bunch;
            } else {
                bunchName = MediaLocationsAndSettingsTimeService.getDefaultBunch();
            }
        }

        if(!checkCameraDevice(getActivity().getApplicationContext())){
            Toast.makeText(getActivity().getApplicationContext(), "There is not camera device.", Toast.LENGTH_SHORT).show();
            Log.e("Error", "There is not camera device.");
            return;
        }

        cameraActiveSide = CameraSides.back;
        startDisplayAndCamera();
        setMediaImages();

        Button zoomIn = (Button) getView().findViewById(R.id.mediaFragmentZoomIn);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomIn(v);
            }
        });

        Button zoomOut = (Button) getView().findViewById(R.id.mediaFragmentZoomOut);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOut(v);
            }
        });

        ImageView cameraChangeButton = (ImageView) getActivity().findViewById(R.id.mediaFragmentChangeCameraButton);
        if(setCameraChangeOption(cameraChangeButton)){
            cameraChangeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeCameraSide();
                }
            });
        }

        mediaFunctions();
    }

    private void startDisplayAndCamera(){
        displayArea = (FrameLayout) getView().findViewById(R.id.mediaFragmentDisplaySurface);
        startPreview(findCameraCode(cameraActiveSide));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraClose();
        cameraShow = null;
        displayArea.removeAllViews();
        stopOrientationService(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setVisibilityOfOrientationBar();
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

    private void setVisibilityOfOrientationBar(){
        RelativeLayout orientationBar = (RelativeLayout) getView().findViewById(R.id.mediaFragmentOrientationBar);
        if(MediaLocationsAndSettingsTimeService.getShowAdditionalDataOnScreen()){
            orientationBar.setVisibility(View.VISIBLE);
            startOrientationService();
        }else{
            orientationBar.setVisibility(View.INVISIBLE);
            stopOrientationService(false);
        }
    }

    protected void takePicture(){
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                OrientationDataPackage orientationData = null;
                if(MediaLocationsAndSettingsTimeService.getSaveAdditionalData()) {
                    orientationData = orientationService.getActualOrientation();
                }

                boolean backCameraSide = true;
                if(cameraActiveSide == CameraSides.back){
                    backCameraSide = false;
                }

                int rotationDegrees = MediaLocationsAndSettingsTimeService.orientationChange(getActivity().getApplicationContext());
                MediaDataSaver.savePhoto(bytes, bunchName, orientationData,
                        DatabaseService.getDbInstance(getActivity().getApplicationContext()), rotationDegrees, backCameraSide, true);
                restartPreview();
            }
        });
    }

    protected void startOrientationService(){
        if(orientationService == null) {
            orientationService = new OrientationService(this, (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE));
        }
        orientationService.startOrientationSensors();
        orientationService.addDataVisitor(this);
    }

    protected void stopOrientationService(boolean highPriority){
        if(highPriority || !MediaLocationsAndSettingsTimeService.getShowAdditionalDataOnScreen()) {
            if (orientationService != null) {
                orientationService.stopOrientationSensors();
                orientationService.stopGPS();
                orientationService.removeDataVisitor(this);
            }
        }
    }

    protected void restartPreview(){
        startPreview(findCameraCode(cameraActiveSide));
    }

    protected void setMediaImages() {
        Button zoomIn = (Button) getView().findViewById(R.id.mediaFragmentZoomIn);
        Button zoomOut = (Button) getView().findViewById(R.id.mediaFragmentZoomOut);

        zoomIn.getBackground().setAlpha(100);
        zoomOut.getBackground().setAlpha(100);

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

        resetSecondaryButtonListener();

        setVisibilityOfOrientationBar();
    }

    protected void resetSecondaryButtonListener(){
        ImageView secondaryButton = (ImageView) getView().findViewById(R.id.mediaFragmentGalleryButton);

        secondaryButton.setOnTouchListener(null);

        secondaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FrameGalleryActivity.class);
                intent.putExtra(GalleryActivityTemplate.GALLERY_TYPE_EXTRAS_NAME, GalleryActivityTemplate.INSIDE_BUNCH_GALLERY_CODE);
                intent.putExtra(FrameGalleryActivity.SELECTED_BUNCH_EXTRAS_NAME, bunchName);
                startActivity(intent);
            }
        });
    }

    protected void mediaFunctions(){
    }
	
	protected void zoomIn(View view){
        Camera.Parameters param = camera.getParameters();
        int maxZoom;
        if(param.isZoomSupported()){
            maxZoom = param.getMaxZoom();

            if(zoomLevel+zoomStep >= maxZoom){
                zoomLevel = maxZoom;
            }else{
                zoomLevel += zoomStep;
            }
            param.setZoom(zoomLevel);
        }
        camera.setParameters(param);
    }

    protected void zoomOut(View view){
        Camera.Parameters param = camera.getParameters();
        if(param.isZoomSupported()){
            if(zoomLevel-zoomStep <= 0){
                zoomLevel = 0;
            }else{
                zoomLevel -= zoomStep;
            }
            param.setZoom(zoomLevel);
        }
        camera.setParameters(param);
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
    }

    @Override
    public void GPSDataReady() {
    }

    @Override
    public void getOrientationData(OrientationDataPackage actualOrientationData) {
        if(MediaLocationsAndSettingsTimeService.getShowAdditionalDataOnScreen()){
            TextView azimuthTextBox = (TextView) getView().findViewById(R.id.mediaFragmentOrientationAzimuthTextBar);
            TextView pitchTextBox = (TextView) getView().findViewById(R.id.mediaFragmentOrientationPitchTextBar);

            azimuthTextBox.setText(String.format("%.2f°", actualOrientationData.getAzimuth()));
            pitchTextBox.setText(String.format("%.2f°", actualOrientationData.getPitch()));
        }
    }

    protected enum CameraSides {
        back, front
    }
}