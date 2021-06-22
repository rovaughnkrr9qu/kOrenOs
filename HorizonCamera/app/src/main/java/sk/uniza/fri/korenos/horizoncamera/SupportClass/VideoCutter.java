package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markos on 3. 12. 2016.
 */

public class VideoCutter {
    private String videoPath;
    List<OrientationDataPackage> recordingOrientationData;
    private long videoStartTime;

    public VideoCutter(String videoFullPath, long recordingStartTime) {
        videoPath = videoFullPath;
        videoStartTime = recordingStartTime;
    }

    public List<byte[]> cutFrames(List<OrientationDataPackage> videoRecordingOrientationData){
        List<OrientationDataPackage> updatedOrientationData = new ArrayList<OrientationDataPackage>();
        List<byte[]> outputFrames = new ArrayList<byte[]>();
        byte[] controll;

        for(OrientationDataPackage orientData : videoRecordingOrientationData){
            if(orientData.getTimeStamp() > videoStartTime){
                updatedOrientationData.add(orientData);
                controll = getImage(orientData.getTimeStamp()-videoStartTime);
                if(controll == null){
                    return null;
                }
                outputFrames.add(controll);
            }
        }

        recordingOrientationData = updatedOrientationData;
        return outputFrames;
    }

    public List<OrientationDataPackage> getUpdatedOrientationData(){
        return recordingOrientationData;
    }

    private byte[] getImage(long millisecondTime) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(videoPath);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return null;
        }

        Bitmap bmpOriginal = mediaMetadataRetriever.getFrameAtTime(millisecondTime*1000, MediaMetadataRetriever.OPTION_CLOSEST);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmpOriginal.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
