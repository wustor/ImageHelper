package com.wustor.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.wustor.helper.cache.BitmapCache;
import com.wustor.helper.config.DisplayConfig;
import com.wustor.helper.utils.BitmapDecoder;
import com.wustor.helper.utils.ImageViewHelper;
import com.wustor.helper.utils.MD5Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class BitmapRequest implements Runnable {
    private String imageUrl;
    private Context mContext;
    private String imageUriMD5;
    private DisplayConfig displayConfig;
    private WeakReference<ImageView> imageViewSoft;
    public ImageHelper.ImageListener imageListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    public BitmapRequest(Context context, ImageView imageView, String imageUrl, DisplayConfig displayConfig, ImageHelper.ImageListener imageListener) {
        this.imageViewSoft = new WeakReference<>(imageView);
        imageView.setTag(imageUrl);
        this.imageUrl = imageUrl;
        this.mContext = context;
        this.imageUriMD5 = MD5Utils.toMD5(imageUrl);
        if (displayConfig != null) {
            this.displayConfig = displayConfig;
        }
        this.imageListener = imageListener;

    }


    @Override
    public void run() {
        //加载BitMap
        BitmapCache bitmapCache = ImageHelper.getInstance(mContext).getConfig().getBitmapCache();
        Bitmap bitmap = bitmapCache.get(getImageUriMD5());
        if (bitmap == null) {
            bitmap = onLoad(mContext);
            cacheBitmap(bitmap);
        }
        deliveryToUIThread(bitmap);
    }


    private Bitmap onLoad(Context context) {
        //先下载  后读取
        downloadImgByUrl(getImageUrl(), getCache(context));
        BitmapDecoder decoder = new BitmapDecoder() {
            @Override
            public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                String absolutePath = getCache(mContext).getAbsolutePath();
                return BitmapFactory.decodeFile(absolutePath, options);
            }
        };
        int targetWidth = ImageViewHelper.getImageViewWidth(getImageView());
        int targetHeight = ImageViewHelper.getImageViewHeight(getImageView());
        Log.d("size---->", targetWidth + "VS" + targetHeight);
        return decoder.decodeBitmap(targetWidth, targetHeight);
    }


    private static boolean downloadImgByUrl(String urlStr, File file) {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int status = conn.getResponseCode();
            Log.d("code-------->", String.valueOf(status));
            is = conn.getInputStream();
            fos = new FileOutputStream(file);
            byte[] buf = new byte[512];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return true;
        } catch (Exception e) {
            Log.d("error---->", e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
//                Log.d("error---->", e.toString());
            }

        }

        return false;

    }

    private File getCache(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath();
        }
        File image = new File(cachePath, getImageUriMD5());
        if (!image.exists()) {
            try {
                boolean newFile = image.createNewFile();
                Log.d("result--->", String.valueOf(newFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }


    protected void deliveryToUIThread(final Bitmap bitmap) {
        ImageView imageView = getImageView();
        if (imageView != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateImageView(bitmap);
                }
            });
        }
    }

    private void updateImageView(Bitmap bitmap) {
        //加载正常  防止图片错位
        if (bitmap != null && getImageView().getTag().equals(getImageUrl())) {
            getImageView().setImageBitmap(bitmap);
        }
        //有可能加载失败
        if (bitmap == null && displayConfig != null && displayConfig.faildImage != -1) {
            getImageView().setImageResource(displayConfig.faildImage);
        }
        //监听
        //回调 给圆角图片  特殊图片进行扩展
        if (imageListener != null) {
            imageListener.onComplete(getImageView(), bitmap, getImageUrl());
        }
    }

    private void cacheBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (BitmapRequest.class) {
                BitmapCache bitmapCache = ImageHelper.getInstance(mContext).getConfig().getBitmapCache();
                bitmapCache.put(getImageUrl(), bitmap);
            }
        }
    }


    public ImageView getImageView() {
        return imageViewSoft.get();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageUriMD5() {
        return imageUriMD5;
    }

}
