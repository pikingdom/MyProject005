package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.bean.GameBean;
import com.nd.hilauncherdev.plugin.navigation.bean.Joke;
import com.nd.hilauncherdev.plugin.navigation.bean.PicBean;
import com.nd.hilauncherdev.plugin.navigation.bean.VPHAdBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.MyPhoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linliangbin_dian91 on 2015/10/20.
 */
public class VPHAdLoader extends CardDataLoader {

    private static final String NAVI_FILE = "navi_card_vph.txt";
    private static final String NAVI_CARD_PATH = NaviCardLoader.NAV_DIR + NAVI_FILE;
    private static final String JSON_LIST = "AdList";
    private ArrayList<VPHAdBean> allList = new ArrayList<VPHAdBean>();
    // 上一次显示的数据内容，当不需要刷新数据时使用
    private ArrayList<VPHAdBean> preVPHAdList = new ArrayList<VPHAdBean>();

    @Override
    public String getCardShareImagePath() {
        return NaviCardLoader.NAV_DIR + "navigation_vph_share.jpg";
    }

    @Override
    public String getCardShareImageUrl() {
        if(CommonLauncherControl.DX_PKG){
            return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/04/25/23b319e4de2b40228841423bfe7a6197.jpg";
        }
        if(CommonLauncherControl.AZ_PKG){
            return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/05/19/739a94898b2c42e7a0e178e01a0dfea9.png";
        }
        return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/03/11/a20f65b1a09d402cba8daed98cc0c493.jpg";
    }

    @Override
    public String getCardShareWord() {
        return "货比三家求正品？用@" + CommonLauncherControl.LAUNCHER_NAME + " 品牌特卖一次搞定！";
    }

    @Override
    public String getCardShareAnatics() {
        return "pptm";
    }

    @Override
    public String getCardDetailImageUrl() {
        return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/03/11/3784e77622cc4ffab131efed4f945e1f.png";
    }

    @Override
    public void loadDataS(Context context, Card card, boolean needRefreshData, int showSize) {
        if(!needRefreshData && preVPHAdList.size() >0){
            ArrayList<VPHAdBean> preVPHAdListTemp = new ArrayList<VPHAdBean>();
            for(VPHAdBean ad:preVPHAdList)
                preVPHAdListTemp.add(ad);
            refreshView(preVPHAdListTemp);
            return;
        }
        mContext = context;
        NaviCardLoader.createBaseDir();
        String localStr = "";
        try {
            allList.clear();
            localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
            if (TextUtils.isEmpty(localStr)) {
                FileUtil.copyPluginAssetsFile(context, NAVI_FILE, NaviCardLoader.NAV_DIR, NAVI_FILE);
                localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
                loadDataFromServer(context,card,true);
            }

            JSONObject jo = new JSONObject(localStr);
            JSONArray ja = new JSONArray(jo.getString(JSON_LIST));
            for (int i = 0; i < ja.length(); i++) {
                JSONObject dataJo = ja.getJSONObject(i);
                VPHAdBean picBean = new VPHAdBean(dataJo);
                allList.add(picBean);
            }
            if (allList.size() != 0)
                refreshView(allList);
            else {
                FileUtil.writeFile(NAVI_CARD_PATH, "", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.writeFile(NAVI_CARD_PATH, "", false);
        }
    }

    @Override
    public void loadDataFromServer(Context context, Card card, boolean needRefreshView) {
        try {
            ArrayList<VPHAdBean> objList = new ArrayList<VPHAdBean>();
            JSONObject paramsJO = new JSONObject();
            try {
                paramsJO.put("ScreenWidth", ScreenUtil.getCurrentScreenWidth(context));
                paramsJO.put("ScreenHeight", ScreenUtil.getCurrentScreenHeight(context));
                int network = MyPhoneUtil.getTelephoneConcreteNetworkState(context);
                if (network == MyPhoneUtil.NETWORK_CLASS_WIFI)
                    paramsJO.put("Network", "WIFI");
                else if (network >= MyPhoneUtil.NETWORK_CLASS_YD_2G && network <= MyPhoneUtil.NETWORK_CLASS_2G)
                    paramsJO.put("Network", "2G");
                else if (network >= MyPhoneUtil.NETWORK_CLASS_YD_3G && network <= MyPhoneUtil.NETWORK_CLASS_3G)
                    paramsJO.put("Network", "3G");
                else if (network == MyPhoneUtil.NETWORK_CLASS_4G)
                    paramsJO.put("Network", "4G");
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/distributeaction/5035");
            ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            if (csResult == null || !csResult.isRequestOK()) {
                return;
            }
    
            String resString = csResult.getResponseJson();
            JSONObject jo = null;
            JSONArray ja = null;
            try {
                jo = new JSONObject(resString);
                ja = new JSONArray(jo.getString("AdList"));
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject dataJo = ja.getJSONObject(i);
                    VPHAdBean gameBean = new VPHAdBean(dataJo);
                    objList.add(gameBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
    
            if (objList.size() == 0) {
                return;
            }
    
            FileUtil.writeFile(NAVI_CARD_PATH, jo.toString(), false);
            if (needRefreshView) {
                refreshView(objList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
    }

    @Override
    public <E> void refreshView(ArrayList<E> objList) {
        if (handler == null) {
            return;
        }

        ArrayList<VPHAdBean> beanList = (ArrayList<VPHAdBean>) objList;
        if(beanList != null && beanList.size() >0){
            preVPHAdList.clear();
            for(VPHAdBean ad : beanList)
                preVPHAdList.add(ad);
        }
        Message msg = new Message();
        msg.what = CardViewHelper.MSG_LOAD_VPH_AD_SUC;
        msg.obj = objList;
        handler.sendMessage(msg);
    }
}
