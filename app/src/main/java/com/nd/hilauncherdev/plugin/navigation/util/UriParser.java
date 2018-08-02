package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;

import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;


/**
 * Created by linliangbin_dian91 on 2016/3/23.
 */
public class UriParser {

    private static final String TAG = "UriParser";
    private static final String URI_91_KEY = "zm91txwd20141231";
    private static final String URI_91_SERVICE_KEY = "zm91txwd20141231service";

    public static final String ACTION_THEME_DOWNLOAD = "action.themeshop.theme.download";
    public static final String ACTION_THEME_SHOP_MODULE_DOWNLOAD = "action.themeshop.module.download";



    /**
     * activity协议转service协议
     * @param s
     * @return
     */
    public static String changeActivityUriToService(String s){
        if(s == null || !s.contains(URI_91_KEY) || s.contains(URI_91_SERVICE_KEY))
            return s;
        return s.replace(URI_91_KEY, URI_91_SERVICE_KEY);
    }




    /**
     * 处理头部为zm91txwd20141231://、zm91txwd20141231service://、http://、#Intent的uri
     * @return
     */
    public static void handleUriEx(Context ctx, String url) {
        if (ctx == null)
            return;
        ReflectInvoke.handleUriEx(ctx,url);
    }
}
