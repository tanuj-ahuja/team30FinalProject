package com.example.team30finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String email;
    private String mobile;
    private String name;

    private BoardFragment boardFragment;
    private ChatFragment chatFragment;
    private PostFragment postFragment;

    private FloatingActionButton fab;

    private double latitude;
    private double longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private static final int LOCATION_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");
        name = getIntent().getStringExtra("name");

        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.add);

        String email = getIntent().getExtras().getString("email");

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(5 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                getLocationValues(location);
            }
        };

        updateLocation();
        startLocationUpdates();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocationUpdates();
                Intent intent = new Intent(DashboardActivity.this, ProductDetailActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);

        boardFragment = BoardFragment.newInstance(email);
        chatFragment = ChatFragment.newInstance(email, mobile, name);
        postFragment = new PostFragment();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(boardFragment, "Board");
        viewPagerAdapter.addFragment(chatFragment, "Chat");
        viewPagerAdapter.addFragment(postFragment, "Posts");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_dashboard_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_chat_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_inbox_24);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateLocation();
    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void updateLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DashboardActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    getLocationValues(location);
                }
            });
        } else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void getLocationValues(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                updateLocation();
            } else{
                Toast.makeText(this, "Location Permission is required to search by location", Toast.LENGTH_SHORT). show();
            }
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();


        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}