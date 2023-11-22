package com.example.solo2squad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.solo2squad.Authentication.LoginActivity;
import com.example.solo2squad.Authentication.MainActivity;
import com.example.solo2squad.ProfileSection.ProfileUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private TextView textViewName, textViewEmail, textViewPhoneNumber, textViewDOB, textViewAddress;
    private ImageView imageViewProfile;
    private View profileOverlay;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private ImageButton profileButton;
 
    private boolean isProfileVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);



        // Initialize fragments
        gameSelectionFragment = new FragGameSelection();
        hostGameFragment = new FragHostEvents();
        manageEventsFragment = new FragManageEvents();
        upcomingGamesFragment = new FragMySchedules();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.fragment_container);
        tabLayout = findViewById(R.id.tab_layout);

        textViewName = findViewById(R.id.textView4);
        textViewEmail = findViewById(R.id.textView5);
        textViewPhoneNumber = findViewById(R.id.textViewPhone);
        textViewDOB = findViewById(R.id.textViewDOB);
        textViewAddress = findViewById(R.id.textViewAddress);
        imageViewProfile = findViewById(R.id.imageView4);
        ConstraintLayout logoutLayout = findViewById(R.id.LogoutLayout);
        ConstraintLayout backButton = findViewById(R.id.backButton);


        profileOverlay = findViewById(R.id.profile_overlay);
        View profileButton = findViewById(R.id.profileButton);

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


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleProfileOverlay(profileOverlay);
                if (isProfileVisible) {
                    ProfileUtil.fetchDataFromFirebase(DashboardActivity.this, textViewName, textViewEmail, textViewPhoneNumber, textViewDOB, textViewAddress, imageViewProfile);
                }
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }

            private void signOut() {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Sign out successful, navigate to the LoginActivity and clear the activity stack
                            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // Handle sign-out failure
                            Toast.makeText(DashboardActivity.this, "Sign out failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click here
                onBackPressed(); // This will simulate the system back button press
            }
        });
    }


    @Override
    public void onBackPressed() {
        // Add your custom back button handling
        // For example, hide the profile overlay instead of finishing the activity
        if (isProfileVisible) {
            toggleProfileOverlay(profileOverlay);
        } else {
            super.onBackPressed();
        }
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
