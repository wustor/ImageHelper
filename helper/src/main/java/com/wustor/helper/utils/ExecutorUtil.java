package com.wustor.helper.utils;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * author pangchao
 * created on 2017/6/20
 * email fat_chao@163.com.
 */
public class ExecutorUtil {
    private static final String TAG = ExecutorUtil.class.getSimpleName();

    private static final String PATH_CPU = "/sys/devices/system/cpu/";
    private static final String CPU_FILTER = "cpu[0-9]+";
    private static int CPU_CORES = 0;


    /**
     * Get available processors.
     */
    public static int getProcessorsCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or available processors if failed to get result
     */
    public static int getCoresNumbers() {
        if (CPU_CORES > 0) {
            return CPU_CORES;
        }
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches(CPU_FILTER, pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            //Get directory containing CPU info
            File dir = new File(PATH_CPU);
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            CPU_CORES = files.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CPU_CORES < 1) {
            CPU_CORES = Runtime.getRuntime().availableProcessors();
        }
        if (CPU_CORES < 1) {
            CPU_CORES = 1;
        }
        Log.i(TAG, "CPU cores: " + CPU_CORES);
        return CPU_CORES;
    }

    public static ThreadPoolExecutor createThreadPool() {
        // 控制最多4个keep在pool中
        int corePoolSize = Math.min(4, ExecutorUtil.getCoresNumbers());
        return new ThreadPoolExecutor(
                corePoolSize,
                Integer.MAX_VALUE,
                5, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    static final String NAME = "wustor";
                    AtomicInteger IDS = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, NAME + IDS.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.DiscardPolicy());
    }

}
