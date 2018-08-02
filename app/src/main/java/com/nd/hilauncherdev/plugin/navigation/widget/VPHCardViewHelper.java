package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.VPHAdBean;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linliangbin_dian91 on 2015/10/20.
 */
public class VPHCardViewHelper extends CardInnerViewHelperBase {

    //各个Zone_id 对应不同的广告位
    private static final int ZONE_ID_TOP_LEFT = 362;
    private static final int ZONE_ID_TOP_RIGHT = 422;
    private static final int ZONE_ID_BOTTOM_LEFT = 423;
    private static final int ZONE_ID_BOTTOM_CENTER = 424;
    private static final int ZONE_ID_BOTTOM_RIGHT = 425;

    //各个广告位的尺寸
    private static final int TOP_LEFT_WITDTH = 445;
    private static final int TOP_LEFT_HEIGHT = 280;
    private static final int TOP_RIGHT_WIDTH = 220;
    private static final int TOP_RIGHT_HEIGHT = 280;
    private static final int BOTTOM_ANY_WIDTH = 220;
    private static final int BOTTOM_ANY_HEIGHT = 160;

    //卡片距离外部边框
    private static final int LEFT_RIGHT_MARGIN_EXTERNAL = 36;
    //广告卡片之间距离
    private static final int LEFT_RIGHT_MARGIN_INTERNAL = 2;

    //更多精品跳转短地址
    private static final String moreClickUrl = "http://url.felink.com/NreyUv";


    HashMap<Integer, SoftReference<ImageView>> adImageMap = new HashMap<Integer, SoftReference<ImageView>>();
    int[] imageIDs = {R.id.navigation_vph_top_left, R.id.navigation_vph_top_right, R.id.navigation_vph_bottom_left, R.id.navigation_vph_bottom_center,
            R.id.navigation_vph_bottom_right};
    ImageView top_left, top_right, bottom_left, bottom_center, bottom_right;

