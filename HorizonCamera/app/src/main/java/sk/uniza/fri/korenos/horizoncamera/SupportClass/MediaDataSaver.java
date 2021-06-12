package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;

/**
 * Created by Markos on 25. 11. 2016.
 */

public class MediaDataSaver {

    public static void savePhoto(byte[] photoData, String bunchName, OrientationDataPackage orientationData, Context context){
        MediaLocationsAndSettingsTimeService.setBunchName(bunchName);

        MediaLocationData locationData = MediaLocationsAndSettingsTimeService.getPhotoName();

        try {
            if(locationData == null){
                Log.e("Error", "Not possible to find proper photo name.");
                return;
            }
            FileOutputStream fos = new FileOutputStream(locationData.getFullPath());
            fos.write(photoData);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("Error", "File not found: " + e.getMessage());
            return;
        } catch (IOException e) {
            Log.e("Error", "Error accessing file: " + e.getMessage());
            return;
        }

        DatabaseService database = DatabaseService.getDbInstance(context);
        database.insertRow(new Frame(locationData.getFrameNumber(),locationData.getBaseName(), database.findBunchID(bunchName),
                orientationData.getTimeStamp(), 0, orientationData.getPitch(), orientationData.getAzimuth()));

        Cursor cursor = database.selectRow(new Frame(null, null, null, null, null, null, null));
        cursor.moveToFirst();
    }
}
