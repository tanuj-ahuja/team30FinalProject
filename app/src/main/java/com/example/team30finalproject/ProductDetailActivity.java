package com.example.team30finalproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private String uploadImageName;
    private Uri uploadImageContentUri;
    private static final int LOCATION_REQUEST_CODE = 99;
    private EditText nameEditText, priceEditText, quantityEditText;
    private TextView nearestLandmark;
    private Button camera, gallery, submit, location;
    private CheckBox image_added, location_added;
    private DatabaseReference mDatabase;
    private String currentPhotoPath, fileName;
    private String strAddress;
    private double latitude;
    private double longitude;
    StorageReference storageReference;
    ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        nameEditText = (EditText) findViewById(R.id.name);
        quantityEditText = (EditText) findViewById(R.id.quantity);
        priceEditText = (EditText) findViewById(R.id.price);
        camera = (Button) findViewById(R.id.camera);
        gallery = (Button) findViewById(R.id.gallery);
        submit = (Button) findViewById(R.id.submit_product);
        location = (Button) findViewById(R.id.location_selection);
        image_added = (CheckBox) findViewById(R.id.image_added);
        location_added = (CheckBox) findViewById(R.id.location_added);
        nearestLandmark = (TextView) findViewById(R.id.landmark_location);
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String email = getIntent().getExtras().getString("email");

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(5 * 1000);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String quantityString = quantityEditText.getText().toString();
                String priceString = priceEditText.getText().toString();

                if (quantityString.isEmpty()) {
                    quantityEditText.setError("Quantity cannot be empty");
                    return;
                }
                if (priceString.isEmpty()) {
                    priceEditText.setError("Price cannot be empty");
                    return;
                }
                if (name.isEmpty()) {
                    nameEditText.setError("Product name cannot be empty");
                    return;
                }
                
                if (uploadImageContentUri != null && !uploadImageName.isEmpty()) {
                    uploadImageToFirebase(uploadImageName, uploadImageContentUri);
                }

                DatabaseReference accountRef = mDatabase.child("account").child(String.valueOf(email.hashCode()));
                DatabaseReference newAccountRef = accountRef.push();

                Produce p = new Produce(name, Double.parseDouble(priceString), Integer.parseInt(quantityString),
                        fileName, latitude, longitude, strAddress);
                newAccountRef.setValue(p);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryActivityResultLauncher.launch(gallery);
                
            }
        });

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            File f = new File(currentPhotoPath);
                            Uri contentUri = Uri.fromFile(f);

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(contentUri);
                            sendBroadcast(mediaScanIntent);
                            // add to firebase
                            uploadImageName = f.getName();
                            uploadImageContentUri = contentUri;
//                            uploadImageToFirebase(f.getName(), contentUri);
//                            Log.d("currentPhotoPath", currentPhotoPath);
                        }
                    }
                }
        );

        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri contentUri = data.getData();
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "JPEG_" + timeStamp + ".jpg";
                            // add to firebase
                            uploadImageName = imageFileName;
                            uploadImageContentUri = contentUri;
//                            uploadImageToFirebase(imageFileName, contentUri);
//                            Log.d("currentPhotoPath", currentPhotoPath);
                        }
                    }
                }
        );

    }

    private void updateLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ProductDetailActivity.this);

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
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
        location_added.setChecked(true);
        Geocoder geocoder = new Geocoder(ProductDetailActivity.this);
        try{
            List<Address> address = geocoder.getFromLocation(latitude,longitude,1);
            strAddress = address.get(0).getAddressLine(0);
            nearestLandmark.setText(strAddress);
        } catch (Exception e){
            strAddress = "NA";
            nearestLandmark.setText("Unable to get street address");
        }
    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is required to use camera", Toast.LENGTH_SHORT). show();
            }
        }
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                updateLocation();
            } else{
                Toast.makeText(this, "Location Permission is required to add the location", Toast.LENGTH_SHORT). show();
            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        StorageReference image = storageReference.child("images/" + name);
        fileName = name;
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "Success" + uri.toString());
                    }
                });
                Toast.makeText(ProductDetailActivity.this, "Done!", Toast.LENGTH_SHORT).show();
           
                image_added.setChecked(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // code taken from offical docs: https://developer.android.com/training/camera/photobasics
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // code taken from offical docs: https://developer.android.com/training/camera/photobasics
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("heyy", ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraActivityResultLauncher.launch(takePictureIntent);
            }
        }
    }
}