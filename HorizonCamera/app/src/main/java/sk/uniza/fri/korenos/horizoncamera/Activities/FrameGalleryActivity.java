package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.EntityInterface;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.ConnectionService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DataOperationServices;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;
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

    private List<EntityInterface> getFrameDatabaseData(){
        return DataOperationServices.getAllFramesOfBunch(selectedBunchName, this);
    }

    @Override
    protected void clickedItemAction(GalleryRecyclerAdapter.ViewHolder listItem) {
        DatabaseService database = DatabaseService.getDbInstance(this);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + DataOperationServices.getPicturePath(selectedBunchName,
                ((Frame)listItem.getItemData()).getFrameName(), ((Frame)listItem.getItemData()).getFrameNumber(), database));
        intent.setDataAndType(uri,"image/*");
        startActivity(intent);
    }

    @Override
    protected boolean deleteSelected() {
        for(int i = 0; i < selectedListItems.size(); i++){
            int bunchID = DataOperationServices.findBunchID(selectedBunchName, DatabaseService.getDbInstance(this));
            if(bunchID == -1){
                return false;
            }
            deletePictureFromDatabase(bunchID, ((Frame)selectedListItems.get(i).getItemData()).getFrameName(),
                    ((Frame)selectedListItems.get(i).getItemData()).getFrameNumber());
            selectedListItems.get(i).deleteAnimation();
        }
        return true;
    }

    @Override
    protected boolean sendSelected() {
        int actualBunchID = DataOperationServices.findBunchID(selectedBunchName, DatabaseService.getDbInstance(this));

        ArrayList<Frame> framesToSend = new ArrayList<>();
        ArrayList<String> pathsToFrames = new ArrayList<>();

        for(GalleryRecyclerAdapter.ViewHolder viewedData : selectedListItems){
            framesToSend.add((Frame)viewedData.getItemData());
            pathsToFrames.add(DataOperationServices.composeImagePath(
                    DataOperationServices.getBunchPath(actualBunchID, DatabaseService.getDbInstance(this)),
                    ((Frame)viewedData.getItemData()).getFrameName(),((Frame)viewedData.getItemData()).getFrameNumber()));
        }

        ConnectionService.sendData(MediaLocationsAndSettingsTimeService.getServerURLAddress(),
                framesToSend, pathsToFrames, getApplicationContext());     //http://posttestserver.com/post.php
        return false;
    }

    private int deletePictureFromDatabase(Integer bunchID, String frameName, int frameNumber){
        DatabaseService database = DatabaseService.getDbInstance(this);

        Cursor selectedBunch = database.selectRow(new Bunch(bunchID, null, null, null, null, null, null));
        selectedBunch.moveToFirst();
        String imagePath = DataOperationServices.composeImagePath(selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[3])),
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
