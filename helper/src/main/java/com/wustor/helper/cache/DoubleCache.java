package com.wustor.helper.cache;

import android.content.Context;
import android.graphics.Bitmap;


/**
 * Created by Administrator on 2017/2/8 0008.
 */

public class DoubleCache implements BitmapCache {

    //内存缓存
    private MemoryCache mMemoryCache = new MemoryCache();
    //硬盘缓存
    private DiskCache mDiskCache;

    public DoubleCache(Context context) {
        mDiskCache = DiskCache.getInstance(context);
    }

    @Override
    public void put(String request, Bitmap bitmap) {
        mMemoryCache.put(request, bitmap);
        mDiskCache.put(request, bitmap);
    }

    @Override
    public Bitmap get(String request) {
        Bitmap bitmap = mMemoryCache.get(request);
        if(bitmap == null){
            bitmap = mDiskCache.get(request);
            if(bitmap != null){
                //放入内存，方便再获取
                mMemoryCache.put(request, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void remove(String request) {
        mMemoryCache.remove(request);
        mDiskCache.remove(request);
    }

}
