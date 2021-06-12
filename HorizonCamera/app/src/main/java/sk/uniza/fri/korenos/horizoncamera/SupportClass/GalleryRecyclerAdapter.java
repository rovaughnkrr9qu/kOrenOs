package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.R;

/**
 * Created by Markos on 24. 11. 2016.
 */

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private GalleryListDataPackage itemData;
        private GalleryItemClickedInterface listeningActivity;

        private ImageView itemImage;
        private TextView itemMainName;
        private TextView itemFirstProperty;
        private TextView itemSecondProperty;
        private TextView itemThirdProperty;

        private int highlightTime = 500;
        private boolean highlighted = false;
        ViewHolder selfInstance;

        public ViewHolder(View v, final GalleryItemClickedInterface activity) {
            super(v);
            itemImage = (ImageView) v.findViewById(R.id.itemImage);
            itemMainName = (TextView) v.findViewById(R.id.itemMainName);
            itemFirstProperty = (TextView) v.findViewById(R.id.itemFirstProperty);
            itemSecondProperty = (TextView) v.findViewById(R.id.itemSecondProperty);
            itemThirdProperty = (TextView) v.findViewById(R.id.itemThirdProperty);

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

        public TextView getItemFirstProperty() {
            return itemFirstProperty;
        }

        public TextView getItemSecondProperty() {
            return itemSecondProperty;
        }

        public TextView getItemThirdProperty() {
            return itemThirdProperty;
        }

        public GalleryListDataPackage getItemData() {
            return itemData;
        }

        public void setItemData(GalleryListDataPackage itemData){
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
    private List<GalleryListDataPackage> listData;
    private boolean bunchListAdapter;

    public GalleryRecyclerAdapter(List<GalleryListDataPackage> dataForList, GalleryItemClickedInterface demandingActivity, boolean bunchList) {
        activity = demandingActivity;
        listData = dataForList;
        bunchListAdapter = bunchList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, activity);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setItemData(listData.get(position));

        holder.getItemImage().setImageBitmap(listData.get(position).getItemImage());
        holder.getItemMainName().setText(listData.get(position).getItemMainName());
        holder.getItemFirstProperty().setText("Date: "+listData.get(position).getItemFirstProperty());

        if(bunchListAdapter) {
            if(listData.get(position).getItemSecondProperty() == null || listData.get(position).getItemThirdProperty() == null){
                holder.getItemSecondProperty().setText("GPS: unknown");
            }else{
                holder.getItemSecondProperty().setText("GPS: "+listData.get(position).getItemSecondProperty()
                        + ", " + listData.get(position).getItemThirdProperty());
            }
            holder.getItemThirdProperty().setText("Pictures in bunch: "+listData.get(position).getItemForthProperty());
        }else{
            holder.getItemSecondProperty().setText("Source: "+listData.get(position).getItemSecondProperty());
            if(listData.get(position).getItemThirdProperty() == null || listData.get(position).getItemForthProperty()==null){
                holder.getItemThirdProperty().setText("Azimuth:  Pitch: ");
            }else{
                holder.getItemThirdProperty().setText("Azimuth: "+listData.get(position).getItemThirdProperty()
                        +"°  Pitch: "+listData.get(position).getItemForthProperty()+"°");
            }
        }
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
