package com.nd.hilauncherdev.plugin.navigation.util;


import java.io.File;
import java.util.Map;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * MyPhone������
 *
 * @author youy
 *
 */
public class MyPhoneUtil {

    /**
     * ��ȡ��ǰ�û��ֻ�ľ���״̬
     * ������no,wifi,2g,3g,4g,unknow
     * <p>Title: getTelephoneConcreteNetworkState</p>
     * <p>Description: </p>
     * @return
     * @author maolinnan_350804
     */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_CLASS_YD_2G = 1;
    public static final int NETWORK_CLASS_LT_2G = 2;
    public static final int NETWORK_CLASS_DX_2G = 3;
    public static final int NETWORK_CLASS_2G = 4;
    public static final int NETWORK_CLASS_YD_3G = 5;
    public static final int NETWORK_CLASS_LT_3G = 6;
    public static final int NETWORK_CLASS_DX_3G = 7;
    public static final int NETWORK_CLASS_3G = 8;
    public static final int NETWORK_CLASS_4G = 9;
    public static final int NETWORK_CLASS_WIFI = 10;
    public static final int NETWORK_CLASS_NO = 11;



    public static int getTelephoneConcreteNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null){
            return NETWORK_CLASS_UNKNOWN;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {// û����
            return NETWORK_CLASS_NO;
        }
        String type = networkInfo.getTypeName();
        if ("WIFI".equals(type) || "wifi".equals(type)) {
            return NETWORK_CLASS_WIFI;
        } else if ("MOBILE".equals(type) || "mobile".equals(type)) {
            return getNetworkClass(networkInfo.getType());
        } else {
            return NETWORK_CLASS_UNKNOWN;
        }
    }

    private static int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:// ��ͨ2g
                return NETWORK_CLASS_LT_2G;
            case TelephonyManager.NETWORK_TYPE_EDGE:// �ƶ�2g
                return NETWORK_CLASS_YD_2G;
            case TelephonyManager.NETWORK_TYPE_CDMA:// ����2g
                return NETWORK_CLASS_DX_2G;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:// ��ͨ3g
            case TelephonyManager.NETWORK_TYPE_HSDPA:// ��ͨ3g
                return NETWORK_CLASS_LT_3G;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:// ����3g
            case TelephonyManager.NETWORK_TYPE_EVDO_A:// ����3g
            case TelephonyManager.NETWORK_TYPE_EVDO_B:// ����3g
                return NETWORK_CLASS_DX_3G;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:

            case 14:// TelephonyManager.NETWORK_TYPE_EHRPD
            case 15:// TelephonyManager.NETWORK_TYPE_HSPAP
                return NETWORK_CLASS_3G;
            case 13:// TelephonyManager.NETWORK_TYPE_LTE	LTE��׼��4g
                return NETWORK_CLASS_4G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

}
