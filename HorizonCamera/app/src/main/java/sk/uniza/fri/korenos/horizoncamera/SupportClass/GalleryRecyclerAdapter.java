package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Bunch;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.EntityInterface;
import sk.uniza.fri.korenos.horizoncamera.DatabaseEntities.Frame;
import sk.uniza.fri.korenos.horizoncamera.R;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DataOperationServices;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.DatabaseService;
import sk.uniza.fri.korenos.horizoncamera.ServiceModules.MediaLocationsAndSettingsTimeService;

/**
 * Created by Markos on 24. 11. 2016.
 */

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private EntityInterface itemData;
        private GalleryItemClickedInterface listeningActivity;

        private ImageView itemImage;
        private TextView itemMainName;
        private TextView itemDate;
        private TextView itemGPSandSource;
        private TextView itemPictureNumberAndOrientation;

        private int highlightTime = 200;
        private boolean highlighted = false;
        ViewHolder selfInstance;

        public ViewHolder(View v, final GalleryItemClickedInterface activity) {
            super(v);
            itemImage = (ImageView) v.findViewById(R.id.itemImage);
            itemMainName = (TextView) v.findViewById(R.id.itemMainName);
            itemDate = (TextView) v.findViewById(R.id.itemDate);
            itemGPSandSource = (TextView) v.findViewById(R.id.itemSecondProperty);
            itemPictureNumberAndOrientation = (TextView) v.findViewById(R.id.itemThirdProperty);

            listeningActivity = activity;

            selfInstance = this;

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listeningActivity.isSelectable()) {
                        RelativeLayout actualCardView = (RelativeLayout) itemView.findViewById(R.id.itemBackgroundLayout);

                        if (highlighted) {
                            changeHighlightState(highlighted, actualCardView, false);
                        } else {
                            changeHighlightState(highlighted, actualCardView, false);
                        }
                    }
                    listeningActivity.itemHasBeenClicked(selfInstance);
                }
            });
        }

        public ImageView getItemImage() {
            return itemImage;
        }

        public TextView getItemMainName() {
            return itemMainName;
        }

        public TextView getItemDate() {
            return itemDate;
        }

        public TextView getItemGPSandSource() {
            return itemGPSandSource;
        }

        public TextView getItemPictureNumberAndOrientation() {
            return itemPictureNumberAndOrientation;
        }

        public EntityInterface getItemData() {
            return itemData;
        }

        public void setItemData(EntityInterface itemData){
            this.itemData = itemData;
        }

        public void unlight(){
            RelativeLayout actualCardView = (RelativeLayout) itemView.findViewById(R.id.itemBackgroundLayout);
            changeHighlightState(true, actualCardView, false);
        }

        public void deleteAnimation(){
            CardView actualCardView = (CardView) itemView.findViewById(R.id.itemCardView);
            changeHighlightState(true, actualCardView, true);
        }

        private void changeHighlightState(boolean isHighlighted, final View target, final boolean deleting){
            final float finalAlpha;

            if(isHighlighted){
                target.setAlpha(1f);
                finalAlpha = 0f;
                highlighted = false;
            }else{
                target.setAlpha(0f);
                finalAlpha = 1f;
                highlighted = true;
            }

            target.animate().alpha(finalAlpha).setDuration(highlightTime).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    target.setAlpha(finalAlpha);
                    if(deleting) {
                        listeningActivity.onDeleteAnimationFinished(selfInstance);
                    }
                }
            });
        }
    }

    private GalleryItemClickedInterface activity;
    private List<EntityInterface> listData;
    private DatabaseService database;

    public GalleryRecyclerAdapter(List<EntityInterface> dataForList, GalleryItemClickedInterface demandingActivity, DatabaseService openedDatabase) {
        activity = demandingActivity;
        listData = dataForList;
        database = openedDatabase;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, activity);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (listData.get(position).getClass() == Bunch.class) {
            bindBunchData(holder, position);
        } else {
            bindFrameData(holder, position);
        }

        holder.setItemData(listData.get(position));
    }

    private void bindFrameData(ViewHolder holder, int position){
        Frame bindedFrame = (Frame) listData.get(position);

        String bunchName = DataOperationServices.findBunchName(bindedFrame.getIDBunch(), database);

        holder.getItemImage().setImageBitmap(getImage(bunchName, bindedFrame.getFrameName(), bindedFrame.getFrameNumber()));
        holder.getItemMainName().setText(bindedFrame.getFullFrameName());
        holder.getItemDate().setText(MediaLocationsAndSettingsTimeService.transformToTime(bindedFrame.getDate()));
        holder.getItemGPSandSource().setText("Source: "+ DataOperationServices.formatCodeDecoder(bindedFrame.getFormat()));
        if(bindedFrame.getPitch()==null || bindedFrame.getOrientation()==null) {
            holder.getItemPictureNumberAndOrientation().setText("Azimuth:  Pitch: ");
        }else{
            holder.getItemPictureNumberAndOrientation().setText("Azimuth: "+bindedFrame.getOrientation()
                    +"°  Pitch: "+bindedFrame.getPitch()+"°");
        }
    }

    private void bindBunchData(ViewHolder holder, int position){
        Bunch bindedBunch = (Bunch) listData.get(position);

        Bitmap imagePicture = null;

        String pictureName = DataOperationServices.getFirstImageOfFolder(bindedBunch.getPath());
        if(pictureName != null){
            imagePicture = DataOperationServices.getSavedImage(bindedBunch.getPath()+"/"+pictureName);
        }

        holder.getItemImage().setImageBitmap(imagePicture);
        holder.getItemMainName().setText(bindedBunch.getBunchName());
        holder.getItemDate().setText(MediaLocationsAndSettingsTimeService.transformToTime(bindedBunch.getDate()));
        if(bindedBunch.getLatitude() == null || bindedBunch.getLongitude() == null) {
            holder.getItemGPSandSource().setText("GPS: unknown");
        }else{
            holder.getItemGPSandSource().setText("GPS: "+bindedBunch.getLatitude()
                    + ", " + bindedBunch.getLongitude());
        }
        holder.getItemPictureNumberAndOrientation().setText("Pictures in bunch: "
                +DataOperationServices.getCountOfPicturesInBunch(bindedBunch.getBunchName(), database));
    }

    private Bitmap getImage(String bunchNnme, String frameName, int frameNumber){
        String imagePath = DataOperationServices.composeImagePath(DataOperationServices.getBunchPath(bunchNnme, database), frameName, frameNumber);
        return DataOperationServices.getSavedImage(imagePath);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public interface GalleryItemClickedInterface{
        void itemHasBeenClicked(ViewHolder listItem);
        boolean isSelectable();
        void onDeleteAnimationFinished(ViewHolder listItem);
    }
}
