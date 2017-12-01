package com.wustor.helper.loader;

import android.graphics.Bitmap;

import com.wustor.helper.request.BitmapRequest;


public class NullLoader extends AbstractLoader {
    @Override
    protected Bitmap onLoad(BitmapRequest request) {
        return null;
    }
}
