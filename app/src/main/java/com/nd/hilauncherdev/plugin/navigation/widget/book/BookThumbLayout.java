package com.nd.hilauncherdev.plugin.navigation.widget.book;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;

/**
 * 书籍预览图布局
 * Created by linliangbin on 2017/8/16 10:53.
 */

public class BookThumbLayout extends RelativeLayout {


    AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    private ImageView thumbImage;
    private TextView nameText, logoText;

    public BookThumbLayout(Context context) {
        super(context);
        init();
    }

    public BookThumbLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.navi_shelf_card_book_thumb, this);
        thumbImage = (ImageView) this.findViewById(R.id.iv_bookshelf_item_thumb_image);
        nameText = (TextView) this.findViewById(R.id.iv_bookshelf_thumb_title);
        logoText = (TextView) this.findViewById(R.id.iv_bookshelf_thumb_logo);
        nameText.setVisibility(INVISIBLE);
        logoText.setVisibility(INVISIBLE);
    }

    public void showThumb(String thumb, String name) {

        if ("local_res".equals(thumb) || TextUtils.isEmpty(thumb)) {
            nameText.setVisibility(VISIBLE);
            logoText.setVisibility(VISIBLE);
            nameText.setText(name);
            thumbImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_book_cover_unknown));
        } else {
            nameText.setVisibility(INVISIBLE);
            logoText.setVisibility(INVISIBLE);
            if (!TextUtils.isEmpty(thumb) &&
                    (thumb.startsWith("http") || thumb.startsWith("https"))) {
                Drawable drawable = asyncImageLoader.loadDrawableOnlyCache(thumb);
                if (drawable != null) {
                    thumbImage.setImageDrawable(drawable);
                } else {
                    asyncImageLoader.showDrawable(thumb, thumbImage, ImageView.ScaleType.CENTER_CROP, R.drawable.ic_book_cover_unknown);
                }
            } else {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(thumb);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        thumbImage.setImageDrawable(new BitmapDrawable(bitmap));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
