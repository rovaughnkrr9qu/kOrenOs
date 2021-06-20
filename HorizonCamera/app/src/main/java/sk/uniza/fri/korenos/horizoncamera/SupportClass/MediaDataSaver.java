package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DataOperationServices;

/**
 * Created by Markos on 25. 11. 2016.
 */

public class MediaDataSaver {

    public static void savePhoto(byte[] photoData, String bunchName, OrientationDataPackage orientationData,
                                 DatabaseService database, int rotationDegrees, boolean frontCamera){
        DataOperationServices.checkOrCreateDefaultBunch(MediaLocationsAndSettingsTimeService.getBaseLocation(),
                bunchName, database);

        MediaLocationsAndSettingsTimeService.setBunchName(bunchName);

        MediaLocationData locationData = MediaLocationsAndSettingsTimeService.getPhotoName();

        try {
            if(locationData == null){
                Log.e("Error", "Not possible to find proper photo name.");
                return;
            }

            if(frontCamera){
                rotationDegrees = (rotationDegrees+180)%360;
            }

            FileOutputStream outputStream = new FileOutputStream(locationData.getFullPath());

            Bitmap realImage = MediaLocationsAndSettingsTimeService.rotate(BitmapFactory.decodeByteArray(photoData, 0, photoData.length)
                    , rotationDegrees);
            realImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("Error", "File not found: " + e.getMessage());
            return;
        } catch (IOException e) {
            Log.e("Error", "Error accessing file: " + e.getMessage());
            return;
        }

        if(orientationData == null){
            database.insertRow(new Frame(locationData.getFrameNumber(),locationData.getBaseName(), DataOperationServices.findBunchID(bunchName, database),
                    MediaLocationsAndSettingsTimeService.getCurrentTime(), 0, null, null));
        }else{
            database.insertRow(new Frame(locationData.getFrameNumber(),locationData.getBaseName(), DataOperationServices.findBunchID(bunchName, database),
                    orientationData.getTimeStamp(), 0, orientationData.getPitch(), orientationData.getAzimuth()));
        }

        Cursor cursor = database.selectRow(new Frame(null, null, null, null, null, null, null));
        cursor.moveToFirst();
    }
}
