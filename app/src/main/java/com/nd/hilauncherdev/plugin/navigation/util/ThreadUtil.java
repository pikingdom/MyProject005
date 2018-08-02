package com.nd.hilauncherdev.plugin.navigation.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 线程池管理工具类
 */
public class ThreadUtil {
    /**
     * 非固定数量线程池
     */
    private static ExecutorService moreExecutorService = Executors.newCachedThreadPool();

    private static ExecutorService singleExecutorService = Executors.newFixedThreadPool(1);

    /**
     * 非固定数量线程池
     * @param command
     */
    public static void executeMore(Runnable command) {
        moreExecutorService.execute(command);
    }


    public static void executeSingle(Runnable command) {
        singleExecutorService.execute(command);
    }

}
