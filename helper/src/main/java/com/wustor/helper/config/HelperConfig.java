package com.wustor.helper.config;


import com.wustor.helper.cache.BitmapCache;
import com.wustor.helper.cache.MemoryCache;


public class HelperConfig {

    private BitmapCache bitmapCache = new MemoryCache();
    //显示的配置
    private DisplayConfig displayConfig = new DisplayConfig();

    private HelperConfig() {
    }

    /**
     * 建造者模式
     * 和AlterDialog建造过程类似
     */
    public static class Builder {
        private HelperConfig config;

        public Builder() {
            config = new HelperConfig();
        }

        /**
         * 设置缓存策略
         *
         * @param bitmapCache
         * @return
         */
        public Builder setCachePolicy(BitmapCache bitmapCache) {
            config.bitmapCache = bitmapCache;
            return this;
        }
        /**
         * 设置加载过程中的图片
         *
         * @param resID
         * @return
         */
        public Builder setLoadingImage(int resID) {
            config.displayConfig.loadingImage = resID;
            return this;
        }

        /**
         * 设置加载过程中的图片
         *
         * @param resID
         * @return
         */
        public Builder setFailedImage(int resID) {
            config.displayConfig.faildImage = resID;
            return this;
        }

        public HelperConfig build() {
            return config;
        }
    }

    public BitmapCache getBitmapCache() {
        return bitmapCache;
    }




    public DisplayConfig getDisplayConfig() {
        return displayConfig;
    }
}
