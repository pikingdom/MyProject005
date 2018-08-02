package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.bean.UCClientEvent;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherLibUtil;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.Base64;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by linliangbin_dian91 on 2016/4/20.
 */
public class ZeroNewsReporter {


    public static final String APP_KEY = "C1lCyYdK0sjIwMfZ";
    public static final String AES_KEY = "l7b65iE9dvAdozLI";
    public static final String AES_IV = "OeUxnjF116gH1O2d";
    public static final String HMAC_KEY = "vBQF5U0I8J52Qb9hoGE0EsEPnT3yM65e";


    private static final Base64 base64;

    static {
        base64 = new Base64();
    }

    private final static String coding = "utf-8";


    /**
     * 加密字符串 in AES-128 with a given key
     *
     * @param context
     * @param password
     * @param text
     * @return String Base64 and AES encoded String
     */
    public static String encode(String stringToEncode, byte[] secretKey, byte[] iv) {
        if (stringToEncode.length() == 0 || stringToEncode == null) {
            throw new NullPointerException("Please give text");
        }
        try {
            // SecretKeySpec skeySpec = getKey(secretKey);
            SecretKeySpec skeySpec = new SecretKeySpec(secretKey, "AES");
            byte[] clearText = stringToEncode.getBytes("UTF8");
            // IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            // Cipher is not thread safe
//                Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

            byte[] encodeByte = cipher.doFinal(clearText);
            return base64.encode(encodeByte);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * HmacSHA256消息摘要
     *
     * @param data 待做摘要处理的数据
     * @return String 消息摘要
     * @throws UnsupportedEncodingException
     */
    public static String encodeHmacSHA256(byte[] key, String data) {
        // 加入BouncyCastleProvider的支持
//        Security.addProvider(new BouncyCastleProvider());
        // 还原密钥，因为密钥是以byte形式为消息传递算法所拥有
        SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
        // 实例化Mac
        Mac mac = null;
        try {
            mac = Mac.getInstance(secretKey.getAlgorithm());
            // 初始化Mac
            mac.init(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(base64.encode(mac.doFinal(data.getBytes())));

    }



    /**
     * 根据网易新闻需求，统计数据
     * @param action
     * @param info
     */
    public static void reportNewsRead(final Context context, final String url, final int action, final String info){
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(url) || TextUtils.isEmpty(info))
                    return;
                String docId = parseDocIdFromUrl(url);
                if(TextUtils.isEmpty(docId))
                    return;
                JSONObject paramsJO = new JSONObject();

                try {
                    String timeStampString = System.currentTimeMillis() + "";
                    /**
                     * 请求序号，百度91自定义
                     采用UUID
                     */
                    paramsJO.put("reqId", timeStampString.hashCode());

                    /**
                     * appkey	String	M	百度91账号，网易分配
                     */
                    paramsJO.put("appkey", APP_KEY);

                    /**
                     * appSecuret	String(256)	M	参数签名=hmac-sha256(key, ts)
                     Key为网易服务器给用户分配的秘钥；
                     */

                    paramsJO.put("appSecret", encodeHmacSHA256(HMAC_KEY.getBytes(),timeStampString));

                    /**
                     * 百度91用户标示，未登录为空
                     为空或没有携带，则返回默认的新闻内容
                     */
                    paramsJO.put("userId", URLEncoder.encode(LauncherLibUtil.getCUID(context), "UTF-8"));

                    /**
                     * 用户设备，IMEI；
                     默认加密，算法为AES128-CBC，密钥为网易分配的appKey对应的密钥；
                     为空或没有携带，则返回默认的新闻内容；
                     userId和deviceId务必

                     */
                    paramsJO.put("deviceId", encode(TelephoneUtil.getIMEI(context), AES_KEY.getBytes(), AES_IV.getBytes()));

                    /**
                     * 时间戳
                     */
                    paramsJO.put("ts", timeStampString);

                    /**
                     *
                     */
                    JSONArray jsonArray = new JSONArray();
                    JSONObject aciontObject = new JSONObject();
                    /**
                     * Link=http://3g.163.com/touch/article.html?docid=BL0U47JF900147JG&qd=91zm_page
                     * docId 从链接中获取
                     */

                    aciontObject.put("docId", docId);
                    aciontObject.put("action", action);
                    aciontObject.put("info", info);
                    jsonArray.put(aciontObject);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("actionList",jsonArray);
                    paramsJO.put("data", jsonObject);


                    String jsonParams = paramsJO.toString();
                    HashMap<String, String> paramsMap = new HashMap<String, String>();
                    LauncherHttpCommon httpCommon = new LauncherHttpCommon("http://cont.3g.163.com/recsdk/feedback91Action");
                    ServerResultHeader csResult = httpCommon.getResponseAsCsResultPostJson(paramsMap, jsonParams);
                    if (csResult != null) {
                        try {
                            String str = csResult.getResponseJson();
                            JSONObject jo = new JSONObject(str);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

    }



    /**
     * 从给定的地址中解析docId
     * @param url
     * @return
     */
    public static String parseDocIdFromUrl(String url){

        try {
            if(TextUtils.isEmpty(url))
                return "";

            StringBuilder mHeaderBuilder;

            int pos = url.indexOf("?");
            if (pos == -1) {
                mHeaderBuilder = new StringBuilder(url);
                return "";
            }

            mHeaderBuilder = new StringBuilder(url.substring(0, pos));
            String temp = url.substring(pos + 1);
            StringTokenizer token = new StringTokenizer(temp, "&", false);
            while (token.hasMoreElements()) {
                String[] str = token.nextToken().split("=");
                if (str != null && str.length == 2 && "docid".equalsIgnoreCase(str[0])) {
                    return str[1];
                }
            }

            return "";
        }catch (Exception e){
            e.printStackTrace();
        }
        return  "";
    }

    /**
     * 上报UC客户端事件
     *
     * @param context
     * @param logs
     * @return
     */
    public static void reportUCClientEvent(final Context context, final UCClientEvent... logs) {
        if (logs == null || logs.length < 1) {
            return;
        }
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                JSONObject paramsJO = new JSONObject();

                try {
                    JSONArray logArray = new JSONArray();
                    for (UCClientEvent event : logs) {
                        logArray.put(event.toJsonObject());
                    }

                    paramsJO.put("logs", logArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String jsonParams = paramsJO.toString();
                HashMap<String, String> paramsMap = new HashMap<String, String>();
                LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
                LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9057");
                ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            }
        });
    }

}
