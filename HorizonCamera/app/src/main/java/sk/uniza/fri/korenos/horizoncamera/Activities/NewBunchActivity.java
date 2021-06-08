package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.app.Activity;
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
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationDemandingActivityInterface;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationService;

/**
 * Created by Markos on 21. 11. 2016.
 */

public class NewBunchActivity extends AppCompatActivity implements OrientationDemandingActivityInterface{

    private int successRequestCode = 200;
    private String bunchPath;
    private OrientationService orientationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_bunch_activity_layout);
    }


    public void findExistingBunchAction(View view) {

    }

    public void createBunchAction(View view) {
        EditText bunchNameEditText = (EditText) findViewById(R.id.newBunchActivityNewBunchNameText);
        bunchNameEditText.setEnabled(false);

        CheckBox additionalInformationSave = (CheckBox) findViewById(R.id.newBunchActivitySaveAdditionalDataCheckBox);
        additionalInformationSave.setEnabled(false);

        CheckBox pictureMediaCheckBox = (CheckBox) findViewById(R.id.newBunchActivityPictureMediaCheckBox);
        pictureMediaCheckBox.setEnabled(false);

        String folderName = bunchNameEditText.getText().toString();

        bunchPath = MediaLocationsAndSettingsTimeService.getBaseLocation()+"/"+folderName;

        checkOrCreate(MediaLocationsAndSettingsTimeService.getBaseLocation());
        if(checkOrCreate(bunchPath)){
            addToDatabase();
        }
    }

    private boolean checkOrCreate(String path){
        File folder = new File(path);
        if (!folder.exists()) {
            return folder.mkdir();
        }
        return false;
    }

    private void addToDatabase() {
        CheckBox additionalInformationSave = (CheckBox) findViewById(R.id.newBunchActivitySaveAdditionalDataCheckBox);

        if (additionalInformationSave.isChecked()){
            orientationService = new OrientationService(this, (SensorManager) getSystemService(SENSOR_SERVICE));
            orientationService.getMomentalGPSLocation();

            Toast.makeText(this, "Wait for GPS localisation.", Toast.LENGTH_LONG).show();
        }else{
            createBunchInstance(false, null);
        }

    }
    private void createBunchInstance(boolean saveInformation, Location GPSlocation){
        String folderName = ((EditText) findViewById(R.id.newBunchActivityNewBunchNameText)).getText().toString();
        Bunch newBunch;

        if(saveInformation){
            Toast.makeText(this, "GPS location found", Toast.LENGTH_SHORT).show();

            newBunch = new Bunch(null, folderName, MediaLocationsAndSettingsTimeService.getCurrentTime(),
                    bunchPath, 1, GPSlocation.getLatitude(), GPSlocation.getLongitude());
        }else{
            newBunch = new Bunch(null, folderName, MediaLocationsAndSettingsTimeService.getCurrentTime(),
                    bunchPath, 0, null, null);
        }

        if(DatabaseService.getDbInstance(this).insertRow(newBunch) == -1){
            Toast.makeText(this, "It wasn't possible to create bunch with this name.", Toast.LENGTH_SHORT).show();
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
