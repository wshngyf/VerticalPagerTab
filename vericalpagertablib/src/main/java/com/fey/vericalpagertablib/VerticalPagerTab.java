package com.fey.vericalpagertablib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

/**
 * 滑动指示器pstsindicatorcolor颜色
 * 在视图的底部的全宽度的线pstsunderlinecolor颜色
 * 选项卡之间的分隔pstsdividercolor颜色
 * 滑动指示器pstsindicatorheightheight
 * 在视图的底部的全宽度的线pstsunderlineheight高度
 * pstsdividerpadding顶部和底部填充的分频器
 * pststabpaddingleftright左、右填充每个选项卡
 * pstsscrolloffset卷轴被选择的标签的偏移
 * pststabbackground背景绘制的每个标签，应该是一个statelistdrawable
 * pstsshouldexpand如果设置为TRUE，每个标签都给予同样的重量，默认为false
 * pststextallcaps如果为真，所有选项卡标题都是大写，默认为true
 */
public class VerticalPagerTab extends ScrollView {

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;// 默认布局方式
    private LinearLayout.LayoutParams expandedTabLayoutParams;// 权重布局方式

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;// 标签布局
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;// 当前下标
    private int selectedPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xFF666666;// 已选中tab颜色
    private int underlineColor = 0x1A000000;// 未选中tab颜色
    private int dividerColor = 0x1A000000;// 中间间隔线颜色

    private boolean shouldExpand = false;// 是否使用权重布局
    private boolean textAllCaps = true;//是否大写

    private int scrollOffset = 52;
    private int indicatorHeight = 8;// 已选中底部条高度
    private int indicatorPadding = 0;//底部条左右padding
    private int underlineHeight = 2;// 未选中底部条高度
    private int dividerPadding = 12;// 间隔线上下padding
    private int tabRightPadding = 8;// tab padding 左右间距
    private int tabTopPadding=10;//tab 上下间距
    private int dividerWidth = 1;// 间隔线宽度
    private int textBackgroundColor = 0xFFBCBDBF;//文字的背景色
    private int tabTextSize = 12;// 字体大小
    private int tabTextColor = 0xFF666666;// 字体颜色
    private int selectedTabTextColor = 0xFF666666;
    private int tabTextMaxLength = 20;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;// 字体

    private int lastScrollX = 0;

    private int tabBackgroundResId = R.drawable.vpt_background_tab;// tab背景

    private Locale locale;

    private OnTabClickListener mOnTabClickListener;

    public VerticalPagerTab(Context context) {
        this(context, null);
    }

    public VerticalPagerTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("ResourceType")
    public VerticalPagerTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        //设置主布局
        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.VERTICAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        //获取各属性
        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        indicatorPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorPadding, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabRightPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
        tabTopPadding=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabTopPadding, dm);
        // get system attrs (android:textSize and android:textColor)
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);

        a.recycle();

        // get custom attrs
        a = context.obtainStyledAttributes(attrs, R.styleable.VerticalPagerTab);

        //indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);

        //tab文字选中时的颜色,默认和滑动指示器的颜色一致

        selectedTabTextColor = a.getColor(R.styleable.VerticalPagerTab_vptSelectedTabTextColor, indicatorColor);
        tabTextColor = a.getColor(R.styleable.VerticalPagerTab_vptTabTextColor, tabTextColor);
        tabRightPadding = a.getDimensionPixelSize(R.styleable.VerticalPagerTab_vptTabPaddingLeftRight, tabRightPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.VerticalPagerTab_vptTabBackground, tabBackgroundResId);
        textAllCaps = a.getBoolean(R.styleable.VerticalPagerTab_vptTextAllCaps, textAllCaps);
        tabTextSize = a.getDimensionPixelSize(R.styleable.VerticalPagerTab_vptTabTextSize, tabTextSize);
        tabTextMaxLength = a.getInteger(R.styleable.VerticalPagerTab_vptTabTextMaxLength, tabTextMaxLength);
        textBackgroundColor = a.getColor(R.styleable.VerticalPagerTab_vptTabTextBackgroundColor, textBackgroundColor);
        tabTopPadding = a.getDimensionPixelSize(R.styleable.VerticalPagerTab_vptTabPaddingTopBottom, tabTopPadding);


        //用适配布局方式适配文字大小
        //tabTextSize = AutoUtils.getPercentWidthSize(tabTextSize);
        //indicatorHeight = AutoUtils.getPercentWidthSize(indicatorHeight);
        //dividerPadding = AutoUtils.getPercentHeightSize(dividerPadding);
        //tabPadding = AutoUtils.getPercentWidthSize(tabPadding);
        //indicatorPadding = AutoUtils.getPercentWidthSize(indicatorPadding);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        //初始化各布局样式
        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public ViewPager getPager() {
        return pager;
    }

    public int getTabCount() {
        return tabCount;
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    private void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setMaxEms(tabTextMaxLength);
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setText(title);
        //tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onTabClckListener(position);
                }
            }
        });
        //设置上下边距
        tab.setPadding(tabRightPadding, tabTopPadding, tabRightPadding, tabTopPadding);
        tabsContainer.addView(tab, position, defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
                if (i == selectedPosition) {
                    tab.setTextColor(selectedTabTextColor);
                    tab.setBackgroundColor(textBackgroundColor);
                }
            }
        }

    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw underline
        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw indicator line
        rectPaint.setColor(indicatorColor);

        // default: line below current tab 获取当前view
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();//view的长宽 0.0
        float lineRight = currentTab.getRight();//120

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
            //偏移量大于0 移动
            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        //canvas.drawRect(lineLeft + indicatorPadding, height - indicatorHeight, lineRight - indicatorPadding, height, rectPaint);
        //绘制左边绿色竖线
        // draw divider

        dividerPaint.setColor(dividerColor);
        if (dividerPadding != 0) {
            for (int i = 0; i < tabCount - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
            }
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            VerticalPagerTab.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            VerticalPagerTab.this.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageSelected(int position) {
            VerticalPagerTab.this.onPageSelected(position);
        }

    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position;
        currentPositionOffset = positionOffset;
        // 避免tab项为null异常
        View childAt = tabsContainer.getChildAt(position);
        if (childAt != null) {
            scrollToChild(position, (int) (positionOffset * childAt.getWidth()));
        }

        invalidate();

        if (delegatePageListener != null) {
            delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            scrollToChild(pager.getCurrentItem(), 0);
        }

        if (delegatePageListener != null) {
            delegatePageListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageSelected(int position) {
        selectedPosition = position;
//        currentPosition = position;
        updateTabStyles();
        if (delegatePageListener != null) {
            delegatePageListener.onPageSelected(position);
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;

        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        notifyDataSetChanged();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setSelectedTextColor(int textColor) {
        this.selectedTabTextColor = textColor;
        updateTabStyles();
    }

    public void setSelectedTextColorResource(int resId) {
        this.selectedTabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getSelectedTextColor() {
        return selectedTabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
        updateTabStyles();
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabRightPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabRightPadding;
    }

    public int getTabTopPadding() {
        return tabTopPadding;
    }

    public void setTabTopPadding(int tabTopPadding) {
        this.tabTopPadding = tabTopPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public OnTabClickListener getOnTabClickListener() {
        return mOnTabClickListener;
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        mOnTabClickListener = onTabClickListener;
    }

    public interface OnTabClickListener {
        public void onTabClckListener(int position);
    }
}