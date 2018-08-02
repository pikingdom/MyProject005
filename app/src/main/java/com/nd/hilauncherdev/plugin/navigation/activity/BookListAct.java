package com.nd.hilauncherdev.plugin.navigation.activity;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.loader.BookLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.BookAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nd.hilauncherdev.plugin.navigation.widget.MyPhoneViewPager;
import com.nd.hilauncherdev.plugin.navigation.widget.MyPhoneViewPagerTab;
import com.nostra13.universalimageloader.ex.ImageCallback;

public class BookListAct extends BaseActivity {

	private static final int STATUS_NONE = 0;
	private static final int STATUS_LOADING = 1;

	public final static int PAGE_RECOMMEND = 0;
	public final static int PAGE_MY = 1;

	private Context mContext;
	private View recommendAllV;
	private View myAllV;
	private TextView totalNumV;
	private TextView editV;
	private GridView myGrid;
	private GridView recommendGrid;
	private BookAdapter myBookSAdapter;
	private TextView infoV;
	private View infoAllV;
	private View loadingV;
	HeaderView headView;
	private ArrayList<View> viewContainter = new ArrayList<View>();

	private BookImgAdapter bookImgAdapter;
	private BookLoader bookLoader;
	private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
	private ArrayList<Book> recommendBookL = new ArrayList<>();
	private int status = 0;
	private Object lock = new Object();
	private boolean isShowOp = false;
	private ProgressBar progress_small_title;
	MyPhoneViewPager myPhoneViewPager;
	MyPhoneViewPagerTab myPhoneViewPagerTab;
	private String[] titleResIds = {"推荐","我的"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_book_list);
		headView = (HeaderView) findViewById(R.id.head_view);
		headView.setTitle("小说");
		headView.setGoBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		headView.setMenuVisibility(View.VISIBLE);
		headView.setMenuListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BookSearchAct.class);
				mContext.startActivity(intent);
			}
		});
		headView.setMenuImageResource(R.drawable.blue_search_btn);
		headView.setBackgroundRes(R.drawable.common_head_bg_without_line);
		bookLoader = new BookLoader();
		initControls();
		loadRecommendData();
		loadMyData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isShowOp = false;
		editV.setText("编辑");
		if (myBookSAdapter != null) {
			myBookSAdapter.isShowOp = isShowOp;
			myBookSAdapter.notifyDataSetChanged();
		}

		if (CardManager.getInstance().getIsMyBookChanged(mContext)) {
			CardManager.getInstance().setIsMyBookChanged(mContext, false);
			loadMyData();
		}
	}

	private void loadRecommendData() {
		infoAllV.setVisibility(View.VISIBLE);
		loadingV.setVisibility(View.VISIBLE);
		infoV.setVisibility(View.GONE);
		recommendGrid.setVisibility(View.GONE);
		if (TelephoneUtil.isNetworkAvailable(mContext)) {
			ThreadUtil.executeMore(new Runnable() {
				@Override
				public void run() {
					status = STATUS_LOADING;
					if (bookLoader.bookNum != -1 && bookLoader.curPage * BookLoader.PAGE_BOOK_SIZE > bookLoader.bookNum) {
						status = STATUS_NONE;
						return;
					}

					bookLoader.loadRecommendBookS(mContext, handler);
					status = STATUS_NONE;
				}
			});
		} else {
			infoAllV.setVisibility(View.VISIBLE);
			loadingV.setVisibility(View.GONE);
			infoV.setVisibility(View.VISIBLE);
			infoV.setText("请检查一下网络哦!");
		}
	}

	private void loadMyData() {
		bookLoader.handler = handler;
		bookLoader.loadDataS(mContext, new Card(), false, Integer.MAX_VALUE);
	}

	private void initControls() {

		recommendAllV = LayoutInflater.from(this).inflate(R.layout.view_recommend_book, null);
		progress_small_title=(ProgressBar) recommendAllV.findViewById(R.id.progress_small_title);
		progress_small_title.setBackgroundResource(CommonLauncherControl.getThemeLoadingBackgroad());
		infoV = (TextView) recommendAllV.findViewById(R.id.loading_info);
		infoAllV = recommendAllV.findViewById(R.id.loading_info_all);
		loadingV = recommendAllV.findViewById(R.id.loading);
		recommendGrid = (GridView) recommendAllV.findViewById(R.id.book_recommend_list_v);
		viewContainter.add(recommendAllV);
		myAllV = LayoutInflater.from(this).inflate(R.layout.view_my_book, null);
		viewContainter.add(myAllV);
		totalNumV = (TextView) myAllV.findViewById(R.id.book_my_total);
		editV = (TextView) myAllV.findViewById(R.id.book_my_edit);
		editV.setOnClickListener(mClickListener);
		myGrid = (GridView) myAllV.findViewById(R.id.book_list_v);
		myPhoneViewPagerTab = (MyPhoneViewPagerTab) findViewById(R.id.container_pagertab);
		myPhoneViewPager = (MyPhoneViewPager) findViewById(R.id.container_pager);
		initVP();
		initRecommendV();
		if (getIntent().hasExtra("index")) {
			int index = getIntent().getIntExtra("index", PAGE_RECOMMEND);
			if (index == PAGE_MY) {
				setMyBg();
			} else {
				setRecommendBg();
			}
			myPhoneViewPager.setInitTab(index);
			myPhoneViewPagerTab.setInitTab(index);
		} else {
			myPhoneViewPager.setInitTab(PAGE_RECOMMEND);
			myPhoneViewPagerTab.setInitTab(PAGE_RECOMMEND);
			setRecommendBg();
		}
	}

	private void initRecommendV() {
		recommendGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				synchronized (lock) {
					if (view.getLastVisiblePosition() != recommendBookL.size() - 1) {
						return;
					}
					ThreadUtil.executeMore(new Runnable() {
						@Override
						public void run() {
							if (status == STATUS_NONE) {
								status = STATUS_LOADING;
								if (bookLoader.bookNum != -1 && bookLoader.curPage * BookLoader.PAGE_BOOK_SIZE > bookLoader.bookNum) {
									status = STATUS_NONE;
									return;
								}

								bookLoader.loadRecommendBookS(mContext, handler);
								status = STATUS_NONE;
							}
						}
					});
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void initVP() {
		myPhoneViewPagerTab.addTitle(titleResIds);
		myPhoneViewPagerTab.setViewpager(myPhoneViewPager);
		myPhoneViewPagerTab.setUsedAsSecondTitle();
		myPhoneViewPager.setTab(myPhoneViewPagerTab);
		myPhoneViewPager.addView(recommendAllV);
		myPhoneViewPager.addView(myAllV);
		myPhoneViewPagerTab.setInitTab(PAGE_RECOMMEND);
		myPhoneViewPager.setInitTab(PAGE_RECOMMEND);
	}

	OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.book_my_edit:
				doEditMyBook();
				break;
			default:
				break;
			}
		}
	};

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CardViewHelper.MSG_LOAD_MY_BOOK_SUC:
				showMyBookS(msg);
				break;
			case CardViewHelper.MSG_LOAD_RECOMMEND_BOOK_SUC:
				showRecommendBookS(msg);
				break;
			default:
				break;
			}
		};
	};



	protected void doEditMyBook() {
		if (!isShowOp) {
			editV.setText("取消编辑");
			isShowOp = true;
			myBookSAdapter.isShowOp = isShowOp;
			myBookSAdapter.notifyDataSetChanged();
		} else {
			editV.setText("编辑");
			isShowOp = false;
			myBookSAdapter.isShowOp = isShowOp;
			myBookSAdapter.notifyDataSetChanged();
		}
	}

	@SuppressWarnings("unchecked")
	protected void showRecommendBookS(Message msg) {
		recommendBookL.addAll((ArrayList<Book>) msg.obj);
		if (recommendBookL.size() > 0) {
			infoAllV.setVisibility(View.GONE);
			recommendGrid.setVisibility(View.VISIBLE);
			if (bookImgAdapter == null) {
				bookImgAdapter = new BookImgAdapter(mContext, asyncImageLoader);
				recommendGrid.setAdapter(bookImgAdapter);
			}

			bookImgAdapter.notifyDataSetChanged();
		} else {
			infoAllV.setVisibility(View.VISIBLE);
			loadingV.setVisibility(View.GONE);
			infoV.setVisibility(View.VISIBLE);
			infoV.setText("没有小说哦!");
			recommendGrid.setVisibility(View.GONE);
		}
	}

	@SuppressWarnings("unchecked")
	protected void showMyBookS(Message msg) {
		ArrayList<Book> bookList = (ArrayList<Book>) msg.obj;
		int size = bookList.size();
		if (size > 0) {
			totalNumV.setText("共" + bookList.size() + "本");
			editV.setVisibility(View.VISIBLE);
		} else {
			totalNumV.setText("您未添加书籍");
			editV.setVisibility(View.GONE);
		}

		if (myBookSAdapter == null) {
			myBookSAdapter = new BookAdapter(mContext, asyncImageLoader, bookList, true);
			myBookSAdapter.layoutId = R.layout.book_list_item;
			myBookSAdapter.handler = handler;
			myBookSAdapter.bookLoader = bookLoader;
		}

		myBookSAdapter.isShowOp = isShowOp;

		myBookSAdapter.list = bookList;
		myGrid.setAdapter(myBookSAdapter);
		myBookSAdapter.notifyDataSetChanged();
	}



	@SuppressWarnings("deprecation")
	private void setMyBg() {
		myPhoneViewPager.setInitTab(PAGE_MY);
		myPhoneViewPagerTab.setInitTab(PAGE_MY);
	}

	@SuppressWarnings("deprecation")
	private void setRecommendBg() {
		myPhoneViewPager.setInitTab(PAGE_RECOMMEND);
		myPhoneViewPagerTab.setInitTab(PAGE_RECOMMEND);
	}

	private class BookImgAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater layoutInflater;

		private AsyncImageLoader asyncImageLoader;

		private class ViewHolder {
			View allV;
			ImageView imageView;
			TextView nameV;
			TextView authorV;
			TextView infoV;
		}

		public BookImgAdapter(Context context, AsyncImageLoader asyncImageLoader) {
			mContext = context;
			layoutInflater = LayoutInflater.from(mContext);
			this.asyncImageLoader = asyncImageLoader;
		}

		@Override
		public int getCount() {
			return recommendBookL.size();
		}

		@Override
		public Book getItem(int position) {
			if (position < recommendBookL.size()) {
				return recommendBookL.get(position);
			}

			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try {
				final Book item = getItem(position);
				if (null == item) {
					return new View(mContext);
				}

				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.book_recommend_item, null);
				}

				if (convertView.getTag() == null) {
					ViewHolder holder = new ViewHolder();
					holder.allV = convertView.findViewById(R.id.book_item_all);
					holder.imageView = (ImageView) convertView.findViewById(R.id.book_item_ic);
					holder.authorV = (TextView) convertView.findViewById(R.id.book_item_author);
					holder.infoV = (TextView) convertView.findViewById(R.id.book_item_info);
					holder.nameV = (TextView) convertView.findViewById(R.id.book_item_name);
					convertView.setTag(holder);
				}

				final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
				viewHolder.imageView.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
				Drawable cachedImage = asyncImageLoader.loadDrawable(item.icon, new ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
						if (imageDrawable != null && imageUrl.equals(item.icon)) {
							viewHolder.imageView.setImageDrawable(imageDrawable);
						}
					}
				});
				if (cachedImage != null) {
					viewHolder.imageView.setImageDrawable(cachedImage);
				}

				viewHolder.nameV.setText(item.name);
				viewHolder.infoV.setText(item.recommend);
				viewHolder.allV.setTag(item.url);
				viewHolder.allV.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, BookDetailAct.class);
						intent.putExtra("bookId", item.bookId);
						mContext.startActivity(intent);
						BookListAct.this.submitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_STORY_CARD_RECOMMEND_COLUMN_DISTRIBUTE_EFFECT, item.bookId);
					}
				});
				viewHolder.authorV.setText("作者: " + item.author);
				return convertView;
			} catch (Exception e) {
				return new View(mContext);
			}

		}
	}
}
