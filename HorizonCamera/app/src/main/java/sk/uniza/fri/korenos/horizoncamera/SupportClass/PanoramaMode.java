package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import java.util.ArrayList;

import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationService;

/**
 * Created by Markos on 20. 11. 2016.
 */

public class PanoramaMode implements DataVisitorInterface{

    private double degreeGap;
    private AutomaticModeInterface automObject;
    private OrientationService orientationService;

    private double initAzimuthDegree;
    private double actualAzimuthDegree;
    private double normalisedDegreeBase;
    private double maxChange;
    private double lastValidData = 0;
    private double dataThreshold;

    private ArrayList<Double> buffer;
    private int maxBuffer = 20;
    private int iterator = 0;

    private boolean panoramaModeWorking = false;

    public PanoramaMode(AutomaticModeInterface automatisedObject, int pictureDegreeGap, OrientationService orientationService,
                        double maxMoveChange, double validDataThreshold){
        automObject = automatisedObject;
        degreeGap = pictureDegreeGap;
        dataThreshold = validDataThreshold;

        maxChange = maxMoveChange;

        this.orientationService = orientationService;

        orientationService.startOrientationSensors();

        buffer = new ArrayList<>();
    }

    public void startPanoramaSequence(){
        orientationService.addDataVisitor(this);
    }

    private void initPanoramaSequence(){
        panoramaModeWorking = true;
        OrientationDataPackage orientation = orientationService.getActualOrientation();

        initAzimuthDegree = orientation.getAzimuth();
        actualAzimuthDegree = 0;
        normalisedDegreeBase = 360 - initAzimuthDegree;
        lastValidData = actualAzimuthDegree;

        automObject.takePicture(orientation.getAzimuth());
    }

    public void stopPanoramaSequence(){
        orientationService.removeDataVisitor(this);
        orientationService.stopOrientationSensors();

        panoramaModeWorking = false;
    }

    private boolean istPanoramaWorking(){
        return panoramaModeWorking;
    }

    @Override
    public void getOrientationData(OrientationDataPackage dataPackage) {
        if(!panoramaModeWorking){
            initPanoramaSequence();
        }

        double temp = getNormalisedDegrees(dataPackage.getAzimuth());

        //System.out.println(temp+" tu");
        if(temp > lastValidData + dataThreshold || temp < lastValidData - dataThreshold){
            return;
        }else{
            lastValidData = temp;
        }

        if(buffer.size() < maxBuffer) {
            buffer.add(temp);
        }else{
            buffer.set(iterator, temp);
        }

        iterator++;
        if(iterator == maxBuffer){
            iterator = 0;
        }

        if(Math.abs(temp - actualAzimuthDegree) > degreeGap &&
                Math.abs(temp - actualAzimuthDegree) < maxChange
                && temp > actualAzimuthDegree && bufferCheck(actualAzimuthDegree+degreeGap)){
            actualAzimuthDegree = temp;
            automObject.takePicture(dataPackage.getAzimuth());
        }

        if(temp > 360 - degreeGap/2 && Math.abs(temp - actualAzimuthDegree) < maxChange){
            stopPanoramaSequence();
            automObject.automaticModeDone();
        }
    }

    private boolean bufferCheck(double nextVal){
        int positiveIter = 0;

        for (Double value : buffer){
            if(value > nextVal){
                positiveIter++;
            }
        }

        if(positiveIter > 0.5*buffer.size()){
            return true;
        }
        return false;
    }

    private double getNormalisedDegrees(double realDegrees){
        if(realDegrees >= initAzimuthDegree){
            return realDegrees-initAzimuthDegree;
        }else{
            return normalisedDegreeBase+realDegrees;
        }
    }
}
