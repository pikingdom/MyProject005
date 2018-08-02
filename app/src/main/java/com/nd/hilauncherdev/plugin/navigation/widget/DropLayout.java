package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;

public class DropLayout extends LinearLayout {

	private ScrollView sc;
	private LayoutInflater inflater;
	private LinearLayout header;
	private int headerHeight;
	private int lastHeaderPadding;
	private boolean isBack;
	private int headerState = DONE;
	private RefreshCallBack callBack;
	ScaleAnimation scaleAnimation;
	TranslateAnimation translateAnimation;
	@SuppressWarnings("unused")
	private View tailV;
	private View clothV;
	@SuppressWarnings("unused")
	private View bodyV;
	private TextView updateRecord;
	static final private int RELEASE_To_REFRESH = 0;
	static final private int PULL_To_REFRESH = 1;
	static final private int REFRESHING = 2;
	static final private int DONE = 3;
	private boolean hasUpdate = false;
	public DropLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sc = (ScrollView) inflater.inflate(R.layout.widget_main_sc, null);
		header = (LinearLayout) inflater.inflate(R.layout.drag_drop_header, null);
//		tailV = header.findViewById(R.id.super_man_tail);
//		clothV = header.findViewById(R.id.super_man_cloth);
		bodyV = header.findViewById(R.id.super_man_body);
		updateRecord = (TextView)header.findViewById(R.id.lastUpdateTime);
		initAnim();
		measureView(header);
		headerHeight = header.getMeasuredHeight();
		lastHeaderPadding = (-1 * headerHeight);
		header.setPadding(0, lastHeaderPadding, 0, 0);
		header.invalidate();
		this.addView(header, 0);
		this.addView(sc, 1);

		sc.setOnTouchListener(new OnTouchListener() {
			private int beginY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						// Log.e("getY beginY", event.getRawY() + "  " + beginY +
						// "   " + lastHeaderPadding);
						if (beginY == 0) {
							beginY = (int) event.getRawY();
						}
						if(!hasUpdate){
							updateTimeText();
							hasUpdate = true;
						}
						if ((sc.getScrollY() == 0 || lastHeaderPadding > (-1 * headerHeight)) && headerState != REFRESHING) {
							int interval = (int) (event.getRawY() - beginY);
							if (interval > 0) {
								interval = interval / 2;
								lastHeaderPadding = interval + (-1 * headerHeight);
								if(lastHeaderPadding <=40)
									header.setPadding(0, lastHeaderPadding, 0, 0);
								else{
									header.setPadding(0, 40, 0, lastHeaderPadding-40);
								}
								if (lastHeaderPadding > 40) {
									headerState = RELEASE_To_REFRESH;
									if (!isBack) {
										isBack = true;
										changeHeaderViewByState();
									}
								} else {
									headerState = PULL_To_REFRESH;
									changeHeaderViewByState();
								}
							}
						}else {
							beginY = 0;
						}
						break;
					case MotionEvent.ACTION_DOWN:
						beginY = (int) event.getRawY();
						if(!hasUpdate){
							updateTimeText();
							hasUpdate = true;
						}
						break;
					case MotionEvent.ACTION_UP:
						beginY = 0;
						if (headerState != REFRESHING) {
							switch (headerState) {
								case DONE:
									break;
								case PULL_To_REFRESH:
									headerState = DONE;
									lastHeaderPadding = -1 * headerHeight;
									header.setPadding(0, lastHeaderPadding, 0, 0);
									changeHeaderViewByState();
									break;
								case RELEASE_To_REFRESH:
									isBack = false;
									headerState = REFRESHING;
									changeHeaderViewByState();
									onRefresh();
									break;
								default:
									break;
							}
						}
						break;
				}

				if (lastHeaderPadding > (-1 * headerHeight) && headerState != REFRESHING) {
					return true;
				} else {
					return false;
				}
			}
		});
	}


	private void updateTimeText(){
		long lastTime = CardManager.getInstance().getRefreshTime(this.getContext());
		long currentTime = System.currentTimeMillis();
		long duration = currentTime - lastTime;
		duration = duration/(1000*60);
		if(duration == 0){
			updateRecord.setText(1+"分钟前刷新");
		}else if(duration >0  && duration<60){
			updateRecord.setText(duration+"分钟前刷新");
		}else if(duration >=60 && duration <60*24 ){
			updateRecord.setText(duration/60+"小时前刷新");
		}else{
			updateRecord.setText("下拉后松手即可更新");
		}
	}
	private void initAnim() {
//		scaleAnimation = new ScaleAnimation(1f, 1.05f, 1f, 1.05f);
//		scaleAnimation.setDuration(100);
//		scaleAnimation.setRepeatCount(Integer.MAX_VALUE);
//		scaleAnimation.setRepeatMode(Animation.REVERSE);
//		clothV.startAnimation(scaleAnimation);
//		translateAnimation = new TranslateAnimation(1f, 1f, 0.1f, 10f);
//		translateAnimation.setDuration(500);
//		translateAnimation.setFillAfter(false);
//		translateAnimation.setRepeatCount(Integer.MAX_VALUE);
//		translateAnimation.setRepeatMode(Animation.REVERSE);
//		header.startAnimation(translateAnimation);
	}

	public void setRefreshCallBack(RefreshCallBack callBack) {
		this.callBack = callBack;
	}

	private void changeHeaderViewByState() {
		switch (headerState) {
			case PULL_To_REFRESH:
				if (isBack) {
					isBack = false;
				}
				stopRefreshAnim();
				break;
			case RELEASE_To_REFRESH:
				startRefreshAnim();
				break;
			case REFRESHING:
				lastHeaderPadding = 0;
				header.setPadding(0, lastHeaderPadding, 0, 0);
				header.invalidate();
				startRefreshAnim();
				updateRecord.setText(R.string.release_to_refresh);
				break;
			case DONE:
				lastHeaderPadding = -1 * headerHeight;
				header.setPadding(0, lastHeaderPadding, 0, 0);
				header.invalidate();
				hasUpdate = false;
				stopRefreshAnim();
				break;
			default:
				break;
		}
	}

	private void startRefreshAnim() {
//		translateAnimation.startNow();
//		scaleAnimation.startNow();
	}

	private void stopRefreshAnim() {
//		translateAnimation.cancel();
//		scaleAnimation.cancel();
	}

	Handler AnimCancleHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onRefreshComplete();
		}
	};

	private void onRefresh() {
		new RefreshAsyncTask().execute();
		AnimCancleHandler.sendEmptyMessageDelayed(0,10000);
	}



	private class RefreshAsyncTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			if (callBack != null) {
				callBack.doInBackground();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			onRefreshComplete();
			saveCurrentTime();
			if (callBack != null) {
				callBack.complete();
			}
		}
	}

	public interface RefreshCallBack {
		public void doInBackground();

		public void complete();
	}

	public void onRefreshComplete() {
		AnimCancleHandler.removeMessages(0);
		if(headerState != DONE){
			headerState = DONE;
			changeHeaderViewByState();
		}
	}

	private void saveCurrentTime(){
		CardManager.getInstance().setRefreshTime(this.getContext(),System.currentTimeMillis());
	}
	private void measureView(View childView) {
		android.view.ViewGroup.LayoutParams p = childView.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int height = p.height;
		int childHeightSpec;
		if (height > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		childView.measure(childWidthSpec, childHeightSpec);
	}
}
