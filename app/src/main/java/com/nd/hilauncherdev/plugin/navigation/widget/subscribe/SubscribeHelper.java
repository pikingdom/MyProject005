package com.nd.hilauncherdev.plugin.navigation.widget.subscribe;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.loader.SubscribeLoader;

/**
 * Created by linliangbin on 16-7-15.
 */
public class SubscribeHelper {
    
    public static String SP_MY_ADDED_SUSCRIBE_SITE = "sp_my_add_subscribe_site";
    
    
    //TODO llbeng 改单例
    
    /**
     * 数据格式返回当前添加的订阅号
     * @param context
     * @return
     */
    public static int[] getAddedSubscribeSiteByArray(Context context){
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        String ids = sp.getString(SP_MY_ADDED_SUSCRIBE_SITE, "");
        
        int resultArr[] = new int[0];
        if(!TextUtils.isEmpty(ids)){
            String id[] = ids.split(",");
            resultArr = new int[id.length];
            for(int i =0 ;i<id.length;i++){
            
                if(TextUtils.isEmpty(id[i]))
                    continue;
            
                try {
                    int temp = Integer.parseInt(id[i]);
                    resultArr[i] = temp;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return resultArr;
        
    }
    
    /**
     * 返回当前添加的订阅号ID列表
     * 
     * @param context
     * @return
     */
    public static String getAddedSubscribeSite(Context context){
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME,Context.MODE_PRIVATE);
        String ids = sp.getString(SP_MY_ADDED_SUSCRIBE_SITE,"");
        String result = "";
        if(!TextUtils.isEmpty(ids)){
            String id[] = ids.split(",");
            for(int i =0 ;i<id.length;i++){
                
                if(TextUtils.isEmpty(id[i]))
                    continue;
                
                try {
                    int temp = Integer.parseInt(id[i]);
                    result += temp + ",";
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
    
            if(result.length() >= 2 && result.lastIndexOf(",") == result.length()-1){
                result = result.substring(0,result.length() -1 );
            }
        }

        return  result;
                
    }
    
    /**
     * 返回当前添加的订阅号数
     * @param context
     * @return
     */
    public static int getAddedSubscribeSiteCount(Context context){
    
        int addedSiteCount = 0;
        
        try {
            String siteString = getAddedSubscribeSite(context);
            String stringArr[] = siteString.split(",");
            
            for(int i=0;i<stringArr.length;i++){
                try {
                    Integer.parseInt(stringArr[i]);
                    addedSiteCount ++;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return addedSiteCount;
        
    }
    
    /**
     * 当前ID 是否为已经添加的状态
     * @param context
     * @param siteId
     * @return
     */
    public static boolean hasSubscribeThisSite(Context context, int siteId){
        
        int sites[] = getAddedSubscribeSiteByArray(context);
        if(sites == null || sites.length == 0)
            return false;
        for(int i=0;i<sites.length;i++){
            if(sites[i] == siteId)
                return true;
        }
        return false;
    }
    
    public static void addSubscribeSite(Context context, int siteId){
        //添加前的订阅号个数
        int beforeCount = getAddedSubscribeSiteCount(context);
        
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        int ids[] = getAddedSubscribeSiteByArray(context);
        String currentSite = "";
        if(ids != null && ids.length >0){
            for(int i =0;i<ids.length;i++){
                if(ids[i] != 0){
                    currentSite += ids[i] + ",";
                }
            }
        }
        
        currentSite += siteId;
        sp.edit().putString(SP_MY_ADDED_SUSCRIBE_SITE, currentSite).commit();
        
        //添加后的订阅号个数
        int afterCount = getAddedSubscribeSiteCount(context);
        
        //订阅号状态从无到有，需要通知刷新卡片界面
        if(beforeCount == 0 && afterCount > 0){
            CardManager.getInstance().setIsCardListChanged(context, true);
        }
        
        // 显示卡片时需要更新数据
        SubscribeLoader.setSubscribeCardSiteChange(context,true);
    }
    
    public static void removeSubscribeSite(Context context,int siteId){
        //添加前的订阅号个数
        int beforeCount = getAddedSubscribeSiteCount(context);
        
        String siteString = getAddedSubscribeSite(context);
        if(!TextUtils.isEmpty(siteString)){
            siteString = siteString.replace(siteId+",","");
            siteString = siteString.replace(siteId+"","");
            SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
            sp.edit().putString(SP_MY_ADDED_SUSCRIBE_SITE, siteString).commit();
        }
        //添加后的订阅号个数
        int afterCount = getAddedSubscribeSiteCount(context);
    
        //订阅号状态从有到无，需要通知刷新卡片界面
        if(beforeCount > 0 && afterCount == 0){
            CardManager.getInstance().setIsCardListChanged(context, true);
        }
        // 显示卡片时需要更新数据
        SubscribeLoader.setSubscribeCardSiteChange(context,true);
    }

    /**
     * 清楚已添加卡片数据
     * @param context
     */
    public static void clearAddedSubscribeSite(Context context){
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME,Context.MODE_PRIVATE);
        sp.edit().putString(SP_MY_ADDED_SUSCRIBE_SITE,"").commit();
    }
}
