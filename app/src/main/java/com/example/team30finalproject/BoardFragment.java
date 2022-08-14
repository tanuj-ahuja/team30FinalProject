package com.example.team30finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardFragment extends Fragment {

    RecyclerView recyclerView;
    private DatabaseReference  dbRef = FirebaseDatabase.getInstance().getReference();
    BoardFragmentAdapter adapter;
    ArrayList<BoardFragmentModel> productList;

    private String email;
    private double latitude;
    private double longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final int LOCATION_REQUEST_CODE = 99;
    private static final String ARG_EMAIL = "email";

    public BoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.**/
    public static BoardFragment newInstance(String param1) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, param1);
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
        Log.d("onCreate", "Fragment Board onCreate call");
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
        Log.d("onCreateView", "Fragment Board onCreateView call");

        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
        }

        final String currentAccountKey = String.valueOf(email.hashCode());

        View v = inflater.inflate(R.layout.fragment_board, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        productList = new ArrayList<>();
        adapter = new BoardFragmentAdapter(productList, getContext());
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

                            float[] distance_results= new float[1];
                            Location.distanceBetween(latitude, longitude, product_latitude, product_longitude, distance_results);
                            double product_distance = Double.parseDouble(df.format(distance_results[0] / 1609.344));

                            BoardFragmentModel model = new BoardFragmentModel(product_name,product_price,
                                    product_quantity,product_latitude,product_longitude, product_streetAddr,
                                    product_distance, "https://firebasestorage.googleapis.com/v0/b/team30-final-project.appspot.com/o/images%2FJPEG_20220813_192331.jpg?alt=media&token=eaf8929c-485b-42ff-9e48-a9ede11a6920");
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