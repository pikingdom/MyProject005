package com.nd.hilauncherdev.plugin.navigation.activity;

import java.util.ArrayList;
import java.util.HashSet;
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
import android.widget.ListView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.BitmapUtils;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.CardAddAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nostra13.universalimageloader.ex.ImageCallback;

/**
 * 添加卡片<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CardAddSubAct extends BaseActivity {

	private Context mContext;
	private ListView mCardList;
	private CardAddAdapter mCardAdapter;
	private View mCardAllAddedV;
	private View cardAddedNoticeV;
	private View viewAddedCardV;
	private ImageView cardIV;
	private TextView cardDescV;

	private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
	private int cardType;
	private boolean isFromNewCardNotify = false;
	private Card mCard;

	//从零屏主界面的推荐添加点击进入，需要打点
	private boolean isViaNotify = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		cardType = getIntent().getIntExtra("cardType", 0);
		isViaNotify =getIntent().getBooleanExtra("viaNotify",false);
		isFromNewCardNotify = getIntent().getBooleanExtra(Card.IS_FROM_NEW_CARD_NOTIFY, false);
		setContentView(R.layout.navigation_card_sub_add);
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
		mCardList = (ListView) findViewById(R.id.navi_card_list);
		mCardAllAddedV = findViewById(R.id.navi_card_all_added);
		cardAddedNoticeV = findViewById(R.id.navi_card_added_notice);
		cardAddedNoticeV.setVisibility(View.GONE);
		viewAddedCardV = findViewById(R.id.navi_view_added_card);
		cardAddedNoticeV.setOnClickListener(mClickListener);
		mCard = new Card();
		mCard.type = cardType;
		initView();
	}

	private void initView() {
		cardDescV = (TextView) findViewById(R.id.navi_card_detail_desc);
		cardIV = (ImageView) findViewById(R.id.navi_card_detail_ic);
		cardIV.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
		if (mCard.type == CardManager.CARD_STAR_TYPE) {
			mCard.bigImgUrl = UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/03/11/60a33728f3184b399e657ee9fccebb52.png";
		}

		final int width = ScreenUtil.getCurrentScreenWidth(mContext) - ScreenUtil.dip2px(mContext, 20);
		Drawable drawable = asyncImageLoader.loadDrawable(mCard.bigImgUrl, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
				if (imageDrawable != null && imageUrl.equals(mCard.bigImgUrl)) {
					cardIV.setImageDrawable(imageDrawable);
					LayoutParams layoutParams = (LayoutParams) cardIV.getLayoutParams();
					layoutParams.width = width;
					layoutParams.height = BitmapUtils.getHeightByWidth(imageDrawable, width);
					cardIV.setLayoutParams(layoutParams);
				}
			}
		});
		if (drawable != null) {
			cardIV.setImageDrawable(drawable);
			LayoutParams layoutParams = (LayoutParams) cardIV.getLayoutParams();
			layoutParams.width = width;
			layoutParams.height = BitmapUtils.getHeightByWidth(drawable, width);
			cardIV.setLayoutParams(layoutParams);
		}
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.navi_card_added_notice:
				finish();
				break;

			default:
				break;
			}
		}
	};

	private void initCard() {
		ArrayList<Card> cardS = CardManager.getInstance().getCardSCanBeAddedByType(mContext, cardType);
		ArrayList<Card> curCardL = CardManager.getInstance().getCurrentCardS(mContext);
		HashSet<Integer> curCardS = new HashSet<Integer>();
		for (Card card : curCardL) {
			curCardS.add(card.id);
		}

		for (Card card : cardS) {
			if (curCardS.contains(card.id)) {
				card.isAdded = true;
			} else {
				card.isAdded = false;
			}
		}

		if (cardS.size() > 0) {
			cardDescV.setText(cardS.get(0).desc);
		}

		initSubCardDesc(cardS);
		mCardAdapter = new CardAddAdapter(CardAddSubAct.this, cardS);
		mCardAdapter.isViaNotify = isViaNotify;
		mCardAdapter.isFromSubAdd = true;
		mCardList.setAdapter(mCardAdapter);
		mCardAdapter.notifyDataSetChanged();
		if (cardS.size() > 0) {
			mCardList.setVisibility(View.VISIBLE);
			mCardAllAddedV.setVisibility(View.GONE);
		} else {
			mCardList.setVisibility(View.GONE);
			mCardAllAddedV.setVisibility(View.VISIBLE);
		}
	}

	private void initSubCardDesc(ArrayList<Card> cardS) {
		for (Card card : cardS) {
			switch (card.id) {
			case 900:
				card.desc = "3.21 - 4.19";
				break;
			case 901:
				card.desc = "4.20 - 5.20";
				break;
			case 902:
				card.desc = "5.21 - 6.21";
				break;
			case 903:
				card.desc = "6.22 - 7.22";
				break;
			case 904:
				card.desc = "7.23 - 8.22";
				break;
			case 905:
				card.desc = "8.23 - 9.22";
				break;
			case 906:
				card.desc = "9.23 - 10.23";
				break;
			case 907:
				card.desc = "10.24 - 11.22";
				break;
			case 908:
				card.desc = "11.23 - 12.21";
				break;
			case 909:
				card.desc = "12.22 - 1.19";
				break;
			case 910:
				card.desc = "1.20 - 2.18";
				break;
			case 911:
				card.desc = "2.19 - 3.20";
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		initCard();
		super.onResume();
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

	protected void toCardAdd() {
		if (isFromNewCardNotify) {
			finish();
			return;
		}

		Intent intent = new Intent();
		intent.setClass(mContext, CardAddActivity.class);
		mContext.startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		toCardAdd();
		super.onBackPressed();
	}
}
