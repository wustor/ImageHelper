package com.wustor.helper.request;

import android.util.Log;

import com.wustor.helper.loader.Loader;
import com.wustor.helper.loader.LoaderManager;

import java.util.concurrent.BlockingQueue;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/2/6 0006.
 * 转发器
 * 请求转发线程  不断从请求队列中获取请求
 */

public class RequestDispatcher extends Thread {
    //请求队列
    private BlockingQueue<BitmapRequest> mRequestQueue;

    public RequestDispatcher(BlockingQueue<BitmapRequest> mRequestQueue) {
        this.mRequestQueue = mRequestQueue;
    }

    @Override
    public void run() {
        while (!isInterrupted())
        {
            try {
                //阻塞式函数
                BitmapRequest request=mRequestQueue.take();
                /**
                 * 处理请求对象
                 */
                String schema=parseSchema(request.getImageUrl());
                //获取加载器
                Loader loader= LoaderManager.getInstance().getLoader(schema);
                loader.loadImage(request);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    private String parseSchema(String imageUrl) {
         if(imageUrl.contains("://"))
         {
             return imageUrl.split("://")[0];
         }
         else
         {
             Log.i(TAG,"不支持此类型");
         }

        return null;
    }
}
