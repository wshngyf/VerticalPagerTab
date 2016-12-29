package com.fey.verticalpagertab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fey.vericalpagertablib.NoSlidingViewPager;
import com.fey.vericalpagertablib.VerticalPagerTab;
import com.fey.verticalpagertab.adapter.DemoFragmentAdapter;

public class MainActivity extends AppCompatActivity {

    private DemoFragmentAdapter demoFragmentAdapter = null;
    String[] titles = {"分类1", "分类2", "分类3"};

    VerticalPagerTab verticalPagerTab;
    NoSlidingViewPager noSlidingViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAction();
    }

    private void initAction() {
        demoFragmentAdapter = new DemoFragmentAdapter(getSupportFragmentManager(), titles);
        noSlidingViewPager.setAdapter(demoFragmentAdapter);
        //预加载3个页面
        noSlidingViewPager.setOffscreenPageLimit(3);
        //禁止滑动与动画
        noSlidingViewPager.setPagingEnabled(false);
        verticalPagerTab.setViewPager(noSlidingViewPager);

    }

    private void initView() {
        verticalPagerTab = (VerticalPagerTab) findViewById(R.id.pager_tabs_interaction);
        noSlidingViewPager = (NoSlidingViewPager) findViewById(R.id.vp_context_interaction);
    }
}
