package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.content.Context;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationService;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.AutomaticModeInterface;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.PanoramaMode;

/**
 * Created by Markos on 10. 11. 2016.
 */

public class VideoDisplaySpecialisation extends CameraDisplayFragment implements AutomaticModeInterface{

    private MediaRecorder mediaRecorder;
    private boolean panoramaMode = false;
    private RecordingState recState = RecordingState.preview;

    @Override
    protected void setMediaImages() {
        super.setMediaImages();

        ImageView tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentChangeButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp));

        tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentLightButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_panorama_horizontal_black_24dp));

        setPanoramaButtonFunction(tempImage);

        cameraShow.setMedia(false);
    }

    @Override
    public void onDestroy() {
        if(recState == RecordingState.pause || recState == RecordingState.recording){
            stopRecording();
        }
        recState = RecordingState.preview;
        super.onDestroy();
    }

    @Override
    protected void mediaFunctions() {
        super.mediaFunctions();

        ImageView actionButton = (ImageView) getView().findViewById(R.id.mediaFragmentActionButton);
        ImageView secondaryButton = (ImageView) getView().findViewById(R.id.mediaFragmentGalleryButton);

        switch(recState){
            case preview:
                previewStateAction(actionButton, secondaryButton);
                break;
            case recording:
                recordingStateAction(actionButton, secondaryButton);
                break;
            case pause:
                pauseStateAction(actionButton, secondaryButton);
                break;
            case panorama:
                ImageView panoramaButton = (ImageView) getView().findViewById(R.id.mediaFragmentLightButton);
                panoramaModeAction(actionButton, panoramaButton);
                break;
        }
    }

    private void previewStateAction(ImageView actionButton, ImageView secondaryButton){
        changeActionButtonsAppearance(actionButton, R.drawable.ic_theaters_black_24dp, secondaryButton, R.drawable.ic_portrait_black_24dp);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recState = RecordingState.recording;

                startRecording();
                mediaFunctions();
            }
        });

        ImageView panoramaButton = (ImageView) getView().findViewById(R.id.mediaFragmentLightButton);
        setPanoramaButtonFunction(panoramaButton);
        resetSecondaryButtonListener();

        stopOrientationService(false);
    }

    private void recordingStateAction(ImageView actionButton, ImageView secondaryButton){
        setPanoramaDisable();

        changeActionButtonsAppearance(actionButton, R.drawable.ic_pause_circle_filled_black_24dp, secondaryButton, R.drawable.ic_stop_black_24dp);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recState = RecordingState.pause;
                mediaFunctions();
            }
        });

        secondaryButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                recState = RecordingState.preview;

                stopRecording();
                mediaFunctions();
                return true;
            }
        });
    }

    private void pauseStateAction(ImageView actionButton, ImageView secondaryButton){
        changeActionButtonsAppearance(actionButton, R.drawable.ic_play_circle_filled_black_24dp, secondaryButton, R.drawable.ic_stop_black_24dp);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recState = RecordingState.recording;
                mediaFunctions();
            }
        });
    }

    private void startPanorama(ImageView actionButton) {
        panoramaMode = true;

        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_black_24dp));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panoramaRollback();
                recState = RecordingState.preview;
                mediaFunctions();
            }
        });

        PanoramaMode panoramaMode = new PanoramaMode(this ,36, new OrientationService(this,
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE)), 45, 20, 0.5);

        panoramaMode.startPanoramaSequence();
    }

    private void panoramaModeAction(final ImageView actionButton, ImageView panoramaButton){
        changeActionButtonsAppearance(actionButton, R.drawable.ic_panorama_horizontal_black_24dp, panoramaButton, R.drawable.ic_theaters_black_24dp);

        panoramaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panoramaRollback();
                recState = RecordingState.preview;
                mediaFunctions();
            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPanorama(actionButton);
            }
        });

        startOrientationService();
    }

    private void panoramaRollback(){
        ImageView panoramaButton = (ImageView) getView().findViewById(R.id.mediaFragmentLightButton);
        panoramaButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_panorama_horizontal_black_24dp));
    }

    private void setPanoramaButtonFunction(final ImageView panoramaButton) {
        panoramaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recState = RecordingState.panorama;
                mediaFunctions();
            }
        });
    }

    private void setPanoramaDisable(){
        ImageView panoramaButton = (ImageView) getView().findViewById(R.id.mediaFragmentLightButton);
        panoramaButton.getBackground().setAlpha(255);

        panoramaButton.setOnClickListener(null);
    }

    private void changeActionButtonsAppearance(ImageView actionImageButton, int actionButtonIcon, ImageView secondaryImageButton, int secondaryButtonIcon){
        actionImageButton.setImageDrawable(getResources().getDrawable(actionButtonIcon));
        secondaryImageButton.setImageDrawable(getResources().getDrawable(secondaryButtonIcon));
    }

    private void startRecording(){
        if (!prepareMediaRecorder()) {
            Toast.makeText(getActivity(), "Failure!", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        Toast.makeText(getActivity(), "Recording started", Toast.LENGTH_LONG).show();
        Runnable videoRecording = new Runnable() {
            public void run() {
                try {
                    mediaRecorder.start();
                } catch (final Exception ex) {
                }
            }
        };
        executeOnProcessThread(videoRecording);
    }

    private void stopRecording(){
        mediaRecorder.stop();
        closeMediaRecorder();
        Toast.makeText(getActivity(), "Video captured!", Toast.LENGTH_LONG).show();
        restartPreview();
    }

    private boolean prepareMediaRecorder()
    {
        mediaRecorder = new MediaRecorder();
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        profile.fileFormat = MediaLocationsAndSettingsTimeService.selectedVideoFormat();
        mediaRecorder.setProfile(profile);
        mediaRecorder.setOutputFile(MediaLocationsAndSettingsTimeService.getVideoName().getFullPath());
        mediaRecorder.setOrientationHint(cameraShow.orientationChange());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            closeMediaRecorder();
            return false;
        } catch (IOException e) {
            closeMediaRecorder();
            return false;
        }
        return true;
    }

    private void closeMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
    }

    private void executeOnProcessThread(Runnable task){
        Thread processThread = new Thread(task);
        processThread.start();
    }

    @Override
    public void takePictureCall(double degree) {
        takePicture();
        Toast.makeText(getActivity().getApplicationContext(), degree+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void automaticModeDone() {
        panoramaRollback();
        recState = RecordingState.preview;
        mediaFunctions();
    }


    private enum RecordingState {
        preview, recording, pause, panorama
    }
}
