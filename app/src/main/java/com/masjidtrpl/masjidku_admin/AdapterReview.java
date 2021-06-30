package com.masjidtrpl.masjidku_admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.CardViewHolder> {
    private ArrayList<ModelsReview> reviews;
    private Context context;

    public AdapterReview(Context context, ArrayList<ModelsReview> reviews) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReview.CardViewHolder holder, int position) {
        String user = reviews.get(position).getUser();
        String review = reviews.get(position).getReview();

        holder.user.setText(user);
        holder.review.setText(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView user, review;
        CardView cardView;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.itemReview_User);
            review = itemView.findViewById(R.id.itemReview_review);
            cardView = itemView.findViewById(R.id.itemReview_Cardview);
        }
    }
}
