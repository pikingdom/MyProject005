package com.nd.hilauncherdev.plugin.navigation.activity;

import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.BitmapUtils;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nostra13.universalimageloader.ex.ImageCallback;

/**
 * 卡片详情<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CardDetailActivity extends BaseActivity {

	private Context mContext;
	private Card mCard;
	private int mCardId;
	private boolean isAdded = false;

	private ImageView cardDescIV;
	private TextView mCardAddV;
	private View cardAddedNoticeV;
	private View viewAddedCardV;
	private TextView descV;
	private boolean isFromSubAdd = false;
	private boolean isFromNewCardNotify = false;
	private int cardType;
	//从零屏主界面的推荐添加点击进入，需要打点
	private boolean isViaNotify = false;

	private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		isFromSubAdd = getIntent().getBooleanExtra(Card.IS_FROM_SUB_ADD, false);
		isFromNewCardNotify = getIntent().getBooleanExtra(Card.IS_FROM_NEW_CARD_NOTIFY, false);
		cardType = getIntent().getIntExtra("cardType", 0);
		isViaNotify =getIntent().getBooleanExtra("viaNotify",false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.navigation_card_detail);
//		if (CommonGlobal.isDianxinLauncher(mContext)) {
//			AsyncImageLoader.initImageLoaderConfig(mContext);
//		}
		HeaderView headView = (HeaderView) findViewById(R.id.head_view);
		headView.setTitle("添加卡片");
		headView.setGoBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toCardAdd();
			}
		});
		mCardId = getIntent().getIntExtra("cardId", 0);
		initCard();
		setCardAddState();
		initView();
		cardAddedNoticeV = findViewById(R.id.navi_card_added_notice);
		cardAddedNoticeV.setVisibility(View.GONE);
		viewAddedCardV = findViewById(R.id.navi_view_added_card);
		viewAddedCardV.setOnClickListener(mClickListener);
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.navi_view_added_card:
				finish();
				break;

			default:
				break;
			}
		}
	};

	protected void toCardAdd() {
		Intent intent = new Intent();
		if (isFromNewCardNotify) {
			finish();
			return;
		}

		if (isFromSubAdd) {
			intent.setClass(mContext, CardAddSubAct.class);
			intent.putExtra("cardType", cardType);
		} else {
			intent.setClass(mContext, CardAddActivity.class);
		}

		mContext.startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		toCardAdd();
		super.onBackPressed();
	}

	private void setCardAddState() {
		mCardAddV = (TextView) findViewById(R.id.navi_card_add_v);
		ArrayList<Card> cardS = CardManager.getInstance().getCurrentCardS(mContext);
		for (Card card : cardS) {
			if (card.id == mCard.id) {
				isAdded = true;
				mCardAddV.setText("已添加");
				mCardAddV.setBackgroundResource(R.drawable.navi_card_added_btn);
				break;
			}
		}

		mCardAddV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isAdded) {
					if(isViaNotify){
						CardViewHelper.submitDefaultNotifyCancleAndAdd(mContext,mCard,true);
					}
					CardManager.getInstance().addCard(mContext, mCard);
					mCardAddV.setText("已添加");
					mCardAddV.setBackgroundResource(R.drawable.navi_card_added_btn);
					CardManager.getInstance().addToAddedCardS(mContext, mCard.id);
					isAdded = true;
					CardManager.getInstance().setIsCardListChanged(mContext, true);
					showCardAddedNotice();
				}
			}
		});
	}

	private void initView() {
		cardDescIV = (ImageView) findViewById(R.id.navi_card_detail_desc);
		cardDescIV.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
		CardDataLoader dataLoader = CardDataLoaderFactory.getInstance().getCardLoader(mCard.type);
		if(dataLoader != null){
			mCard.bigImgUrl = dataLoader.getCardDetailImageUrl();
		}
		final int width = ScreenUtil.getCurrentScreenWidth(mContext) - ScreenUtil.dip2px(mContext, 20);
		Drawable drawable = asyncImageLoader.loadDrawable(mCard.bigImgUrl, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
				if (imageDrawable != null && imageUrl.equals(mCard.bigImgUrl)) {
					cardDescIV.setImageDrawable(imageDrawable);
					LayoutParams layoutParams = (LayoutParams) cardDescIV.getLayoutParams();
					layoutParams.width = width;
					layoutParams.height = BitmapUtils.getHeightByWidth(imageDrawable, width);
					cardDescIV.setLayoutParams(layoutParams);
				}
			}
		});
		if (drawable != null) {
			cardDescIV.setImageDrawable(drawable);
			LayoutParams layoutParams = (LayoutParams) cardDescIV.getLayoutParams();
			layoutParams.width = width;
			layoutParams.height = BitmapUtils.getHeightByWidth(drawable, width);
			cardDescIV.setLayoutParams(layoutParams);
		}

		descV = (TextView) findViewById(R.id.navi_card_add_desc);
		descV.setText(mCard.desc);
	}

	public void showCardAddedNotice() {
		cardAddedNoticeV.setVisibility(View.VISIBLE);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				cardAddedNoticeV.setVisibility(View.GONE);
			}
		}, 5000);
	}

	public Handler handler = new Handler() {

	};

	private void initCard() {
		ArrayList<Card> cardS = CardManager.getInstance().getAllCardS(mContext);
		for (Card card : cardS) {
			if (card.id == mCardId) {
				mCard = card;
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
