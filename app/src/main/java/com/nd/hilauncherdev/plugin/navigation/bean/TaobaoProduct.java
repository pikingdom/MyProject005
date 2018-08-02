package com.nd.hilauncherdev.plugin.navigation.bean;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * description: 淘宝单品实体<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/3/22<br/>
 */
public class TaobaoProduct implements Serializable {
    public static final int TAO_BAO_RES_TYPE_SINGLE = 1; // 淘宝单品
    public static final int TAO_BAO_RES_TYPE_ACTIVITY = 2; // 活动
    public static final int TAO_BAO_RES_TYPE_COLLECTION = 3; // 淘宝专辑

    public static final String ID = "promoter";
    public static final String TITLE = "title";
    public static final String CATEGORY = "category";
    public static final String PRICE = "price";
    public static final String PIC = "picurl";
    public static final String URL = "clickurl";
    public static final String PROMOPRICE = "promoprice";
    public static final String ON_SELF_TIME = "onShelfTime";
    public static final String DISCOUNT_DES = "discountDes";
    public static final String ACTIVITY_TYPE = "activityType";
    public static final String ACTIVITY_DESC = "activityDesc";
    public static final String PRODUCT_COUNT = "productCount";
    public static final String SOURCE_ID = "sourceId";
    public static final String RES_ID = "resId";
    public static final String RES_TYPE = "resType";

    public String promoter;// 物料id，如果是人工编辑的资源，该字段空
    public int category;// 广告来源
    public String urlClick;// 广告点击跳转链接
    public String imageUrl;// 广告图片地址
    public String title;// 广告文案
    public double price;// 商品单价
    public double promoprice;// 折扣价

    public String onShelfTime; //上架时间，单位ms
    public String discountDes; //折扣描述
    public int activityType; //活动类型，1限时特惠，2最热好货，默认0
    public String activityDesc; //活动描述，已购人数、正在疯抢等
    public int productCount; //专辑中商品总数

    public int sourceId; //内部sourceId，CV 统计使用
    public int resId; //内部资源Id，CV 统计使用
    public int resType; //资源类型，单品/专辑

    public static final int ITEM_LEFT = 0;
    public static final int ITEM_RIGHT = 1;
    /**
     * item为左、右两边两个视图，在这里打个标记位做区分
     */
    public int view_type;

    /**
     * 获取单品的最终显示价格
     */
    public String getProductsShowPrice() {
        DecimalFormat df = new DecimalFormat("#.##");
        if (promoprice > 0 && promoprice != Double.NaN) {
            return "￥" + df.format(promoprice);
        } else {
            return "￥" + df.format(price);
        }
    }


    /**
     * 获取单品的单价
     */
    public String getProductsPrice() {
        DecimalFormat df = new DecimalFormat("#.##");
        return "￥" + df.format(price);
    }

    public String getProductCount() {
        return productCount > 1 ? String.format(Locale.getDefault(), "共%d款", productCount) : null;
    }

