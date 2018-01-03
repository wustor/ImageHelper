package com.wustor.imagehelper;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wustor.helper.ImageHelper;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ImageView ivDemo;
    private String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1515000362638&di=e5733da1d8534b6d2161b68a625d8171&imgtype=0&src=http%3A%2F%2Fd.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F83025aafa40f4bfb8862debb094f78f0f63618aa.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ivDemo = (ImageView) findViewById(R.id.iv_demo);
        ImageHelper.getInstance(this).setConfig(R.mipmap.ic_launcher, R.mipmap.ic_launcher).displayImage(ivDemo, url);

    }


}
