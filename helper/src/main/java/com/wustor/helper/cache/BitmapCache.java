package com.wustor.helper.cache;

import android.graphics.Bitmap;


public interface BitmapCache {

    void put(String request, Bitmap bitmap);

    /**
     * 通过请求取Bitmap
     *
     * @param request
     * @return
     */
    Bitmap get(String request);

    /**
     * 移除缓存
     *
     * @param request
     */
    void remove(String request);
}
