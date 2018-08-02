package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.loader.BookLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.Map;

public class BookDetailAct extends BaseActivity {

	public static final int MSG_LOAD_BOOK_SUC = 100;
	public static final int ONE_CHAPTER_HEIGHT = 20;

	private Context mContext;
	private ImageView ic;
	private TextView nameV;
	private TextView authorV;
	private TextView statusV;
	private TextView abstractV;
	private LinearLayout chapterL;
	private View pageInfoAllV;
	private TextView pageInfoV;
	private View loadingV;
	private View allV;
	private View scrollV;
	private View toReadV;
	private TextView addV;

	private String bookId = "";
	private Book book;
	private BookLoader bookLoader;
	private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
	private boolean isAdded = false;
	private ProgressBar progress_small_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_book_detail);
		HeaderView headView = (HeaderView) findViewById(R.id.head_view);
		progress_small_title=(ProgressBar) findViewById(R.id.progress_small_title);
		progress_small_title.setBackgroundResource(CommonLauncherControl.getThemeLoadingBackgroad());
		headView.setTitle("小说详情");
		headView.setGoBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		headView.setMenuVisibility(View.INVISIBLE);
		bookId = getIntent().getStringExtra("bookId");
		if (TextUtils.isEmpty(bookId)) {
			Toast.makeText(mContext, "获取小说信息失败", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		bookLoader = new BookLoader();
		initControls();
		pageInfoAllV.setVisibility(View.VISIBLE);
		allV.setVisibility(View.GONE);
		scrollV.setVisibility(View.GONE);
		if (TelephoneUtil.isNetworkAvailable(mContext)) {
			ThreadUtil.executeMore(new Runnable() {
				@Override
				public void run() {
					bookLoader.loadBook(mContext, bookId, handler);
				}
			});
		} else {
			pageInfoAllV.setVisibility(View.VISIBLE);
			pageInfoV.setVisibility(View.VISIBLE);
			loadingV.setVisibility(View.GONE);
			pageInfoV.setText("请检查一下网络哦!");
			allV.setVisibility(View.GONE);
			scrollV.setVisibility(View.GONE);
		}
	}

	private void initControls() {
		ic = (ImageView) findViewById(R.id.book_ic);
		nameV = (TextView) findViewById(R.id.book_name);
		authorV = (TextView) findViewById(R.id.book_author);
		statusV = (TextView) findViewById(R.id.book_chapter_status);
		abstractV = (TextView) findViewById(R.id.abstract_v);
		chapterL = (LinearLayout) findViewById(R.id.chapter_l);
		addV = (TextView) findViewById(R.id.book_add);
		addV.setOnClickListener(mClickListener);
		toReadV = findViewById(R.id.book_read);
		toReadV.setOnClickListener(mClickListener);
		pageInfoAllV = findViewById(R.id.book_info_all);
		loadingV = findViewById(R.id.loading);
		pageInfoV = (TextView) findViewById(R.id.book_page_info);
		allV = findViewById(R.id.book_all);
		scrollV = findViewById(R.id.scroll_v);
		ic.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.book_add:
				addBook();
				break;
			case R.id.book_read:
				toRead();
				break;
			default:
				break;
			}
		}
	};

	protected void addBook() {
		if (book == null) {
			return;
		}

		if (isAdded) {
			return;
		}

		BookLoader.addToMyBook(mContext, book);
		Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
		CardManager.getInstance().setIsCardListChanged(mContext, true);
		CardManager.getInstance().setIsMyBookChanged(mContext, true);
		isAdded = true;
		if (isAdded) {
			addV.setText("已添加");
		}
		BookDetailAct.this.submitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_STORY_CARD_ADD_BOOKRACK, book.bookId);
	}

	protected void toRead() {
		if (book == null) {
			return;
		}

		BookLoader.toRead(mContext, book);
		BookDetailAct.this.submitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_STORY_CARD_ONLINE_READ, book.bookId);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOAD_BOOK_SUC:
				showBook(msg);
				break;
			default:
				break;
			}
		}
	};

	protected void showBook(Message msg) {
		book = (Book) msg.obj;
		if (TextUtils.isEmpty(book.bookId)) {
			Toast.makeText(mContext, "获取小说信息失败", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		Drawable cachedImage = asyncImageLoader.loadDrawable(book.icon, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
				if (imageDrawable != null && imageUrl.equals(book.icon)) {
					ic.setImageDrawable(imageDrawable);
				}
			}
		});
		if (cachedImage != null) {
			ic.setImageDrawable(cachedImage);
		}

		nameV.setText(book.name);
		authorV.setText("作者：" + book.author);
		String status = "";
		if (book.type == 0) {
			status += "已完结；";
		} else {
			status += "未完结；";
		}

		status += "共" + book.num + "章";
		statusV.setText(status);
		abstractV.setText(book.abstractStr);
		for (String chapter : book.chapters) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.item_text_l, null);
			((TextView) view.findViewById(R.id.item_text)).setText(chapter);
			chapterL.addView(view);
		}

		pageInfoAllV.setVisibility(View.GONE);
		allV.setVisibility(View.VISIBLE);
		scrollV.setVisibility(View.VISIBLE);
		setAdd();
	}

	private void setAdd() {
		isAdded = BookLoader.isBookExist(book);
		if (isAdded) {
			addV.setText("已添加");
		}
	}
}
