package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.GalleryListDataPackage;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.GalleryRecyclerAdapter;

/**
 * Created by Markos on 25. 11. 2016.
 */

public class FrameGalleryActivity extends GalleryActivityTemplate{

    public static final String SELECTED_BUNCH_EXTRAS_NAME = "selectedBunch";

    private String selectedBunchName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extrasData = getIntent().getExtras();
        selectedBunchName = extrasData.getString(FrameGalleryActivity.SELECTED_BUNCH_EXTRAS_NAME);
    }

    @Override
    protected void galleryTypeSpecifications() {
        showData(getFrameDatabaseData());
    }

    private List<GalleryListDataPackage> getFrameDatabaseData(){
        Frame selectDefinitionFrame = new Frame(null, null, null, null, null, null, null);

        DatabaseService database = DatabaseService.getDbInstance(this);
        Cursor databaseData = database.selectRow(selectDefinitionFrame);

        ArrayList<GalleryListDataPackage> inputListData = new ArrayList<>();
        GalleryListDataPackage listItem;

        int frameNumber;
        String frameName;
        String imagePath;
        Bitmap imagePicture;

        databaseData.moveToFirst();
        for (int i = 0; i < databaseData.getCount(); i++) {
            frameNumber = Integer.parseInt(databaseData.getString(databaseData.getColumnIndex(Frame.COLUMN_NAMES[0])));     //frame number
            frameName = databaseData.getString(databaseData.getColumnIndex(Frame.COLUMN_NAMES[1]));                 //frame name

            imagePath = composeImagePath(database.getBunchPath(selectedBunchName), frameName, frameNumber);
            imagePicture = getSavedImage(imagePath);

            listItem = new GalleryListDataPackage(imagePicture, frameName+frameNumber+".jpeg",
                    MediaLocationsAndSettingsTimeService.transformToTime(
                            Long.parseLong(databaseData.getString(databaseData.getColumnIndex(Frame.COLUMN_NAMES[3])))),     //frame date
                    formatCodeDecoder(databaseData.getInt(databaseData.getColumnIndex(Frame.COLUMN_NAMES[4]))),     //frame format
                    databaseData.getString(databaseData.getColumnIndex(Frame.COLUMN_NAMES[6])),     //frame azimuth
                    databaseData.getString(databaseData.getColumnIndex(Frame.COLUMN_NAMES[5])),      //frame pitch
                    frameName, frameNumber
            );

            inputListData.add(listItem);
            databaseData.moveToNext();
        }

        return inputListData;
    }

    @Override
    protected void clickedItemAction(GalleryRecyclerAdapter.ViewHolder listItem) {
        DatabaseService database = DatabaseService.getDbInstance(this);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" +database.getPicturePath(selectedBunchName,
                listItem.getItemData().getSpecialPropertyString(), listItem.getItemData().getSpecialPropertyInt()));
        intent.setDataAndType(uri,"image/*");
        startActivity(intent);
    }

    @Override
    protected boolean deleteSelected() {
        for(int i = 0; i < selectedListItems.size(); i++){
            int bunchID = DatabaseService.getDbInstance(this).findBunchID(selectedBunchName);
            if(bunchID == -1){
                return false;
            }
            deletePictureFromDatabase(bunchID, selectedListItems.get(i).getItemData().getSpecialPropertyString(),
                    selectedListItems.get(i).getItemData().getSpecialPropertyInt());
            selectedListItems.get(i).deleteAnimation();
        }
        return true;
    }

    @Override
    protected boolean sendSelected() {
        return super.sendSelected();
    }

    private int deletePictureFromDatabase(Integer bunchID, String frameName, int frameNumber){
        DatabaseService database = DatabaseService.getDbInstance(this);

        Cursor selectedBunch = database.selectRow(new Bunch(bunchID, null, null, null, null, null, null));
        selectedBunch.moveToFirst();
        String imagePath = composeImagePath(selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[3])),
                frameName, frameNumber);

        File file = new File(imagePath);
        file.delete();

        return database.deleteRow(new Frame(frameNumber, frameName, bunchID, null, null, null, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        galleryTypeSpecifications();
    }
}
