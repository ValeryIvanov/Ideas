package com.walts.ideas.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.walts.ideas.R;
import com.walts.ideas.SlidingTabLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "ListIdeasActivity";

    private Context context;

    @InjectView(R.id.viewPager)
    ViewPager viewPager;

    @InjectView(R.id.slidingTabLayout)
    SlidingTabLayout slidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        ButterKnife.inject(this);

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        slidingTabLayout.setViewPager(viewPager);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] CLASSES = new String[] {IdeasListFragment.class.getName(), QuestionsListFragment.class.getName()};
        private final String[] TITLES = new String[]{"Ideas", "Questions"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return CLASSES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment.instantiate(context, CLASSES[position]);
        }
    }

}
