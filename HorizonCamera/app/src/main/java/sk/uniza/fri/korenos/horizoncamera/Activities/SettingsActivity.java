package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;

/**
 * Created by Markos on 21. 11. 2016.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_layout);

        CheckBox saveAdditionalDataCheckBox = (CheckBox) findViewById(R.id.settingsSaveAdditionalDataCheckBox);
        saveAdditionalDataCheckBox.setChecked(MediaLocationsAndSettingsTimeService.getSaveAdditionalData());

        CheckBox showAdditionalDataOnScreen = (CheckBox) findViewById(R.id.settingsSaveShowAdditionalDataOnScreenCheckBox);
        showAdditionalDataOnScreen.setChecked(MediaLocationsAndSettingsTimeService.getShowAdditionalDataOnScreen());

        CheckBox deleteVideoAfterProcessing = (CheckBox) findViewById(R.id.settingsDeleteVideoAfterProcessingCheckBox);
        deleteVideoAfterProcessing.setChecked(MediaLocationsAndSettingsTimeService.getDeleteVideoAfterProcessing());

        EditText URLAddress = (EditText) findViewById(R.id.settingsURLAddressEditText);
        URLAddress.setText(MediaLocationsAndSettingsTimeService.getServerURLAddress());

        EditText defaultBunch = (EditText) findViewById(R.id.settingsDefaultBunchEditText);
        defaultBunch.setText(MediaLocationsAndSettingsTimeService.getDefaultBunch());

        EditText savedFramesPerSecondVideo = (EditText) findViewById(R.id.settingsVideoSavedFramesPerSecondEditText);
        savedFramesPerSecondVideo.setText(MediaLocationsAndSettingsTimeService.getVideoSavedFramesPerSecond()+"");
    }

    @Override
    protected void onPause() {
        super.onPause();

        EditText URLAddress = (EditText) findViewById(R.id.settingsURLAddressEditText);
        MediaLocationsAndSettingsTimeService.setServerULDAddress(URLAddress.getText().toString());

        EditText defaultBunch = (EditText) findViewById(R.id.settingsDefaultBunchEditText);
        MediaLocationsAndSettingsTimeService.setDefaultBunch(defaultBunch.getText().toString());

        EditText savedFramesPerSecondVideo = (EditText) findViewById(R.id.settingsVideoSavedFramesPerSecondEditText);
        MediaLocationsAndSettingsTimeService.setVideoSavedFramesPerSecond(Integer.parseInt(savedFramesPerSecondVideo.getText().toString()));

        CheckBox saveAdditionalDataCheckBox = (CheckBox) findViewById(R.id.settingsSaveAdditionalDataCheckBox);
        MediaLocationsAndSettingsTimeService.setSaveAdditionalData(saveAdditionalDataCheckBox.isChecked());

        CheckBox showAdditionalDataOnScreen = (CheckBox) findViewById(R.id.settingsSaveShowAdditionalDataOnScreenCheckBox);
        MediaLocationsAndSettingsTimeService.setShowAdditionalDataOnScreen(showAdditionalDataOnScreen.isChecked());

        CheckBox deleteVideoAfterProcessing = (CheckBox) findViewById(R.id.settingsDeleteVideoAfterProcessingCheckBox);
        MediaLocationsAndSettingsTimeService.setDeleteVideoAfterProcessing(deleteVideoAfterProcessing.isChecked());
    }
}
