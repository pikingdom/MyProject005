package com.nd.hilauncherdev.plugin.navigation.widget.search.hotword;

import android.content.Context;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;
import com.nd.hilauncherdev.plugin.navigation.util.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 多类型热词生成工具类
 * <p>
 * Created by linliangbin on 2017/9/21 11:19.
 */

public class ComplexHotwordGenerator extends ServerHotwordGenerator {


    public static final int[] SAY_HI_WORDS = {
            R.string.hello_word_morning,
            R.string.hello_word_afternoon,
            R.string.hello_word_evening,
            R.string.hello_word_night,
            R.string.hello_word_midnight
    };
    public static final int[] SEARCH_GUIDE_WORDS = {
            R.string.search_guide_word_1,
            R.string.search_guide_word_2,
            R.string.search_guide_word_3,
            R.string.search_guide_word_4,
            R.string.search_guide_word_5,
            R.string.search_guide_word_6,

    };
    public static final int LOOP_COUNT = 4;
    /**
     * 当前展示问候语的Index
     */
    private int sayHiWordIndex = -1;
    /**
     * 当前搜索引导热词Index
     */
    private int searchGuideWordIndex = 0;
    /**
     * 热词计数器
     * 用于区分当前显示搜索引导热词 还是 服务端热词
     */
    private int totalWordCounter = 0;
    public ComplexHotwordGenerator(Context context) {
        super(context);
    }

    @Override
    public void reset() {
        super.reset();
        totalWordCounter = 0;
    }

    @Override
    public boolean isServerHotwordAvailable() {
        return serverWords != null && serverWords.size() > 0;
    }

    /**
     * @desc 获取欢迎热词
     * @author linliangbin
     * @time 2017/9/21 14:20
     */

    private String getSayHelloWord() {

        int hiWordIndex = getNowSayHelleWordIndex();
        if (hiWordIndex >= 0 && hiWordIndex < SAY_HI_WORDS.length) {
            return context.getResources().getString(SAY_HI_WORDS[hiWordIndex]);
        }

        return null;
    }


    /**
     * @desc 当前小时数 => 问候语Index
     * @author linliangbin
     * @time 2017/9/21 14:56
     */
    private int getNowSayHelleWordIndex() {

        int hour = Integer.parseInt(DateUtil.getHour());
        if (hour == -1) return -1;

        int hiWordIndex = -1;
        if (hour >= 5 && hour < 11) {
            hiWordIndex = 0;
        } else if (hour >= 11 && hour < 15) {
            hiWordIndex = 1;
        } else if (hour >= 15 && hour < 20) {
            hiWordIndex = 2;
        } else if (hour >= 20 && hour < 0) {
            hiWordIndex = 3;
        } else if (hour >= 0 && hour < 5) {
            hiWordIndex = 4;
        }
        return hiWordIndex;
    }


    private String getSearchGuideWord() {
        if (searchGuideWordIndex < 0 || searchGuideWordIndex >= SEARCH_GUIDE_WORDS.length) {
            searchGuideWordIndex = 0;
        }
        return context.getResources().getString(SEARCH_GUIDE_WORDS[searchGuideWordIndex++]);
    }


    private HotwordItemInfo assembleSayHiWord() {

        HotwordItemInfo word = new HotwordItemInfo();
        word.name = getSayHelloWord();
        word.type = HotwordItemInfo.TYPE_LOCAL_WORD;

        return word;
    }

    private HotwordItemInfo assembleSearchGuideWord() {

        HotwordItemInfo word = new HotwordItemInfo();
        word.name = getSearchGuideWord();
        word.type = HotwordItemInfo.TYPE_LOCAL_WORD;
        return word;

    }

    private HotwordItemInfo assembleServerWord() {

        if (EmptyUtils.isEmpty(serverWords)) {
            return null;
        }
        if (serverWordIndex < 0 || serverWordIndex >= serverWords.size()) {
            serverWordIndex = 0;
        }
        if (!EmptyUtils.isEmpty(serverWords)) {
            return serverWords.get(serverWordIndex++);
        }
        return null;
    }

    @Override
    public boolean isHotwordsAvailable() {
        return true;
    }


    @Override
    public void appendHotwords(List<HotwordItemInfo> list) {

        if (this.serverWords == null) {
            serverWords = new ArrayList<HotwordItemInfo>();
        }
        if (!EmptyUtils.isEmpty(list)) {
            serverWords.addAll(list);
        }
    }


    /**
     * @desc 获取下一个热词
     * 问好词变化时展示问好词
     * 搜索引导词：热词 按照 1:3 比例轮流出现
     * @author linliangbin
     * @time 2017/9/21 14:42
     */
    @Override
    public HotwordItemInfo popupNextHotword() {

        if (isCounterLimit > MAX_COUNTER) {
            return null;
        }
        int nowHiIndex = getNowSayHelleWordIndex();
        if (nowHiIndex != sayHiWordIndex) {
            sayHiWordIndex = nowHiIndex;
            currentWord = assembleSayHiWord();
            return currentWord;
        }

        if (totalWordCounter % LOOP_COUNT == 0) {
            totalWordCounter++;
            currentWord = assembleSearchGuideWord();
            return currentWord;
        }
        totalWordCounter++;
        currentWord = assembleServerWord();
        return currentWord;

    }

}
