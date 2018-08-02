package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.bean.DailyFortune;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 每日运势数据加载类
 * Created by linliangbin on 2017/5/26 15:37.
 */

public class DailyFortuneLoader {


    public static final String CALENDAR_SP = "calendarWidgetSet";

    /**
     * 最近一次更新时间
     */
    public static final String CONFIG_KEY_LASTTIME = "fortune_last_update";
    /**
     * 运势数据
     */
    public static final String CONFIG_KEY_DATA = "fortune_data";
    /**
     * 用户生日（yyyy-MM-dd）
     */
    public static final String CONFIG_KEY_BIRTHDAY = "fortune_user_birthday";

    /**
     * 用户性别（默认 女）
     * SEX_MAN = 1;
     * SEX_LADY = 2;
     */
    public static final String CONFIG_KEY_SEX = "fortune_user_sex";


    public static final int SEX_MAN = 1;
    public static final int SEX_LADY = 2;
    private static DailyFortuneLoader instance;
    SharedPreferences sp;
    private Context context;


    public DailyFortuneLoader(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(CALENDAR_SP, Context.MODE_PRIVATE | 4);
    }

    public static synchronized DailyFortuneLoader getInstance(Context context) {
        if (instance == null) {
            instance = new DailyFortuneLoader(context);
        }
        return instance;
    }

    public long getFortuneLastUpdate() {
        long lastupdate = 0;
        if (sp != null) {
            lastupdate = sp.getLong(CONFIG_KEY_LASTTIME, 0);
        }
        return lastupdate;
    }

    public void setFortuneLastUpdate(long lastUpdate) {

        if (sp != null) {
            sp.edit().putLong(CONFIG_KEY_LASTTIME, lastUpdate).commit();
        }
    }

    public String getFortuneData() {

        String data = null;
        if (sp != null) {
            data = sp.getString(CONFIG_KEY_DATA, null);
        }
        return data;
    }

    public void setFortuneData(String data) {

        if (sp != null) {
            sp.edit().putString(CONFIG_KEY_DATA, data).commit();
        }
    }

    public String getUserBirthday() {

        String birthday = null;
        if (sp != null) {
            //TODO llbeing 测试代码 需要返回空
            birthday = sp.getString(CONFIG_KEY_BIRTHDAY, "1990-09-10");
        }
        return birthday;
    }

    public void setUserBirthday(String birthday) {

        if (sp != null) {
            sp.edit().putString(CONFIG_KEY_BIRTHDAY, birthday).commit();
        }
    }

    public int getUserSex() {
        int sex = SEX_LADY;
        if (sp != null) {
            sex = sp.getInt(CONFIG_KEY_SEX, sex);
            if (sex != SEX_LADY && sex != SEX_MAN) {
                sex = SEX_LADY;
            }
        }
        return sex;
    }

    public void setUserSex(int sex) {

        if (sp != null) {
            sp.edit().putInt(CONFIG_KEY_SEX, sex).commit();
        }
    }


    public DailyFortune getCurrentDailyFortune() {

        try {
            return DailyFortune.jsonToDailyFortune(new JSONObject(getFortuneData()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadDailyFortune() {

        String jsonParams = "";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Birthday", getUserBirthday());
            jsonObject.put("Sex", getUserSex());
            jsonParams = jsonObject.toString();
        } catch (Exception var15) {
            var15.printStackTrace();
        }

        HashMap paramsMap = new HashMap();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, context.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9053");
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        String response = csResult.getResponseJson();
        if (csResult == null || !csResult.isRequestOK() || TextUtils.isEmpty(response)) {
            return;
        }
        setFortuneData(response);

    }

}
