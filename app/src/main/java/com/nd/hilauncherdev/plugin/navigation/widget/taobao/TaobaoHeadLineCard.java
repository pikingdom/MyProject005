package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoHotword;
import com.nd.hilauncherdev.plugin.navigation.loader.TaobaoLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.SharedPreferencesUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 淘宝-头条卡片<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/15<br/>
 */
public class TaobaoHeadLineCard extends TaobaoCard {

    private TextSwitcher textSwitcher;
    private List<TaobaoHotword> mData = new ArrayList<TaobaoHotword>();
    private int mHotwordIndex = 0;
    private Context mContext;
    private SharedPreferencesUtil spUtil;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setSearchHotwordNext();
        }
    };

    public TaobaoHeadLineCard(final Context context, ViewGroup parent) {
        super(parent);
        setType(CardType.HEADLINE);

        mContext = context;
        spUtil = new SharedPreferencesUtil(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.taobao_viewtype_headline, parent, false);
        setContentView(contentView);

        textSwitcher = (TextSwitcher) contentView.findViewById(R.id.search);
        HeadLineFactory factory = new HeadLineFactory(context);
        textSwitcher.setFactory(factory);
        Animation inAnim = AnimationUtils.loadAnimation(context, R.anim.push_up_in);
        textSwitcher.setInAnimation(inAnim);
        Animation outAnim = AnimationUtils.loadAnimation(context, R.anim.push_up_out);
        textSwitcher.setOutAnimation(outAnim);
        textSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData == null || mData.size() < 1) {
                    return;
                }
                int d = mHotwordIndex - 1;
                d = d < 0 ? 0 : d;
                int index = d % mData.size();
                TaobaoHotword hotword = mData.get(index);
                if (hotword != null) {
                    TaobaoData.getInstance().cancelBannerTimers();//离开页面停止定时器
                    CvAnalysis.submitClickEvent(context, CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_HEADLINE, CvAnalysisConstant.TAOBAO_SCREEN_RESIDEX, CvAnalysisConstant.RESTYPE_LINKS);
                    Intent intent = new Intent();
                    intent.setClass(mContext, NewsDetailActivity.class);
                    intent.putExtra("url", hotword.clickUrl);
                    SystemUtil.startActivitySafely(mContext, intent);
                }
            }
        });
        //默认隐藏的
        setCardVisible(false);
    }


    private static class HeadLineFactory implements ViewSwitcher.ViewFactory {

        private Context ctx;

        public HeadLineFactory(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public View makeView() {
            TextView tv = new TextView(ctx);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setSingleLine();
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setTextColor(Color.parseColor("#1c1c1c"));
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL;
            tv.setLayoutParams(lp);
            return tv;
        }
    }

    private void setSearchHotwordNext() {
        if (null == textSwitcher || mData.isEmpty()) return;
        int nextIndex = mHotwordIndex++ % mData.size();
        TaobaoHotword info = mData.get(nextIndex);
        if (null == info || TextUtils.isEmpty(info.title)) return;
        handler.removeCallbacksAndMessages(null);
        textSwitcher.setText(info.title);
        handler.sendMessageDelayed(handler.obtainMessage(), 3000);
    }

    private void loadData() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {
                    TaobaoLoader.getTaobaoHeadLine(mContext);
                    final String headline = spUtil.getString(SharedPreferencesUtil.TAOBAO_HEADLINE);
                    if (TextUtils.isEmpty(headline)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setCardVisible(false);
                            }
                        });
                    }else {
                        final List<TaobaoHotword> list = new ArrayList<TaobaoHotword>();
                        JSONArray jsonArray = new JSONObject(headline).optJSONArray("List");
                        for (int i = 0, len = jsonArray.length(); i < len; i++) {
                            TaobaoHotword hotword = jsonToHotWord(jsonArray.optJSONObject(i));
                            if (hotword != null) {
                                list.add(hotword);
                            }
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (list.size() > 0) {
                                    setCardVisible(true);
                                    mData.clear();
                                    mHotwordIndex = 0;
                                    mData.addAll(list);
                                    setSearchHotwordNext();
                                }
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setCardVisible(false);
                        }
                    });
                }
            }
        });
    }

    private TaobaoHotword jsonToHotWord(JSONObject json) {
        if (json == null) {
            return null;
        }
        TaobaoHotword hotword = new TaobaoHotword();
        hotword.id = json.optInt("Id");
        hotword.title = json.optString("Title");
        hotword.clickUrl = json.optString("LinkUrl");
        return hotword;
    }

    @Override
    public void update() {
        loadData();
    }
}
