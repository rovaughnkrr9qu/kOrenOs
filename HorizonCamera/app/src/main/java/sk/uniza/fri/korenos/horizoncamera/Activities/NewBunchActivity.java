package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.File;

import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettings;

/**
 * Created by Markos on 21. 11. 2016.
 */

public class NewBunchActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_bunch_activity_layout);
    }


    public void findExistingBunchAction(View view) {

    }

    public void createBunchAction(View view) {
        String folderName = ((EditText) findViewById(R.id.NewBunchActivityNewBunchNameText)).getText().toString();

        File folder = new File(MediaLocationsAndSettings.getBaseLocation()+folderName);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            System.out.println("vyslo to");
            // Do something on success
        } else {
            // Do something else on failure
        }
    }
}
