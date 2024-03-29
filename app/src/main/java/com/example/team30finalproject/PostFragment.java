package com.example.team30finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class PostFragment extends Fragment {

    RecyclerView recyclerView;
    private DatabaseReference  dbRef = FirebaseDatabase.getInstance().getReference();
    PostFragmentAdapter adapter;
    ArrayList<PostFragmentModel> productList;

    private String email, username;
    private double latitude;
    private double longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final int LOCATION_REQUEST_CODE = 99;
    private static final String ARG_EMAIL = "email";
    private static final String ARG_NAME = "name";

    private FloatingActionButton fab;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, param1);
        args.putString(ARG_NAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(5 * 1000);

        updateLocation();
        Log.d("onCreate", "Fragment Post onCreate call");
    }

    private void updateLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((Context)getActivity());

        if(ActivityCompat.checkSelfPermission((Context)getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            });
        } else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                updateLocation();
            } else{
                Toast.makeText(getContext(), "Location Permission is required to search by location", Toast.LENGTH_SHORT). show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("onCreateView", "Fragment Post onCreateView call");

        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
            username = getArguments().getString(ARG_NAME);
        }

        final String currentAccountKey = String.valueOf(email.hashCode());

        View v = inflater.inflate(R.layout.fragment_post, container, false);

        fab = (FloatingActionButton) v.findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("name", username);
                startActivity(intent);
            }
        });

        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        productList = new ArrayList<>();
        adapter = new PostFragmentAdapter(productList, getContext());
        recyclerView.setAdapter(adapter);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.child("account").getChildren()){
                    final String accountKey = dataSnapshot.getKey();
                    if(currentAccountKey.equals(accountKey) && dataSnapshot.getChildrenCount()>1){
                        for(DataSnapshot innerDataSnapshot: dataSnapshot.getChildren()){
                            if(innerDataSnapshot.getKey().equals("email")){
                                continue;
                            }
                            String product_name = innerDataSnapshot.child("name").getValue(String.class);
                            Integer product_quantity = innerDataSnapshot.child("quantity").getValue(Integer.class);
                            Double product_price = innerDataSnapshot.child("price").getValue(Double.class);
                            String product_streetAddr = innerDataSnapshot.child("streetAddress").getValue(String.class);
                            Double product_latitude = innerDataSnapshot.child("latitude").getValue(Double.class);
                            Double product_longitude = innerDataSnapshot.child("longitude").getValue(Double.class);
                            String product_time = innerDataSnapshot.child("postTime").getValue(String.class);
                            PostFragmentModel model = new PostFragmentModel(product_name,product_price,
                                    product_quantity,product_latitude,product_longitude, product_streetAddr,
                                    product_time);
                            productList.add(model);
                        }
                        Collections.sort(productList);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }
}