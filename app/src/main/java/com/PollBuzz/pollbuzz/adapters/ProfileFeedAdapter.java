package com.PollBuzz.pollbuzz.adapters;

import com.PollBuzz.pollbuzz.responses.Descriptive_type_response;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.google.firebase.Timestamp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.PollBuzz.pollbuzz.results.ResultActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ProfileViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    private Context mContext;
    private Boolean bool;

    public ProfileFeedAdapter(Context mContext, ArrayList<PollDetails> mPollDetails, Boolean bool) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
        this.bool = bool;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.card_layout, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        setData(holder, position);
        clickListener(holder, position);
    }

    private void clickListener(@NonNull ProfileViewHolder holder, int position) {
        if (bool) {
            holder.cardV.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, PercentageResult.class);
                intent.putExtra("UID", mPollDetails.get(position).getUID());
                intent.putExtra("type", mPollDetails.get(position).getPoll_type());
                mContext.startActivity(intent);
            });
        } else {
            if(mPollDetails.get(position).getExpiry_date().compareTo(Timestamp.now().toDate())>=0)
            holder.cardV.setOnClickListener(view -> {
                startIntent(mPollDetails.get(position).getUID(),mPollDetails.get(position).getPoll_type());
            });
            else{
                holder.cardV.setOnClickListener(view -> {
                    Toast.makeText(mContext, "This poll has expired!\nYou can't vote this...", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void startIntent(String uid, String pollType) {
        Intent intent;
        switch (pollType) {
            case "SINGLE CHOICE":
                intent = new Intent(mContext, Single_type_response.class);
                break;
            case "MULTI SELECT":
                intent = new Intent(mContext, Multiple_type_response.class);
                break;
            case "DESCRIPTIVE POLL":
                intent = new Intent(mContext, Descriptive_type_response.class);
                break;
            case "RANKED":
                intent = new Intent(mContext, Ranking_type_response.class);
                break;
            case "PICTURE BASED":
                intent = new Intent(mContext, Image_type_responses.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + pollType);
        }
        intent.putExtra("UID", uid);
        mContext.startActivity(intent);
    }

    private void setData(@NonNull ProfileViewHolder holder, int position) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            if (mPollDetails.get(position).getPoll_type() != null)
                holder.card_type.setText(mPollDetails.get(position).getPoll_type());
            if (mPollDetails.get(position).getQuestion() != null)
                holder.card_query.setText(mPollDetails.get(position).getQuestion().trim());
            if (mPollDetails.get(position).getAuthor() != null)
                holder.card_author.setText(mPollDetails.get(position).getAuthor());
            if (mPollDetails.get(position).getCreated_date() != null) {
                String date = df.format(mPollDetails.get(position).getCreated_date());
                holder.card_date.setText(date.trim());
            }

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mPollDetails.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private CardView cardV;
        private TextView card_type, card_query, card_author, card_date;

        ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            setGlobals(itemView);
        }

        private void setGlobals(@NonNull View itemView) {
            cardV = itemView.findViewById(R.id.cardV);
            card_type = itemView.findViewById(R.id.card_type);
            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
        }
    }
}