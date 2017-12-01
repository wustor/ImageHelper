package com.wustor.helper.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.wustor.helper.disk.DiskLruCache;
import com.wustor.helper.disk.IOUtil;
import com.wustor.helper.utils.MD5Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2017/2/6 0006.
 */

public class DiskCache implements BitmapCache {
    private static DiskCache mDiskCache;
    //缓存路径
    private String mCacheDir = "cmyy";
    //MB
    private static final int MB = 1024 * 1024;
    //jackwharton的杰作
    private DiskLruCache mDiskLruCache;

    private DiskCache(Context context) {
        iniDiskCache(context);
    }

    public static DiskCache getInstance(Context context) {
        if (mDiskCache == null) {
            synchronized (DiskCache.class) {
                if (mDiskCache == null) {
                    mDiskCache = new DiskCache(context);
                }
            }
        }
        return mDiskCache;
    }

    private void iniDiskCache(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath();
        }
        File directory = new File(cachePath, "cmyy");
        if (!directory.exists()) {
            boolean is = directory.mkdir();
            Log.d("is---->", String.valueOf(is));
        }
        try {
            //最后一个参数 指定缓存容量
            mDiskLruCache = DiskLruCache.open(directory, 1, 1, 100 * MB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void put(String request, Bitmap bitmap) {
        DiskLruCache.Editor edtor = null;
        OutputStream os = null;
        try {
            //路径必须是合法字符
            edtor = mDiskLruCache.edit(MD5Utils.toMD5(request));
            os = edtor.newOutputStream(0);
            if (persistBitmap2Disk(bitmap, os)) {
                edtor.commit();
            } else {
                edtor.abort();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean persistBitmap2Disk(Bitmap bitmap, OutputStream os) {
        BufferedOutputStream bos = new BufferedOutputStream(os);

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        try {
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            IOUtil.closeQuietly(bos);
        }
        return true;

    }

    @Override
    public Bitmap get(String request) {
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(MD5Utils.toMD5(request));
            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void remove(String request) {
        try {
            mDiskLruCache.remove(MD5Utils.toMD5(request));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
