package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.EntityInterface;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;

/**
 * Created by Markos on 26. 11. 2016.
 */

public class DataOperationServices {

    public static String getBunchPath(String bunchName, DatabaseService databaseInstance){
        Cursor selectedBunch = databaseInstance.selectRow(new Bunch(null, bunchName, null, null, null, null, null));
        selectedBunch.moveToFirst();
        return selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[3]));
    }

    public static String getBunchPath(int bunchID, DatabaseService databaseInstance){
        Cursor selectedBunch = databaseInstance.selectRow(new Bunch(bunchID, null, null, null, null, null, null));
        selectedBunch.moveToFirst();
        return selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[3]));
    }

    public static String getPicturePath(String bunchName, String pictureName, int pictureNumber, DatabaseService databaseInstance){
        String bunchPath = getBunchPath(bunchName, databaseInstance);

        StringBuilder picturePath = new StringBuilder(bunchPath);
        picturePath.append("/");
        picturePath.append(pictureName);
        picturePath.append(pictureNumber);
        picturePath.append(".jpeg");

        return picturePath.toString();
    }

    public static int findBunchID(String bunchName, DatabaseService databaseInstance){
        Cursor selectedBunch = databaseInstance.selectRow(new Bunch(null, bunchName, null, null, null, null, null));

        if(selectedBunch.getCount() == 0){
            return -1;
        }
        selectedBunch.moveToFirst();

        return Integer.parseInt(selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[0])));
    }

    public static String findBunchName(int bunchID, DatabaseService databaseInstance){
        Cursor selectedBunch = databaseInstance.selectRow(new Bunch(bunchID, null, null, null, null, null, null));

        if(selectedBunch.getCount() == 0){
            return null;
        }
        selectedBunch.moveToFirst();

        return selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[1]));
    }

    public static Frame packDataToFrame(int bunchID, String pictureName, int pictureNumber, DatabaseService databaseInstance){
        Cursor selectedFrame = databaseInstance.selectRow(new Frame(pictureNumber, pictureName, bunchID, null, null, null, null));
        selectedFrame.moveToFirst();

        long date = selectedFrame.getLong(selectedFrame.getColumnIndex(Frame.COLUMN_NAMES[3]));
        int format = selectedFrame.getInt(selectedFrame.getColumnIndex(Frame.COLUMN_NAMES[4]));
        Double pitch = selectedFrame.getDouble(selectedFrame.getColumnIndex(Frame.COLUMN_NAMES[5]));
        Double azimuth = selectedFrame.getDouble(selectedFrame.getColumnIndex(Frame.COLUMN_NAMES[6]));

        return new Frame(pictureNumber, pictureName, bunchID, date, format, pitch, azimuth);
    }

    public static Frame packDataToFrame(String bunchName, String pictureName, int pictureNumber, DatabaseService databaseInstance){
        return packDataToFrame(findBunchID(bunchName, databaseInstance), pictureName, pictureNumber, databaseInstance);
    }

    public static Bitmap getSavedImage(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path, options);
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

    public static String formatCodeDecoder(int formatCode){
        switch (formatCode){
            case 0: return "Picture";
            case 1: return "Video";
            default: return "Unknown";
        }
    }

    public static String composeImagePath(String bunchPath, String frameName, int frameNumber){
        StringBuilder imagePathBuilder = new StringBuilder(bunchPath);
        imagePathBuilder.append("/");
        imagePathBuilder.append(frameName);
        imagePathBuilder.append(frameNumber);
        imagePathBuilder.append(".jpeg");

        return imagePathBuilder.toString();
    }

    public static String composeImagePath(String bunchPath, String frameName){
        StringBuilder imagePathBuilder = new StringBuilder(bunchPath);
        imagePathBuilder.append("/");
        imagePathBuilder.append(frameName);

        return imagePathBuilder.toString();
    }

    public static String getFirstImageOfFolder(String path){
        File directory = new File(path);
        if(directory == null){
            return null;
        }
        File[] contents = directory.listFiles();
        if (contents == null || contents.length == 0) {
            return null;
        }else {
            for(File file : contents){
                if(file.getName().endsWith(".jpeg")){
                    return file.getName();
                }
            }
        }
        return null;
    }

    public static List<EntityInterface> getAllFramesOfBunch(String bunchName, Context context){
        DatabaseService database = DatabaseService.getDbInstance(context);

        Frame selectDefinitionFrame = new Frame(null, null, DataOperationServices.findBunchID(bunchName,
                database), null, null, null, null);
        Cursor databaseData = database.selectRow(selectDefinitionFrame);

        ArrayList<EntityInterface> inputListData = new ArrayList<>();
        Frame listItem;

        int frameNumber;
        String frameName;
        int frameBunchID;
        long frameDate;
        int frameFormat;
        Double framePitch;
        Double frameAzimuth;

        databaseData.moveToFirst();
        for (int i = 0; i < databaseData.getCount(); i++) {

            frameNumber = databaseData.getInt(databaseData.getColumnIndex(Frame.COLUMN_NAMES[0]));
            frameName = databaseData.getString(databaseData.getColumnIndex(Frame.COLUMN_NAMES[1]));
            frameBunchID = databaseData.getInt(databaseData.getColumnIndex(Frame.COLUMN_NAMES[2]));
            frameDate = databaseData.getLong(databaseData.getColumnIndex(Frame.COLUMN_NAMES[3]));
            frameFormat = databaseData.getInt(databaseData.getColumnIndex(Frame.COLUMN_NAMES[4]));
            framePitch = databaseData.getDouble(databaseData.getColumnIndex(Frame.COLUMN_NAMES[5]));
            frameAzimuth = databaseData.getDouble(databaseData.getColumnIndex(Frame.COLUMN_NAMES[6]));

            listItem = new Frame(frameNumber, frameName, frameBunchID, frameDate, frameFormat, framePitch, frameAzimuth);

            inputListData.add(listItem);
            databaseData.moveToNext();
        }

        return inputListData;
    }

    public static int getCountOfPicturesInBunch(String bunchName, DatabaseService database){
        Cursor selectedBunch = database.selectRow(new Frame(null, null, DataOperationServices.findBunchID(bunchName, database), null, null, null, null));
        return selectedBunch.getCount();
    }

    public static boolean checkOrCreateFolder(String path){
        File folder = new File(path);
        if (!folder.exists()) {
            return folder.mkdir();
        }
        return false;
    }

    public static void checkOrCreateDefaultBunch(String baseLocation, String defaultBunchName, DatabaseService database) {
        checkOrCreateFolder(baseLocation);
        String defaultBunchNamePath = baseLocation+"/"+defaultBunchName;
        checkOrCreateFolder(defaultBunchNamePath);

        Bunch newBunch = new Bunch(null, defaultBunchName, MediaLocationsAndSettingsTimeService.getCurrentTime(),
                defaultBunchNamePath, 0, null, null);

        Bunch checkBunch = new Bunch(null, defaultBunchName, null, null, null, null, null);

        if(database.selectRow(checkBunch).getCount() == 0) {
            database.insertRow(newBunch);
        }
    }
}
