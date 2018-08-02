package com.nd.hilauncherdev.plugin.navigation.util;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;

public class DialogCommon extends Dialog {

	public DialogCommon(Context context, CharSequence title, CharSequence message, CharSequence lBtnText, CharSequence rBtnTet) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.market_dialog_layout);
		ViewGroup layout = (ViewGroup) findViewById(R.id.market_dialog_layout);
		int width = (int) (ScreenUtil.getScreenWidth(context) * 0.9f);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(lp);
		((TextView) layout.findViewById(R.id.market_dialog_top_title)).setText(title);
		TextView textView = (TextView) layout.findViewById(R.id.market_dialog_content);
		textView.setText(Html.fromHtml(message.toString()));
		((Button) layout.findViewById(R.id.market_dialog_right_button)).setText(rBtnTet);
		((Button) layout.findViewById(R.id.market_dialog_left_button)).setText(lBtnText);
	}

	public void setClickListener(final View.OnClickListener msgClickListener, final View.OnClickListener leftClickListener, final View.OnClickListener rightClickListener) {
		findViewById(R.id.market_dialog_content).setOnClickListener(msgClickListener);
		((Button) findViewById(R.id.market_dialog_left_button)).setOnClickListener(leftClickListener);
		((Button) findViewById(R.id.market_dialog_right_button)).setOnClickListener(rightClickListener);
	}

}
