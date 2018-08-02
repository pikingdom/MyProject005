package com.nd.hilauncherdev.plugin.navigation.kit.advert;

/**
 * Created by Administrator on 2016/8/16.
 */
public enum AdState {

    OTHER(0), NOTLIMIT(1), NOTYET(2), TIMESOVER(3), FORCECLOSE(4);

    public int flag;

    private AdState(int flag) {
        this.flag = flag;
    }

    public static AdState getStateByFlag(int flag) {
        switch (flag) {
            case 4:
                return FORCECLOSE;
            case 3:
                return TIMESOVER;
            case 2:
                return NOTYET;
            case 1:
                return NOTLIMIT;
            case 0:
            default:
                return OTHER;
        }
    }

    public static AdState getState(int showCount, int showInterval, int currentCount, long lastTime) {
        //可展示数为0 或 显示间隔时间为 0 均为可展示
        if (showCount == 0 || showInterval == 0) return NOTLIMIT;

        if (currentCount >= showCount) {
            return TIMESOVER;
        }
        if ((System.currentTimeMillis() - lastTime) < showInterval * 60L * 1000L) {
            return NOTYET;
        }
        return OTHER;
    }

    /**
     * 简单地判断当前状态是否可展示
     *
     * @param state
     * @return
     */
    public static boolean isShowable(AdState state) {
        if (state == null) return true;
        switch (state.flag) {
            case 1://无限制
                return true;
            case 2://还未到下次的展示时间
                return false;
            case 3://当天展示次数已用完
                return false;
            case 0://其他情况
            default:
                return true;
        }
    }

    /**
     * 严格判断当前状态是否可展示，
     *
     * @param state
     * @param lastTime
     * @param interval 毫秒
     * @return
     */
    public static boolean isShowableStrict(AdState state, long lastTime, long interval) {
        if (state == null) return false;
        if (state.flag == 2) {
            return (System.currentTimeMillis() - lastTime) >= interval;
        } else {
            return isShowable(state);
        }
    }
}
