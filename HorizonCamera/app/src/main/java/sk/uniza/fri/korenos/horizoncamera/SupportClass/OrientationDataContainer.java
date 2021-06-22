package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationService;

/**
 * Created by Markos on 3. 12. 2016.
 */

public class OrientationDataContainer{

    private List<OrientationDataPackage> gatheredOrientationData;
    private int millisecondsSleepTimeInterval;
    private boolean recordingInProcess = false;
    private OrientationService orientationService;

    private Runnable timeLooper = new Runnable() {
        @Override
        public void run() {
            while(recordingInProcess){
                timerSequenceListener();
                try {
                    Thread.sleep(millisecondsSleepTimeInterval);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    };

    public OrientationDataContainer(int millisecondsTimeInterval, OrientationService orientationServiceDataSource) {
        millisecondsSleepTimeInterval = millisecondsTimeInterval;
        gatheredOrientationData = new ArrayList<>();
        orientationService = orientationServiceDataSource;
    }

    public void startTimerAndGatheringProcess(){
        recordingInProcess = true;
        new Thread(timeLooper).start();
    }

    public void stopGatheringProcess(){
        recordingInProcess = false;
    }

    public List<OrientationDataPackage> getGatheredData(){
        return  gatheredOrientationData;
    }

    private void timerSequenceListener(){
        OrientationDataPackage actualOrientationData = orientationService.getActualOrientation();
        if(actualOrientationData != null) {
            gatheredOrientationData.add(actualOrientationData);
        }
    }
}
