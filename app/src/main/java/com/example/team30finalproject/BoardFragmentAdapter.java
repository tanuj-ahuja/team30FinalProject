package com.example.team30finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BoardFragmentAdapter extends RecyclerView.Adapter<BoardFragmentAdapter.ViewHolder> {

    ArrayList<BoardFragmentModel> arrayList;

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
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product, quantity, price, streetAddress, distance, seller;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.product_quantity);
            price = itemView.findViewById(R.id.product_price);
            streetAddress = itemView.findViewById(R.id.product_streetaddr);
            distance = itemView.findViewById(R.id.product_distance);
            seller = itemView.findViewById(R.id.product_seller);
        }
    }
}
