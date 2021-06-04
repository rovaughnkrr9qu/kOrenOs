package sk.uniza.fri.korenos.horizoncamera.ServiceModules;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import sk.uniza.fri.korenos.horizoncamera.SupportClass.OrientationDataPackage;

/**
 * Created by Markos on 13. 11. 2016.
 */

public interface OrientationDemandingActivityInterface {
    Context getApplicationContext();
    int getSuccessRequestCode();
    Activity getDemandingActivity();
    void orientationDataReady();
}
