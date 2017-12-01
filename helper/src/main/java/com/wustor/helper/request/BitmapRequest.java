package com.wustor.helper.request;

import android.content.Context;
import android.widget.ImageView;

import com.wustor.helper.config.DisplayConfig;
import com.wustor.helper.loader.SimpleImageLoader;
import com.wustor.helper.policy.LoadPolicy;
import com.wustor.helper.utils.MD5Utils;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/2/6 0006.
 * List
 */

public class BitmapRequest implements Comparable<BitmapRequest> {
    //持有imageview的软引用
    private WeakReference<ImageView> imageViewSoft;
    //图片路径
    private String imageUrl;
    //MD5的图片路径
    private String imageUriMD5;

    public void setContext(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    private Context mContext;
    //下载完成监听
    public SimpleImageLoader.ImageListener imageListener;

    private DisplayConfig displayConfig;

    public BitmapRequest(Context context, ImageView imageView, String imageUrl, DisplayConfig displayConfig, SimpleImageLoader.ImageListener imageListener) {
        this.imageViewSoft = new WeakReference<>(imageView);
        //设置可见的Image的Tag，要下载的图片路径
        imageView.setTag(imageUrl);
        this.imageUrl = imageUrl;
        this.mContext=context;
        this.imageUriMD5 = MD5Utils.toMD5(imageUrl);
        if (displayConfig != null) {
            this.displayConfig = displayConfig;
        }
        this.imageListener = imageListener;

    }

    //加载策略
    private LoadPolicy loadPolicy = SimpleImageLoader.getInstance().getConfig().getLoadPolicy();
    /**
     * 编号
     */
    private int serialNo;


    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitmapRequest that = (BitmapRequest) o;

        if (serialNo != that.serialNo) return false;
        return loadPolicy != null ? loadPolicy.equals(that.loadPolicy) : that.loadPolicy == null;

    }

    @Override
    public int hashCode() {
        int result = loadPolicy != null ? loadPolicy.hashCode() : 0;
        result = 31 * result + serialNo;
        return result;
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

    public DisplayConfig getDisplayConfig() {
        return displayConfig;
    }

    public LoadPolicy getLoadPolicy() {
        return loadPolicy;
    }

    @Override
    public int compareTo(BitmapRequest o) {
        return loadPolicy.compareto(o, this);
    }
}
