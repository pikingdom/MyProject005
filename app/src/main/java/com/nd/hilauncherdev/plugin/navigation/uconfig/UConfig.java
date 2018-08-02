package com.nd.hilauncherdev.plugin.navigation.uconfig;

import android.content.Context;

import com.nd.hilauncherdev.plugin.navigation.util.Global;

/**
 * Description: 万能配置通用类</br>
 * Author: cxy
 * Date: 2017/2/20.
 */

public class UConfig {

    private static UConfig obj;
    private UConfigHelper helper;

    private UConfig() {
        initialise(Global.context.getApplicationContext());
    }

    public static UConfig get() {
        if (obj == null) {
            synchronized (UConfig.class) {
                if (obj == null) {
                    obj = new UConfig();
                }
            }
        }
        return obj;
    }

    private final void initialise(Context context) {
        helper = new UConfigHelper(context);
    }


    public final int getVersion(String configName) {
        return helper.getVersion(configName);
    }

    public final void markDone(String configName) {
        markDone(configName, helper.getVersion(configName) + 1);
    }

    public final void markDone(String configName, int newVer) {
        final int oldVer = helper.getVersion(configName);
        if (newVer > oldVer) {
            helper.setVersion(configName, newVer);
        } else {
            helper.setVersion(configName, helper.getVersion(configName) + 1);
        }
    }

    public final String getContent(String configName) {
        return helper.getContent(configName);
    }

    public final void commit(String configName, String content) {
        helper.setContent(configName, content);
    }

    public final <T> T getContent(String configName, ContentConverter<T> converter) {
        final String src = getContent(configName);
        if (converter != null) {
            return converter.convert(src);
        }
        return null;
    }
}
