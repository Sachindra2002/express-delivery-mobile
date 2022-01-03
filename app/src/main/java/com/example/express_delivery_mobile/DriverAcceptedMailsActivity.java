package com.example.express_delivery_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;


import com.example.express_delivery_mobile.Fragment.FragmentAdapter;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Service.DriverClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.Util.AuthHandler;
import com.example.express_delivery_mobile.Util.NavHandler;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;


public class DriverAcceptedMailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private SearchView searchView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private FragmentAdapter fragmentAdapter;

    private List<Mail> mails;
    private String token;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if authorization token is valid
        String result = AuthHandler.validate(DriverAcceptedMailsActivity.this, "driver");

        if (result != null) {
            if (result.equals("unauthorized") || result.equals("expired")) return;
        }

        //Load layout
        setContentView(R.layout.activity_accepted_mails_driver);

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);
        email = sharedPreferences.getString("email", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);

        //Setup navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.open_nav_drawer,
                R.string.close_nav_drawer
        );

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager_2);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Accepted"));
        tabLayout.addTab(tabLayout.newTab().setText("Started"));
        tabLayout.addTab(tabLayout.newTab().setText("Picked up"));
        tabLayout.addTab(tabLayout.newTab().setText("In Transit"));
        tabLayout.addTab(tabLayout.newTab().setText("Delivered"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleDriverNav(item, DriverAcceptedMailsActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
