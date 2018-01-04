package com.wustor.helper.cache;

import android.graphics.Bitmap;


public interface BitmapCache {
    void put(String tag, Bitmap bitmap);
    Bitmap get(String tag);
    void remove(String tag);
}
