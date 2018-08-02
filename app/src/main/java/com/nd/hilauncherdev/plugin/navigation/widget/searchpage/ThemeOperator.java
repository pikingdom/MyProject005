package com.nd.hilauncherdev.plugin.navigation.widget.searchpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.nd.hilauncherdev.plugin.navigation.CardManager;

/**
 * 桌面主题相关动作
 * Created by linliangbin on 2017/8/9 21:59.
 */

public class ThemeOperator {

    /**
     * 推荐添加卡片展示方式：
     * 0： 女生
     * 1： 常规
     * 2： 男生
     */
    public static final int NOTIFY_ADD_STYLE_BOY = 0;
    public static final int NOTIFY_ADD_STYLE_DEFAULT = 1;
    public static final int NOTIFY_ADD_STYLE_GIRL = 2;
    public static String INTENT_CURRENT_THEME_INFO = "com.nd.android.pandahome.THEME_INFO";
    Context context;
    private ThemeOperateListener listener;


    private BroadcastReceiver ThemeInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (INTENT_CURRENT_THEME_INFO.equals(action)) {
                if (listener != null) {
                    listener.onLauncherThemeApplied();
                }
            }
        }
    };


    public ThemeOperator(Context context) {
        this.context = context;
    }

    public void setListener(ThemeOperateListener listener) {
        this.listener = listener;
    }

    public void updateThemeChoose(int position) {
        switch (position) {
            case NOTIFY_ADD_STYLE_GIRL:
                CardManager.getInstance().setThemeChoose(context, NOTIFY_ADD_STYLE_GIRL);
                CardManager.getInstance().setNotifyNewCardIdL(context, CardManager.NOTIFY_CARD_GIRL);
                CardManager.getInstance().setIsCardListChanged(context, true);
                break;
            case NOTIFY_ADD_STYLE_DEFAULT:
                CardManager.getInstance().setThemeChoose(context, NOTIFY_ADD_STYLE_DEFAULT);
                //CardManager.getInstance().setNotifyNewCardIdL(context,CardManager.NOTIFY_CARD_DEFAULT);
                break;
            case NOTIFY_ADD_STYLE_BOY:
                CardManager.getInstance().setThemeChoose(context, NOTIFY_ADD_STYLE_BOY);
                CardManager.getInstance().setNotifyNewCardIdL(context, CardManager.NOTIFY_CARD_BOY);
                CardManager.getInstance().setIsCardListChanged(context, true);
                break;
        }
    }

    public void registerThemeReceiver() {

        try {
            IntentFilter intentFilter = new IntentFilter(INTENT_CURRENT_THEME_INFO);
            context.registerReceiver(ThemeInfoReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterThemeReceiver() {
        try {
            context.unregisterReceiver(ThemeInfoReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ThemeOperateListener {
        public void onLauncherThemeApplied();
    }

}
