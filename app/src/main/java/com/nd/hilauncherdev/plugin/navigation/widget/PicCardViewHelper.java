package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.PicBean;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.util.BitmapUtils;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

public class PicCardViewHelper extends CardInnerViewHelperBase {

	public PicCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	@Override
	public void initCardView(View cardView, final Card card) {
		((TextView) cardView.findViewById(R.id.navi_card_base_next)).setText("下一张");
		((TextView) cardView.findViewById(R.id.navi_card_base_pre)).setText("上一张");
		cardView.findViewById(R.id.navi_card_base_pre).setVisibility(View.VISIBLE);
		((TextView) cardView.findViewById(R.id.navi_card_base_more)).setVisibility(View.GONE);
		cardView.findViewById(R.id.navi_card_base_next).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadData(navigationView.handler, card, true);
			}
		});
		cardView.findViewById(R.id.navi_card_base_pre).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CardDataLoaderFactory.getInstance().getCardLoader(card.type).handler = navigationView.handler;
				CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataS(mContext, card, true, -1);
			}
		});
//		cardView.findViewById(R.id.navi_card_base_more).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LauncherCaller.openUrl(mContext, "", CardManager.getInstance().getPicClickUrl(mContext),0,
//						CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_PIC_CARD_CLICK,
//						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_PIC_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
//			}
//		});
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
		View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_pic_card, null);
		cardInnerTotalV.addView(objCardBaseV);
		setCardView(cardInnerTotalV);
	}

	@Override
	public String getCardDisName(Card card) {
		return "jrmt";
	}

	@Override
	public int getCardIcon(Card card) {
		return R.drawable.navi_card_pic_ic;
	}

	@Override
	public void showDataS(final NavigationView2 navigationView, final Card card, Message msg) {
		View view = CardViewFactory.getInstance().getView(card);
		if (view == null) {
			return;
		}

		final int width = ScreenUtil.getCurrentScreenWidth(mContext) - ScreenUtil.dip2px(mContext, 35);
		final ImageView ic = ((ImageView) view.findViewById(R.id.pic_v));
		ic.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
		@SuppressWarnings("unchecked")
		ArrayList<PicBean> objL = (ArrayList<PicBean>) msg.obj;
		if (objL.size() == 0) {
			setLoadingStat(ic, width);
			return;
		}
		final PicBean picBean = objL.get(0);
		final TextView infoV = ((TextView) view.findViewById(R.id.info_v));
		ic.setVisibility(View.VISIBLE);
		infoV.setVisibility(View.GONE);
		Drawable cachedDrawable = navigationView.asyncImageLoader.loadDrawable(picBean.imgUrl, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
				if (imageDrawable != null && imageUrl.equals(picBean.imgUrl)) {
					ic.setImageDrawable(imageDrawable);
					LayoutParams layoutParams = (LayoutParams) ic.getLayoutParams();
					layoutParams.width = width;
					layoutParams.height = BitmapUtils.getHeightByWidth(imageDrawable, width);
					ic.setLayoutParams(layoutParams);
				} else if (imageDrawable == null && imageUrl.equals(picBean.imgUrl)) {
					ic.setVisibility(View.GONE);
					LayoutParams layoutParams = (LayoutParams) ic.getLayoutParams();
					layoutParams.width = width;
					layoutParams.height = 800 * width / 640;
					infoV.setLayoutParams(layoutParams);
					infoV.setVisibility(View.VISIBLE);
				}
			}
		});
		if (cachedDrawable != null) {
			ic.setImageDrawable(cachedDrawable);
			LayoutParams layoutParams = (LayoutParams) ic.getLayoutParams();
			layoutParams.width = width;
			layoutParams.height = BitmapUtils.getHeightByWidth(cachedDrawable, width);
			ic.setLayoutParams(layoutParams);
		} else {
			setLoadingStat(ic, width);
		}

		view.findViewById(R.id.pic_v).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LauncherCaller.openUrl(mContext, "", picBean.clickUrl,0,
						CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_PIC_CARD_CLICK,
						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_PIC_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
				
			}
		});
	}

	private void setLoadingStat(ImageView ic, int width) {
		ic.setImageResource(CommonLauncherControl.getLoadingBackgroad());
		LayoutParams layoutParams = (LayoutParams) ic.getLayoutParams();
		int loadingDimen = ScreenUtil.dip2px(mContext, 50);
		int loadingMargin = (width - loadingDimen) / 2;
		layoutParams.width = loadingDimen;
		layoutParams.height = (int) (width * 800.0 / 640.0);
		layoutParams.leftMargin = loadingMargin;
		layoutParams.rightMargin = loadingMargin;
		ic.setLayoutParams(layoutParams);
	}
}
