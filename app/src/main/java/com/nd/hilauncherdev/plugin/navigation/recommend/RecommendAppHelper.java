package com.nd.hilauncherdev.plugin.navigation.recommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.http.DownloadHelper;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviCardLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AppAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.DialogCommon;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;
import com.nd.hilauncherdev.plugin.navigation.widget.NewsCardViewHelper;

public class RecommendAppHelper {

    public static String APP_SAVE_NAME = "navi_app.apk";
    public static String appName ;

    public static void recommendApp(Context baseContext, Activity act, Context navi, String pkgName) {
        if (isAppExist(act, pkgName)) {
            Intent intent = act.getPackageManager().getLaunchIntentForPackage(pkgName);
            if(intent != null){
                act.startActivity(intent);
            }else{
                startDialog(act,act,navi,pkgName);
            }
        } else {
            startDialog(baseContext,act,navi,pkgName);
        }
    }

    public static void startDialog(Context baseContext, final Activity act, final Context navi, final String pkgName){
        final DialogCommon dlg;
        if(NewsCardViewHelper.RECOMMEND_APP_PKG_TENCENT_KB.equals(pkgName)){
            dlg = new DialogCommon(baseContext, navi.getText(R.string.recommend_dialog_dailynews_title_kuaibao),
                    navi.getText(R.string.recommend_dialog_dailynews_msg_kuaibao), navi.getText(R.string.common_dialog_confirm), navi.getText(R.string.common_button_cancel));

        }else{
            dlg = new DialogCommon(baseContext, navi.getText(R.string.recommend_dialog_dailynews_title_toutiao),
                    navi.getText(R.string.recommend_dialog_dailynews_msg_toutiao), navi.getText(R.string.common_dialog_confirm), navi.getText(R.string.common_button_cancel));
        }
        dlg.setClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = URLs.getDownloadUrlFromPackageName(act, pkgName, null, AppAnalysisConstant.SP_NAVIGATION_NEWS_APP_DOWNLOAD);
                String id = DownloadHelper.DOWNLOAD_KEY_PRE + pkgName;
                Intent intent = new Intent(act.getPackageName() + ".FORWARD_SERVICE");
                intent.putExtra("identification", id);
                intent.putExtra("fileType", 0);
                intent.putExtra("downloadUrl", url);
                appName = NewsCardViewHelper.RECOMMEND_APP_PKG_TENCENT_KB.equals(pkgName) ? "天天快报" : "今日头条";
                intent.putExtra("title", appName);
                intent.putExtra("savedDir", NaviCardLoader.NAV_DIR);
                APP_SAVE_NAME = pkgName + "navi_app.apk";
                intent.putExtra("savedName", APP_SAVE_NAME);
                intent.putExtra("sp", AppAnalysisConstant.SP_NAVIGATION_NEWS_APP_DOWNLOAD);
                intent.putExtra("disId",pkgName);
                navi.startService(intent);
                dlg.dismiss();
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        dlg.setCancelable(true);
        dlg.show();

    }


    public static boolean isAppExist(Context context, String pkgName) {
        if (pkgName == null || "".equals(pkgName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
