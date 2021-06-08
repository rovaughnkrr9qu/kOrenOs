package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

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
    }

    public void saveAdditionalDataChangedAction(View view){
        CheckBox saveAdditionalDataCheckBox = (CheckBox) findViewById(R.id.settingsSaveAdditionalDataCheckBox);
        MediaLocationsAndSettingsTimeService.setSaveAdditionalData(saveAdditionalDataCheckBox.isChecked());
    }
}
