package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.bean.GameBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linliangbin_dian91 on 2015/11/10.
 */
public class GameH5HistoryLoader {

    private static final String GAME_CARD_H5_OPEN_HISTORY = "game_card_h5_open_history";

    /**
     * 添加一个当前打开的H5游戏
     * @param context
     * @param game
     */
    public static void addOpenH5Game(Context context, GameBean game) {
        String currentHistory = getOpenH5GameString(context);
        String mergedHistory = "";
        String currentGameJo = "";
        ArrayList<GameBean> currentHisotyrGame = new ArrayList<>();
        currentHisotyrGame = getOpenH5GameBean(context);
        game.openTime = System.currentTimeMillis();
        currentHisotyrGame.add(game);

        //去重
        HashMap<String,GameBean> map = new HashMap<String,GameBean>();
        for(int i=0;i < currentHisotyrGame.size();i++){
            GameBean gameBean = currentHisotyrGame.get(i);
            if(!map.containsKey(gameBean.gameName)){
                map.put(gameBean.gameName,gameBean);
            }else{
                GameBean alreadyIn = map.get(gameBean.gameName);
                if(gameBean.openTime > alreadyIn.openTime){
                    map.remove(gameBean.gameName);
                    map.put(gameBean.gameName,gameBean);
                }
            }
        }

        currentHisotyrGame.clear();
        for(Map.Entry<String,GameBean> entry : map.entrySet()){
            currentHisotyrGame.add(entry.getValue());
        }
        //排序
        Collections.sort(currentHisotyrGame, new Comparator<GameBean>() {
            @Override
            public int compare(GameBean lhs, GameBean rhs) {
                return (int) (rhs.openTime - lhs.openTime);
            }
        });

        //转换
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<currentHisotyrGame.size();i++){
            GameBean tempGame = currentHisotyrGame.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Id",tempGame.ID);
                jsonObject.put("GameName",tempGame.gameName);
                jsonObject.put("GameUrl",tempGame.downloadUrl);
                jsonObject.put("IconUrl",tempGame.iconUrl);
                jsonObject.put("openTime",tempGame.openTime);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(GAME_CARD_H5_OPEN_HISTORY, jsonArray.toString()).commit();
    }


    public static String getOpenH5GameString(Context context) {
        String historyGameString = "";
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        historyGameString =  sp.getString(GAME_CARD_H5_OPEN_HISTORY, "");
        return  historyGameString;
    }


    /**
     * 获取当前SP 中保存的打开游戏历史
     * @param context
     * @return
     */
    public static ArrayList<GameBean> getOpenH5GameBean(Context context) {
        String historyGameString = "";
        ArrayList<GameBean> currentHisotyrGame = new ArrayList<>();
        historyGameString =  getOpenH5GameString(context);
        try {
            JSONArray ja = new JSONArray(historyGameString);
            for(int i = 0;i<ja.length();i++){
                JSONObject JO = ja.getJSONObject(i);
                GameBean gameBean = new GameBean(JO,GameBean.H5_TYPE);
                currentHisotyrGame.add(gameBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  currentHisotyrGame;
    }
}
