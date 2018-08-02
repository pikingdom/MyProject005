package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.content.SharedPreferences;


public class SPUtil {

    /**
     * 桌面配置文件
     */
    public static final String NAME = "configsp";

    //用户第一次安装的桌面版本
    private static final String KEY_VERSION_FROM = "is_resident";

    /**
     * 获取新增用户时记录的桌面版本号
     * @Title: getVersionCodeForResident
     * @author lytjackson@gmail.com
     * @date 2014-3-31
     * @return
     */
    public static int getVersionCodeFrom(Context mContext) {
        SharedPreferences launcherVersionSP = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return launcherVersionSP.getInt(KEY_VERSION_FROM, -1);
    }

}
