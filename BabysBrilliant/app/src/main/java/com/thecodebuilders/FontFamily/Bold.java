package com.thecodebuilders.FontFamily;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class Bold extends TextView {

public Bold(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
}

public Bold(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
}

public Bold(Context context) {
    super(context);
    init();
}

private void init() {
    Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                                           "fonts/Bold.ttf");
    setTypeface(tf);
}

}