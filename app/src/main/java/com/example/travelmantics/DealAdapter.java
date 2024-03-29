package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {

    ArrayList<TravelDeal> mTravelDeals;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;

    public DealAdapter() {
        //FirebaseUtil.openFirebaseReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        mTravelDeals = FirebaseUtil.mDeals;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
                deal.setId(dataSnapshot.getKey());
                mTravelDeals.add(deal);
                Log.d("Deal title:", deal.getTitle());
                notifyItemInserted(mTravelDeals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).
                inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal = mTravelDeals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return mTravelDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView tvTitle, tvDescription, tvPrice;
        ImageView imageView;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPrice = itemView.findViewById(R.id.tv_price);
            imageView = itemView.findViewById(R.id.img_deal);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal travelDeal) {
            tvTitle.setText(travelDeal.getTitle());
            Log.d("Deal title on bind: ", tvTitle.getText().toString());
            tvDescription.setText(travelDeal.getDescription());
            tvPrice.setText(travelDeal.getPrice());
            Glide.with(itemView.getContext()).load(travelDeal.getImageUrl()).centerCrop().into(imageView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            TravelDeal selectedDeal = mTravelDeals.get(position);
            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra("Deal", selectedDeal);
            view.getContext().startActivity(intent);
        }
    }
}
