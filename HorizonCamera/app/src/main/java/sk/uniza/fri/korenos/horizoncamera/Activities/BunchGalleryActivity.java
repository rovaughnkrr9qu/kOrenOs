package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

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

public class BunchGalleryActivity extends GalleryActivityTemplate {

    @Override
    protected void galleryTypeSpecifications(){
        showData(getBunchDatabaseData());
    }

    private List<GalleryListDataPackage> getBunchDatabaseData(){
        Bunch selectDefinitionBunch = new Bunch(null, null, null, null, null, null, null);

        DatabaseService database = DatabaseService.getDbInstance(this);
        Cursor databaseData = database.selectRow(selectDefinitionBunch);

        ArrayList<GalleryListDataPackage> inputListData = new ArrayList<>();
        GalleryListDataPackage listItem;

        String pictureName;
        String bunchName;
        String imagePath;
        Bitmap imagePicture;

        databaseData.moveToFirst();
        for (int i = 0; i < databaseData.getCount(); i++) {
            imagePicture = null;
            imagePath = databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[3])); //bunch image path
            pictureName = getFirstImageOfFolder(imagePath);
            if(pictureName != null){
                imagePicture = getSavedImage(imagePath+"/"+pictureName);
            }
            bunchName = databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[1]));

            listItem = new GalleryListDataPackage(imagePicture, bunchName,
                    MediaLocationsAndSettingsTimeService.transformToTime(
                            Long.parseLong(databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[2])))),     //bunch date
                    databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[5])),     //bunch longitude
                    databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[6])),     //bunch latitude
                    getCountOfPicturesInBunch(bunchName)+""
            );

            inputListData.add(listItem);
            databaseData.moveToNext();
        }

        return inputListData;
    }

    private String getFirstImageOfFolder(String path){
        File directory = new File(path);
        if(directory == null){
            return null;
        }
        File[] contents = directory.listFiles();
        if (contents == null || contents.length == 0) {
            return null;
        }else {
            for(File file : contents){
                if(file.getName().endsWith(".jpeg")){
                    return file.getName();
                }
            }
        }
        return null;
    }

    @Override
    protected void clickedItemAction(GalleryRecyclerAdapter.ViewHolder listItem) {
        super.clickedItemAction(listItem);

        switch (actualType){
            case BUNCH_GALLERY_CODE:
                Intent intent = new Intent(this,FrameGalleryActivity.class);
                intent.putExtra(GalleryActivityTemplate.GALLERY_TYPE_EXTRAS_NAME, GalleryActivityTemplate.INSIDE_BUNCH_GALLERY_CODE);
                intent.putExtra(FrameGalleryActivity.SELECTED_BUNCH_EXTRAS_NAME, listItem.getItemData().getItemMainName());
                startActivity(intent);
                break;
            case NEW_BUNCH_CHOOSE_GALLERY_CODE:
                Intent resultIntent = new Intent();
                resultIntent.putExtra(NEW_BUNCH_RESULT_EXTRAS_NAME, listItem.getItemData().getItemMainName());
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
            nameTemp = selectedListItems.get(i).getItemData().getItemMainName();
            int bunchID = DatabaseService.getDbInstance(this).findBunchID(nameTemp);
            if(bunchID == -1){
                return false;
            }
            deletePicturesOfBrunch(bunchID);
            deleteBunch(nameTemp);
            selectedListItems.get(i).deleteAnimation();
        }
        return true;
    }

    private int getCountOfPicturesInBunch(String bunchName){
        DatabaseService database = DatabaseService.getDbInstance(this);
        Cursor selectedBunch = database.selectRow(new Frame(null, null, database.findBunchID(bunchName), null, null, null, null));
        return selectedBunch.getCount();
    }

    private int deleteBunch(String bunchName){
        DatabaseService database = DatabaseService.getDbInstance(this);

        File file = new File(database.getBunchPath(bunchName));
        file.delete();

        return database.deleteRow(new Bunch(null, bunchName, null, null, null, null, null));
    }

    private int deletePicturesOfBrunch(Integer bunchID){
        DatabaseService database = DatabaseService.getDbInstance(this);

        String bunchPath = database.getBunchPath(bunchID);

        File bunch = new File(bunchPath);
        if (bunch.isDirectory())
        {
            String[] children = bunch.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(composeImagePath(bunchPath, children[i])).delete();
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
