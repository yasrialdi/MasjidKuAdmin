package com.masjidtrpl.masjidku_admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterKegiatan extends RecyclerView.Adapter<AdapterKegiatan.CardViewHolder> {
    private List<ModelsKegiatan> kegiatan;
    private Context context;

    public AdapterKegiatan(Context context, List<ModelsKegiatan> kegiatan) {
        this.kegiatan = kegiatan;
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kegiatan, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterKegiatan.CardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return kegiatan.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        CardView cardView;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.itemKegiatan_Image);
            title = itemView.findViewById(R.id.itemKegiatan_Title);
            cardView = itemView.findViewById(R.id.itemKegiatan_Cardview);
        }
    }
}
