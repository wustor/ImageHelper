package com.wustor.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.wustor.helper.cache.CacheManager;
import com.wustor.helper.config.DisplayConfig;
import com.wustor.helper.config.LoadListener;
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
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class BitmapRequest implements Runnable {
    private String imageUrl;
    private Context mContext;
    private String imageUriMD5;
    private LoadListener loadListener;
    private DisplayConfig displayConfig;
    private static OkHttpClient mClient;
    private WeakReference<ImageView> imageViewSoft;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    public BitmapRequest(Context context, ImageView imageView, String imageUrl, DisplayConfig displayConfig, LoadListener loadListener) {
        this.imageViewSoft = new WeakReference<>(imageView);
        imageView.setTag(imageUrl);
        this.imageUrl = imageUrl;
        this.mContext = context;
        this.imageUriMD5 = MD5Utils.toMD5(imageUrl);
        if (displayConfig != null) {
            this.displayConfig = displayConfig;
        }
        this.loadListener = loadListener;

    }


    @Override
    public void run() {
        //加载BitMap
        CacheManager cacheManager = ImageHelper.getInstance(mContext).getConfig().getCacheManager();
        Bitmap bitmap = cacheManager.getFromDisk(getImageUriMD5());
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
        initializeOkHttp();
        try {
            HttpURLConnection conn = new OkUrlFactory(mClient).open(new URL(urlStr));
            conn.setConnectTimeout(15 * 3000);
            conn.setReadTimeout(15 * 3000);
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
    private static void initializeOkHttp() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }


                }};

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            mClient = new OkHttpClient();
            mClient.setSslSocketFactory(sc.getSocketFactory());
            mClient.setHostnameVerifier(new NullHostNameVerifier());

        } catch (Exception e) {
            e.printStackTrace();
        }

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
        if (bitmap != null && getImageView().getTag().equals(getImageUrl())) {
            getImageView().setImageBitmap(bitmap);
        }
        if (bitmap == null && displayConfig != null && displayConfig.failedImage != -1) {
            getImageView().setImageResource(displayConfig.failedImage);
        }
        if (loadListener != null) {
            loadListener.load(getImageUrl(), "failed");
        }
    }

    private void cacheBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (BitmapRequest.class) {
                CacheManager cacheManager = ImageHelper.getInstance(mContext).getConfig().getCacheManager();
                cacheManager.put(getImageUriMD5(), bitmap);
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
