package com.wustor.helper.cache;

import android.content.Context;
import android.graphics.Bitmap;


public class CacheManager {

    //内存缓存
    private MemoryCache mMemoryCache;
    //硬盘缓存
    private DiskCache mDiskCache;


    private static CacheManager mCacheManager;


    public CacheManager(Context context) {
        mDiskCache = DiskCache.getInstance(context);
        mMemoryCache = new MemoryCache();
    }

    public static CacheManager getCacheManager(Context mContext) {
        if (mCacheManager == null)
            mCacheManager = new CacheManager(mContext);
        return mCacheManager;
    }


    public void put(String tag, Bitmap bitmap) {
        mMemoryCache.put(tag, bitmap);
        mDiskCache.put(tag, bitmap);

    }

    public void putMemory(String tag, Bitmap bitmap) {
        mMemoryCache.put(tag, bitmap);
    }

    public void putDisk(String tag, Bitmap bitmap) {
        mDiskCache.put(tag, bitmap);
    }

    public Bitmap getFromMemory(String tag) {
        return mMemoryCache.get(tag);
    }

    public Bitmap getFromDisk(String tag) {
        return mDiskCache.get(tag);
    }

    public void removeMemory(String tag) {
        mMemoryCache.remove(tag);
    }

    public void removeDisk(String tag) {
        mDiskCache.remove(tag);
    }

}
