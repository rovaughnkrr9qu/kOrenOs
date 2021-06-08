package sk.uniza.fri.korenos.horizoncamera.SupportClass;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sk.uniza.fri.korenos.horizoncamera.R;

/**
 * Created by Markos on 24. 11. 2016.
 */

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private GalleryListDataPackage itemData;

        private ImageView itemImage;
        private TextView itemMainName;
        private TextView itemFirstProperty;
        private TextView itemSecondProperty;
        private TextView itemThirdProperty;

        private GalleryItemClickedInterface listeningActivity;

        public ViewHolder(View v, GalleryItemClickedInterface activity) {
            super(v);
            itemImage = (ImageView) v.findViewById(R.id.itemImage);
            itemMainName = (TextView) v.findViewById(R.id.itemMainName);
            itemFirstProperty = (TextView) v.findViewById(R.id.itemFirstProperty);
            itemSecondProperty = (TextView) v.findViewById(R.id.itemSecondProperty);
            itemThirdProperty = (TextView) v.findViewById(R.id.itemThirdProperty);

            listeningActivity = activity;

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listeningActivity.itemHasBeenClicked(itemData);
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

        public void setItemData(GalleryListDataPackage itemData ){
            this.itemData = itemData;
        }
    }

    private GalleryItemClickedInterface activity;
    private List<GalleryListDataPackage> listData;

    public GalleryRecyclerAdapter(List<GalleryListDataPackage> dataForList, GalleryItemClickedInterface demandingActivity) {
        activity = demandingActivity;
        listData = dataForList;
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
        holder.getItemFirstProperty().setText(listData.get(position).getItemFirstProperty());
        holder.getItemSecondProperty().setText(listData.get(position).getItemSecondProperty());
        holder.getItemThirdProperty().setText(listData.get(position).getItemThirdProperty());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public interface GalleryItemClickedInterface{
        void itemHasBeenClicked(GalleryListDataPackage detailInfo);
    }
}
