package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Surface;

import java.util.ArrayList;

import sk.uniza.fri.korenos.horizoncamera.SupportClass.DataVisitorInterface;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.OrientationDataPackage;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.Statistics;

/**
 * Created by Markos on 13. 11. 2016.
 */

public class OrientationService implements SensorEventListener{

    private final double twoPi = 2*Math.PI;
    private SensorManager sensorManager;
    private Sensor rotationVector;
    private float[] matrixData;
    private int lastAccuracy = -1;

    private LocationManager locationManager;
    private GPSstate momentalGPSState = GPSstate.GPSstopped;
    private OrientationDemandingActivityInterface demandingActivity;

    private ArrayList<DataVisitorInterface> dataVisitor;

    private LocationListener locationAction = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            demandingActivity.GPSDataReady();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private OrientationDataPackage actualOrientationPackage = null;
    private Statistics azimuthStatisticAverage;
    private Statistics pitchStatisticAverage;

    public OrientationService(OrientationDemandingActivityInterface paDemandingActivity, SensorManager paSensorManager) {
        setUpGPS(paDemandingActivity);
        setUpOrientationSensors(paSensorManager);

        azimuthStatisticAverage = new Statistics(50, 180);
        pitchStatisticAverage = new Statistics(20, 180);

        dataVisitor = new ArrayList<>();
    }

    private void setUpGPS(OrientationDemandingActivityInterface paDemandingActivity){
        demandingActivity = paDemandingActivity;
        locationManager = (LocationManager) demandingActivity.getDemandingActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        startGPS();
    }

    private void setUpOrientationSensors(SensorManager paSensorManager){
        sensorManager = paSensorManager;

        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        startOrientationSensors();
    }

    private void startGPS(){
        if (ActivityCompat.checkSelfPermission(demandingActivity.getDemandingActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(demandingActivity.getDemandingActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    demandingActivity.getSuccessRequestCode());
            return;
        }

        momentalGPSState = GPSstate.GPSWork;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 2, locationAction);
    }

    public void stopGPS(){
        if(locationManager != null){
            if (ActivityCompat.checkSelfPermission(demandingActivity.getDemandingActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(demandingActivity.getDemandingActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        demandingActivity.getSuccessRequestCode());
                return;
            }

            locationManager.removeUpdates(locationAction);
            momentalGPSState = GPSstate.GPSstopped;
        }
    }

    public Location getMomentalGPSLocation(){
        if(momentalGPSState == GPSstate.GPSstopped) {
            startGPS();
        }

        return getLocation();
    }

    private Location getLocation(){
        if (ActivityCompat.checkSelfPermission(demandingActivity.getDemandingActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(demandingActivity.getDemandingActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    demandingActivity.getSuccessRequestCode());
            return null;
        }

        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public void startOrientationSensors(){
        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stopOrientationSensors(){
        sensorManager.unregisterListener(this, rotationVector);
    }

    public OrientationDataPackage getActualOrientation(){
        return new OrientationDataPackage(actualOrientationPackage);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (lastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            matrixData = new float[16];

            SensorManager.getRotationMatrixFromVector(matrixData, sensorEvent.values);

            int axisX = 0;
            int axisY = 0;

            switch (demandingActivity.getDemandingActivity().getWindowManager().getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_0:
                    axisX = SensorManager.AXIS_X;
                    axisY = SensorManager.AXIS_Z;
                    break;
                case Surface.ROTATION_90:
                    axisX = SensorManager.AXIS_Z;
                    axisY = SensorManager.AXIS_MINUS_X;
                    break;
                case Surface.ROTATION_180:
                    break;
                case Surface.ROTATION_270:
                    axisX = SensorManager.AXIS_MINUS_Z;
                    axisY = SensorManager.AXIS_X;
                    break;
                default:
                    break;
            }

            float[] orientation = new float[3];
            float[] rotationMatrix = new float[16];
            SensorManager.remapCoordinateSystem(matrixData, axisX, axisY, rotationMatrix);
            SensorManager.getOrientation(rotationMatrix, orientation);

            double azimuth = convertToDegreesAzimuth((orientation[0] + twoPi) % twoPi);
            double pitch = convertToDegreesPitch((orientation[1] + twoPi) % twoPi);

            saveData(azimuth, pitch);

            for(DataVisitorInterface visitor : dataVisitor){
                visitor.getOrientationData(actualOrientationPackage);
            }

            if(azimuthStatisticAverage.dataGathered() && pitchStatisticAverage.dataGathered()){
                demandingActivity.getActualOrientationData(actualOrientationPackage);
            }
        }
    }

    private void saveData(double azimuth, double pitch){
        azimuthStatisticAverage.add(azimuth);
        pitchStatisticAverage.add(pitch);

        if(actualOrientationPackage == null){
            actualOrientationPackage = new OrientationDataPackage(azimuthStatisticAverage.getAverage(),
                    pitchStatisticAverage.getAverage(), MediaLocationsAndSettingsTimeService.getCurrentTime());
        }else{
            actualOrientationPackage.resetAll(azimuthStatisticAverage.getAverage(),
                    pitchStatisticAverage.getAverage(), MediaLocationsAndSettingsTimeService.getCurrentTime());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (lastAccuracy != accuracy) {
            lastAccuracy = accuracy;
        }
    }

    private double convertToDegreesAzimuth(double radianAngle){
        return radianAngle * 180 / Math.PI;
    }

    private double convertToDegreesPitch(double radianAngle){
        double value180 = ((radianAngle * 180 / Math.PI)+90)%180;
        if(value180 <= 90){
            return 90-value180;
        }else{
            return -1.0*value180+90;
        }
    }

    public void addDataVisitor(DataVisitorInterface visitor){
        dataVisitor.add(visitor);
    }

    public void removeDataVisitor(DataVisitorInterface visitor){
        dataVisitor.remove(visitor);
    }


    public enum GPSstate{
        GPSstopped, GPSWork
    }
}

