package com.wustor.helper.config;


import com.wustor.helper.cache.CacheManager;


public class HelperConfig {


    private CacheManager cacheManager;
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

        public Builder setCacheManager(CacheManager cacheManager) {
            config.cacheManager = cacheManager;
            return this;
        }

        public Builder setLoadingImage(int resID) {
            config.displayConfig.loadingImage = resID;
            return this;
        }

        public Builder setFailedImage(int resID) {
            config.displayConfig.failedImage = resID;
            return this;
        }

        public HelperConfig build() {
            return config;
        }
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public DisplayConfig getDisplayConfig() {
        return displayConfig;
    }
}
