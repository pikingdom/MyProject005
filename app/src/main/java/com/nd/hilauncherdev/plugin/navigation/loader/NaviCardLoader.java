package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * 卡片基础数据加载<br/>
 *
 * @author chenzhihong_9101910
 */
public class NaviCardLoader {

    public static final String NAV_DIR = CommonGlobal.getCachesHome() + "navigation/";

    public static void createBaseDir() {
        FileUtil.createDir(NaviCardLoader.NAV_DIR);
    }

    public static void loadCardInfo(final Context context) {
        JSONObject paramsJO = new JSONObject();
        try {
            paramsJO.put("Type", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonParams = paramsJO.toString();
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon("http://pandahome.ifjing.com/action.ashx/otheraction/9006");
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                try {
                    JSONObject jo = new JSONObject(csResult.getResponseJson());
                    JSONArray resJA = jo.getJSONArray("ResultList");
                    String avaiableCardS = "";
                    ArrayList<Card> allCardS = CardManager.getInstance().getAllCardS(context);
                    ArrayList<Card> curCardS = CardManager.getInstance().getCurrentCardS(context);
                    HashMap<Integer, Card> cardIdCardM = new HashMap<>();

                    HashMap<String, String> currentCardMap = new HashMap<String, String>();
                    if (curCardS != null && curCardS.size() > 0) {
                        for (Card card : curCardS) {
                            currentCardMap.put(card.id + "", "");
                        }
                    }
                    HashSet<Integer> nowAvailable = CardManager.getInstance().getAaviableCardS(context);
                    HashMap<String, String> nowAvailableMap = new HashMap<String, String>();
                    if (nowAvailable != null) {
                        for (Integer integer : nowAvailable) {
                            nowAvailableMap.put(integer.intValue() + "", "");
                        }
                    }

                    for (int i = 0; i < resJA.length(); i++) {
                        JSONObject cardJo = resJA.getJSONObject(i);
                        Card card = new Card();
                        card.id = Integer.valueOf(cardJo.optString("CardId"));
                        card.desc = cardJo.optString("Description");
                        card.name = cardJo.optString("CardName");
                        card.smallImgUrl = cardJo.optString("SmallImageUrl");
                        card.bigImgUrl = cardJo.optString("BigImageUrl");
                        card.position = cardJo.optInt("Position");
                        card.type = CardManager.getCardTypeById(card.id);
                        avaiableCardS += card.id + ",";
                        cardIdCardM.put(card.id, card);
                        if (nowAvailableMap != null && nowAvailableMap.containsKey(card.id + "")) {
                            nowAvailableMap.remove(card.id + "");
                        }
                    }

                    if (nowAvailableMap != null && nowAvailableMap.size() > 0) {
                        try {
                            JSONArray jsonArray = new JSONArray();
                            for (Map.Entry<String, String> entry : nowAvailableMap.entrySet()) {
                                if (currentCardMap != null && currentCardMap.containsKey(entry.getKey())) {
                                    jsonArray.put(entry.getKey());
                                }
                            }
                            CardManager.getInstance().setDisableCardList(context, jsonArray.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //保存当前Avail 卡片
                    if (!TextUtils.isEmpty(avaiableCardS)) {
                        avaiableCardS = "[" + avaiableCardS.substring(0, avaiableCardS.length() - 1) + "]";
                        CardManager.getInstance().setAvaiableCardS(context, avaiableCardS);
                    }

                    //更新当前卡片SP的描述等信息
                    for (Card curCard : curCardS) {
                        if (cardIdCardM.containsKey(curCard.id)) {
                            Card card = cardIdCardM.get(curCard.id);
                            curCard.desc = card.desc;
                            curCard.name = card.name;
                            curCard.smallImgUrl = card.smallImgUrl;
                            curCard.bigImgUrl = card.bigImgUrl;
                            curCard.type = card.type;
                        }
                    }

                    CardManager.getInstance().saveCardSToOneSpField(context, CardManager.SP_CURRENT_CARDS, curCardS);
                    ArrayList<Card> mergeAllCardS = new ArrayList<>();
                    HashMap<Integer, Card> localAllCardS = new HashMap<>();
                    for (Card curCard : allCardS) {
                        localAllCardS.put(curCard.id, curCard);
                    }
                    //更新所有卡片的描述信息
                    for (Iterator<Integer> it = cardIdCardM.keySet().iterator(); it.hasNext(); ) {
                        Integer key = (Integer) it.next();
                        Card card = cardIdCardM.get(key);
                        if (localAllCardS.containsKey(key)) {
                            Card localCard = localAllCardS.get(key);
                            card.isDefaultOpen = localCard.isDefaultOpen;
                            card.canBeDeleted = localCard.canBeDeleted;
                            card.canMoved = localCard.canMoved;
                            card.canBeRepeatAdded = localCard.canBeRepeatAdded;
                            card.position = localCard.position;
                        } else {
                            card.isDefaultOpen = true;
                            card.canBeDeleted = true;
                            card.canMoved = true;
                            card.canBeRepeatAdded = true;
                        }

                        mergeAllCardS.add(card);
                    }

                    Collections.sort(mergeAllCardS, CardManager.comparator);
                    CardManager.getInstance().saveCardSToOneSpField(context, CardManager.SP_ALL_CARDS, mergeAllCardS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
