package com.nd.hilauncherdev.plugin.navigation.widget.combine.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.VideoBean;
import com.nd.hilauncherdev.plugin.navigation.loader.VideoDataLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.ListViewTool;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.video.JCMediaManager;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayer;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayerManager;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayerStandard;
import com.nd.hilauncherdev.plugin.navigation.video.VideoPlayerHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.listview.PullListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;
import com.nd.hilauncherdev.plugin.navigation.widget.pull.WallpaperHeaderListView;
import com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.calculator.SingleListViewItemActiveCalculator;
import com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.scroll_utils.ListViewItemPositionGetter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linliangbin on 2017/4/21 14:56.
 */

public class VideoListAdapter extends PullListViewAdapter implements VideoDataLoader.VideoDataCallback, AbsListView.OnScrollListener, JCVideoPlayerStandard.VideoPlayCallback, WallpaperHeaderListView.TouchCallback {

    public static VideoBean currentActive = null;
    public static VideoBean currentDeactive = null;
    //优化方案，只有在快速滚动的时候，才调用
    protected boolean mOnScrolleFilpping = false;
    SingleListViewItemActiveCalculator mListItemVisibilityCalculator;
    Handler mOnScrollChangedHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (!mOnScrolleFilpping) {
                notifyDataSetChanged();
            }
        }
    };
    private ArrayList<VideoBean> videoList;
    private VideoDataLoader videoDataLoader;
    private WallpaperHeaderListView wallpaperHeaderListView;
    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    private ListViewItemPositionGetter mItemsPositionGetter;
    private int currentPageIndex = 1;
    private boolean isAutoPlay = false;
    private VideoBean autoPlayBean = null;
    private List<AdvertSDKManager.AdvertInfo> bannerAd = new ArrayList<AdvertSDKManager.AdvertInfo>();
    private List<AdvertSDKManager.AdvertInfo> videoAd = new ArrayList<AdvertSDKManager.AdvertInfo>();
    /**
     * 可以展示的banner广告数量
     */
    private int bannerAdCount = 0;
    private HashMap<Integer, Integer> bannerAdPositionMap = new HashMap<Integer, Integer>();
    private int videoAdCount = 0;
    private HashMap<Integer, Integer> videoAdPositionMap = new HashMap<Integer, Integer>();

    public VideoListAdapter(Context context, WallpaperHeaderListView listView) {
        super(context, listView);
        this.wallpaperHeaderListView = listView;
        initView();
    }

    public ArrayList<VideoBean> getVideoList() {
        return videoList;
    }

    public void initView() {

        videoList = new ArrayList<VideoBean>();
        videoDataLoader = VideoDataLoader.getInstance(context);
        videoDataLoader.setCallback(this);
        currentPageIndex = CardManager.getInstance().getVideoListPageIndex(context);
        videoDataLoader.requestVideoPage(currentPageIndex, true, true);
        syncFooterViewState(STATUS_PULL_LOADING);
        wallpaperHeaderListView.setOutterScrollLisenter(this);
        wallpaperHeaderListView.setTouchCallback(this);
        mListItemVisibilityCalculator = new SingleListViewItemActiveCalculator();
        mItemsPositionGetter = new ListViewItemPositionGetter(wallpaperHeaderListView);
    }

    /***
     * 刷新所有分页数据
     */
    public void doRefresh() {
        currentPageIndex = CardManager.getInstance().getVideoListPageIndex(context);
        videoDataLoader.requestVideoPage(currentPageIndex, false, true);
    }

    @Override
    public int getCount() {

        return videoList.size();
    }

    private Object getVideoItem(int position) {
        if (position < 0 || position >= videoList.size()) {
            return null;
        }
        VideoBean video = videoList.get(position);
        if (videoAdPositionMap.containsKey(new Integer(position))) {
            int adIndex = videoAdPositionMap.get(new Integer(position));
            if (adIndex < videoAd.size()) {
                AdvertSDKManager.AdvertInfo ad = videoAd.get(adIndex);
                video.advertInfo = ad;
            }
        }
        return video;
    }

    @Override
    public Object getItem(int position) {
        if (bannerAdPositionMap.containsKey(new Integer(position))) {
            int adIndex = bannerAdPositionMap.get(new Integer(position));
            if (adIndex < bannerAd.size()) {
                return bannerAd.get(adIndex);
            } else {
                return getVideoItem(position);
            }
        } else {
            return getVideoItem(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView || convertView.getTag() == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.multi_resource_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (viewHolder != null) {
            viewHolder.initByObject(position, getItem(position));
            return convertView;
        }
        return new View(context);
    }

    /**
     * @desc 重新计算当前的Banner 广告的数量
     * @author linliangbin
     * @time 2017/6/1 10:43
     */
    //TODO llbeing 优化实现
    private void calculateBannerAdCount() {
        int AdStartIndex = 5;
        int AdPage = 7;
        int AdCount = 0;
        int AdNextIndex = AdStartIndex;
        bannerAdPositionMap.clear();
        for (int i = 0; i < videoList.size(); i++) {
            if (i == AdNextIndex) {
                bannerAdPositionMap.put(new Integer(i), new Integer(AdCount));
                AdPage++;
                AdNextIndex = AdNextIndex + AdPage;
                AdCount++;
            }
        }
        if (AdCount > bannerAd.size()) {
            bannerAdCount = bannerAd.size();
        } else {
            bannerAdCount = AdCount;
        }


        AdStartIndex = 3;
        AdPage = 5;
        AdCount = 0;
        AdNextIndex = AdStartIndex;
        videoAdPositionMap.clear();
        for (int i = 0; i < videoList.size(); i++) {
            if (i == AdNextIndex) {
                videoAdPositionMap.put(new Integer(i), new Integer(AdCount));
                AdPage++;
                AdNextIndex = AdNextIndex + AdPage;
                AdCount++;
            }
        }
        if (AdCount > videoAd.size()) {
            videoAdCount = videoAd.size();
        } else {
            videoAdCount = AdCount;
        }

    }

    private void reset() {
        if (videoList != null) {
            videoList.clear();
        }
    }

    private void printList(ArrayList<VideoBean> videoBeen) {
        for (int i = 0; i < videoBeen.size(); i++) {
            if (videoBeen.get(i).advertInfo != null && videoBeen.get(i).adType == VideoBean.AD_TYPE_BANNER) {
//                Log.i("llbeing","i=" + i +",type="+videoBeen.get(i).adType);
            }
        }
    }

    @Override
    public void onVideoListUpdate(ArrayList<VideoBean> videos, List<AdvertSDKManager.AdvertInfo> bannerAd, List<AdvertSDKManager.AdvertInfo> videoAd, int resultCode, boolean isLast, boolean isAppend) {


        if (resultCode == VideoDataLoader.VideoDataCallback.RESULT_LOAD_SUCCESS) {
            if (this.videoList == null) {
                this.videoList = new ArrayList<VideoBean>();
            }
            if (!isAppend) {
                reset();
            }
            this.videoList.addAll(videos);
            if (this.videoList.size() > 0) {
                syncFooterViewState(STATUS_READY);
                notifyDataSetChanged();
            } else {
                syncFooterViewState(STATUS_PULL_ERROR);
            }
        } else {
            syncFooterViewState(STATUS_PULL_ERROR);
        }
        currentPageIndex++;
        CardManager.getInstance().setVideoListPageIndex(context, currentPageIndex);

        if (!isAppend && this.videoList.size() > 0) {
            ListViewTool.scroolToHeader(this.listView);
        }
    }

    private VideoBean getBeanByUrl(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            return null;
        }
        for (int i = 0; i < getCount(); i++) {
            Object object = getItem(i);
            if (object instanceof VideoBean) {
                if (videoUrl.equals(((VideoBean) object).downloadUrl)) {
                    return ((VideoBean) object);
                }
            }
        }
        return null;
    }

    /**
     * @desc 处理部分情况无法及时弹框的问题
     * @author linliangbin
     * @time 2017/6/15 15:19
     */
    private void handleDeactiveIncase() {

        int firstVisible = wallpaperHeaderListView.getFirstVisiblePosition();
        int lastVisible = wallpaperHeaderListView.getLastVisiblePosition();
//        Log.i("llbeing","handleDeactiveIncase:"+firstVisible + "," + lastVisible + "," + wallpaperHeaderListView.getChildCount());
        if (JCVideoPlayerManager.getFirstFloor() != null && JCMediaManager.instance().isPlaying()) {
            boolean isInVisible = false;
            for (int index = firstVisible; index <= lastVisible; index++) {
                VideoBean video = getVideoFromListviewIndex(index);
                if (video != null && !TextUtils.isEmpty(JCMediaManager.CURRENT_PLAYING_URL)
                        && JCMediaManager.CURRENT_PLAYING_URL.equals(video.downloadUrl)) {
                    isInVisible = true;
                    break;
                }
            }
            if (!isInVisible) {
                VideoBean video = getBeanByUrl(JCMediaManager.CURRENT_PLAYING_URL);
                if (video != null) {
                    for (int index = 0; index < wallpaperHeaderListView.getChildCount(); index++) {
                        View view = wallpaperHeaderListView.getChildAt(index);
                        if (view == null) {
                            continue;
                        }
                        Object tagObject = view.getTag();
                        if (tagObject == null) {
                            continue;
                        }

                        if (tagObject instanceof ViewHolder) {
                            JCVideoPlayer jcVideoPlayer = ((ViewHolder) tagObject).jcVideoPlayer;
                            if (jcVideoPlayer != null) {
//                                Log.i("llbeing","removeTexture");
                                jcVideoPlayer.removeTexttureFromContainer();
                                jcVideoPlayer.cancelProgressTimer();
                            }
                        }
                    }
                    if (JCMediaManager.textureView != null && JCMediaManager.textureView.getParent() != null) {
//                        Log.i("llbeing","handleDeactiveIncase:removeInCase");
                        ((ViewGroup) JCMediaManager.textureView.getParent()).removeView(JCMediaManager.textureView);
                    }
//                    Log.i("llbeing","handleDeactiveIncase:need start Tiny");
//                    VideoPlayerHelper.startWindowTiny(null, context, video.downloadUrl, video.title, video.thumbUrl);
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
        if (scrollState == SCROLL_STATE_IDLE && !getVideoList().isEmpty()) {
            mListItemVisibilityCalculator.onScroll(mItemsPositionGetter, view.getFirstVisiblePosition(), view.getLastVisiblePosition(), mScrollState);
            mOnScrolleFilpping = false;
            mOnScrollChangedHandle.sendEmptyMessage(500);
            handleDeactiveIncase();
        } else {
            mOnScrolleFilpping = true;
        }
    }

    @Override
    public boolean isEmptyList() {
        if (getCount() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void requestPageData() {
        videoDataLoader.requestVideoPage(currentPageIndex, true, false);

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if (!getVideoList().isEmpty()) {
            mListItemVisibilityCalculator.onScroll(mItemsPositionGetter, view.getFirstVisiblePosition(), view.getLastVisiblePosition(), mScrollState);
        }
    }




    @Override
    public void onVideoPlayCompleted(final int index) {

        //TODO llbeing api 11 以上
        if (TelephoneUtil.getApiLevel() > 11 && TelephoneUtil.isWifiEnable(context)) {
            listView.post(new Runnable() {
                @Override
                public void run() {

                    isAutoPlay = true;
//                    Log.i("llbeing", "onVideoPlayCompleted:" + (JCVideoPlayerManager.playingUrlListIndex + 1) + ",isAutoPlay:" + isAutoPlay);
                    listView.smoothScrollToPositionFromTop(JCVideoPlayerManager.playingUrlListIndex + 1, ScreenUtil.dip2px(context, 250), 300);
                }
            });
        }

    }

    @Override
    public void onVideoPlayBack() {

    }

    @Override
    public void handleTouchEvent() {
        if (autoPlayBean != null && autoPlayBean.isAutoPlay) {
            autoPlayBean.isAutoPlay = false;
        }
        isAutoPlay = false;
    }


    private VideoBean getVideoFromListviewIndex(int visibleIndex) {

        int index = visibleIndex - listView.getHeaderViewsCount();
        Object data = getItem(index);
        VideoBean positionBean = null;
        if (data instanceof VideoBean) {
            positionBean = (VideoBean) data;
        }
        if (positionBean == null) {
            return null;
        } else {
            return positionBean;
        }

    }

    public class ViewHolder extends BaseVideoViewHolder implements View.OnClickListener {

        JCVideoPlayerStandard jcVideoPlayer;
        TextView titleText;
        TextView playCountText;
        TextView videoTimeText;
        TextView commentText;
        ImageView percentText;
        View videoLayout;

        View adLayout;
        FrameLayout adContainer;
        ImageView bannerImage;

        int index = -1;

        public ViewHolder(View itemView) {

            adLayout = itemView.findViewById(R.id.layout_video_ad);
            bannerImage = (ImageView) itemView.findViewById(R.id.iv_video_list_ad);
            adContainer = (FrameLayout) itemView.findViewById(R.id.layout_ad_container);

            videoLayout = itemView.findViewById(R.id.layout_video_video);
            titleText = (TextView) itemView.findViewById(R.id.tv_video_title);
            playCountText = (TextView) itemView.findViewById(R.id.tv_video_play_count);
            videoTimeText = (TextView) itemView.findViewById(R.id.tv_video_timelong);
            commentText = (TextView) itemView.findViewById(R.id.tv_video_comment_count);
            jcVideoPlayer = (JCVideoPlayerStandard) itemView.findViewById(R.id.videoplayer);
            percentText = (ImageView) itemView.findViewById(R.id.tv_enlarge_percent);

            itemView.setOnClickListener(this);
        }

        public void initByObject(int index, Object data) {
            if (data instanceof VideoBean) {
                if (VideoBean.AD_TYPE_BANNER == ((VideoBean) data).adType && ((VideoBean) data).advertInfo != null) {
                    initByAd(index, ((VideoBean) data).advertInfo);
                } else {
                    initByBean(index, (VideoBean) data);
                }
            }
        }

        private void initByAd(int index, final AdvertSDKManager.AdvertInfo ad) {
            this.index = index;
            videoLayout.setTag(null);
            videoLayout.setVisibility(View.GONE);
            adLayout.setVisibility(View.VISIBLE);
            adLayout.setTag(ad);
            adLayout.setOnClickListener(this);
            if (mOnScrolleFilpping) {
                Drawable memoryCacheDrawable = ImageLoader.getInstance().loadDrawableIfExistInMemory(ad.picUrl);
                if (memoryCacheDrawable != null) {
                    showAdBanner(context, memoryCacheDrawable, bannerImage, adContainer, ad);
                }
            } else {
                final Drawable drawable = ImageLoader.getInstance().loadDrawable(ad.picUrl, new ImageCallback() {
                    @Override
                    public void imageLoaded(Drawable drawable, String s, Map map) {
                        if (drawable != null && ad.picUrl.equals(s)) {
                            showAdBanner(context, drawable, bannerImage, adContainer, ad);
                        }
                    }
                });
                if (drawable != null) {
                    showAdBanner(context, drawable, bannerImage, adContainer, ad);
                }
            }

        }

        private void initByBean(int index, VideoBean videoBean) {
//            Log.i("llbeing", "initByBean:" + index + "," + videoBean.title);
//            Log.i("llbeing", "initByBean:" + index + "," + videoBean.downloadUrl);

            adLayout.setTag(null);
            adLayout.setVisibility(View.GONE);
            videoLayout.setVisibility(View.VISIBLE);
            videoLayout.setTag(videoBean);
            titleText.setText(videoBean.title);
            playCountText.setText(videoBean.getPlayCount());
            videoTimeText.setText(videoBean.getVideoTimeLong());
            videoTimeText.setVisibility(View.VISIBLE);
            commentText.setText(videoBean.getCommentCount());

            jcVideoPlayer.setIndex(index + listView.getHeaderViewsCount());
            jcVideoPlayer.setResId(videoBean.resId);
            jcVideoPlayer.setVideoPlayCallback(VideoListAdapter.this);
//            Log.i("llbeing","initByBean:"+videoBean.thumbUrl);
            if (videoBean.advertInfo != null) {
                videoBean.advertInfo = videoDataLoader.popVideoAd();
                playCountText.setText(videoBean.getPlayCount());
                jcVideoPlayer.setUp(
                        mOnScrolleFilpping, videoBean.downloadUrl, videoBean.advertInfo, JCVideoPlayer.SCREEN_LAYOUT_LIST,
                        videoBean.title, videoBean.thumbUrl);
            } else {
                jcVideoPlayer.setUp(
                        mOnScrolleFilpping, videoBean.downloadUrl, null, JCVideoPlayer.SCREEN_LAYOUT_LIST,
                        videoBean.title, videoBean.thumbUrl);
            }
            jcVideoPlayer.setOuterTimeView(videoTimeText);
            jcVideoPlayer.setTag(videoBean);
            videoLayout.setOnClickListener(this);
            if (videoBean.isStartTiny) {
//                Log.i("llbeing","initByBean:startTiny:" + videoBean.title);
                VideoPlayerHelper.startWindowTiny(jcVideoPlayer, context, videoBean.downloadUrl, videoBean.title, videoBean.thumbUrl);
                videoBean.isStartTiny = false;
                currentDeactive = videoBean;
                currentActive = null;
            } else if (videoBean.isPlayOnthis) {
                final JCVideoPlayer secondPlayer = JCVideoPlayerManager.getSecondFloor();
                if (secondPlayer != null) {
                    secondPlayer.cancelProgressTimer();
//                    Log.i("llbeing","initByBean:playOnthis:nullPlayer");
//                    return;
                }
                jcVideoPlayer.playOnThisJcvd();
                currentActive = videoBean;
                currentDeactive = null;
//                    Log.i("llbeing","initByBean:playOnthis:" + videoBean.title);
//                }else{
//                    Log.i("llbeing","initByBean:playOnthis:url dismatch");
//                }
                videoBean.isPlayOnthis = false;
            } else if (videoBean.isAutoPlay) {
//                Log.i("llbeing","initByBean:autoPlay:" + videoBean.title);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIDEO_LIST, "lbbf");
                if(JCVideoPlayer.playIndexMap != null && !JCVideoPlayer.playIndexMap.containsKey(videoBean.resId+"")){
                    PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIDEO_LIST, "lbqc");
                    JCVideoPlayer.playIndexMap.put(videoBean.resId+"","");
                }
                jcVideoPlayer.prepareMediaPlayer();
                videoBean.isAutoPlay = false;
                currentActive = videoBean;
                currentDeactive = null;

            } else {
//                Log.i("llbeing","initByBean:doNothing:" + videoBean.title);
            }


        }


        private void showAdBanner(Context context, Drawable drawable, ImageView imageView, FrameLayout bannerLayout, AdvertSDKManager.AdvertInfo advertInfo) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            int viewWidth = ScreenUtil.getCurrentScreenWidth(context);
            int viewHeight = (int) (drawableHeight / (float) drawableWidth * viewWidth);
            imageView.setImageDrawable(drawable);
            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
            lp.width = viewWidth;
            lp.height = viewHeight;
            try {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (Throwable t) {

            }
            AdvertSDKManager.addAdvertLogoView(context, bannerLayout, advertInfo);
            AdvertSDKController.submitShowEvent(context, new Handler(), advertInfo);
            CvAnalysis.submitShowEvent(NavigationView2.activity, BaseNavigationSearchView.CV_PAGE_ID, CvAnalysisConstant.NAVIGATION_SCREEN_VIDEO_LIST_BANNER_AD,
                    advertInfo.id, CvAnalysisConstant.RESTYPE_ADS);
        }


        @Override
        public void setActive(final View AA, int newActiveViewPosition) {


            newActiveViewPosition = newActiveViewPosition - listView.getHeaderViewsCount();
            Object data = getItem(newActiveViewPosition);
            VideoBean positionBean = null;
            if (data instanceof VideoBean) {
                positionBean = (VideoBean) data;
            }
            if (positionBean == null) {
                return;
            }
//            Log.e("llbeing", "setActive:" + newActiveViewPosition);

            if (positionBean.equals(currentActive)) {
                return;
            }


            /** 优先播放小窗，避免播放显示异常 */
            if (positionBean.isStartTiny) {
                return;
            }

            if (isAutoPlay) {
                positionBean.isAutoPlay = true;
                positionBean.isPlayOnthis = false;
                positionBean.isStartTiny = false;
                autoPlayBean = positionBean;
                notifyDataSetChanged();
//                Log.e("llbeing", "autoPlay:" + newActiveViewPosition + "," + positionBean.title);
                isAutoPlay = false;
            } else {
                final JCVideoPlayer secondPlayer = JCVideoPlayerManager.getSecondFloor();
                final JCVideoPlayer firstPlayer = JCVideoPlayerManager.getFirstFloor();

                if (secondPlayer == null) {
//                    if(firstPlayer != null)
//                    Log.i("llbeing", "playOnthis:" + newActiveViewPosition + ",null,return:"+firstPlayer.url);
//                    Log.e("llbeing", "playOnthis:" + newActiveViewPosition + ",null,return");

                    return;
                }
                if (JCVideoPlayerManager.isCurrentPlayInSecondFloor(positionBean.downloadUrl)) {
//                    Log.e("llbeing", "playOnthis:" + newActiveViewPosition + "," + positionBean.title);
                    /** 取消自动回复播放功能 */
//                    positionBean.isPlayOnthis = true;
//                    positionBean.isAutoPlay = false;
//                    positionBean.isStartTiny = false;
//                    notifyDataSetChanged();
                }
//                else if(secondPlayer.currentState != CURRENT_STATE_PLAYING){
//                    Log.e("llbeing", "playOnthis:" + newActiveViewPosition + ",state inCorrect");
//                }else if(!positionBean.downloadUrl.equals(secondPlayer.url)){
//                    Log.e("llbeing", "playOnthis:" + newActiveViewPosition + ",url mismatch");
//                }
            }
        }

        @Override
        public void deactivate(View currentView, int position) {
            position = position - listView.getHeaderViewsCount();
            Object data = getItem(position);
            if (!(data instanceof VideoBean)) {
                return;
            }
            VideoBean videoBean = (VideoBean) data;

//            Log.i("llbeing", "deactivate:" + position);
            if (videoBean.equals(currentDeactive)) {
                return;
            }

            JCVideoPlayer currentPlaying = JCVideoPlayerManager.getFirstFloor();


            if (currentPlaying == null) {
//                Log.i("llbeing","startTiny:" + position + ",currentPlayingNull");
            } else if (!JCMediaManager.instance().isPlaying()) {

            } else if (TextUtils.isEmpty(JCMediaManager.CURRENT_PLAYING_URL) ||
                    !JCMediaManager.CURRENT_PLAYING_URL.equals(videoBean.downloadUrl)) {
//                Log.i("llbeing", "startTiny:" + position + "," + videoBean.downloadUrl);
                if (!TextUtils.isEmpty(JCMediaManager.CURRENT_PLAYING_URL)
                        && videoBean.advertInfo != null && videoBean.adType == VideoBean.AD_TYPE_VIDEO
                        && JCMediaManager.CURRENT_PLAYING_URL.equals(videoBean.advertInfo.videoUrl)) {
                    JCVideoPlayer.releaseAllVideos();
                }
            } else {
                /** 取消小窗播放功能 */
//                videoBean.isStartTiny = true;
//                videoBean.isPlayOnthis = false;
//                videoBean.isAutoPlay = false;
//                Log.i("llbeing", "startTiny:" + position + "," + videoBean.downloadUrl);
//                notifyDataSetChanged();
                JCVideoPlayer.releaseAllVideos();
            }
        }

        @Override
        public void onClick(View v) {
            Object tag = videoLayout.getTag();

            if (tag != null && tag instanceof VideoBean) {
                LauncherCaller.openUrl(context, "", ((VideoBean) tag).detailUrl);
                return;
            }

            tag = adLayout.getTag();
            if (tag != null && tag instanceof AdvertSDKManager.AdvertInfo) {
                LauncherCaller.openUrl(context, "", ((AdvertSDKManager.AdvertInfo) tag).actionIntent);
                AdvertSDKController.submitClickEvent(context, new Handler(), ((AdvertSDKManager.AdvertInfo) tag));
                CvAnalysis.submitClickEvent(NavigationView2.activity, BaseNavigationSearchView.CV_PAGE_ID, CvAnalysisConstant.NAVIGATION_SCREEN_VIDEO_LIST_BANNER_AD,
                        ((AdvertSDKManager.AdvertInfo) tag).id, CvAnalysisConstant.RESTYPE_ADS);
                return;
            }

        }
    }


}
