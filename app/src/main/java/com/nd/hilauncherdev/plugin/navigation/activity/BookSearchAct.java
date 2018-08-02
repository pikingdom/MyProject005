package com.nd.hilauncherdev.plugin.navigation.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.loader.BookLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.BookAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;

public class BookSearchAct extends BaseActivity {
	private static final int STATUS_NONE = 0;
	private static final int STATUS_LOADING = 1;

	public static final int MSG_SEARCH_BOOK_SUC = 10001;

	private Context mContext;

	private EditText searchTextV;
	private View searchV;
	private TextView infoV;
	private View infoAllV;
	private View loadingV;
	private GridView searchResL;

	private BookLoader bookLoader = new BookLoader();
	private BookAdapter bookAdapter;
	private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
	private ArrayList<Book> searchBookL = new ArrayList<>();
	private String key = "";
	private int status = 0;
	private Object lock = new Object();
	private ProgressBar progress_small_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_book_search);
		HeaderView headView = (HeaderView) findViewById(R.id.head_view);
		progress_small_title=(ProgressBar) findViewById(R.id.progress_small_title);
		progress_small_title.setBackgroundResource(CommonLauncherControl.getThemeLoadingBackgroad());
		headView.setTitle("小说搜索");
		headView.setGoBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		headView.setMenuVisibility(View.INVISIBLE);
		initControls();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(searchTextV, InputMethodManager.RESULT_UNCHANGED_SHOWN);
			}
		}, 300);
	}

	private void initControls() {
		searchTextV = (EditText) findViewById(R.id.navi_search_et);
		searchTextV.setCursorVisible(true);
		searchV = findViewById(R.id.navi_search_btn);
		searchV.setOnClickListener(mClickListener);
		infoAllV = findViewById(R.id.loading_info_all);
		infoAllV.setVisibility(View.GONE);
		infoV = (TextView) findViewById(R.id.loading_info);
		loadingV = findViewById(R.id.loading);
		searchResL = (GridView) findViewById(R.id.book_list_v);
		searchResL.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				synchronized (lock) {
					if (view.getLastVisiblePosition() != searchBookL.size() - 1) {
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

								if (!TextUtils.isEmpty(key)) {
									bookLoader.searchBookS(mContext, handler, key);
								}

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

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.navi_search_btn:
				doSearch();
				break;
			default:
				break;
			}
		}
	};

	protected void doSearch() {
		key = searchTextV.getText().toString();
		if (TextUtils.isEmpty(key)) {
			Toast.makeText(mContext, "关键词不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(findViewById(R.id.navi_search_btn).getWindowToken(), 0);
		if (!TelephoneUtil.isNetworkAvailable(mContext)) {
			infoAllV.setVisibility(View.VISIBLE);
			loadingV.setVisibility(View.GONE);
			infoV.setText("请检查网络哦！");
			infoV.setVisibility(View.VISIBLE);
			searchResL.setVisibility(View.GONE);
			return;
		}

		searchBookL.clear();
		bookLoader.curPage = 1;
		bookLoader.bookNum = -1;
		infoAllV.setVisibility(View.VISIBLE);
		loadingV.setVisibility(View.VISIBLE);
		infoV.setVisibility(View.GONE);
		searchResL.setVisibility(View.GONE);
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					status = STATUS_LOADING;
					if (bookLoader.bookNum != -1 && bookLoader.curPage * BookLoader.PAGE_BOOK_SIZE > bookLoader.bookNum) {
						status = STATUS_NONE;
						return;
					}

					bookLoader.searchBookS(mContext, handler, key);
					status = STATUS_NONE;
				}
			}
		});
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SEARCH_BOOK_SUC:
				showBookS(msg);
				break;
			default:
				break;
			}
		};
	};

	@SuppressWarnings("unchecked")
	protected void showBookS(Message msg) {
		searchBookL.addAll((ArrayList<Book>) msg.obj);
		if (searchBookL.size() > 0) {
			infoAllV.setVisibility(View.GONE);
			searchResL.setVisibility(View.VISIBLE);
			if (bookAdapter == null) {
				bookAdapter = new BookAdapter(mContext, asyncImageLoader, searchBookL, false);
				bookAdapter.layoutId = R.layout.book_list_item;
				searchResL.setAdapter(bookAdapter);
			}

			bookAdapter.notifyDataSetChanged();
		} else {
			infoAllV.setVisibility(View.VISIBLE);
			loadingV.setVisibility(View.GONE);
			infoV.setText("没有搜索到小说哦！");
			infoV.setVisibility(View.VISIBLE);
			searchResL.setVisibility(View.GONE);
		}
	}
}
