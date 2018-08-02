package com.nd.hilauncherdev.plugin.navigation.widget.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.ConfigPreferences;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.util.ActivityResultHelper;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FuelManagerToggleUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.TextSwitcherFactory;
import com.nd.hilauncherdev.plugin.navigation.widget.VoiceRecognitionWindow;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;
import com.nd.hilauncherdev.plugin.navigation.widget.search.hotword.ServerHotwordGenerator;

import java.util.List;


/**
 * Created by linliangbin on 2017/4/25 15:42.
 */

public class SearchLayout extends RelativeLayout implements View.OnClickListener {


    public static final int MSG_NEXT_TEXT_SWITER = 100;
    public static boolean forceDisableSwitcher = false;
    public static boolean enableSwitcher = true;
    public RelativeLayout searchView;
    TextSwitcherFactory textSwitcherFactory;
    ServerHotwordGenerator hotwordGenerator;
    private TextView txtSearchInputEx;
    private ImageView btnQRCode;
    private VoiceRecognitionWindow voiceRecognitionWindow;
    private ImageView btnVoiceEx;
    private TextSwitcher textSwitcher;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (MSG_NEXT_TEXT_SWITER == msg.what) {
                updateHotwordSwitcher();
            }
        }
    };
    private boolean isSwitchering = false;
    private boolean hasInitSwitcher = false;

    public SearchLayout(Context context) {
        super(context);
        init();
    }

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setHotwordGenerator(ServerHotwordGenerator hotwordGenerator) {
        this.hotwordGenerator = hotwordGenerator;
    }

    public void setEnableSwitcher(boolean enableSwitcher) {
        this.enableSwitcher = enableSwitcher;
    }

    /**
     * @desc 返回需要显示的布局ID
     * 子类可以自定义该布局
     * @author linliangbin
     * @time 2017/5/2 11:46
     */
    public int getBaseLayoutResId() {
        return R.layout.layout_search;
    }

    public int getTextSwitcherId() {
        return R.id.navi_search_text_switcher;
    }

    private void init() {
        hotwordGenerator = new ServerHotwordGenerator(getContext());
        LayoutInflater.from(getContext()).inflate(getBaseLayoutResId(), this);

        // ------------------------ 搜索结果列表初始化 --------------------------\\
        txtSearchInputEx = (TextView) findViewById(R.id.txtSearchInput);
        txtSearchInputEx.setOnClickListener(this);

        searchView = (RelativeLayout) findViewById(R.id.search_view);

        // --------------------------- 二维码扫描 -------------------------------\\
        btnQRCode = (ImageView) findViewById(R.id.navi_qrcode);
        if (ConfigPreferences.getInstance(getContext()).isTorchEnable()) {
            btnQRCode.setOnClickListener(this);
        } else {
            btnQRCode.setVisibility(INVISIBLE);
        }


        // --------------------------- 语音识别 -------------------------------\\
        voiceRecognitionWindow = new VoiceRecognitionWindow(getContext());
        btnVoiceEx = (ImageView) findViewById(R.id.btnVoice);
        if (VoiceRecognitionWindow.isVoiceRecognitionEnable(getContext())) {
            btnVoiceEx.setVisibility(View.VISIBLE);
            btnVoiceEx.setOnClickListener(this);
            voiceRecognitionWindow.setOnRecognitionItemClickListener(new VoiceRecognitionWindow.OnRecognitionItemClickListener() {
                @Override
                public void onItemClick(View view, CharSequence text) {
                    startSearchActivity(text);
                    voiceRecognitionWindow.dismiss();
                }
            });

            // 重试按钮
            voiceRecognitionWindow.setOnRightButtonClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    voiceRecognitionWindow.dismiss();
                    startVoiceRecognition();
                }
            });
        } else {
            btnVoiceEx.setVisibility(View.GONE);
        } // end of if

    }

    private void startSearchActivity(CharSequence text) {
        Intent intent = new Intent();
        intent.setClassName(getContext(), "com.nd.hilauncherdev.launcher.navigation.SearchActivity");
        if (!TextUtils.isEmpty(text)) {
            intent.putExtra("defaultWord", text);
        }
        intent.putExtra("from", "open_from_navigation");
        SystemUtil.startActivityForResultSafely(BaseNavigationSearchView.activity, intent, SystemUtil.REQUEST_SEARCH_ACTIVITY_POSITION);
        PluginUtil.invokeSubmitEvent(BaseNavigationSearchView.activity, AnalyticsConstant.SEARCH_PERCENT_CONVERSION_SEARCH_INLET, "1");
    }

    public void startVoiceRecognition() {
        VoiceRecognitionWindow.startVoiceRecognition(getContext(), BaseNavigationSearchView.activity, ActivityResultHelper.REQUEST_VOICE_RECOGNITION_NAVI);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {

                case R.id.txtSearchInput:
                    Object tagObject = ((TextSwitcher) findViewById(getTextSwitcherId())).getTag();
                    if (tagObject instanceof HotwordItemInfo) {
                        if(((HotwordItemInfo) tagObject).type != HotwordItemInfo.TYPE_LOCAL_WORD){
                            startSearchActivity(((HotwordItemInfo) tagObject).name);
                        }else{
                            startSearchActivity("");
                        }
                    } else if (tagObject == null || tagObject instanceof String) {
                        startSearchActivity(((String) tagObject));
                    }
                    PluginUtil.invokeSubmitEvent(BaseNavigationSearchView.activity, AnalyticsConstant.NAVIGATION_DZ_SEARCH_PAGE_SEARCH_INPUT, "dj");
                    break;
                case R.id.navi_qrcode:
                    FuelManagerToggleUtil.widgetZxingScan(getContext());
                    /**
                     * maolinnan_350804于14.1.14日添加的统计信息
                     */
                    PluginUtil.invokeSubmitEvent(BaseNavigationSearchView.activity, AnalyticsConstant.SEARCH_QR_CODE, "1");
                    break;


            }
        }
    }

    public void showVoiceRecognitionResult(List<String> results) {
        voiceRecognitionWindow.show(this, results);
    }

    public void onBackKeyDown() {
        if (voiceRecognitionWindow != null && voiceRecognitionWindow.isShowing()) {
            voiceRecognitionWindow.dismiss();
        }
    }

    /**
     * 是否二维码或语音按钮被touch
     */
    public boolean isBtnQRCodeOrVoiceTouched(MotionEvent ev) {
        float touchX = ev.getRawX();
        float touchY = ev.getRawY();
        int[] location = new int[2];
        int btnWidth = 0;
        int btnHeight = 0;

        if (btnQRCode != null && btnQRCode.getVisibility() == View.VISIBLE) {
            btnWidth = btnQRCode.getWidth();
            btnHeight = btnQRCode.getHeight();
            btnQRCode.getLocationOnScreen(location);
            if (touchX >= location[0] && touchX <= location[0] + btnWidth && touchY >= location[1] && touchY <= location[1] + btnHeight) {
                return true;
            }
        }

        if (btnVoiceEx != null && btnVoiceEx.getVisibility() == View.VISIBLE) {
            btnWidth = btnVoiceEx.getWidth();
            btnHeight = btnVoiceEx.getHeight();
            btnVoiceEx.getLocationOnScreen(location);
            if (touchX >= location[0] && touchX <= location[0] + btnWidth && touchY >= location[1] && touchY <= location[1] + btnHeight) {
                return true;
            }
        }

        return false;
    }


    public void setHotword(List<HotwordItemInfo> dataList) {
        if (hotwordGenerator != null) {
            hotwordGenerator.appendHotwords(dataList);
        }
    }

    public void onStopHotwordSwitch() {

        if (isSwitchering) {
            handler.removeMessages(MSG_NEXT_TEXT_SWITER);
            isSwitchering = false;
        }
    }

    public boolean isHotwordAvailable() {
        if (hotwordGenerator != null) {
            return hotwordGenerator.isHotwordsAvailable();
        }
        return false;
    }

    public void onStartHotwordSwitch() {
        Log.i("llbeing", "onStartHotwordSwitch");
        if (!isSwitchering && isHotwordAvailable()) {
            initSeachBoxView(Color.parseColor("#bcbec2"));
            if (null != textSwitcher) {
                if (enableSwitcher && !forceDisableSwitcher && hotwordGenerator != null) {
                    HotwordItemInfo current = hotwordGenerator.getCurrentHotword();
                    if (current != null) {
                        textSwitcher.setCurrentText(current.name);
                        textSwitcher.setTag(current);
                    }
                }
            }
            startNextTask(2000);
            isSwitchering = true;
        }
    }


    public void updateHotwordSwitcher() {

        if(hotwordGenerator != null && hotwordGenerator.isCounterHit()){
            return;
        }

        if (null != textSwitcher) {
            if (enableSwitcher && !forceDisableSwitcher && hotwordGenerator != null) {
                HotwordItemInfo current = hotwordGenerator.popupNextHotword();
                if (current != null) {
                    if(hotwordGenerator != null){
                        hotwordGenerator.increaseCounter();
                    }
                    textSwitcher.setText(current.name);
                    textSwitcher.setTag(current);
                    PluginUtil.invokeSubmitEvent(BaseNavigationSearchView.activity, AnalyticsConstant.NAVIGATION_DZ_SEARCH_PAGE_SEARCH_INPUT, "zs");
                }
            }
        }
        startNextTask();

    }

    public void startNextTask() {
        startNextTask(4000);
    }

    public void startNextTask(int next) {
        handler.removeMessages(MSG_NEXT_TEXT_SWITER);
        Message nextMsg = new Message();
        nextMsg.what = MSG_NEXT_TEXT_SWITER;
        handler.sendMessageDelayed(nextMsg, next);
    }

    public void initSeachBoxView(int color) {

        if (!hasInitSwitcher) {
            hasInitSwitcher = true;
            textSwitcher = (TextSwitcher) findViewById(getTextSwitcherId());

            textSwitcherFactory = new TextSwitcherFactory(getContext(), color);
            textSwitcher.setFactory(textSwitcherFactory);
            Animation inAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
            textSwitcher.setInAnimation(inAnim);
            Animation outAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_out);
            textSwitcher.setOutAnimation(outAnim);
        }


    }

    public void initSeachBoxView() {

        if (!hasInitSwitcher) {
            hasInitSwitcher = true;
            textSwitcher = (TextSwitcher) findViewById(getTextSwitcherId());
            textSwitcherFactory = new TextSwitcherFactory(getContext());
            textSwitcher.setFactory(textSwitcherFactory);
            Animation inAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
            textSwitcher.setInAnimation(inAnim);
            Animation outAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_out);
            textSwitcher.setOutAnimation(outAnim);

        }

    }

}
