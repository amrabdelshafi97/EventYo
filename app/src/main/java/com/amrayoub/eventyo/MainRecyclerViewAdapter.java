package com.amrayoub.eventyo;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Amr Ayoub on 7/30/2017.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.EventsViewHolder> {
    ArrayList<Event_info> myEvents;
    Context mContext;

    MainRecyclerViewAdapter(ArrayList<Event_info> myEvents,Context mContext){
        this.myEvents = myEvents;
        this.mContext=mContext;
    }

    class EventsViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView date;
        TextView title;
        ImageView photo;

        public EventsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.main_rv_cv);
            date = (TextView) itemView.findViewById(R.id.main_rv_date);
            title = (TextView) itemView.findViewById(R.id.main_rv_title);
            photo = (ImageView) itemView.findViewById(R.id.main_rv_photo);
        }
    }
    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_rv_item, parent, false);
        return  new EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventsViewHolder holder, int position) {
        holder.date.setText(myEvents.get(position).getmDate());
        holder.title.setText(myEvents.get(position).getmTilte());
        Picasso.with(mContext).load(myEvents.get(position).getmPhotoUrl()).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return myEvents.size();
    }
}