    public VPHCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
        super(navigationView, card, context);
    }

    @Override
    public void showDataS(final NavigationView2 navigationView, Card card, Message msg) {
        final ArrayList<VPHAdBean> adList = (ArrayList<VPHAdBean>) msg.obj;
        for (int i = 0; i < adList.size() && i < imageIDs.length; i++) {
            final int index = i;
            final VPHAdBean tempAd = adList.get(i);
            ImageView curImage = null;
            SoftReference<ImageView> imageRefer = adImageMap.get(tempAd.zone_id);
            if (imageRefer != null) {
                curImage = imageRefer.get();
            }
            if (curImage == null) {
                curImage = (ImageView) getCardView(card).findViewById(imageIDs[i]);
            }
            if (curImage == null)
                continue;
            final ImageView currentImageView = curImage;
            if (tempAd == null || TextUtils.isEmpty(tempAd.imgUrl))
                continue;
            Drawable cachedDrawable = navigationView.asyncImageLoader.loadDrawable(tempAd.imgUrl, new ImageCallback() {
                @Override
                public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                    if (imageDrawable != null && imageUrl.equals(tempAd.imgUrl)) {
                        currentImageView.setImageDrawable(imageDrawable);
                    } else if (imageDrawable == null && imageUrl.equals(tempAd.imgUrl)) {

                    }
                }
            });
            if (cachedDrawable != null) {
                curImage.setImageDrawable(cachedDrawable);
            }
            if (!TextUtils.isEmpty(tempAd.clickUrl)) {
                curImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LauncherCaller.openUrl(mContext, "", tempAd.clickUrl,IntegralTaskIdContent.NAVIGATION_SHOPPING_BRAND_SALE,
                                CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_VPH_CARD_CLICK,
                                CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_VPH_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
                        PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIPSHOP_CARD, "ad" + index);

                    }
                });
            }

        }

    }

    @Override
    public void initCardView(View cardView, Card card) {
        TextView textView = (TextView) cardView.findViewById(R.id.navi_card_base_next);
        textView.setText(mContext.getString(R.string.vph_more));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LauncherCaller.openUrl(mContext, "", moreClickUrl, IntegralTaskIdContent.NAVIGATION_SHOPPING_BRAND_SALE,
                        CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_VPH_CARD_CLICK,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_VPH_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIPSHOP_CARD, "gdjp");
            }
        });
        cardView.findViewById(R.id.navi_card_base_pre).setVisibility(View.GONE);
        cardView.findViewById(R.id.navi_card_base_more).setVisibility(View.GONE);

    }


    @Override
    public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
        View adView = LayoutInflater.from(mContext).inflate(R.layout.navigation_vph_card, null);
        top_left = (ImageView) adView.findViewById(R.id.navigation_vph_top_left);
        top_right = (ImageView) adView.findViewById(R.id.navigation_vph_top_right);
        bottom_left = (ImageView) adView.findViewById(R.id.navigation_vph_bottom_left);
        bottom_center = (ImageView) adView.findViewById(R.id.navigation_vph_bottom_center);
        bottom_right = (ImageView) adView.findViewById(R.id.navigation_vph_bottom_right);

        adImageMap.put(ZONE_ID_TOP_LEFT, new SoftReference<ImageView>(top_left));
        adImageMap.put(ZONE_ID_TOP_RIGHT, new SoftReference<ImageView>(top_right));
        adImageMap.put(ZONE_ID_BOTTOM_LEFT, new SoftReference<ImageView>(bottom_left));
        adImageMap.put(ZONE_ID_BOTTOM_CENTER, new SoftReference<ImageView>(bottom_center));
        adImageMap.put(ZONE_ID_BOTTOM_RIGHT, new SoftReference<ImageView>(bottom_right));

        if (CommonLauncherControl.DX_PKG) {
        	top_left.setImageResource(R.drawable.icon_loading_dx);
        	top_right.setImageResource(R.drawable.icon_loading_dx);
        	bottom_left.setImageResource(R.drawable.icon_loading_dx);
        	bottom_center.setImageResource(R.drawable.icon_loading_dx);
        	bottom_right.setImageResource(R.drawable.icon_loading_dx);
		}else {
			top_left.setImageResource(CommonLauncherControl.getVph());
			top_right.setImageResource(CommonLauncherControl.getVph());
			bottom_left.setImageResource(CommonLauncherControl.getVph());
			bottom_center.setImageResource(CommonLauncherControl.getVph());
			bottom_right.setImageResource(CommonLauncherControl.getVph());
		}
        
        cardInnerTotalV.addView(adView);
        setCardView(cardInnerTotalV);
        int width = ScreenUtil.getCurrentScreenWidth(mContext) - ScreenUtil.dip2px(mContext, LEFT_RIGHT_MARGIN_EXTERNAL);
        width -= ScreenUtil.dip2px(mContext, LEFT_RIGHT_MARGIN_INTERNAL);
        int viewWidth = 0;
        int viewHeight = 0;
        LinearLayout.LayoutParams rlp;
        viewWidth = (int) (width * (((float) TOP_LEFT_WITDTH / (TOP_LEFT_WITDTH + TOP_RIGHT_WIDTH))));
        viewHeight = (int) (viewWidth * (((float) TOP_LEFT_HEIGHT / TOP_LEFT_WITDTH)));
        rlp = (LinearLayout.LayoutParams) top_left.getLayoutParams();
        rlp.width = viewWidth;
        rlp.height = viewHeight;
        top_left.setLayoutParams(rlp);

        viewWidth = (int) (width * (((float) TOP_RIGHT_WIDTH / (TOP_LEFT_WITDTH + TOP_RIGHT_WIDTH))));
        viewHeight = (int) (viewWidth * (((float) TOP_RIGHT_HEIGHT / (TOP_RIGHT_WIDTH))));
        rlp = (LinearLayout.LayoutParams) top_right.getLayoutParams();
        rlp.width = viewWidth;
        rlp.height = viewHeight;
        top_right.setLayoutParams(rlp);

        width -= ScreenUtil.dip2px(mContext, LEFT_RIGHT_MARGIN_INTERNAL);
        viewWidth = (int) (width * (((float) BOTTOM_ANY_WIDTH / (BOTTOM_ANY_WIDTH * 3))));
        viewHeight = (int) (viewWidth * (((float) BOTTOM_ANY_HEIGHT / BOTTOM_ANY_WIDTH)));
        LinearLayout.LayoutParams rlp2 = (LinearLayout.LayoutParams) bottom_left.getLayoutParams();
        rlp2.width = viewWidth;
        rlp2.height = viewHeight;
        bottom_left.setLayoutParams(rlp2);

        viewWidth = (int) (width * (((float) BOTTOM_ANY_WIDTH / (BOTTOM_ANY_WIDTH * 3))));
        viewHeight = (int) (viewWidth * (((float) BOTTOM_ANY_HEIGHT / BOTTOM_ANY_WIDTH)));
        rlp = (LinearLayout.LayoutParams) bottom_center.getLayoutParams();
        rlp.width = viewWidth;
        rlp.height = viewHeight;
        bottom_center.setLayoutParams(rlp);

        viewWidth = (int) (width * (((float) BOTTOM_ANY_WIDTH / (BOTTOM_ANY_WIDTH * 3))));
        viewHeight = (int) (viewWidth * (((float) BOTTOM_ANY_HEIGHT / BOTTOM_ANY_WIDTH)));
        rlp = (LinearLayout.LayoutParams) bottom_right.getLayoutParams();
        rlp.width = viewWidth;
        rlp.height = viewHeight;
        bottom_right.setLayoutParams(rlp);
    }

    @Override
    public String getCardDisName(Card card) {
        return "pptm";
    }

    @Override
    public int getCardIcon(Card card) {
        return R.drawable.navi_card_vph_ic;
    }
}
