package com.nd.hilauncherdev.plugin.navigation.widget.search.hotword;

import android.content.Context;

import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviWordLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端热词产生类
 * Created by linliangbin on 2017/9/22 10:16.
 */

public class ServerHotwordGenerator implements HotwordInterface {


    public static final int MAX_COUNTER = 2;
    /**
     * 播放次数限制
     */
    public int isCounterLimit = 0;
    protected List<HotwordItemInfo> serverWords = new ArrayList<HotwordItemInfo>();
    /**
     * 当前热词Index
     */
    protected int serverWordIndex = 0;

    protected HotwordItemInfo currentWord = null;


    protected Context context;

    public ServerHotwordGenerator(Context context) {
        this.context = context;
    }

    /**
     * @desc 重新开启一轮播放
     * @author linliangbin
     * @time 2017/9/25 14:12
     */
    public void resetOneStepCounter() {

        if (isCounterLimit > MAX_COUNTER) {
            isCounterLimit = MAX_COUNTER;
        }
    }

    public void reset() {
        isCounterLimit = 0;
    }

    public void increaseCounter() {
        isCounterLimit++;
    }

    public boolean isCounterHit() {
        return isCounterLimit > MAX_COUNTER;
    }

    @Override
    public void appendHotwords(List<HotwordItemInfo> dataList) {
        if (serverWords.size() >= NaviWordLoader.ONE_BATCH_SIZE && dataList.size() <= NaviWordLoader.ONE_BATCH_SIZE) {
            serverWords = dataList;
            serverWordIndex = 0;
        } else {
            serverWords.addAll(dataList);
        }
    }

    @Override
    public HotwordItemInfo popupNextHotword() {
        currentWord = serverWords.get(serverWordIndex++ % serverWords.size());
        return currentWord;
    }

    @Override
    public boolean isHotwordsAvailable() {
        return serverWords != null && serverWords.size() > 2;
    }

    public boolean isServerHotwordAvailable(){
        return isHotwordsAvailable();
    }
    @Override
    public HotwordItemInfo getCurrentHotword() {
        return currentWord;
    }

}
