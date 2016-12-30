package com.sergio.trackmyshow.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.database.DatabaseHelper;
import com.sergio.trackmyshow.fragments.MoviesFragment;
import com.sergio.trackmyshow.fragments.ShowsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        mViewPager = (ViewPager) findViewById(R.id.view_pager_main);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        final FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu_main);

        FloatingActionButton fabShow = (FloatingActionButton) findViewById(R.id.fab_shows_main);
        fabShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFab(fabMenu);
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                i.setAction("search.TvShow");
                startActivity(i);
            }
        });

        FloatingActionButton fabMovie = (FloatingActionButton) findViewById(R.id.fab_movies_main);
        fabMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFab(fabMenu);
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                i.setAction("search.Movie");
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int currentFragment = mViewPager.getCurrentItem();
        setupViewPager(mViewPager);
        if (currentFragment != 0) {
            mViewPager.setCurrentItem(currentFragment);
        }
    }

    private void closeFab(FloatingActionMenu menu) {
        if (menu.isOpened()) {
            menu.close(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_main_about:
                Toast.makeText(this, "Soon", Toast.LENGTH_LONG)
                        .show();
                break;
        }
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MoviesFragment(), getResources().getString(R.string.movies));
        adapter.addFragment(new ShowsFragment(), getResources().getString(R.string.tv_shows));
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> titleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private void addFragment(Fragment fragment, String title) {
            this.fragmentList.add(fragment);
            this.titleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}