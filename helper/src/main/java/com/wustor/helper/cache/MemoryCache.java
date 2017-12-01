package com.wustor.helper.cache;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;


public class MemoryCache implements  BitmapCache {
    private LruCache<String,Bitmap> mLruCache;
    public MemoryCache()
    {
        int maxSize= (int) (Runtime.getRuntime().maxMemory()/8);
        Log.d("size--->", String.valueOf(maxSize));
        mLruCache=new LruCache<String,Bitmap>(maxSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    @Override
    public void put(String request, Bitmap bitmap) {
        mLruCache.put(request,bitmap);
    }

    @Override
    public Bitmap get(String request) {
        return mLruCache.get(request);
    }

    @Override
    public void remove(String request) {
         mLruCache.remove(request);
    }
}
