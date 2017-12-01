package com.wustor.helper.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.wustor.helper.cache.BitmapCache;
import com.wustor.helper.config.DisplayConfig;
import com.wustor.helper.config.ImageLoaderConfig;
import com.wustor.helper.request.BitmapRequest;
import com.wustor.helper.request.RequestQueue;


public class SimpleImageLoader {
    //配置
    private ImageLoaderConfig config;
    //请求队列
    private RequestQueue mRequestQueue;
    //单例对象
    private static volatile SimpleImageLoader mInstance;

    private SimpleImageLoader(ImageLoaderConfig imageLoaderConfig) {
        this.config = imageLoaderConfig;
        mRequestQueue = new RequestQueue(config.getThreadCount());
        //开启请求队列
        mRequestQueue.start();
    }

    /**
     * 获取单例方法
     * 第一次调用
     *
     * @param config
     * @return
     */
    public static SimpleImageLoader getInstance(ImageLoaderConfig config) {
        if (mInstance == null) {
            synchronized (SimpleImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new SimpleImageLoader(config);
                }
            }

        }
        return mInstance;
    }

    /**
     * 第二次获取单例
     *
     * @return
     */
    public static SimpleImageLoader getInstance() {
        return mInstance;
    }

    /**
     * 暴露获取图片
     *
     * @param imageView
     * @param uri       http:   file 开头
     */
    public void displayImage(Context context, ImageView imageView, String uri) {
        displayImage(context, imageView, uri, null, null);
    }

    /**
     * 重载
     *
     * @param imageView
     * @param uri
     * @param displayConfig
     * @param imageListener
     */
    public void displayImage(Context context, ImageView imageView, String uri, DisplayConfig displayConfig, ImageListener imageListener) {
        //实例化一个请求
        BitmapCache bitmapCache = SimpleImageLoader.getInstance().getConfig().getBitmapCache();
        Bitmap bitmap = bitmapCache.get(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        imageView.setImageResource(displayConfig.loadingImage);

        BitmapRequest bitmapRequest = new BitmapRequest(context, imageView, uri, displayConfig, imageListener);
        //添加到队列里面
        mRequestQueue.addRequest(bitmapRequest);
    }

    public interface ImageListener {
        /**
         * @param imageView
         * @param bitmap
         * @param uri
         */
        void onComplete(ImageView imageView, Bitmap bitmap, String uri);
    }

    /**
     * 拿到全局配置
     *
     * @return
     */
    public ImageLoaderConfig getConfig() {
        return config;
    }
}