    public boolean isToday() {
        if (TextUtils.isEmpty(onShelfTime)) {
            return false;
        }

        // TODO: 2017/9/18 lxb check today label
        Date today = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String nowDate = f.format(today);
        String timeDate = onShelfTime.split(" ")[0];
        return nowDate.equals(timeDate);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof TaobaoProduct)) {
            return false;
        }

        TaobaoProduct other = (TaobaoProduct) o;
        if (TextUtils.isEmpty(other.promoter) && TextUtils.isEmpty(promoter)) {
            return (resId > 0 && other.resType == resType && other.resId == resId);
        } else if (!TextUtils.isEmpty(promoter)) {
            return promoter.equals(other.promoter);
        }

        return false;
    }

    /**
     * 解析Json列表
     */
    public static List<TaobaoProduct> beansFromJson(Context context, JSONArray jsonArray) {
        List<TaobaoProduct> resultItems = new LinkedList<>();
        if (jsonArray == null) return resultItems;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                resultItems.add(getTaobaoProduct(context, jsonArray.getJSONObject(i)));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return resultItems;
    }

    private static TaobaoProduct getTaobaoProduct(Context context, JSONObject jsonObject) throws JSONException {
        TaobaoProduct item = new TaobaoProduct();
        int resDataNum = 0; // 已购人数，(如果该项是专辑，则表示专辑中商品总数)
        @SuppressWarnings("unchecked")
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (jsonObject.isNull(key)) continue;

            switch (key) {
                case "promoter": {
                    item.promoter = jsonObject.optString(key);
                    break;
                }
                case "category": {
                    item.category = jsonObject.optInt(key);
                    break;
                }
                case "url_click": {
                    item.urlClick = jsonObject.optString(key);
                    break;
                }
                case "img": {
                    String imageUrl = jsonObject.optString(key);
                    // 替换最后面的_200x200为_400x400取大图片
                    imageUrl = computeImageSize(context, imageUrl);
                    item.imageUrl = imageUrl;
                    break;
                }
                case "title": {
                    item.title = jsonObject.optString(key);
                    break;
                }
                case "price": {
                    item.price = jsonObject.optDouble(key);
                    break;
                }
                case "promoprice": {
                    item.promoprice = jsonObject.optDouble(key);
                    break;
                }
                case "on_shelf_time": {
                    item.onShelfTime = jsonObject.optString(key);
                    break;
                }
                case "discount_des": {
                    item.discountDes = jsonObject.optString(key);
                    break;
                }
                case "activity_type": {
                    item.activityType = jsonObject.optInt(key);
                    break;
                }
                case "source_id": {
                    item.sourceId = jsonObject.optInt(key);
                    break;
                }
                case "res_id": {
                    item.resId = jsonObject.optInt(key);
                    break;
                }
                case "res_type": {
                    item.resType = jsonObject.optInt(key);
                    break;
                }
                case "res_data_num": {
                    resDataNum = jsonObject.optInt(key);
                    break;
                }
                case "activity_desc": {
                    // 服务端未实现 activity_desc，此处备用
                    item.activityDesc = jsonObject.optString(key);
                    break;
                }
            }
        }

        if (item.resType == TAO_BAO_RES_TYPE_COLLECTION) {
            item.productCount = resDataNum;
        }

        if (TextUtils.isEmpty(item.activityDesc)) {
            if (resDataNum > 0 && item.resType != TAO_BAO_RES_TYPE_COLLECTION) {
                item.activityDesc = "" + resDataNum + "人已购";
            } else {
                item.activityDesc = "正在疯抢";
            }
        }

        return item;
    }

    public static String items2Json(List<TaobaoProduct> list) {
        if (list == null) return null;

        JSONArray jsonArray = new JSONArray();
        try {
            for (TaobaoProduct p : list) {
                JSONObject obj = new JSONObject();
                obj.put("promoter", p.promoter);
                obj.put("category", p.category);
                obj.put("url_click", p.urlClick);
                obj.put("img", p.imageUrl);
                obj.put("title", p.title);
                obj.put("price", p.price);
                obj.put("promoprice", p.promoprice);
                obj.put("on_shelf_time", p.onShelfTime);
                obj.put("discount_des", p.discountDes);
                obj.put("activity_type", p.activityType);
                obj.put("source_id", p.sourceId);
                obj.put("res_id", p.resId);
                obj.put("res_type", p.resType);
                obj.put("activity_desc", p.activityDesc);
                jsonArray.put(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = jsonArray.toString();
        Log.d(TAG, "items2Json: " + result);

        return jsonArray.toString();
    }

    public static List<TaobaoProduct> json2Items(Context context, String jsonStr) {
        if (jsonStr == null) return null;

        List<TaobaoProduct> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i=0; i < jsonArray.length(); i++) {
                list.add(getTaobaoProduct(context, jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static final String TAG = "TaobaoProduct";

    /**
     * 计算淘宝商品图片尺寸大小
     */
    public static String computeImageSize(Context context, String source) {
        if (TextUtils.isEmpty(source)) {
            return source;
        }
        int len;
        float density = ScreenUtil.getDensity(context);
        //基数80,详细推荐尺寸请参见文档
        if (density <= 1.0) {
            len = 90;
        } else if (density <= 1.5) {
            len = 145;
        } else if (density <= 2.0) {
            len = 180;
        } else if (density <= 2.5) {
            len = 210;
        } else if (density <= 3.0) {
            len = 240;
        } else if (density <= 3.5) {
            len = 284;
        } else if (density <= 4.0) {
            len = 320;
        } else {
            len = 400;
        }
        String res = source;
        try {
            res = source.replaceFirst("_\\d+[x]\\d+", "_" + len + "x" + len);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
