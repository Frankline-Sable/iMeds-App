package com.fsdev.imeds;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Frankline Sable on 19/11/2017.
 */

public class TypeFace_Handler {
private Context mContext;

    public TypeFace_Handler(Context mContext) {
        this.mContext = mContext;
    }

    public Typeface setFont(String mTypeface) {
        return Typeface.createFromAsset(mContext.getAssets(), mTypeface);
    }
}

