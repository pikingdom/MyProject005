package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.BookListAct;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.loader.BookLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

public class BookCardViewHelper extends CardInnerViewHelperBase {

	private final static int ONE_BOOK_HEIGHT = 85;
	public final static int CARD_BOOK_NUM = 3;

	private LinearLayout bookLV;
	private View infoV;

	public BookCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	@Override
	public void initCardView(View cardView, Card card) {
		cardView.findViewById(R.id.navi_card_base_next).setVisibility(View.GONE);
		((TextView) cardView.findViewById(R.id.navi_card_base_more)).setText("更多小说");
		cardView.findViewById(R.id.navi_card_base_more).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BookListAct.class);
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_DISTRIBUTE_EFFECT, "gdxs");
				mContext.startActivity(intent);
			}
		});
	}

	@Override
	public void loadData(final Handler handler, final Card card, final boolean needRefreshData) {
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				CardDataLoaderFactory.getInstance().getCardLoader(card.type).handler = handler;
				CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataS(mContext, card, needRefreshData, CARD_BOOK_NUM);
			}
		});
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
		View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_book_card, null);
		cardInnerTotalV.addView(objCardBaseV);
		setCardView(cardInnerTotalV);
	}

	@Override
	public void showDataS(final NavigationView2 navigationView, Card card, Message msg) {
		@SuppressWarnings("unchecked")
		ArrayList<Book> bookList = (ArrayList<Book>) msg.obj;
		View view = CardViewFactory.getInstance().getView(card);
		if (view == null || bookList == null) {
			return;
		}

		this.navigationView = navigationView;
		bookLV = (LinearLayout) view.findViewById(R.id.navi_book_list_v);
		infoV = view.findViewById(R.id.book_card_info);
		if (bookList.size() > 0) {
			infoV.setVisibility(View.GONE);
			bookLV.setVisibility(View.VISIBLE);
			bookLV.removeAllViews();
			for (int i = 0; i < bookList.size(); i++) {
				Book book = bookList.get(i);
				View bookV = getABookItemView(book, i);
				bookLV.addView(bookV);
			}

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bookLV.getLayoutParams();
			layoutParams.height = (int) bookList.size() * ScreenUtil.dip2px(mContext, ONE_BOOK_HEIGHT);
			bookLV.setLayoutParams(layoutParams);
		} else {
			infoV.setVisibility(View.VISIBLE);
			bookLV.setVisibility(View.GONE);
			infoV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, BookListAct.class);
					intent.putExtra("index", BookListAct.PAGE_RECOMMEND);
					mContext.startActivity(intent);
				}
			});
		}
	}

	@Override
	public String getCardDisName(Card card) {
		return "xsyd";
	}

	@Override
	public int getCardIcon(Card card) {
		return R.drawable.navi_card_book_ic;
	}

	View getABookItemView(final Book item, final int ind) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.book_card_item, null);
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.allV = convertView.findViewById(R.id.book_item_all);
		viewHolder.imageView = (ImageView) convertView.findViewById(R.id.book_item_ic);
		viewHolder.nameV = (TextView) convertView.findViewById(R.id.book_item_name);
		viewHolder.statusV = (TextView) convertView.findViewById(R.id.book_item_chapter_status);
		viewHolder.imageView.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
		Drawable cachedImage = navigationView.asyncImageLoader.loadDrawable(item.icon, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
				if (imageDrawable != null && viewHolder.imageView != null && imageUrl.equals(item.icon)) {
					viewHolder.imageView.setImageDrawable(imageDrawable);
				}
			}
		});
		if (cachedImage != null) {
			viewHolder.imageView.setImageDrawable(cachedImage);
		}

		if (item.bookId.equals("367201121")) {
			viewHolder.imageView.setImageResource(R.drawable.book367201121);
		} else if (item.bookId.equals("369410031")) {
			viewHolder.imageView.setImageResource(R.drawable.book369410031);
		} else if (item.bookId.equals("378327121")) {
			viewHolder.imageView.setImageResource(R.drawable.book378327121);
		}

		viewHolder.nameV.setText(item.name);
		String status = "";
		if (item.type == 0) {
			status += "已完结；";
		} else {
			status += "未完结；";
		}

		status += "共" + item.num + "章";
		viewHolder.statusV.setText(status);
		viewHolder.allV.setTag(item.url);
		viewHolder.allV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BookLoader.toRead(mContext, item);
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_STORY_CARD_DISTRIBUTE_EFFECT, String.valueOf(ind));
			}
		});

		return convertView;
	}

	private class ViewHolder {
		View allV;
		ImageView imageView;
		TextView nameV;
		TextView statusV;
	}
}
