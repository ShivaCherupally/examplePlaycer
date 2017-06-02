package in.playcer.smartView;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by HARIKRISHNA on 8/21/2015.
 * at CaretTech
 */
public class BitmapImage implements SmartImage {
    private Bitmap bitmap;

    public BitmapImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(Context context) {
        return bitmap;
    }
}