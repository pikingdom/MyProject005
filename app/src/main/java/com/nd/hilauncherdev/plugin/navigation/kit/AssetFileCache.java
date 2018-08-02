package com.nd.hilauncherdev.plugin.navigation.kit;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.loader.NaviCardLoader;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;

/**
 * 缓存工具
 * 缓存优先级:优先读取给定的本地文件夹中的数据，若不存在则读取assets中的数据
 * Created by linliangbin on 2017/8/7 16:29.
 */

public class AssetFileCache {


    private static AssetFileCache instance;
    private Context context;
    private String CACHE_DIR = NaviCardLoader.NAV_DIR;


    private AssetFileCache(Context context) {
        this.context = context;
    }

    public synchronized static AssetFileCache getInstance(Context context) {
        if (instance == null) {
            instance = new AssetFileCache(context);
        }
        return instance;
    }


    private String getCacheFilePath(String key) {
        return CACHE_DIR + key;
    }

    /**
     * @desc 本地缓存 > Asset文件
     * @author linliangbin
     * @time 2017/8/7 16:36
     */
    public String readByFile(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        if (FileUtil.isFileExits(getCacheFilePath(key))) {
            return FileUtil.readFileContent(getCacheFilePath(key));
        }
        return null;
    }

    /**
     * @desc 仅从Asset 中读取数据
     * @author linliangbin
     * @time 2017/8/7 16:40
     */
    public String readByAsset(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return FileUtil.readFromAssetsFile(context, key);
    }


    /**
     * @desc 写入本地缓存
     * @author linliangbin
     * @time 2017/8/7 16:40
     */
    public void writeToFile(String key, String data) {
        FileUtil.writeFile(getCacheFilePath(key), data, false);
    }


    /**
     * @desc 清空本地缓存数据
     * @author linliangbin
     * @time 2017/8/7 16:40
     */
    public boolean clearFileCache(String key) {
        if (FileUtil.isFileExits(getCacheFilePath(key))) {
            return FileUtil.delAllFile(getCacheFilePath(key));
        } else {
            return true;
        }
    }

}
