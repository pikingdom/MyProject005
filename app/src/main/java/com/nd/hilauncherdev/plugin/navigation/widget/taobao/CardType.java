package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.nd.hilauncherdev.plugin.navigation.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 淘宝卡片类型<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/18<br/>
 */
public enum CardType {

    /**
     * 1：头条
     * 2：品牌特卖
     * 3：限时抢购
     * 4：搜索
     * 5：分类
     * 6: banner顶部循环广告
     * 7: 底部的单张广告
     *
     * @since 2016/06/21
     */
    MULTIBANNER(6, true),SEARCH(4, false), CATE(5, true), HEADLINE(1, true), CPS(2, true), SPLASHSALE(3, false),SINGLEBANNER(7, true);

    private final static String TAG = "CardType";

    private static SparseArray<Integer> series = new SparseArray<Integer>();

    private static boolean isSeriesChanged = true;
    private static List<Integer> preSeries = new ArrayList<>();

    static {
        series.put(0, 6);
        series.put(1, 4);
        series.put(2, 5);
        series.put(3, 1);
        series.put(4, 2);
        series.put(5, 3);
        series.put(6, 7);
    }

    /**
     * 是否可显示(默认不可见)
     */
    private boolean isVisible = false;

    /**
     * 位置,从0开始
     */
    private int index;

    /**
     * 更多的跳转地址
     */
    private String moreLink;

    /**
     * 标示
     */
    private final int flag;

    private CardType(int flag, boolean isVisible) {
        this.flag = flag;
        this.isVisible = isVisible;
    }

    public void setVisible(boolean visible) {
        if (!(visible && this.isVisible)) isSeriesChanged = true;
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if (this.index != index) isSeriesChanged = true;
        series.put(index, this.flag);
        this.index = index;
    }

    public void setMoreLink(String moreUrl) {
        this.moreLink = moreUrl;
    }

    public String getMoreLink() {
        return this.moreLink;
    }

    /**
     * 强制重置改变量为ture
     */
    public static void forceSeriesChanged() {
        isSeriesChanged = true;
    }

    /**
     * 重置全局改变量，请在确认已判断完成的情况下调用
     */
    public static void restoreSeriesChanged() {
        isSeriesChanged = false;
    }

    /**
     * 当前卡片序列是否发生改变
     *
     * @return
     */
    public static boolean isSeriesChanged(Context context) {
        List<Integer> series = CardType.getSeries(context);
        isSeriesChanged = false;
        if (preSeries.size() == series.size()){
            for (Integer ii = 0;ii<preSeries.size();ii++) {
                Integer preSery = preSeries.get(ii);
                if (ii<series.size()){
                    Integer sery= series.get(ii);
                    CardType type = CardType.getCardTypeByFlag(sery);
                    CardType preType = CardType.getCardTypeByFlag(preSery);
                    if (preSery != sery || type.isVisible != preType.isVisible){
                        isSeriesChanged = true;
                    }
                }
            }
        }else{
            isSeriesChanged = true;
        }
        preSeries.clear();
        preSeries.addAll(series);
        return isSeriesChanged;
    }

    public static List<Integer> getSeries(Context context) {
        SharedPreferencesUtil util = new SharedPreferencesUtil(context);
        String json = util.getString(SharedPreferencesUtil.TAOBAO_CARDSERIES);
        List<Integer> s = new ArrayList<Integer>();
        try {
            if (!TextUtils.isEmpty(json)) {
                Log.d(TAG, "Card type series is not null");
                JSONArray jsonArray = new JSONObject(json).optJSONArray("List");
                if (jsonArray != null) {
                    for (int i = 0, len = jsonArray.length(); i < len; i++) {
                        int f = jsonToCardSetting(jsonArray.optJSONObject(i), i);
                        if (!s.contains(f)) {
                            s.add(f);
                        }
                    }
                }
            } else {
                for (int i = 0, len = series.size(); i < len; i++) {
                    Integer f;
                    if ((f = series.get(i)) != null && !s.contains(f)) {
                        s.add(f);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Card type get series error : " + e.getMessage());
        }
        return s;
    }

    public static CardType getCardTypeByFlag(int flag) {
        switch (flag) {
            case 1:
                return HEADLINE;
            case 2:
                return CPS;
            case 3:
                return SPLASHSALE;
            case 4:
                return SEARCH;
            case 5:
                return CATE;
            case 6:
                return MULTIBANNER;
            case 7:
                return SINGLEBANNER;
            default:
                return null;
        }
    }

    private static int jsonToCardSetting(JSONObject json, int index) {
        if (json == null) return -1;

        int blockType = json.optInt("BlockType");
        CardType cardType = CardType.getCardTypeByFlag(blockType);
        if (cardType != null) {
            cardType.setIndex(index);
            try {
                int hidden = json.getInt("Hidden");
                if (hidden != 0) cardType.setVisible(false);
                else cardType.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                //默认为不可见
                cardType.setVisible(false);
            }
            cardType.setMoreLink(json.optString("MoreLink"));
        }
        return blockType;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("card type : ");
        sb.append("flag : " + flag + ",");
        sb.append("index : " + index + ",");
        sb.append("visible : " + isVisible);
        return sb.toString();
    }
}
