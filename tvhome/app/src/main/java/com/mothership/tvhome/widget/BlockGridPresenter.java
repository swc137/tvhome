package com.mothership.tvhome.widget;

import android.content.Context;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayout.Spec;
import static android.support.v7.widget.GridLayout.spec;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mothership.tvhome.R;
import com.tv.ui.metro.model.Block;
import com.tv.ui.metro.model.DisplayItem;

/**
 * Created by wangwei on 3/14/16.
 */
public class BlockGridPresenter extends RowPresenter {
    ViewGroup mParent;
    final DisplayItemSelector mDisplayItemSelector = new DisplayItemSelector();
    FocusHelper.ItemFocusHighlight mFocusHighlight;
    public static class ViewHolder extends RowPresenter.ViewHolder {
        Context mContext;
        GridLayout mGridLayout;
        public ViewHolder(View rootView, GridLayout gridView) {
            super(rootView);
            mContext = rootView.getContext();
            mGridLayout = gridView;
            int paddingh = (int)rootView.getResources().getDimension(R.dimen.grid_block_horizontal_padding);
            mGridLayout.setPadding(paddingh,0,paddingh,0);

        }
    }


    @Override
    protected ViewHolder createRowViewHolder(ViewGroup parent) {
        mParent = parent;
        GridLayout gridLayout = new FocusGridLayout(parent.getContext());
        gridLayout.setUseDefaultMargins(true);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        gridLayout.setClipToPadding(false);
        if(mFocusHighlight==null){
            mFocusHighlight = new FocusHelper.ItemFocusHighlight(parent.getContext());
        }
        return new ViewHolder(gridLayout,gridLayout);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        ViewHolder vh = (ViewHolder) holder;
        if (item instanceof Block) {
            Block<DisplayItem> displayItemBlock = (Block<DisplayItem>) item;
            super.onBindRowViewHolder(holder, new Row(new HeaderItem(0, displayItemBlock.title)));
            vh.mGridLayout.removeAllViews();
            int columns = displayItemBlock.ui_type.columns();
            int rows = displayItemBlock.items.size() / columns;
            vh.mGridLayout.setColumnCount(columns);
            int itemmargin = (int) mParent.getResources().getDimension(R.dimen.grid_item_margin);
            int itemwidth = (int) ((mParent.getWidth() - vh.mGridLayout.getPaddingLeft() - vh.mGridLayout.getPaddingRight()
                    - itemmargin * (columns - 1)) / columns);
            //vh.mGridLayout.setItemMargin(itemmargin);
            int itemheight = (int) (itemwidth / displayItemBlock.ui_type.ratio());
            BasePresenter basePresenter = (BasePresenter)mDisplayItemSelector.getPresenter(displayItemBlock);
            basePresenter.setBaseSize(itemwidth, itemheight);
            for (int i = 0; i < displayItemBlock.items.size(); ++i) {
                if (displayItemBlock.items.get(i).ui_type != null) {
                    DisplayItem di = displayItemBlock.items.get(i);

                    BasePresenter.VH itemholder = (BasePresenter.VH)basePresenter.onCreateViewHolder(vh.mGridLayout);
                    basePresenter.onBindViewHolder(itemholder,di);
                    View view = itemholder.view;
                    int columnstart = displayItemBlock.items.get(i).ui_type.columns();
                    int columnspan = displayItemBlock.items.get(i).ui_type.columnspan();
                    int rowstart = displayItemBlock.items.get(i).ui_type.rows();
                    int rowspan = displayItemBlock.items.get(i).ui_type.rowspan();
                    GridLayout.Spec itemColumnSpec              = spec(columnstart, columnspan);
                    GridLayout.Spec itemRowSpec              = spec(rowstart,rowspan);
                    GridLayout.LayoutParams gridlayout = (GridLayout.LayoutParams)view.getLayoutParams();
                            new GridLayout.LayoutParams(itemRowSpec, itemColumnSpec);
                    gridlayout.width = basePresenter.getRealWidth(mParent.getContext())*columnspan+(columnspan - 1) * itemmargin;
                    gridlayout.height = basePresenter.getRealHeight(mParent.getContext())*rowspan+(rowspan - 1) * itemmargin;
                    gridlayout.columnSpec = itemColumnSpec;
                    gridlayout.rowSpec = itemRowSpec;
                    gridlayout.setMargins(itemmargin / 2, itemmargin / 2, itemmargin / 2, itemmargin / 2);
                    vh.mGridLayout.addView(view, gridlayout);
                    View imageView = itemholder.getBaseSizeView();
                    ViewGroup.LayoutParams lpImg = imageView.getLayoutParams();
                    lpImg.width = itemwidth*columnspan+(columnspan-1)*itemmargin;
                    lpImg.height = itemheight*rowspan+(rowspan - 1) * itemmargin;
                    view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            mFocusHighlight.onItemFocused(v,hasFocus);
                        }
                    });

                }
            }

        }
    }
}
