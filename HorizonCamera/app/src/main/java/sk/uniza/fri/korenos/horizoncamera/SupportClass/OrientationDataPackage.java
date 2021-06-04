package sk.uniza.fri.korenos.horizoncamera.SupportClass;

/**
 * Created by Markos on 18. 11. 2016.
 */

public class OrientationDataPackage {

    private double azimuth;
    private double pitch;
    private long timeStamp;

    public OrientationDataPackage(double azimuth, double pitch, long timeStamp) {
        resetAll(azimuth, pitch, timeStamp);
    }

    public OrientationDataPackage(OrientationDataPackage copyGPSDataPacket) {
        setAzimuth(copyGPSDataPacket.getAzimuth());
        setPitch(copyGPSDataPacket.getPitch());
        setTimeStamp(copyGPSDataPacket.getTimeStamp());
    }

    public double getAzimuth() {
        return azimuth;
    }

    public double getPitch() {
        return pitch;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void resetAll(double azimuth, double pitch, long timeStamp){
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.timeStamp = timeStamp;
    }
}
