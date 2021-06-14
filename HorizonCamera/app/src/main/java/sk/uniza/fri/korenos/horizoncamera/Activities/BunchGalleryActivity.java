package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.content.Intent;
import android.database.Cursor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.EntityInterface;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DataOperationServices;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.GalleryRecyclerAdapter;

/**
 * Created by Markos on 25. 11. 2016.
 */

public class BunchGalleryActivity extends GalleryActivityTemplate {

    @Override
    protected void galleryTypeSpecifications(){
        showData(getBunchDatabaseData());
    }

    private List<EntityInterface> getBunchDatabaseData(){
        Bunch selectDefinitionBunch = new Bunch(null, null, null, null, null, null, null);

        DatabaseService database = DatabaseService.getDbInstance(this);
        Cursor databaseData = database.selectRow(selectDefinitionBunch);

        ArrayList<EntityInterface> inputListData = new ArrayList<>();
        Bunch listItem;

        int bunchID;
        String bunchName;
        Long bunchDate;
        String bunchPath;
        int saveAddData;
        Double bunchLatitude;
        Double bunchLongitude;

        databaseData.moveToFirst();
        for (int i = 0; i < databaseData.getCount(); i++) {

            bunchID = databaseData.getInt(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[0]));
            bunchName = databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[1]));
            bunchDate = databaseData.getLong(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[2]));
            bunchPath = databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[3]));
            saveAddData = databaseData.getInt(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[4]));
            bunchLatitude = databaseData.getDouble(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[5]));
            bunchLongitude = databaseData.getDouble(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[6]));

            listItem = new Bunch(bunchID, bunchName, bunchDate, bunchPath, saveAddData, bunchLatitude, bunchLongitude);

            inputListData.add(listItem);
            databaseData.moveToNext();
        }

        return inputListData;
    }

    @Override
    protected void clickedItemAction(GalleryRecyclerAdapter.ViewHolder listItem) {
        super.clickedItemAction(listItem);

        switch (actualType){
            case BUNCH_GALLERY_CODE:
                Intent intent = new Intent(this,FrameGalleryActivity.class);
                intent.putExtra(GalleryActivityTemplate.GALLERY_TYPE_EXTRAS_NAME, GalleryActivityTemplate.INSIDE_BUNCH_GALLERY_CODE);
                intent.putExtra(FrameGalleryActivity.SELECTED_BUNCH_EXTRAS_NAME, ((Bunch)listItem.getItemData()).getBunchName());
                startActivity(intent);
                break;
            case NEW_BUNCH_CHOOSE_GALLERY_CODE:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(NEW_BUNCH_RESULT_EXTRAS_NAME, ((Bunch)listItem.getItemData()).getBunchName());
                setResult(SUCCESS_RESULT_CODE, resultIntent);
                finish();
                break;
        }
    }

    @Override
    protected boolean sendSelected() {
        return super.sendSelected();
    }

    @Override
    protected boolean deleteSelected() {
        String nameTemp;

        for(int i = 0; i < selectedListItems.size(); i++){
            nameTemp = ((Bunch)selectedListItems.get(i).getItemData()).getBunchName();
            int bunchID = DataOperationServices.findBunchID(nameTemp, DatabaseService.getDbInstance(this));
            if(bunchID == -1){
                return false;
            }
            deletePicturesOfBrunch(bunchID);
            deleteBunch(nameTemp);
            selectedListItems.get(i).deleteAnimation();
        }
        return true;
    }

    private int deleteBunch(String bunchName){
        DatabaseService database = DatabaseService.getDbInstance(this);

        File file = new File(DataOperationServices.getBunchPath(bunchName, database));
        file.delete();

        return database.deleteRow(new Bunch(null, bunchName, null, null, null, null, null));
    }

    private int deletePicturesOfBrunch(Integer bunchID){
        DatabaseService database = DatabaseService.getDbInstance(this);

        String bunchPath = DataOperationServices.getBunchPath(bunchID, database);

        File bunch = new File(bunchPath);
        if (bunch.isDirectory())
        {
            String[] children = bunch.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(DataOperationServices.composeImagePath(bunchPath, children[i])).delete();
            }
        }

        return database.deleteRow(new Frame(null, null, bunchID, null, null, null, null));
    }

    @Override
    protected void onResume() {
        super.onResume();

        galleryTypeSpecifications();
    }
}
