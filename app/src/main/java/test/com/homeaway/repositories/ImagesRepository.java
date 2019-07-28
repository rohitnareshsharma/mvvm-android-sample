package test.com.homeaway.repositories;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import test.com.homeaway.HomeAwayApplication;

public class ImagesRepository {

    @BindingAdapter(value={"imageUrl", "placeholder"}, requireAll=false)
    public static void setImageUrl(ImageView imageView, String url,
                                   Drawable placeHolder) {
        if (url == null) {
            imageView.setImageDrawable(placeHolder);
        } else {
            Glide.with(imageView.getContext().getApplicationContext())
                    .load(url)
                    .centerCrop()
                    .placeholder(placeHolder)
                    .into(imageView);
        }
    }
}
