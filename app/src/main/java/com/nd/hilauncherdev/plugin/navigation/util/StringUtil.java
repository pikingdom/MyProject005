package com.nd.hilauncherdev.plugin.navigation.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linliangbin_dian91 on 2015/10/27.
 */
public class StringUtil {


    public static String mergeString(String ori,String add,boolean addFromFront){

        String mergeStringWithSame = add + ori;
        String mergeStringWithoutSame = "";
        String mergerString_split[] = mergeStringWithSame.split(",");
        HashMap<String,String> map = new HashMap<String,String>();
        ArrayList<String> result = new ArrayList<String>();

        for(int i = 0 ;i < mergerString_split.length; i++){
            String current = mergerString_split[i];
            if(TextUtils.isEmpty(current))
                continue;
            if(!map.containsKey(current)){
                map.put(current,current);
                result.add(current);
            }
        }
        for(int i =0;i < result.size();i++)
            mergeStringWithoutSame += result.get(i) + ",";


        if(!TextUtils.isEmpty(mergeStringWithoutSame) && mergeStringWithoutSame.length() > 0)
            mergeStringWithoutSame = mergeStringWithoutSame.substring(0,mergeStringWithoutSame.length()-1);
        return mergeStringWithoutSame;
    }


    public static boolean isNetEmpty(String s) {
        return s == null || s.length() <= 0 || "null".equalsIgnoreCase(s);
    }
}
