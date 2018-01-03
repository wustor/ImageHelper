package com.wustor.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.wustor.helper.cache.BitmapCache;
import com.wustor.helper.cache.DoubleCache;
import com.wustor.helper.config.DisplayConfig;
import com.wustor.helper.config.HelperConfig;
import com.wustor.helper.utils.ExecutorUtil;

import java.lang.ref.WeakReference;
import java.util.concurrent.ThreadPoolExecutor;

public class ImageHelper {
    private HelperConfig config;
    private ThreadPoolExecutor executor;
    private static WeakReference<Context> mContext;
    private static volatile ImageHelper imageHelper;


    /**
     * @param context
     * @return
     */
    public static ImageHelper getInstance(Context context) {
        mContext = new WeakReference<>(context);
        if (imageHelper == null) {
            synchronized (ImageHelper.class) {
                if (imageHelper == null) {
                    imageHelper = new ImageHelper();
                }
            }

        }
        return imageHelper;
    }

    public ImageHelper() {
        if (executor == null) {
            executor = ExecutorUtil.createThreadPool();
        }
    }

    public ImageHelper setConfig(int loading, int error) {
        HelperConfig.Builder builder = new HelperConfig.Builder();
        builder.setCachePolicy(new DoubleCache(mContext.get()))
                .setLoadingImage(loading)
                .setFailedImage(error);//缓存策略
        config = builder.build();
        return this;

    }

    public void displayImage( ImageView imageView, String url) {
        displayImage(mContext.get(), imageView, url, config.getDisplayConfig(), null);
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
        BitmapCache bitmapCache = config.getBitmapCache();
        Bitmap bitmap = bitmapCache.get(uri);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        imageView.setImageResource(displayConfig.loadingImage);
        BitmapRequest bitmapRequest = new BitmapRequest(context, imageView, uri, displayConfig, imageListener);
        //加入到线程池
        executor.submit(bitmapRequest);
    }

    public interface ImageListener {
        void onComplete(ImageView imageView, Bitmap bitmap, String uri);
    }

    /**
     * 拿到全局配置
     *
     * @return
     */
    public HelperConfig getConfig() {
        return config;
    }
}
