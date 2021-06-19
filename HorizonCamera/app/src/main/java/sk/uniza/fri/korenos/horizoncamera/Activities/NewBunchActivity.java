package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DataOperationServices;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationDemandingActivityInterface;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationService;

/**
 * Created by Markos on 21. 11. 2016.
 */

public class NewBunchActivity extends AppCompatActivity implements OrientationDemandingActivityInterface{

    private int successRequestCode = 200;
    private int newActivityRequestCode = 23;
    private String bunchPath;
    private OrientationService orientationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_bunch_activity_layout);
    }

    public void findExistingBunchAction(View view) {
        Intent intent = new Intent(this,BunchGalleryActivity.class);
        intent.putExtra(GalleryActivityTemplate.GALLERY_TYPE_EXTRAS_NAME, GalleryActivityTemplate.NEW_BUNCH_CHOOSE_GALLERY_CODE);
        startActivityForResult(intent, newActivityRequestCode);
    }

    public void createBunchAction(View view) {
        disablingFunctions();

        EditText bunchNameEditText = (EditText) findViewById(R.id.newBunchActivityNewBunchNameText);
        String folderName = bunchNameEditText.getText().toString();

        bunchPath = MediaLocationsAndSettingsTimeService.getBaseLocation()+"/"+folderName;

        DataOperationServices.checkOrCreateFolder(MediaLocationsAndSettingsTimeService.getBaseLocation());
        DataOperationServices.checkOrCreateFolder(bunchPath);
        addToDatabase();
    }

    private void disablingFunctions(){
        EditText bunchNameEditText = (EditText) findViewById(R.id.newBunchActivityNewBunchNameText);
        bunchNameEditText.setEnabled(false);

        CheckBox additionalInformationSave = (CheckBox) findViewById(R.id.newBunchActivitySaveAdditionalDataCheckBox);
        additionalInformationSave.setEnabled(false);

        CheckBox pictureMediaCheckBox = (CheckBox) findViewById(R.id.newBunchActivityPictureMediaCheckBox);
        pictureMediaCheckBox.setEnabled(false);
    }

    private void addToDatabase() {
        CheckBox additionalInformationSave = (CheckBox) findViewById(R.id.newBunchActivitySaveAdditionalDataCheckBox);

        if (additionalInformationSave.isChecked()){
            orientationService = new OrientationService(this, (SensorManager) getSystemService(SENSOR_SERVICE));
            orientationService.getMomentalGPSLocation();

            Toast.makeText(this, R.string.newBunchActivityGPSWaitingToastText, Toast.LENGTH_LONG).show();
        }else{
            createBunchInstance(false, null);
        }

    }
    private void createBunchInstance(boolean saveInformation, Location GPSlocation){
        String folderName = ((EditText) findViewById(R.id.newBunchActivityNewBunchNameText)).getText().toString();
        Bunch newBunch;

        if(checkNameInDatabase(folderName)) {
            if (saveInformation) {
                Toast.makeText(this, R.string.newBunchActivityGPSFoundToastText, Toast.LENGTH_SHORT).show();

                newBunch = new Bunch(null, folderName, MediaLocationsAndSettingsTimeService.getCurrentTime(),
                        bunchPath, 1, GPSlocation.getLatitude(), GPSlocation.getLongitude());
            } else {
                newBunch = new Bunch(null, folderName, MediaLocationsAndSettingsTimeService.getCurrentTime(),
                        bunchPath, 0, null, null);
            }

            if (DatabaseService.getDbInstance(this).insertRow(newBunch) == -1) {
                Toast.makeText(this, getResources().getString(R.string.newBunchActivityBunchNotCreatedToastText), Toast.LENGTH_SHORT).show();
            }
        }

        openMediaActivity(folderName);
    }

    private boolean checkNameInDatabase(String bunchName) {
        return DatabaseService.getDbInstance(this).selectRow(new Bunch(null, bunchName, null, null, null, null, null)).getCount() == 0;
    }

    private void openMediaActivity(String bunchName) {
        CheckBox mediaCHeckBox = (CheckBox) findViewById(R.id.newBunchActivityPictureMediaCheckBox);

        if(mediaCHeckBox.isChecked()){      //picture media
            Intent intent = new Intent(this,MediaScreenActivity.class);
            intent.putExtra(MediaScreenActivity.MEDIA_NAME_EXTRAS_NAME, MediaScreenActivity.PHOTO_MEDIA_CODE);
            intent.putExtra(CameraDisplayFragment.BUNCH_NAME_EXTRAS_NAME, bunchName);
            startActivity(intent);
        }else{                              //video media
            Intent intent = new Intent(this,MediaScreenActivity.class);
            intent.putExtra(MediaScreenActivity.MEDIA_NAME_EXTRAS_NAME, MediaScreenActivity.VIDEO_MEDIA_CODE);
            intent.putExtra(CameraDisplayFragment.BUNCH_NAME_EXTRAS_NAME, bunchName);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == BunchGalleryActivity.SUCCESS_RESULT_CODE){
            Bundle extras = data.getExtras();
            String bunchName = extras.getString(BunchGalleryActivity.NEW_BUNCH_RESULT_EXTRAS_NAME);

            EditText bunchNameTextBox = (EditText) findViewById(R.id.newBunchActivityNewBunchNameText);
            bunchNameTextBox.setText(bunchName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(orientationService != null){
            orientationService.stopGPS();
            orientationService = null;
        }
    }

    @Override
    public int getSuccessRequestCode() {
        return successRequestCode;
    }

    @Override
    public Activity getDemandingActivity() {
        return this;
    }

    @Override
    public void getActualOrientationData(double azimuth, double pitch) {
    }

    @Override
    public void orientationDataReady() {
    }

    @Override
    public void GPSDataReady() {
        Location GPSlocation = orientationService.getMomentalGPSLocation();

        if(GPSlocation != null){
            orientationService.stopGPS();
            orientationService = null;
            createBunchInstance(true, GPSlocation);
        }
    }
}
