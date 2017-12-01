package com.wustor.helper.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.wustor.helper.request.BitmapRequest;
import com.wustor.helper.utils.BitmapDecoder;
import com.wustor.helper.utils.ImageViewHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UrlLoader extends AbstractLoader {

    @Override
    protected Bitmap onLoad(final BitmapRequest request) {
        //先下载  后读取
        downloadImgByUrl(request.getImageUrl(), getCache(request));
        BitmapDecoder decoder = new BitmapDecoder() {
            @Override
            public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                String absolutePath = getCache(request).getAbsolutePath();
                return BitmapFactory.decodeFile(absolutePath, options);
            }
        };
        int targetWidth = ImageViewHelper.getImageViewWidth(request.getImageView());
        int targetHeight = ImageViewHelper.getImageViewHeight(request.getImageView());
        Log.d("size---->", targetWidth + "VS" + targetHeight);
        return decoder.decodeBitmap(targetWidth, targetHeight);
    }

    public static boolean downloadImgByUrl(String urlStr, File file) {
        Log.d("tagUrl--->", urlStr);
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

    private File getCache(BitmapRequest request) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = request.getContext().getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = request.getContext().getCacheDir().getPath();
        }
        File image = new File(cachePath, request.getImageUriMD5());
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

}
