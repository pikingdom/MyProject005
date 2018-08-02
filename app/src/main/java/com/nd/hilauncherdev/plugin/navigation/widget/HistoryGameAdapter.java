package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginActivity;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.GameBean;
import com.nd.hilauncherdev.plugin.navigation.loader.GameH5HistoryLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.BitmapUtils;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linliangbin_dian91 on 2015/11/9.
 * 游戏卡片：H5游戏打开历史数据适配器
 */
public class HistoryGameAdapter extends BaseAdapter {

    private ArrayList<GameBean> gameList = new ArrayList<>();
    private Context mContext;
    private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    public static final int MAX_COUNT = 20;
    private PluginActivity activity;
    public HistoryGameAdapter(Context context, ArrayList<GameBean> beans) {
        gameList = beans;
        mContext = context;
        activity = (PluginActivity) context;
    }

    @Override
    public int getCount() {
        return gameList.size() > MAX_COUNT ? MAX_COUNT : gameList.size();
    }

    @Override
    public Object getItem(int position) {
        return gameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final GameBean gameBean = gameList.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.navigation_game_card_item_h5_open, null);
            }

            if (convertView.getTag() == null) {
                ViewHolder holder = new ViewHolder();
                holder.allView = convertView;
                holder.game_icon = (ImageView) convertView.findViewById(R.id.game_icon);
                holder.game_name = (TextView) convertView.findViewById(R.id.game_name);
                holder.game_download = (TextView) convertView.findViewById(R.id.game_download);
                convertView.setTag(holder);
            }

            final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.game_icon.setImageResource(CommonLauncherControl.getLoadingBackgroad());
            viewHolder.game_download.setText(R.string.game_h5_open);
            Drawable cachedDrawable = asyncImageLoader.loadDrawable(gameBean.iconUrl, new ImageCallback() {
                @Override
                public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                    if (imageDrawable != null && !TextUtils.isEmpty(imageUrl) && imageUrl.equals(gameBean.iconUrl)) {
                        BitmapDrawable bp = (BitmapDrawable) imageDrawable;
                        Bitmap bitmap = BitmapUtils.toRoundCorner(bp.getBitmap(), 15);
                        if(bitmap != null && !bitmap.isRecycled()){
                            viewHolder.game_icon.setImageDrawable(new BitmapDrawable(bitmap));
                        }
                    }
                }
            });
            if(cachedDrawable != null){
                viewHolder.game_icon.setImageDrawable(cachedDrawable);
            }
            viewHolder.game_name.setText(gameBean.gameName);
            viewHolder.game_name.setTextColor(Color.BLACK);
            viewHolder.allView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //打开H5 游戏页面
                        LauncherCaller.openUrl(mContext, "", gameBean.downloadUrl,0,
                                CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_GAME_CARD_CLICK,
                                CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_GAME_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
                        GameH5HistoryLoader.addOpenH5Game(mContext, gameBean);
                        gameList = GameH5HistoryLoader.getOpenH5GameBean(mContext);
                        notifyDataSetChanged();
                        activity.submitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_GAME_CARD, "h5dkls" + gameBean.ID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return convertView;
        } catch (Exception e) {
            return new View(mContext);
        }

    }



    private class ViewHolder {
        View allView;
        ImageView game_icon;
        TextView game_name;
        TextView game_download;
    }

}
