package com.wustor.helper.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.wustor.helper.request.BitmapRequest;
import com.wustor.helper.utils.BitmapDecoder;
import com.wustor.helper.utils.ImageViewHelper;

import java.io.File;


public class LocalLoader extends AbstractLoader {
    @Override
    protected Bitmap onLoad(BitmapRequest request) {
        //得到本地图片的路径
        final String path = Uri.parse(request.getImageUrl()).getPath();
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        BitmapDecoder decoder = new BitmapDecoder() {
            @Override
            public Bitmap decodeBitmapWithOption(BitmapFactory.Options options) {
                return BitmapFactory.decodeFile(path, options);
            }
        };
        int targetWidth = ImageViewHelper.getImageViewWidth(request.getImageView());
        int targetHeight = ImageViewHelper.getImageViewHeight(request.getImageView());
        return decoder.decodeBitmap(targetWidth, targetHeight);
    }
}
