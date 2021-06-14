package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
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

    public static int getCountOfPicturesInBunch(String bunchName, DatabaseService database){
        Cursor selectedBunch = database.selectRow(new Frame(null, null, DataOperationServices.findBunchID(bunchName, database), null, null, null, null));
        return selectedBunch.getCount();
    }
}
