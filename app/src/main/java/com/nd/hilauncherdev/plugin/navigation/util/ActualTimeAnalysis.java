package com.nd.hilauncherdev.plugin.navigation.util;

import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;

/**
 * 实时统计
 * <p>Title: ActualTimeAnalysis</p>
 * <p>Description: </p>
 * <p>Company: ND</p>
 * @author    MaoLinnan
 * @date       2015年5月14日
 */
public class ActualTimeAnalysis {
    /**V6.9开始32、33统计label意思请查看相关文档双十一统计需求*/
    //淘宝有效点击（1-淘宝插件物料点击,2-淘宝会场点击,3-壁纸广告点击,4-淘宝热词点击,5-淘宝搜索访问次数,6-主题详情点击淘宝广告次数）
    public static final int TAOBAO_VALID_HITS = 32;
    //腾讯广点通相关统计
    public static final int TENCENT_VALID_HITS = 33;


    private static final String ACTUALTIME_REQUEST_URL = URLs.PANDAHOME_BASE_URL + "action.ashx/distributeaction/5013";




    /**
     * 发送实时统计
     * <p>Title: sendActualTimeAnalysis</p>
     * <p>Description: </p>
     * @param functionId
     * @param label
     * @author maolinnan_350804
     */
    public static void sendActualTimeAnalysis(final Context ctx, final int functionId,final String label){
        ThreadUtil.executeMore(new Runnable() {

            @Override
            public void run() {
                String jsonParams;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("FunctionId", functionId);
                    jsonObject.put("Label", label);
                    jsonObject.put("Model", encodeAttrValue(Build.MODEL));
                    jsonObject.put("NetMode", getNetMode(ctx));
                    jsonParams = jsonObject.toString();
                    HashMap<String, String> paramsMap = new HashMap<String, String>();
                    LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx, jsonParams);
                    LauncherHttpCommon httpCommon = new LauncherHttpCommon(ACTUALTIME_REQUEST_URL);
                    httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    /**
     * 获取网络模式
     * <p>Title: getNetMode</p>
     * <p>Description: </p>
     * @param ctx
     * @return
     * @author maolinnan_350804
     */
    private static int getNetMode(Context ctx){
        switch (MyPhoneUtil.getTelephoneConcreteNetworkState(ctx)) {
            case 1:return 53;
            case 2:return 51;
            case 3:return 52;
            case 4:return 50;
            case 5:return 63;
            case 6:return 61;
            case 7:return 62;
            case 8:return 60;
            case 9:return 70;
            case 10:return 10;
            case 11:return 0;
            default:
                return 0;
        }
    }
    /**
     * 对参数编码
     * <p>Title: encodeAttrValue</p>
     * <p>Description: </p>
     * @param value
     * @return
     * @author maolinnan_350804
     */
    private static String encodeAttrValue(String value) {
        String returnValue = "";
        try {
            value = URLEncoder.encode(value + "", "UTF-8");
            returnValue = value.replaceAll("\\+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}
