package com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.calculator;

import android.view.View;
import android.widget.AbsListView;

import com.nd.hilauncherdev.plugin.navigation.widget.combine.adapter.VideoListAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.items.ListItem;
import com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.scroll_utils.ListViewItemPositionGetter;
import com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.scroll_utils.ScrollDirectionDetector;

/**
 * A utility that tracks current {@link ListItem} visibility.
 * Current ListItem is an item defined by calling {@link #setCurrentItem(ListItemData)}.
 * Or it might be mock current item created in method {@link #getMockCurrentItem}
 * <p>
 * The logic is following: when current view is going out of screen (up or down) by {@link #INACTIVE_LIST_ITEM_VISIBILITY_PERCENTS} or more then neighbour item become "active" by calling {@link Callback#activateNewCurrentItem}
 * "Going out of screen" is calculated when {@link #onStateTouchScroll} is called from super class {@link BaseItemsVisibilityCalculator}
 * <p>
 * Method {@link ListItemsVisibilityCalculator#onScrollStateIdle} should be called only when scroll state become idle. // TODO: test it
 * When it's called we look for new current item that eventually will be set as "active" by calling {@link #setCurrentItem(ListItemData)}
 * Regarding the {@link #mScrollDirection} new current item is calculated from top to bottom (if DOWN) or from bottom to top (if UP).
 * The first(or last) visible item is set to current. It's visibility percentage is calculated. Then we are going though all visible items and find the one that is the most visible.
 * <p>
 * Method {@link #onStateFling} is calling {@link Callback#deactivateCurrentItem}
 *
 * @author danylo.volokh
 */
public class SingleListViewItemActiveCalculator implements ScrollDirectionDetector.OnDetectScrollListener {


    private static final int TOP_VISIBLE_LIMIT = 80;
    private static final int TOP_IN_VISIBLE_LIMIT = 40;

    private static final int REAR_VISIBLE_LIMIT = 80;
    private static final int REAR_IN_VISIBLE_LIMIT = 30;


    private final ScrollDirectionDetector mScrollDirectionDetector = new ScrollDirectionDetector(this);

    /**
     * Initial scroll direction should be UP in order to set as active most top item if no active item yet
     */
    private ScrollDirectionDetector.ScrollDirection mScrollDirection = ScrollDirectionDetector.ScrollDirection.UP;


    public SingleListViewItemActiveCalculator() {

    }

    public void onScroll(ListViewItemPositionGetter itemsPositionGetter, int firstVisibleItem, int lastVisibleItem, int scrollState/*TODO: add current item here. start tracking from it*/) {
        mScrollDirectionDetector.onDetectedListScroll(itemsPositionGetter, firstVisibleItem);

        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                onStateTouchScroll(itemsPositionGetter, firstVisibleItem, lastVisibleItem);
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                onStateTouchScroll(itemsPositionGetter, firstVisibleItem, lastVisibleItem);
                break;

            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                break;
        }
    }

    /**
     * When Scrolling list is in this state we start calculating Active Item.
     * Here we assume that scroll state was idle previously and {@link #mCurrentItem} already contains some data
     *
     * @param itemsPositionGetter
     */
    protected void onStateTouchScroll(ListViewItemPositionGetter itemsPositionGetter, int firstVisible, int lastVisible) {
        if (itemsPositionGetter == null) {
            return;
        }

        View firstView = itemsPositionGetter.getChildAt(firstVisible - firstVisible);
        View lastView = itemsPositionGetter.getChildAt(lastVisible - firstVisible);

        int firstViewPercent = VisibleCaculator.getInstance().getViewVisiblePercent(firstView);
        int lastViewPercent = VisibleCaculator.getInstance().getViewVisiblePercent(lastView);

//        Log.i("llbeing","first:Index:" + firstVisible + ",percent"+firstViewPercent + ",view:"+firstView.hashCode());
//        Log.i("llbeing","last:Index:" + lastVisible + ",percent"+lastViewPercent + ",view:"+lastView.hashCode());
//
//        Log.i("llbeing","onStateTouchScroll:"+mScrollDirection + "," + (VideoListAdapter.currentActive == null) +"," + (VideoListAdapter.currentDeactive == null));
        if (mScrollDirection == ScrollDirectionDetector.ScrollDirection.UP) {

                if (lastViewPercent < REAR_IN_VISIBLE_LIMIT) {
                    deactiveViewHolder(lastView, lastVisible);
                }
                if (firstViewPercent > TOP_VISIBLE_LIMIT) {
                    activeViewHolder(firstView, firstVisible);
                }


        } else if (mScrollDirection == ScrollDirectionDetector.ScrollDirection.DOWN) {

                if (firstViewPercent < TOP_IN_VISIBLE_LIMIT) {
                    deactiveViewHolder(firstView, firstVisible);
                }
                if (lastViewPercent > REAR_VISIBLE_LIMIT) {
                    activeViewHolder(lastView, lastVisible);
                }

        }

    }

    @Override
    public void onScrollDirectionChanged(ScrollDirectionDetector.ScrollDirection scrollDirection) {
        mScrollDirection = scrollDirection;
    }

    private void deactiveViewHolder(View view, int index) {

        if (view != null) {
            Object tag = view.getTag();
            if (tag instanceof VideoListAdapter.ViewHolder) {
                VideoListAdapter.ViewHolder viewHolder = (VideoListAdapter.ViewHolder) tag;
                viewHolder.deactivate(view, index);
            }
        }

    }

    private void activeViewHolder(View view, int index) {

        if (view != null) {
            Object tag = view.getTag();
            if (tag instanceof VideoListAdapter.ViewHolder) {
                VideoListAdapter.ViewHolder viewHolder = (VideoListAdapter.ViewHolder) tag;
                viewHolder.setActive(view, index);
            }
        }
    }


}
