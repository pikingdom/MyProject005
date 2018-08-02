package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;

/**
 * 语音识别结果窗口封装<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class VoiceRecognitionWindow extends PopupWindow {

	protected Context mContext;
	private TextView txtTitle;
	private LinearLayout contentView;
	private ListView listView;
	private OnRecognitionItemClickListener itemClickListener;

	public interface OnRecognitionItemClickListener {
		public void onItemClick(View view, CharSequence text);
	}

	public VoiceRecognitionWindow(Context context) {
		super(context);
		this.mContext = context;
	}

	/**
	 * 判断机型(或固件版本)是否支持google语音识别功能
	 * 
	 * @return 支持返回true, 否则返回false
	 */
	public static boolean isVoiceRecognitionEnable(Context context) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		if (activities.size() != 0)
			return true;
		else
			return false;
	}

	/** 调用语音识别工具 */
	public static void startVoiceRecognition(Context context, Activity activity, int requestCode) {
		if (!checkNetwork(context,activity))
			return;

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.searchbox_title_voice));

		try {
			activity.startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(activity, context.getString(R.string.searchbox_speech_reco_not_available), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * <p>
	 * 说明: 设置语音识别结果项被点击时触发的事件
	 * </p>
	 * 
	 * @author Yu.F, 2014-5-19
	 */
	public void setOnRecognitionItemClickListener(OnRecognitionItemClickListener listener) {
		this.itemClickListener = listener;
	}

	/**
	 * <p>
	 * 说明: 设置语音识别结果窗口左边按钮(一般是取消)的点击监听事件, 默认是点击完关闭窗口
	 * </p>
	 * 
	 * @author Yu.F, 2014-5-19
	 */
	public void setOnLeftButtonClickListener(OnClickListener onLeftButtonClickListener) {
		this.onLeftButtonClickListener = onLeftButtonClickListener;
	}

	/**
	 * <p>
	 * 说明: 设置语音识别结果窗口右边按钮(一般是重试)的点击监听事件, 默认无动作
	 * </p>
	 * 
	 * @author Yu.F, 2014-5-19
	 */
	public void setOnRightButtonClickListener(OnClickListener onRightButtonClickListener) {
		this.onRightButtonClickListener = onRightButtonClickListener;
	}

	/**
	 * 初始化语言识别结果界面
	 */
	private void initVoiceRecognitionView() {

		contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.searchbox_voice_recognition_view, null);
		contentView.setPadding(24, mContext.getResources().getDimensionPixelSize(R.dimen.status_bar_height) + 40, 24, mContext.getResources().getDimensionPixelSize(R.dimen.button_bar_height) + 60);
		txtTitle = (TextView) contentView.findViewById(R.id.txtTitle);

		listView = (ListView) contentView.findViewById(R.id.voiceRecoResultList);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (itemClickListener != null) {
					CharSequence text = ((TextView) view).getText();
					itemClickListener.onItemClick(view, text);
				}
			}
		});

		contentView.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() { // 返回按钮
					public void onClick(View v) {
						onLeftButtonClickListener.onClick(v);
					}
				});

		contentView.findViewById(R.id.btnRetry).setOnClickListener(new OnClickListener() {
			public void onClick(View v) { // 重试按钮
				onRightButtonClickListener.onClick(v);
			}
		});

		setContentView(contentView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable(mContext.getResources()));
	}

	private OnClickListener onLeftButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

	private OnClickListener onRightButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 默认无动作, 由后期指定
		}
	};

	/**
	 * 检查网络连接是否可用, 网络不可用时, 使用Toast进行提示
	 * 
	 * @return 网络可用返回true, 不可用返回false
	 */
	private static boolean checkNetwork(Context context,Activity activity) {
		if (!TelephoneUtil.isNetworkAvailable(activity)) {
			String msg = context.getString(R.string.searchbox_network_not_available);
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

			return false;
		} else {
			return true;
		}
	}

	/**
	 * <p>
	 * 说明: 显示结果列表窗口
	 * </p>
	 * 
	 * @author Yu.F, 2014-5-19
	 * @param parent
	 *            父对象
	 * @param results
	 *            结果集
	 */
	public void show(View parent, List<String> results) {
		if (contentView == null)
			initVoiceRecognitionView();

		String title = mContext.getString(R.string.searchbox_subtitle_result_stat, results.size());
		txtTitle.setText(title);
		listView.setAdapter(new ArrayAdapter<String>(mContext, R.layout.searchbox_voice_recognition_result_item, results));

		showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	/**
	 * <p>
	 * 说明: 设置结果列表窗口标题
	 * </p>
	 * 
	 * @author Yu.F, 2014-5-19
	 * @param title
	 */
	public void setTitle(String title) {
		if (txtTitle != null)
			txtTitle.setText(title);
	}

	@Override
	public void dismiss() {
		try {
			super.dismiss();
		} catch (Exception e) {
		}
	}
}
