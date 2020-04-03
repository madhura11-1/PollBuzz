package com.PollBuzz.pollbuzz.adapters;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.results.Descriptive_type_result;
import com.PollBuzz.pollbuzz.results.Image_type_result;
import com.PollBuzz.pollbuzz.results.Multiple_type_result;
import com.PollBuzz.pollbuzz.results.Ranking_type_result;
import com.PollBuzz.pollbuzz.results.Single_type_result;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class VotedFeedAdapter extends RecyclerView.Adapter<VotedFeedAdapter.VotedViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    private Context mContext;

    public VotedFeedAdapter(Context mContext, ArrayList<PollDetails> mPollDetails) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
    }

    @NonNull
    @Override
    public VotedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.card_layout, parent, false);
        return new VotedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VotedViewHolder holder, int position) {
        setData(holder, position);
        clickListener(holder, position);
    }

    private void clickListener(@NonNull VotedViewHolder holder, int position) {
        holder.cardView.setOnClickListener(view -> startIntent(mPollDetails.get(position).getUID().trim(), mPollDetails.get(position).getPoll_type().trim()));
    }

    private void setData(@NonNull VotedViewHolder holder, int position) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            if (mPollDetails.get(position).getPoll_type() != null)
                holder.card_type.setText(mPollDetails.get(position).getPoll_type());
            if (mPollDetails.get(position).getQuestion() != null)
                holder.card_query.setText(mPollDetails.get(position).getQuestion().trim());
            if (mPollDetails.get(position).getAuthor() != null)
                holder.card_author.setText(mPollDetails.get(position).getAuthor().trim());
            if (mPollDetails.get(position).getCreated_date() != null)
            {
                String date=df.format(mPollDetails.get(position).getCreated_date());
                holder.card_date.setText(date.trim());
            }

        }catch(Exception e){
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void startIntent(String uid, String pollType) {
        Intent intent;
        switch (pollType)
        {
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
                throw new IllegalStateException("Unexpected value: " + pollType);
        }
        intent.putExtra("UID", uid);
        intent.putExtra("flag", 0);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mPollDetails.size();
    }

    static class VotedViewHolder extends RecyclerView.ViewHolder {

        private TextView card_title, card_type, card_query, card_author, card_date;
        private CardView cardView;

        VotedViewHolder(@NonNull View itemView) {
            super(itemView);
            setGlobals(itemView);
        }

        private void setGlobals(@NonNull View itemView) {
            card_type = itemView.findViewById(R.id.card_type);
            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
            cardView = itemView.findViewById(R.id.cardV);
        }
    }
}