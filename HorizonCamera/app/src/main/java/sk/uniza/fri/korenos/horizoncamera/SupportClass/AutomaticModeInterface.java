package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import sk.uniza.fri.korenos.horizoncamera.ServiceModules.OrientationDemandingActivityInterface;

/**
 * Created by Markos on 20. 11. 2016.
 */

public interface AutomaticModeInterface{
    void takePicture(double degree);
    void automaticModeDone();
}
