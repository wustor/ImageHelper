package com.wustor.imagehelper;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wustor.helper.cache.DoubleCache;
import com.wustor.helper.config.ImageLoaderConfig;
import com.wustor.helper.loader.SimpleImageLoader;
import com.wustor.helper.policy.ReversePolicy;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ImageView ivDemo;
    private String url = "http://7xi8d6.com1.z0.glb.clouddn.com/20171206084331_wylXWG_misafighting_6_12_2017_8_43_16_390.jpeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();

    }


    private void initView() {
        ivDemo = (ImageView) findViewById(R.id.iv_demo);

    }

    private void initData() {
        //配置
        SimpleImageLoader imageLoader = SimpleImageLoader.getInstance();
        if (imageLoader == null) {
            ImageLoaderConfig.Builder build = new ImageLoaderConfig.Builder();
            build.setThreadCount(5) //线程数量
                    .setLoadPolicy(new ReversePolicy()) //加载策略
                    .setCachePolicy(new DoubleCache(mContext)) //缓存策略
                    .setLoadingImage(R.mipmap.ic_launcher)
                    .setFailedImage(R.mipmap.ic_launcher);
            ImageLoaderConfig config = build.build();
            imageLoader = SimpleImageLoader.getInstance(config);
        }
////        初始化
        imageLoader.displayImage(mContext, ivDemo, url);
    }
}
