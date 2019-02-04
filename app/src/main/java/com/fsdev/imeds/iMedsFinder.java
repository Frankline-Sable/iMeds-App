package com.fsdev.imeds;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class iMedsFinder extends AppCompatActivity {

    private TextView mTextMessage;
    private Fragment geoFrag1,geoFrag2,geoFrag3;
    private ViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imeds_finder);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        geoFrag1=new Fragment_GeoHome();
        geoFrag2=new Fragment_GeoDash();
        geoFrag3=new Fragment_GeoNotify();

        mViewPager=(ViewPager) findViewById(R.id.pager);
        mViewPager.setPageTransformer(true,new DepthPageTransformer());
        setupViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position){
                    case 1:
                        geoFrag1=(Fragment_GeoHome) mViewPager.getAdapter().instantiateItem(mViewPager,0);
                        break;
                    case 2:
                        geoFrag2=(Fragment_GeoDash) mViewPager.getAdapter().instantiateItem(mViewPager,1);
                        break;
                    case 3:
                        geoFrag3=(Fragment_GeoNotify) mViewPager.getAdapter().instantiateItem(mViewPager,2);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public void setupViewPager(ViewPager mPager) {
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(geoFrag1);
        mPagerAdapter.addFragment(geoFrag2);
        mPagerAdapter.addFragment(geoFrag3);
        mPager.setAdapter(mPagerAdapter);
    }
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

}
