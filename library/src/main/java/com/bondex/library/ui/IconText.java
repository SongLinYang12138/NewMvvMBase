package com.bondex.library.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class IconText extends TextView {

    private Typeface iconfont = null;

    public IconText(Context context) {
        super(context);
        initTextivew(context);
    }

    public IconText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initTextivew(context);
    }

    private void initTextivew(Context context) {

        iconfont   = Typeface.createFromAsset(context.getAssets(), "iconfont/iconfont.ttf");
        setTypeface(iconfont);
    }


}
