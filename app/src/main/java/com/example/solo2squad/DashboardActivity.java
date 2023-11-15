package com.example.solo2squad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private FragGameSelection gameSelectionFragment;
    private FragHostEvents hostGameFragment;
    private FragManageEvents manageEventsFragment;
    private FragMySchedules upcomingGamesFragment;

    private ImageButton profileButton;
    private View profileOverlay;
    private boolean isProfileVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize fragments
        gameSelectionFragment = new FragGameSelection();
        hostGameFragment = new FragHostEvents();
        manageEventsFragment = new FragManageEvents();
        upcomingGamesFragment = new FragMySchedules();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.fragment_container);
        tabLayout = findViewById(R.id.tab_layout);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navselectgame) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.navhostevents) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (item.getItemId() == R.id.navmanageevents) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (item.getItemId() == R.id.navupcomingevents) {
                viewPager.setCurrentItem(3);
                return true;
            }
            return false;
        });

        // Add a listener to update the bottom navigation when the ViewPager changes
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Update the selected item in the bottom navigation
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        View profileOverlay = findViewById(R.id.profile_overlay);
        //View profileButton = findViewById(R.id.profileButton);

//        profileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                toggleProfileOverlay(profileOverlay);
//            }
//        });
    }

    private void toggleProfileOverlay(View profileOverlay) {
        float fromXDelta = isProfileVisible ? 0f : 1f;
        float toXDelta = isProfileVisible ? 1f : 0f;

        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, fromXDelta,
                TranslateAnimation.RELATIVE_TO_PARENT, toXDelta,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f
        );

        animation.setDuration(300); // Adjust the duration as needed

        profileOverlay.startAnimation(animation);
        profileOverlay.setVisibility(isProfileVisible ? View.GONE : View.VISIBLE);
        isProfileVisible = !isProfileVisible;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(gameSelectionFragment, "Select Game");
        adapter.addFragment(hostGameFragment, "Host Game");
        adapter.addFragment(manageEventsFragment, "Manage Events");
        adapter.addFragment(upcomingGamesFragment, "Upcoming Games");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
