package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.GalleryListDataPackage;
import sk.uniza.fri.korenos.horizoncamera.SupportClass.GalleryRecyclerAdapter;

/**
 * Created by Markos on 22. 11. 2016.
 */

public class GalleryActivity extends AppCompatActivity implements GalleryRecyclerAdapter.GalleryItemClickedInterface{

    public static final String BUNCH_GALLERY_CODE = "bunchGallery";
    public static final String INSIDE_BUNCH_GALLERY_CODE = "insideBunchGallery";
    public static final String NEW_BUNCH_CHOOSE_GALLERY_CODE = "newBunchChooseGallery";

    public static final String GALLERY_TYPE_EXTRAS_NAME = "galleryType";

    private String actualType;
    private int optionPartHeight = 150;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_layout);

        Bundle extrasData = getIntent().getExtras();
        actualType = extrasData.getString(GALLERY_TYPE_EXTRAS_NAME);

        galleryTypeBasedActions();
    }

    private void galleryTypeBasedActions() {
        switch(actualType){
            case BUNCH_GALLERY_CODE: bunchGalleryTypeSpecifications();
                break;
            case INSIDE_BUNCH_GALLERY_CODE: insideBunchGalleryTypeSpecifications();
                break;
            case NEW_BUNCH_CHOOSE_GALLERY_CODE: newBunchChooseGalleryTypeSpecifications();
                break;
        }
    }

    private void bunchGalleryTypeSpecifications(){
        showData(getBunchDatabaseData());
    }

    private void insideBunchGalleryTypeSpecifications(){

    }

    private void newBunchChooseGalleryTypeSpecifications(){
        showData(getBunchDatabaseData());
    }

    private List<GalleryListDataPackage> getBunchDatabaseData(){
        Bunch selectDefinitionBunch = new Bunch(null, null, null, null, null, null, null);

        DatabaseService database = DatabaseService.getDbInstance(this);
        Cursor databaseData = database.selectRow(selectDefinitionBunch);

        ArrayList<GalleryListDataPackage> inputListData = new ArrayList<>();
        GalleryListDataPackage listItem;

        String pictureName;
        String imagePath;
        Bitmap imagePicture;

        databaseData.moveToFirst();
        for (int i = 0; i < databaseData.getCount(); i++) {
            imagePicture = null;
            imagePath = databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[3]));  //bunch image path
            pictureName = getFirstImageOfDicrectory(imagePath);
            if(pictureName != null){
                imagePicture = getSavedImage(imagePath+"/"+pictureName);
            }

            listItem = new GalleryListDataPackage(imagePicture,
                    databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[1])),     //bunch name
                    databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[2])),     //bunch date
                    databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[5])),     //bunch longitude
                    databaseData.getString(databaseData.getColumnIndex(Bunch.COLUMN_NAMES[6]))      //bunch latitude
            );

            inputListData.add(listItem);
            databaseData.moveToNext();
        }

        return inputListData;
    }

    private Bitmap getSavedImage(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path, options);
    }

    private String getFirstImageOfDicrectory(String path){
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

    private void showData(List<GalleryListDataPackage> dataList){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.galleryMainListPane);
        RecyclerView.LayoutManager recyclerManager =  new LinearLayoutManager(this);

        recyclerView.setLayoutManager(recyclerManager);
        GalleryRecyclerAdapter recyclerAdapter = new GalleryRecyclerAdapter(dataList, this);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void buttonsVisible(boolean isVisible){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.galleryMainListPane);
        View parent = (View)recyclerView.getParent();
        int height = parent.getHeight();
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();

        if(isVisible){
            params.height = height-optionPartHeight;
        }else{
            params.height = height;
        }
        recyclerView.setLayoutParams(params);
    }

    @Override
    public void itemHasBeenClicked(GalleryListDataPackage detailInfo) {

    }
}
