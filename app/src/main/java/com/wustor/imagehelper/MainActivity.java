package com.wustor.imagehelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wustor.helper.ImageHelper;

public class MainActivity extends AppCompatActivity {

    private ImageView ivDemo;
    private String httpUrl = "http://orbm62bsw.bkt.clouddn.com/thread.png";
    private String httpsUrl = "https://raw.githubusercontent.com/Solartisan/WaveSideBar/master/preview/design.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivDemo = (ImageView) findViewById(R.id.iv_demo);
        ImageHelper.getInstance(this).setConfig(R.mipmap.ic_launcher, R.mipmap.ic_launcher).displayImage(ivDemo, httpsUrl);

    }


}
