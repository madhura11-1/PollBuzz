package com.PollBuzz.pollbuzz.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.VoteDetails;
import com.PollBuzz.pollbuzz.results.Descriptive_type_result;
import com.PollBuzz.pollbuzz.results.Image_type_result;
import com.PollBuzz.pollbuzz.results.Multiple_type_result;
import com.PollBuzz.pollbuzz.results.Ranking_type_result;
import com.PollBuzz.pollbuzz.results.Single_type_result;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

public class VoterPageAdapter extends RecyclerView.Adapter<VoterPageAdapter.VoterViewHolder> {

    private List<VoteDetails> mVotedetails;
    private Context mContext;

    public VoterPageAdapter(Context mContext, List<VoteDetails> mVoteDetails) {
        this.mContext = mContext;
        this.mVotedetails = mVoteDetails;
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
        holder.card_view.setOnClickListener(view -> startActivity(mVotedetails.get(position).getUserId().trim(), mVotedetails.get(position).getOption().trim(), mVotedetails.get(position).getPollId().trim()));
    }

    private void setData(@NonNull VoterViewHolder holder, int position) {
        try {
            if (mVotedetails.get(position).getUsername() != null)
                holder.voterUsername.setText(mVotedetails.get(position).getUsername().trim());
            if (mVotedetails.get(position).getProgfileUrl() == null) {
                holder.voterPhoto.setImageResource(R.drawable.ic_person_black_24dp);
            } else {
                Glide.with(mContext)
                        .load(mVotedetails.get(position).getProgfileUrl())
                        .transform(new CircleCrop())
                        .into(holder.voterPhoto);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void startActivity(String useruid, String option, String uid) {
        Intent intent;
        switch (option) {
            case "SINGLE CHOICE":
                intent = new Intent(mContext, Single_type_result.class);
                break;
            case "MULTI SELECT":
                intent = new Intent(mContext, Multiple_type_result.class);
                break;
            case "DESCRIPTIVE POLL":
                intent = new Intent(mContext, Descriptive_type_result.class);
                break;
            case "RANKED":
                intent = new Intent(mContext, Ranking_type_result.class);
                break;
            case "PICTURE BASED":
                intent = new Intent(mContext, Image_type_result.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + option);
        }
        intent.putExtra("UID", uid);
        intent.putExtra("UIDUser", useruid);
        intent.putExtra("flag", 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mVotedetails.size();
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