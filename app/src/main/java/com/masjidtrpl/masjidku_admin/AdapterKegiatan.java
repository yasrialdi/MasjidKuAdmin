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
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdapterKegiatan extends RecyclerView.Adapter<AdapterKegiatan.CardViewHolder> {
    private ArrayList<ModelsKegiatan> kegiatan;
    private Context context;

    public interface dataListener{
        void onDeleteData(ModelsKegiatan modelsKegiatan, int position);
    }

    dataListener listener;

    public AdapterKegiatan(Context context, ArrayList<ModelsKegiatan> kegiatan) {
        this.kegiatan = kegiatan;
        this.context = context;
        listener = (MainMasjidActivity) context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kegiatan, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterKegiatan.CardViewHolder holder, int position) {
        final String judul = kegiatan.get(position).getTitle();
        final String img1 = kegiatan.get(position).getTitle();

        Glide.with(context).load(img1).into(holder.img);
        holder.title.setText(judul);

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String[] action = {"Ubah Data","Hapus Data"};
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Bundle bundle = new Bundle();
                                bundle.putString("dataJudul", kegiatan.get(position).getTitle());
                                bundle.putString("dataDeskripsi", kegiatan.get(position).getDesc());
                                bundle.putString("dataImg1", kegiatan.get(position).getImgUrl1());
                                bundle.putString("dataImg2", kegiatan.get(position).getImgUrl2());
                                bundle.putString("dataImg3", kegiatan.get(position).getImgUrl3());
                                Intent intent = new Intent(v.getContext(), DetailKegiatanActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                break;
                            case 1:
                                listener.onDeleteData(kegiatan.get(position), position);
                                break;
                        }
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });
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
