package com.btomusic.testmusicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btomusic.testmusicplayer.MainScreen;
import com.btomusic.testmusicplayer.Model.Tracks;
import com.btomusic.testmusicplayer.R;
import com.btomusic.testmusicplayer.listener.ItemClickListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SongTracksList extends RecyclerView.Adapter<SongTracksList.SongTracksViewHolder> {

    private Context context;
    List<Tracks> tracksList;
    ItemClickListener itemClickListener;


    public SongTracksList(Context context, List<Tracks> tracksList) {
        this.context = context;
        this.tracksList = tracksList;
    }

    @NonNull
    @Override
    public SongTracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.custom_layout,parent,false);
        return new SongTracksViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull SongTracksViewHolder holder, int position) {
        Tracks model = tracksList.get(position);
        holder.partsTxt.setText(model.getParts());
        holder.timeTxt.setText(convertToMMSS(model.getTime()));

        if (model.isPlaying()){
            holder.playpause.setImageResource(R.drawable.ic_baseline_pause_24);
        }else {
            holder.playpause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }
    public static String convertToMMSS(long duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    public class SongTracksViewHolder extends RecyclerView.ViewHolder{

        public ImageView playpause;
        TextView partsTxt,timeTxt;

        public SongTracksViewHolder(@NonNull View itemView) {
            super(itemView);
            playpause = itemView.findViewById(R.id.pause_play);
            partsTxt = itemView.findViewById(R.id.parts);
            timeTxt = itemView.findViewById(R.id.time);
            playpause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null){
                        itemClickListener.onPlayPauseClick(getAdapterPosition(),itemView);
                    }
                }
            });
            partsTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(getAdapterPosition(),itemView);
                    }
                }
            });
        }
    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
