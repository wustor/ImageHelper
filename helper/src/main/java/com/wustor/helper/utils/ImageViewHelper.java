package com.wustor.helper.utils;

import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;

public class ImageViewHelper {
    //默认的图片宽高
    private static int DEFAULT_WIDTH = 200;
    private static int DEFAULT_HEIGHT = 200;

    public static int getImageViewWidth(final ImageView imageView) {
        if (imageView != null) {
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int height = 0;
            if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = imageView.getWidth();
            }
            if (height <= 0 && params != null) {
                height = params.height;
            }
            if (height <= 0) {
                height = getImageViewFieldValue(imageView, "mMaxHeight");
            }
            return height;
        }
        return DEFAULT_WIDTH;

    }


    /**
     * 获取图片的高度
     *
     * @param imageView
     * @return
     */
    public static int getImageViewHeight(ImageView imageView) {
        if (imageView != null) {
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int height = 0;
            if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = imageView.getWidth();
            }
            if (height <= 0 && params != null) {
                height = params.height;
            }
            if (height <= 0) {
                height = getImageViewFieldValue(imageView, "mMaxHeight");
            }
            return height;
        }
        return DEFAULT_HEIGHT;
    }


    private static int getImageViewFieldValue(ImageView imageView, String fieldName) {
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(imageView);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                return fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
