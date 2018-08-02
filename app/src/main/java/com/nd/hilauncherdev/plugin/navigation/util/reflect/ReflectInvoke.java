package com.nd.hilauncherdev.plugin.navigation.util.reflect;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.nd.hilauncherdev.kitset.util.reflect.CommonKeepForReflect;
import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;

/**
 * 需要兼容旧版本以及定制版本
 * Created by linliangbin on 2017/3/17 15:28.
 */

public class ReflectInvoke {


    /**
     * 提交Activity进入
     * <p>Title: submitPageStartEvent</p>
     * <p>Description: </p>
     *
     * @param ctx
     * @param pageId 页面id
     * @author maolinnan_350804
     */
    public static void submitPageStartEvent(Context ctx, int pageId) {
        if (ctx == null)
            return;
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher() ||
                    CommonGlobal.isDianxinLauncher(ctx) || CommonGlobal.isAndroidLauncher(ctx)) {
                try {
                    CommonKeepForReflect.submitPageStartEvent_V8198(ctx, pageId);
                } catch (Throwable t) {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitPageStartEvent", ctx, pageId);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    CommonKeepForReflect.submitPageStartEvent_V8198(ctx, pageId);
                } else {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitPageStartEvent", ctx, pageId);
                }
            }

        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }

    }

    /**
     * 提交Activity退出
     * <p>Title: submitPageEndEvent</p>
     * <p>Description: </p>
     *
     * @param ctx
     * @param pageId 页面id
     * @author maolinnan_350804
     */
    public static void submitPageEndEvent(Context ctx, int pageId) {
        if (ctx == null)
            return;
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(ctx) || CommonGlobal.isAndroidLauncher(ctx)) {
                try {
                    CommonKeepForReflect.submitPageEndEvent_V8198(ctx, pageId);
                } catch (Throwable t) {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitPageEndEvent", ctx, pageId);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    CommonKeepForReflect.submitPageEndEvent_V8198(ctx, pageId);
                } else {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitPageEndEvent", ctx, pageId);
                }
            }

        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }
    }

    /**
     * 提交展示事件
     * <p>Title: submitShowEvent</p>
     * <p>Description: </p>
     *
     * @param ctx
     * @param pageId  页面id
     * @param posId   位置id
     * @param resId   资源id
     * @param resType 资源类型
     * @author maolinnan_350804
     */
    public static void submitShowEvent(Context ctx, int pageId, int posId, int resId, int resType) {
        if (ctx == null)
            return;
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(ctx) || CommonGlobal.isAndroidLauncher(ctx)) {
                try {
                    CommonKeepForReflect.submitShowEvent_V8198(ctx, pageId, posId, resId, resType);
                } catch (Throwable t) {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitShowEvent", ctx, pageId, posId, resId, resType);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    CommonKeepForReflect.submitShowEvent_V8198(ctx, pageId, posId, resId, resType);
                } else {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitShowEvent", ctx, pageId, posId, resId, resType);
                }
            }


        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }
    }

    /**
     * 提交点击事件
     * <p>Title: submitClickEvent</p>
     * <p>Description: </p>
     *
     * @param ctx
     * @param pageId  页面id
     * @param posId   位置id
     * @param resId   资源id
     * @param resType 资源类型
     * @author maolinnan_350804
     */
    public static void submitClickEvent(Context ctx, int pageId, int posId, int resId, int resType) {
        if (ctx == null)
            return;
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(ctx) || CommonGlobal.isAndroidLauncher(ctx)) {
                try {
                    CommonKeepForReflect.submitClickEvent_V8198(ctx, pageId, posId, resId, resType);
                } catch (Throwable t) {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitClickEvent", ctx, pageId, posId, resId, resType);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    CommonKeepForReflect.submitClickEvent_V8198(ctx, pageId, posId, resId, resType);
                } else {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitClickEvent", ctx, pageId, posId, resId, resType);
                }
            }


        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }
    }

    /**
     * 提交点击事件
     * <p>Title: submitClickEvent</p>
     * <p>Description: </p>
     *
     * @param ctx
     * @param pageId  页面id
     * @param posId   位置id
     * @param resId   资源id
     * @param resType 资源类型
     * @author maolinnan_350804
     */
    public static void submitClickEvent(Context ctx, int pageId, int posId, int resId, int resType, int sourceId) {
        if (ctx == null)
            return;
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(ctx) || CommonGlobal.isAndroidLauncher(ctx)) {
                try {
                    CommonKeepForReflect.submitClickEvent_V8198(ctx, pageId, posId, resId, resType, sourceId);
                } catch (Throwable t) {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitClickEvent", ctx, pageId, posId, resId, resType, sourceId);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    CommonKeepForReflect.submitClickEvent_V8198(ctx, pageId, posId, resId, resType, sourceId);
                } else {
                    Reflect.on("com.nd.hilauncherdev.analysis.cvanalysis.CvAnalysis").call("submitClickEvent", ctx, pageId, posId, resId, resType, sourceId);
                }
            }


        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }
    }


    public static void initImageLoaderConfig(Context context) {
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(context) || CommonGlobal.isAndroidLauncher(context)) {
                try {
                    NavigationKeepForReflect.initImageLoaderConfig_V8198(context);
                } catch (Throwable t) {
                    Reflect.on("com.nd.hilauncherdev.app.activity.BrowserPluginLoader").call("initImageLoaderConfig", context);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    NavigationKeepForReflect.initImageLoaderConfig_V8198(context);
                } else {
                    Reflect.on("com.nd.hilauncherdev.app.activity.BrowserPluginLoader").call("initImageLoaderConfig", context);
                }
            }


        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }
    }

    public static boolean getIsAnzhi() {
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()) {
                try {
                    return NavigationKeepForReflect.getIsAnzhi_V8198();
                } catch (Throwable t) {
                    return Reflect.on("com.nd.hilauncherdev.launcher.navigation.NavigationView").call("getIsAnzhi").get();
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    return NavigationKeepForReflect.getIsAnzhi_V8198();
                } else {
                    return Reflect.on("com.nd.hilauncherdev.launcher.navigation.NavigationView").call("getIsAnzhi").get();
                }
            }


        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }
        return false;
    }

    public static void setNavigationViewChildIndex(Context launcher, int index) {
        try {

            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(launcher) || CommonGlobal.isAndroidLauncher(launcher)) {
                try {
                    NavigationKeepForReflect.setNavigationViewChildIndex_V8198(launcher, index);
                } catch (Throwable t) {
                    Reflect.on(launcher).call("setNavigationViewChildIndex", index);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    NavigationKeepForReflect.setNavigationViewChildIndex_V8198(launcher, index);
                } else {
                    Reflect.on(launcher).call("setNavigationViewChildIndex", index);
                }
            }


        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }
    }

    public static void setNavigationViewChildCount(Context launcher, int index) {
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(launcher) || CommonGlobal.isAndroidLauncher(launcher)) {
                try {
                    NavigationKeepForReflect.setNavigationViewChildCount_V8198(launcher, index);
                } catch (Throwable t) {
                    Reflect.on(launcher).call("setNavigationViewChildCount", index);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    NavigationKeepForReflect.setNavigationViewChildCount_V8198(launcher, index);
                } else {
                    Reflect.on(launcher).call("setNavigationViewChildCount", index);
                }
            }


        } catch (Throwable e) {
            Log.e("llbeing", "reflect invoke fail!");
        }
    }


    public static boolean isShowNavigationView(Context context) {
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(context) || CommonGlobal.isAndroidLauncher(context)) {
                try {
                    return NavigationKeepForReflect.isShowNavigationView_V8198();
                } catch (Throwable t) {
                    return Reflect.on("com.nd.hilauncherdev.launcher.navigation.NavigationView").call("isShowNavigationView").get();
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    return NavigationKeepForReflect.isShowNavigationView_V8198();
                } else {
                    return Reflect.on("com.nd.hilauncherdev.launcher.navigation.NavigationView").call("isShowNavigationView").get();
                }
            }


        } catch (Throwable t) {
            Log.e("llbeing", "reflect invoke fail!");
        }


        return false;
    }

    public static void setShowNavigationView(Context context,boolean isShow) {
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(context) || CommonGlobal.isAndroidLauncher(context)) {
                try {
                    NavigationKeepForReflect.setShowNavigationView_V8198(isShow);
                } catch (Throwable t) {
                    Reflect.on("com.nd.hilauncherdev.launcher.navigation.NavigationView").call("setShowNavigationView", isShow);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    NavigationKeepForReflect.setShowNavigationView_V8198(isShow);
                } else {
                    Reflect.on("com.nd.hilauncherdev.launcher.navigation.NavigationView").call("setShowNavigationView", isShow);
                }
            }


        } catch (Throwable t) {
            Log.e("llbeing", "reflect invoke fail!");
        }

    }

    public static String getBaseDirName(Activity activity) {
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(activity) || CommonGlobal.isAndroidLauncher(activity)) {
                try {
                    return NavigationKeepForReflect.getBaseDirName_V8198(activity);
                } catch (Throwable t) {
                    return Reflect.on("com.nd.hilauncherdev.launcher.navigation.NavigationView").call("getBaseDirName").get();
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    return NavigationKeepForReflect.getBaseDirName_V8198(activity);
                } else {
                    return Reflect.on("com.nd.hilauncherdev.launcher.navigation.NavigationView").call("getBaseDirName").get();
                }
            }


        } catch (Throwable t) {
            Log.e("llbeing", "reflect invoke fail!");
        }

        return "";
    }

    public static void handleUriEx(Context context,String url) {
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher() ||
                    CommonGlobal.isDianxinLauncher(context) || CommonGlobal.isAndroidLauncher(context)) {
                try {
                    NavigationKeepForReflect.handleUriEx_V8198(url);
                } catch (Throwable t) {
                    Reflect.on("com.nd.hilauncherdev.app.activity.BrowserPluginLoader").call("handleUriEx", url);
                }
            } else {
                if (Global.CURRENT_VERSION_CODE >= 8198) {
                    NavigationKeepForReflect.handleUriEx_V8198(url);
                } else {
                    Reflect.on("com.nd.hilauncherdev.app.activity.BrowserPluginLoader").call("handleUriEx", url);
                }
            }


        } catch (Throwable t) {
            Log.e("llbeing", "reflect invoke fail!");
        }

    }

    public static boolean isBackKeyToSohu(Activity activity) {
        try {
            if (LauncherBranchController.isNavigationForCustomLauncher()||
                    CommonGlobal.isDianxinLauncher(activity) || CommonGlobal.isAndroidLauncher(activity)) {
                return NavigationKeepForReflect.isBackKeyToSohu_V8198(activity);
            } else {
                return NavigationKeepForReflect.isBackKeyToSohu_V8198(activity);
            }

        } catch (Throwable t) {
            Log.e("llbeing", "reflect invoke fail!");
        }
        return false;
    }
}
