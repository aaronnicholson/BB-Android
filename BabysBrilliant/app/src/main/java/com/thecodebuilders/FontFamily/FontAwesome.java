package com.thecodebuilders.FontFamily;



import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontAwesome extends TextView {

public FontAwesome(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
}

public FontAwesome(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
}

public FontAwesome(Context context) {
    super(context);
    init();
}

private void init() {
    Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                                           "fonts/FontAwesome_ttf.ttf");
    setTypeface(tf);
}

}
