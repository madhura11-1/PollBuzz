package com.PollBuzz.pollbuzz.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.VoteDetails;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.results.Descriptive_type_result;
import com.PollBuzz.pollbuzz.results.Image_type_result;
import com.PollBuzz.pollbuzz.results.Multiple_type_result;
import com.PollBuzz.pollbuzz.results.Ranking_type_result;
import com.PollBuzz.pollbuzz.results.Single_type_result;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Utils.firebase;
import dmax.dialog.SpotsDialog;

public class FavouriteAuthorAdapter extends RecyclerView.Adapter<FavouriteAuthorAdapter.VoterViewHolder> {

    private ArrayList<Map<String, Object>> authorDetails;
    private Context mContext;
    firebase fb;
    FirebaseAnalytics mFirebaseAnalytics;

    public FavouriteAuthorAdapter(Context mContext, ArrayList<Map<String, Object>>mDetails) {
        this.mContext = mContext;
        this.authorDetails = mDetails;
        fb=new firebase();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public VoterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.voterlistitem, parent, false);
        return new VoterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoterViewHolder holder, int position) {
        setData(holder, position);
        clickListener(holder, position);
    }

    private void clickListener(@NonNull VoterViewHolder holder, int position) {
        holder.card_view.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", fb.getUserId());
            mFirebaseAnalytics.logEvent("favourite_clicked", bundle);
            Log.d("HomeAdapter",authorDetails.get(position).get("username").toString());
            Fragment profileFeed = ProfileFeed.newInstance(authorDetails.get(position).get("UID").toString());
            FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
            fm.beginTransaction().add(R.id.container, profileFeed, "profile").addToBackStack("profile").commit();
        });
    }

    private void setData(@NonNull VoterViewHolder holder, int position) {
        try {
            if (authorDetails.get(position).containsKey("username"))
                holder.voterUsername.setText(authorDetails.get(position).get("username").toString());
            if (!authorDetails.get(position).containsKey("pic")) {
                holder.voterPhoto.setImageResource(R.drawable.ic_person_black_24dp);
            } else {
                Glide.with(mContext)
                        .load(authorDetails.get(position).get("pic").toString())
                        .transform(new CircleCrop())
                        .into(holder.voterPhoto);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }



    @Override
    public int getItemCount() {
        return authorDetails.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class VoterViewHolder extends RecyclerView.ViewHolder {

        private TextView voterUsername;
        private CardView card_view;
        private ImageView voterPhoto;

        VoterViewHolder(@NonNull View itemView) {
            super(itemView);
            setGlobals(itemView);
        }

        private void setGlobals(@NonNull View itemView) {
            card_view = itemView.findViewById(R.id.card_view);
            voterUsername = itemView.findViewById(R.id.voterUsername);
            voterPhoto = itemView.findViewById(R.id.voterPhoto);
        }
    }
}