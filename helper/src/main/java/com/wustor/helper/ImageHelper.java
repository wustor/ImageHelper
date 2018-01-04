package com.wustor.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.wustor.helper.cache.CacheManager;
import com.wustor.helper.config.DisplayConfig;
import com.wustor.helper.config.HelperConfig;
import com.wustor.helper.config.LoadListener;
import com.wustor.helper.utils.ExecutorUtil;
import com.wustor.helper.utils.MD5Utils;

import java.lang.ref.WeakReference;
import java.util.concurrent.ThreadPoolExecutor;

public class ImageHelper {
    private HelperConfig config;
    private ThreadPoolExecutor executor;
    private static WeakReference<Context> mContext;
    private static volatile ImageHelper imageHelper;

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
        builder.setCacheManager(CacheManager.getCacheManager(mContext.get()))
                .setLoadingImage(loading)
                .setFailedImage(error);
        config = builder.build();
        return this;

    }

    public void displayImage(ImageView imageView, String url) {
        displayImage(mContext.get(), imageView, url, config.getDisplayConfig(), null);
    }

    public void displayImage(Context context, ImageView imageView, String url, DisplayConfig displayConfig, LoadListener loadListener) {
        //实例化一个请求
        CacheManager cacheManager = config.getCacheManager();
        Bitmap bitmap = cacheManager.getFromMemory(MD5Utils.toMD5(url));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        imageView.setImageResource(displayConfig.loadingImage);
        BitmapRequest bitmapRequest = new BitmapRequest(context, imageView, url, displayConfig, loadListener);
        //加入到线程池
        executor.submit(bitmapRequest);
    }


    public HelperConfig getConfig() {
        return config;
    }
}
