package com.wustor.helper.loader;


import com.wustor.helper.request.BitmapRequest;

public interface Loader {
    /**
     * 加载图片
     * @param request
     */
    void loadImage(BitmapRequest request);
}
