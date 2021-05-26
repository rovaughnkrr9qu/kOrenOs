package sk.uniza.fri.korenos.horizoncamera.Activities;

import android.view.View;
import android.widget.ImageView;

import sk.uniza.fri.korenos.horizoncamera.R;

/**
 * Created by Markos on 10. 11. 2016.
 */

public class VideoDisplaySpecialisation extends CameraDisplayFragment {

    @Override
    protected void setMediaImages() {
        super.setMediaImages();

        ImageView tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentActionButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_theaters_black_24dp));

        tempImage = (ImageView) getView().findViewById(R.id.mediaFragmentChangeButton);
        tempImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp));
    }
}
