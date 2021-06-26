package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_references);

        CheckBoxPreference saveAdditionalData = (CheckBoxPreference) findPreference(getResources().getString(R.string.settingsSaveAdditionalDataPrefKey));
        CheckBoxPreference showAdditionalData = (CheckBoxPreference) findPreference(getResources().getString(R.string.settingsShowDataOnScreenPrefKey));

        EditTextPreference serverURLData = (EditTextPreference)findPreference(getResources().getString(R.string.settingsServerURLAddressPrefKey));
        EditTextPreference defaultBunchData = (EditTextPreference)findPreference(getResources().getString(R.string.settingsDefaultBunchNamePrefKey));

        EditTextPreference FramesPerSecondData = (EditTextPreference)findPreference(getResources().getString(R.string.settingsVideoSavedFramesPerSecondPrefKey));
        CheckBoxPreference deleteVideoData = (CheckBoxPreference) findPreference(getResources().getString(R.string.settingsDeleteVideoAfterProcessingCheckBoxPrefKey));

        saveAdditionalData.setChecked(MediaLocationsAndSettingsTimeService.getSaveAdditionalData());
        showAdditionalData.setChecked(MediaLocationsAndSettingsTimeService.getShowAdditionalDataOnScreen());

        serverURLData.setText(MediaLocationsAndSettingsTimeService.getServerURLAddress());
        defaultBunchData.setText(MediaLocationsAndSettingsTimeService.getDefaultBunch());

        FramesPerSecondData.setText(MediaLocationsAndSettingsTimeService.getVideoSavedFramesPerSecond()+"");
        deleteVideoData.setChecked(MediaLocationsAndSettingsTimeService.getDeleteVideoAfterProcessing());
    }

    @Override
    protected void onPause() {
        super.onPause();

        CheckBoxPreference saveAdditionalData = (CheckBoxPreference) findPreference(getResources().getString(R.string.settingsSaveAdditionalDataPrefKey));
        CheckBoxPreference showAdditionalData = (CheckBoxPreference) findPreference(getResources().getString(R.string.settingsShowDataOnScreenPrefKey));

        EditTextPreference serverURLData = (EditTextPreference)findPreference(getResources().getString(R.string.settingsServerURLAddressPrefKey));
        EditTextPreference defaultBunchData = (EditTextPreference)findPreference(getResources().getString(R.string.settingsDefaultBunchNamePrefKey));

        EditTextPreference FramesPerSecondData = (EditTextPreference)findPreference(getResources().getString(R.string.settingsVideoSavedFramesPerSecondPrefKey));
        CheckBoxPreference deleteVideoData = (CheckBoxPreference) findPreference(getResources().getString(R.string.settingsDeleteVideoAfterProcessingCheckBoxPrefKey));

        MediaLocationsAndSettingsTimeService.setSaveAdditionalData(saveAdditionalData.isChecked());
        MediaLocationsAndSettingsTimeService.setShowAdditionalDataOnScreen(showAdditionalData.isChecked());

        MediaLocationsAndSettingsTimeService.setServerULDAddress(serverURLData.getText());
        MediaLocationsAndSettingsTimeService.setDefaultBunch(defaultBunchData.getText());

        MediaLocationsAndSettingsTimeService.setVideoSavedFramesPerSecond(Integer.parseInt(FramesPerSecondData.getText()));
        MediaLocationsAndSettingsTimeService.setDeleteVideoAfterProcessing(deleteVideoData.isChecked());
    }
}
