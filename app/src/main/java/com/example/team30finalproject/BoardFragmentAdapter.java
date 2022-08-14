package com.example.team30finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BoardFragmentAdapter extends RecyclerView.Adapter<BoardFragmentAdapter.ViewHolder> {

    ArrayList<BoardFragmentModel> arrayList;
    StorageReference storageReference;
    Bitmap bitmap;

    public BoardFragmentAdapter(ArrayList<BoardFragmentModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_board_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BoardFragmentModel model = arrayList.get(position);
        holder.product.setText(model.getName());
        holder.quantity.setText(String.valueOf(model.getQuantity()));
        holder.price.setText(String.valueOf(model.getPrice()));
        holder.streetAddress.setText(String.valueOf(model.getStreetAddress()));
        holder.distance.setText(String.valueOf(model.getDistance()) + " mi");
        holder.seller.setText(model.getUsername());

        storageReference = FirebaseStorage.getInstance().getReference("images/"+model.getImageFileName());
                            try {
                                File localFile = File.createTempFile("tempfile"+position, "jpg");
                                storageReference.getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                holder.farmImage.setImageBitmap(bitmap);
                                                Log.d("success","Some value stored in bitmap");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("failure","Errororor");
                                            }
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        
        ImageView farmImage;
        TextView product, quantity, price, streetAddress, distance, seller;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.product_quantity);
            price = itemView.findViewById(R.id.product_price);
            streetAddress = itemView.findViewById(R.id.product_streetaddr);
            distance = itemView.findViewById(R.id.product_distance);
            farmImage = itemView.findViewById(R.id.farmImage);
            seller = itemView.findViewById(R.id.product_seller);
        }
    }
}
