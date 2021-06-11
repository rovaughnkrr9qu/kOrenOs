package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;

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

public class GalleryActivityTemplate extends AppCompatActivity implements GalleryRecyclerAdapter.GalleryItemClickedInterface{

    public static final String BUNCH_GALLERY_CODE = "bunchGallery";
    public static final String INSIDE_BUNCH_GALLERY_CODE = "insideBunchGallery";
    public static final String NEW_BUNCH_CHOOSE_GALLERY_CODE = "newBunchChooseGallery";

    public static final String GALLERY_TYPE_EXTRAS_NAME = "galleryType";

    protected String actualType;
    private int optionPanelHeight = 150;
    private int openOptionPanelDuration = 500;

    protected MenuStatus menuStatus = MenuStatus.menuClosed;
    protected ArrayList<GalleryRecyclerAdapter.ViewHolder> selectedListItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_layout);

        Bundle extrasData = getIntent().getExtras();
        actualType = extrasData.getString(GALLERY_TYPE_EXTRAS_NAME);

        selectedListItems = new ArrayList<>();

        galleryTypeBasedActions();
    }

    private void galleryTypeBasedActions() {
        switch(actualType){
            case BUNCH_GALLERY_CODE:
                setTitle(R.string.galleryBunchesTitleText);
                break;
            case NEW_BUNCH_CHOOSE_GALLERY_CODE:
                setTitle(R.string.galleryBunchesTitleText);
                break;
            case INSIDE_BUNCH_GALLERY_CODE:
                Bundle extrasData = getIntent().getExtras();
                String bunchName = extrasData.getString(FrameGalleryActivity.SELECTED_BUNCH_EXTRAS_NAME);
                setTitle(getResources().getString(R.string.galleryBunchTitleText)+" "+bunchName);
                break;
        }

        galleryTypeSpecifications();
    }

    private void buttonsVisible(boolean isVisible){
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.galleryMainListPane);
        View parent = (View)recyclerView.getParent();
        int height = parent.getHeight();
        final int finalHeight;
        final int startHeight;
        final int signum;

        if(isVisible){
            finalHeight = height-optionPanelHeight;
            startHeight = height;
            signum = 1;
        }else{
            finalHeight = height;
            startHeight = height-optionPanelHeight;
            signum = -1;
        }

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    recyclerView.getLayoutParams().height = finalHeight;
                    recyclerView.requestLayout();
                }else{
                    recyclerView.getLayoutParams().height = startHeight - signum*(int)(optionPanelHeight*interpolatedTime);
                    recyclerView.requestLayout();
                }
            }
        };
        animation.setDuration(openOptionPanelDuration);
        recyclerView.startAnimation(animation);
    }

    protected void showData(List<GalleryListDataPackage> dataList){
        boolean bunchData;
        switch(actualType){
            case INSIDE_BUNCH_GALLERY_CODE:
                bunchData = false;
                break;
            default:
                bunchData = true;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.galleryMainListPane);
        RecyclerView.LayoutManager recyclerManager =  new LinearLayoutManager(this);

        recyclerView.setLayoutManager(recyclerManager);
        GalleryRecyclerAdapter recyclerAdapter = new GalleryRecyclerAdapter(dataList, this, bunchData);
        recyclerView.setAdapter(recyclerAdapter);
    }

    protected String getBunchPath(String bunchName){
        DatabaseService database = DatabaseService.getDbInstance(this);

        Cursor selectedBunch = database.selectRow(new Bunch(null, bunchName, null, null, null, null, null));
        selectedBunch.moveToFirst();
        return selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[3]));
    }

    protected String getBunchPath(int bunchID){
        DatabaseService database = DatabaseService.getDbInstance(this);

        Cursor selectedBunch = database.selectRow(new Bunch(bunchID, null, null, null, null, null, null));
        selectedBunch.moveToFirst();
        return selectedBunch.getString(selectedBunch.getColumnIndex(Bunch.COLUMN_NAMES[3]));
    }

    protected String composeImagePath(String bunchPath, String frameName, int frameNumber){
        StringBuilder imagePathBuilder = new StringBuilder(bunchPath);
        imagePathBuilder.append("/");
        imagePathBuilder.append(frameName);
        imagePathBuilder.append(frameNumber);
        imagePathBuilder.append(".jpeg");

        return imagePathBuilder.toString();
    }

    protected String composeImagePath(String bunchPath, String frameName){
        StringBuilder imagePathBuilder = new StringBuilder(bunchPath);
        imagePathBuilder.append("/");
        imagePathBuilder.append(frameName);

        return imagePathBuilder.toString();
    }

    protected Bitmap getSavedImage(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path, options);
    }

    public void galleryCancelAction(View view) {
        buttonsVisible(false);

        for (int i = 0; i < selectedListItems.size(); i++){
            selectedListItems.get(i).unlight();
        }
        selectedListItems.clear();
        menuStatus = MenuStatus.menuClosed;
    }

    public void galleryMainAction(View view) {
        switch (menuStatus){
            case deleteSelected:
                deleteSelected();
                buttonsVisible(false);
                break;
            case sendSelected:
                sendSelected();
                buttonsVisible(false);
                break;
        }

        menuStatus = MenuStatus.menuClosed;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(actualType.compareTo(BUNCH_GALLERY_CODE)==0 || actualType.compareTo(INSIDE_BUNCH_GALLERY_CODE)==0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.gallery_option_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Button actionButton = (Button) findViewById(R.id.galleryOptionActionButton);

        switch (item.getItemId()){
            case R.id.galleryMenuSendOption:
                buttonsVisible(true);
                actionButton.setText(getResources().getText(R.string.galleryOptionActionSendButtonText));
                menuStatus = MenuStatus.sendSelected;
                break;
            case R.id.galleryMenuDeleteOption:
                buttonsVisible(true);
                actionButton.setText(getResources().getText(R.string.galleryOptionActionDeleteButtonText));
                menuStatus = MenuStatus.deleteSelected;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemHasBeenClicked(GalleryRecyclerAdapter.ViewHolder listItem) {
        switch (menuStatus){
            case menuClosed:
                clickedItemAction(listItem);
                break;
            default:
            if (selectedListItems.contains(listItem)) {
                selectedListItems.remove(listItem);
            } else {
                selectedListItems.add(listItem);
            }
        }
    }

    @Override
    public boolean isSelectable() {
        return menuStatus != MenuStatus.menuClosed;
    }

    @Override
    public void onDeleteAnimationFinished(GalleryRecyclerAdapter.ViewHolder listItem) {
        galleryTypeSpecifications();
    }

    protected void galleryTypeSpecifications(){
    }

    protected void clickedItemAction(GalleryRecyclerAdapter.ViewHolder listItem) {
    }

    protected boolean deleteSelected(){
        return false;
    }

    protected boolean sendSelected() {
        return false;
    }


    private enum MenuStatus{
        deleteSelected, sendSelected, menuClosed
    }
}
