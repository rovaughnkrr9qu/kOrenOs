package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Markos on 13. 11. 2016.
 */

public class OrientationService {

    private LocationManager locationManager;
    private GPSstate momentalGPSState = GPSstate.GPSstopped;
    private OrientationDemandingActivityInterface demandingActivity;
    private LocationListener locationAction = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            demandingActivity.onGPSlocationFound(location);
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

    public OrientationService(OrientationDemandingActivityInterface paDemandingActivity) {
        demandingActivity = paDemandingActivity;
        locationManager = (LocationManager) demandingActivity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getMomentalGPSLocation(){
        if(momentalGPSState == GPSstate.GPSstopped) {
            startGPS();
        }

        return getLocation();
    }

    private void startGPS(){
        if (ActivityCompat.checkSelfPermission(demandingActivity.getApplicationContext(),
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
            if (ActivityCompat.checkSelfPermission(demandingActivity.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(demandingActivity.getDemandingActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        demandingActivity.getSuccessRequestCode());
                return;
            }

            locationManager.removeUpdates(locationAction);
            momentalGPSState = GPSstate.GPSstopped;
        }
    }

    private Location getLocation(){
        if (ActivityCompat.checkSelfPermission(demandingActivity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(demandingActivity.getDemandingActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    demandingActivity.getSuccessRequestCode());
            return null;
        }

        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    public enum GPSstate{
        GPSstopped, GPSWork
    }
}

