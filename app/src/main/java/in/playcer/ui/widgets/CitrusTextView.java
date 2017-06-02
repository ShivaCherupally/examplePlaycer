package in.playcer.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import in.playcer.R;
import in.playcer.ui.utils.FontCache;


/**
 * Custom TextView class
 */
public class CitrusTextView extends TextView {
    private static final int OPENSANS_REGULAR = 0;
    private static final int ROBOTO_CONDENSED_REGULAR = 1;
    private static final int ROBOTO_MEDIUM = 2;

    boolean isRupee = false;

    /* Class constructors */

    public CitrusTextView(Context context) {
        super(context);
    }

    public CitrusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /* Get text style form attr */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CitrusTextView,
                0, 0);
        try {

            int textStyleIndex = typedArray.getInt(R.styleable.CitrusTextView_textStyle, 0);
            setTextFontStyle(context, textStyleIndex);
        } finally {
            typedArray.recycle();
        }
    }

    public CitrusTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* Set view font style with font cache */
    private void setTextFontStyle(Context context, int textStyleIndex) {
        Typeface typeface = null;
        switch (textStyleIndex) {
            case OPENSANS_REGULAR:
                typeface = FontCache.getFont(context, "OpenSans-Regular.ttf");
                break;
            case ROBOTO_CONDENSED_REGULAR:
                typeface = FontCache.getFont(context, "RobotoCondensed-Regular.ttf");
                break;
            case ROBOTO_MEDIUM:
                typeface = FontCache.getFont(context, "Roboto-Medium.ttf");
                break;
        }
        setTypeface(typeface);
    }
}
