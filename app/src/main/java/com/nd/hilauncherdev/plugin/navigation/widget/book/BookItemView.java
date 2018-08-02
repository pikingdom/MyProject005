package com.nd.hilauncherdev.plugin.navigation.widget.book;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * 书架卡片-
 * Created by linliangbin on 2017/8/7 15:29.
 */

public class BookItemView extends RelativeLayout {

    private BookThumbLayout thumbLayout;
    private TextView nameText;
    private ImageView recommendIc;


    public BookItemView(Context context) {
        super(context);
        initView();
    }

    public BookItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @desc 移除括号
     * @author linliangbin
     * @time 2017/8/14 16:41
     */
    public static String getPureTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return "";
        }
        String result = title;
        result = title.replace("《", "");
        result = result.replace("》", "");
        return result;
    }


    public int getLayoutId() {
        return R.layout.navi_shelf_card_book_item;
    }

    private void initView() {

        LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        thumbLayout = (BookThumbLayout) findViewById(R.id.iv_bookshelf_item_thumb);
        recommendIc = (ImageView) findViewById(R.id.iv_bookshelf_item_recommend_ic);
        nameText = (TextView) findViewById(R.id.tv_bookshelf_item_title);

    }

    public void setThumbHeight(int height) {
        if (height == 0) {
            return;
        }
        if (thumbLayout != null) {
            ViewGroup.LayoutParams layoutParams = thumbLayout.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = height;
            }
        }

    }

    /**
     * @desc 展示书籍信息
     * @author linliangbin
     * @time 2017/8/7 15:34
     */
    public void showBookInfo(String thumbUrl, String name, boolean isRecommend) {
        thumbLayout.showThumb(thumbUrl, getPureTitle(name));

        nameText.setText(getPureTitle(name));
        if (isRecommend) {
            recommendIc.setVisibility(VISIBLE);
        } else {
            recommendIc.setVisibility(INVISIBLE);
        }
    }


}
