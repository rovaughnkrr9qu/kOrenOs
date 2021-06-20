package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sk.uniza.fri.korenos.horizoncamera.SupportClass.MediaLocationData;

/**
 * Created by Markos on 10. 11. 2016.
 */

public class MediaLocationsAndSettingsTimeService {
    public static final String SETTINGS_SAVE_PREFERENCE_NAME = "horizonCameraSettingsSaves";

    public static final String SETTINGS_SAVE_NAME_SERVER_URL_ADDRESS = "serverURLAddressSave";
    public static final String SETTINGS_SHOW_ADDITIONAL_DATA_ON_SCREEN = "showAdditionalDataOnScreen";
    public static final String SETTINGS_SAVE_NAME_ADDITIONAL_DATA_SAVE_STATUS = "additionalDataSave";
    public static final String SETTINGS_SAVE_NAME_DEFAULT_BUNCH = "defaultBrunchSave";

    private static String serverURLAddress = "http://httpbin.org/post";

    private static String baseLocation = Environment.getExternalStorageDirectory() + "/HorizonCamera";

    private static String photoType = ".jpeg";
    private static String videoType = ".mp4";
    private static String photoBaseName = "photo";
    private static String videoBaseName = "video";
    private static String defaultBunch = "DefaultBunch";

    private static boolean saveAdditionalData = true;
    private static boolean showAdditionalDataOnScreen = false;

    private static String bunchName;
    private static SharedPreferences savePreferences;

    public static void loadSettings(SharedPreferences paSavePreferences){
        savePreferences = paSavePreferences;

        String loadedServerURLAddress = savePreferences.getString(SETTINGS_SAVE_NAME_SERVER_URL_ADDRESS, null);
        if(loadedServerURLAddress != null){
            serverURLAddress = loadedServerURLAddress;
        }

        String loadDefaultBunch = savePreferences.getString(SETTINGS_SAVE_NAME_DEFAULT_BUNCH, null);
        if(loadDefaultBunch != null){
            defaultBunch = loadDefaultBunch;
        }
        bunchName = defaultBunch;

        boolean loadAdditionalDataSave = savePreferences.getBoolean(SETTINGS_SAVE_NAME_ADDITIONAL_DATA_SAVE_STATUS, true);
        saveAdditionalData = loadAdditionalDataSave;

        boolean loadShowAdditionalData = savePreferences.getBoolean(SETTINGS_SHOW_ADDITIONAL_DATA_ON_SCREEN, false);
        showAdditionalDataOnScreen = loadShowAdditionalData;
    }

    public static String getDefaultBunch(){
        return defaultBunch;
    }

    public static void setDefaultBunch(String bunchName){
        defaultBunch = bunchName;

        SharedPreferences.Editor editor = savePreferences.edit();
        editor.putString(SETTINGS_SAVE_NAME_DEFAULT_BUNCH, defaultBunch);
        editor.commit();
    }

    public static void setBaseLocation(String location){
        baseLocation = location;
    }

    public static String getBaseLocation(){
        return baseLocation;
    }

    public static void setPhotoBaseName(String baseName){
        photoBaseName = baseName;
    }

    public static void setVideoBaseName(String baseName){
        videoBaseName = baseName;
    }

    public static void setServerULDAddress(String serverAddress){
        serverURLAddress = serverAddress;

        SharedPreferences.Editor editor = savePreferences.edit();
        editor.putString(SETTINGS_SAVE_NAME_SERVER_URL_ADDRESS, serverURLAddress);
        editor.commit();
    }

    public static String getServerURLAddress(){
        return serverURLAddress;
    }

    public static long getCurrentTime(){
        return System.currentTimeMillis();
    }

    public static String transformToTime(long time){
        return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.UK).format(new Date(time));
    }

    public static void setSaveAdditionalData(boolean saveAdditionalDataStatus){
        saveAdditionalData = saveAdditionalDataStatus;

        SharedPreferences.Editor editor = savePreferences.edit();
        editor.putBoolean(SETTINGS_SAVE_NAME_ADDITIONAL_DATA_SAVE_STATUS, saveAdditionalDataStatus);
        editor.commit();
    }

    public static boolean getShowAdditionalDataOnScreen(){
        return showAdditionalDataOnScreen;
    }

    public static void setShowAdditionalDataOnScreen(boolean showAdditionalDataOnScreenStatus){
        showAdditionalDataOnScreen = showAdditionalDataOnScreenStatus;

        SharedPreferences.Editor editor = savePreferences.edit();
        editor.putBoolean(SETTINGS_SHOW_ADDITIONAL_DATA_ON_SCREEN, showAdditionalDataOnScreenStatus);
        editor.commit();
    }

    public static boolean getSaveAdditionalData(){
        return saveAdditionalData;
    }

    public static void setBunchName(String selectedBunchName){
        bunchName = selectedBunchName;
    }

    public static int orientationChange(Context context){
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int momentalRotation = manager.getDefaultDisplay().getRotation();

        return orientationCalculatior(momentalRotation);
    }

    public static int orientationCalculatior(int momentalRotation){
        switch(momentalRotation){
            case Surface.ROTATION_0:
                return 90;
            case Surface.ROTATION_90:
                return 0;
            case Surface.ROTATION_180:
                return 270;
            case Surface.ROTATION_270:
                return 180;
            default: return 0;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, int rotationDegree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(rotationDegree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static int selectedVideoFormat(){
        switch(videoType){
            case ".mp4": return MediaRecorder.OutputFormat.MPEG_4;
            case ".webm": return MediaRecorder.OutputFormat.WEBM;
            case ".3gp": return MediaRecorder.OutputFormat.THREE_GPP;
            default: return MediaRecorder.OutputFormat.MPEG_4;
        }
    }

    public static MediaLocationData getVideoName(){
        String locationName = baseLocation;
        if(bunchName != null){
            locationName = locationName+"/"+bunchName;
        }

        int counterVideo = 0;
        counterVideo = findNextName(locationName, videoType, videoBaseName, counterVideo);
        if(counterVideo == -1){
            return null;
        }
        MediaLocationData data = new MediaLocationData(baseLocation, bunchName, videoBaseName, counterVideo, videoType);

        return data;
    }

    public static MediaLocationData getPhotoName(){
        String locationName = baseLocation;
        if(bunchName != null){
            locationName = locationName+"/"+bunchName;
        }

        int counterPhoto = 0;
        counterPhoto = findNextName(locationName, photoType, photoBaseName, counterPhoto);
        if(counterPhoto == -1){
            return null;
        }
        MediaLocationData data = new MediaLocationData(baseLocation, bunchName, photoBaseName, counterPhoto, photoType);

        return data;
    }

    private static int findNextName(String baseLocation, String type, String baseName, int count){
        String basePath = baseLocation+"/"+baseName;

        StringBuilder fileID = new StringBuilder();
        fileID.append(count);
        fileID.append(type);

        StringBuilder temp = new StringBuilder(basePath);
        temp.append(fileID);
        File file;

        while(count < 100000) {
            file = new File(temp.toString());
            if (file.exists()) {
                count++;
                fileID.setLength(0);
                fileID.append(count);
                fileID.append(type);
                temp.setLength(0);
                temp.append(basePath);
                temp.append(fileID);
            } else {
                return count;
            }
        }
        return -1;
    }

    public enum videoTypes{
        mp4,
        threeGpp
    }
}
