package com.fey.verticalpagertab.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fey.verticalpagertab.fragment.FragmentTest1;
import com.fey.verticalpagertab.fragment.FragmentTest2;
import com.fey.verticalpagertab.fragment.FragmentTest3;


/**
 * 互动选项卡
 *
 * @author Neil.zh.
 * @version 1.0
 * @date 2016/6/27
 */
public class DemoFragmentAdapter extends FragmentPagerAdapter {

    private String[] tabTitle;//选项卡集合
    private FragmentTest1 Fragment1;
    private FragmentTest2 Fragment2;
    private FragmentTest3 Fragment3;
    public DemoFragmentAdapter(FragmentManager fm, String[] tabTitle) {
        super(fm);
        this.tabTitle = tabTitle;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment ft = null;
        try {
            switch (position) {
                case 0:
                    if (Fragment1 == null) {
                        Fragment1 = new FragmentTest1();
                        //Fragment1.setText("这是第1页");
                    }
                    ft = Fragment1;
                    break;
                case 1:
                    if (Fragment2 == null) {
                        Fragment2 =  new FragmentTest2();
                        //Fragment2.setText("这是第2页");
                    }
                    ft = Fragment2;
                    break;
                case 2:
                    if (Fragment3 == null) {
                        Fragment3 =  new FragmentTest3();
                        //Fragment3.setText("这是第3页");
                    }
                    ft = Fragment3;
                    break;
            }
        } catch (Exception e) {
        }
        return ft;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
    @Override
    public int getCount() {
        return tabTitle.length;
    }
}
