package com.wustor.helper.loader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.wustor.helper.cache.BitmapCache;
import com.wustor.helper.config.DisplayConfig;
import com.wustor.helper.request.BitmapRequest;


public abstract class AbstractLoader implements Loader {
    //拿到用户自定义配置的缓存策略
    private BitmapCache bitmapCache = SimpleImageLoader.getInstance().getConfig().getBitmapCache();
    //拿到显示配置
    private DisplayConfig displayConfig = SimpleImageLoader.getInstance().getConfig().getDisplayConfig();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void loadImage(BitmapRequest request) {
        //从缓存取到Bitmap
        Bitmap bitmap = onLoad(request);
//        if (bitmap == null) {
//            //显示默认加载图片
////            showLoadingImage(request);
//            //开始真正加载图片
//            bitmap = onLoad(request);
//            //缓存图片
//            cacheBitmap(request, bitmap);
//        }
        //缓存图片
        cacheBitmap(request, bitmap);
        deliveryToUIThread(request, bitmap);
    }

    /**
     * 交给主线程显示
     *
     * @param request
     * @param bitmap
     */
    protected void deliveryToUIThread(final BitmapRequest request, final Bitmap bitmap) {
        ImageView imageView = request.getImageView();
        if (imageView != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateImageView(request, bitmap);
                }
            });
        }
    }

    private void
    updateImageView(final BitmapRequest request, final Bitmap bitmap) {
        ImageView imageView = request.getImageView();
        //加载正常  防止图片错位
        if (bitmap != null &&  imageView.getTag().equals(request.getImageUrl())) {
            imageView.setImageBitmap(bitmap);
        }
        //有可能加载失败
        if (bitmap == null && displayConfig != null && displayConfig.faildImage != -1) {
            imageView.setImageResource(displayConfig.faildImage);
        }
        //监听
        //回调 给圆角图片  特殊图片进行扩展
        if (request.imageListener != null) {
            request.imageListener.onComplete(imageView, bitmap, request.getImageUrl());
        }
    }

    /**
     * 缓存图片
     *
     * @param request
     * @param bitmap
     */
    private void cacheBitmap(BitmapRequest request, Bitmap bitmap) {
        if (request != null && bitmap != null) {
            synchronized (AbstractLoader.class) {
                bitmapCache.put(request.getImageUrl(), bitmap);
            }
        }
    }

    //抽象加载策略  因为加载网络图片和本地图片有差异
    protected abstract Bitmap onLoad(BitmapRequest request);


    /**
     * 加载前显示的图片
     *
     * @param request
     */
    protected void showLoadingImage(BitmapRequest request) {
        //指定了，显示配置
        if (hasLoadingPlaceHolder()) {
            final ImageView imageView = request.getImageView();
            if (imageView != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageResource(displayConfig.loadingImage);
                    }
                });
            }

        }
    }

    protected boolean hasLoadingPlaceHolder() {
        return (displayConfig != null && displayConfig.loadingImage > 0);
    }

}
