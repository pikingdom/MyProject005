package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

/**
 * description: 淘宝购物屏卡片<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/15<br/>
 */
public abstract class TaobaoCard {

    private View contentView;
    private View splitView;

    public CardType type;

    /**
     * 所在的淘宝购物屏是否处于可见状态
     */
    private boolean isPageVisible = false;
    protected Rect visibleRect;

    public TaobaoCard(ViewGroup parent) {
    }

    public View getView() {
        return contentView;
    }

    public abstract void update();

    public void setContentView(View view) {
        contentView = view;
        visibleRect = new Rect();
        view.getLocalVisibleRect(visibleRect);

        View splitView = new View(view.getContext());
        splitView.setBackgroundColor(0xfff0f0f0);//分割线颜色设置
        setSplitView(splitView);
    }

    public View getSplitView(){
        return splitView;
    }
    public void setSplitView(View view){
        splitView = view;
    }
    public void setCardVisible(boolean visible){
        if (visible) {
            if (getView() != null)
                getView().setVisibility(View.VISIBLE);
            if (getSplitView() != null)
                getSplitView().setVisibility(View.VISIBLE);
        }else{
            if (getView() != null)
                getView().setVisibility(View.GONE);
            if (getSplitView() != null)
                getSplitView().setVisibility(View.GONE);
        }
    }

    protected void setType(CardType type) {
        this.type = type;
//        mapping.put(type, this);
    }

    public CardType getType() {
        return type;
    }

//    public static TaobaoCard getCardByType(CardType type) {
//        return mapping.get(type);
//    }

    public void setPageVisible(boolean visible){
        this.isPageVisible = visible;
    }

    public void notifyVisibleRectChanged(Rect visibleRect){
        this.visibleRect = visibleRect;
    }

    public boolean isPageVisible(){
        return this.isPageVisible;
    }
}
