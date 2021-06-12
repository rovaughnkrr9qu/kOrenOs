package sk.uniza.fri.korenos.horizoncamera.SupportClass;

/**
 * Created by Markos on 25. 11. 2016.
 */

public class MediaLocationData {

    private String baseLocation;
    private String bunchName;
    private String baseName;
    private int frameNumber;
    private String type;

    public MediaLocationData(String baseLocation, String bunchName, String baseName, int frameNumber, String type) {
        this.baseLocation = baseLocation;
        this.bunchName = bunchName;
        this.baseName = baseName;
        this.frameNumber = frameNumber;
        this.type = type;
    }

    public String getFullPath(){
        StringBuilder pathBuilder = new StringBuilder(baseLocation);
        pathBuilder.append("/");

        if(bunchName != null){
            pathBuilder.append(bunchName);
            pathBuilder.append("/");
        }

        pathBuilder.append(baseName);
        pathBuilder.append(frameNumber);
        pathBuilder.append(type);

        return pathBuilder.toString();
    }

    public String getFileName(){
        StringBuilder pathBuilder = new StringBuilder(baseName);
        pathBuilder.append(frameNumber);
        pathBuilder.append(type);

        return pathBuilder.toString();
    }

    public String getBaseLocation() {
        return baseLocation;
    }

    public String getBunchName() {
        return bunchName;
    }

    public String getBaseName() {
        return baseName;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public String getType() {
        return type;
    }
}
