package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.media.MediaRecorder;
import android.os.Environment;
import android.view.Surface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sk.uniza.fri.korenos.horizoncamera.SupportClass.MediaLocationData;

/**
 * Created by Markos on 10. 11. 2016.
 */

public class MediaLocationsAndSettingsTimeService {

    private static String serverURLAddress = "http://httpbin.org/post";

    private static String baseLocation = Environment.getExternalStorageDirectory() + "/HorizonCamera";
    private static String photoType = ".jpeg";
    private static String videoType = ".mp4";
    private static String photoBaseName = "photo";
    private static String videoBaseName = "video";
    private static int counterPhoto = 0;
    private static int counterVideo = 0;
    private static int videoFrameRate = 30;
    private static boolean soundOn = true;
    private static boolean saveAdditionalData = true;

    private static String bunchName;

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

    public static void setVideoType(videoTypes type){
        switch (type){
            case mp4: videoType = ".mp4";
            case threeGpp: videoType = ".3gp";
        }
    }

    public static void setVideoFrameRate(int frameRate){
        videoFrameRate = frameRate;
    }

    public static int getVideoFrameRate(){
        return videoFrameRate;
    }

    public static void setSoundOn(boolean soundStatus){
        soundOn = soundStatus;
    }

    public static boolean getSoundOn(){
        return soundOn;
    }

    public static void setServerULDAddress(String serverAddress){
        serverURLAddress = serverAddress;
    }

    public static String getServerULDAddress(){
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
    }

    public static boolean getSaveAdditionalData(){
        return soundOn;
    }

    public static void setBunchName(String selectedBunchName){
        bunchName = selectedBunchName;
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

    public enum videoQuality {
        QUALITY_HIGH,
        QUALITY_720P,
        QUALITY_480P,
        QUALITY_1080P,
        QUALITY_CIF,
        QUALITY_LOW,
        QUALITY_QCIF,
        QUALITY_QVGA,
    }
}
