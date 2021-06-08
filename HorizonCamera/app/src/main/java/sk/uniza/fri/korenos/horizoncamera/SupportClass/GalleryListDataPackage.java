package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by Markos on 24. 11. 2016.
 */

public class GalleryListDataPackage {

    private Bitmap itemImage;
    private String itemMainName;
    private String itemFirstProperty;
    private String itemSecondProperty;
    private String itemThirdProperty;

    public GalleryListDataPackage(Bitmap itemImage, String itemMainName, String itemFirstProperty, String itemSecondProperty, String itemThirdProperty) {
        this.itemImage = itemImage;
        this.itemMainName = itemMainName;
        this.itemFirstProperty = itemFirstProperty;
        this.itemSecondProperty = itemSecondProperty;
        this.itemThirdProperty = itemThirdProperty;
    }

    public Bitmap getItemImage() {
        return itemImage;
    }

    public String getItemMainName() {
        return itemMainName;
    }

    public String getItemFirstProperty() {
        return itemFirstProperty;
    }

    public String getItemSecondProperty() {
        return itemSecondProperty;
    }

    public String getItemThirdProperty() {
        return itemThirdProperty;
    }
}
