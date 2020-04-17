package com.PollBuzz.pollbuzz.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.responses.Descriptive_type_response;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import Utils.firebase;

public class ProfileFeedAdapter extends RecyclerView.Adapter<ProfileFeedAdapter.ProfileViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    private Context mContext;
    private Boolean bool;
    private firebase fb;

    public ProfileFeedAdapter(Context mContext, ArrayList<PollDetails> mPollDetails, Boolean bool) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
        this.bool = bool;
        fb = new firebase();
        setHasStableIds(true);
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
                intent.putExtra("selfVote", true);
                mContext.startActivity(intent);
            });
        } else {
            if (mPollDetails.get(position).getExpiry_date() != null) {
                if (mPollDetails.get(position).getExpiry_date().compareTo(Timestamp.now().toDate()) >= 0)
                    holder.cardV.setOnClickListener(view -> {
                        startIntent(mPollDetails.get(position).getUID(), mPollDetails.get(position).getPoll_type());
                    });
                else {
                    holder.cardV.setOnClickListener(view -> {
                        Intent i = new Intent(mContext, PercentageResult.class);
                        i.putExtra("UID", mPollDetails.get(position).getUID());
                        i.putExtra("type", mPollDetails.get(position).getPoll_type());
                        mContext.startActivity(i);
                        Toast.makeText(mContext, "This poll has expired!", Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                holder.cardV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", fb.getUserId());
                        bundle.putString("poll_id", mPollDetails.get(position).getUID());
                        bundle.putString("timestamp", Timestamp.now().toDate().toString());
                        FirebaseAnalytics.getInstance(mContext).logEvent("profile_card_vote_clicked", bundle);
                        if (holder.live.getVisibility() == View.VISIBLE) {
                            showcodedialog(position);
                        } else {
                            Intent i = new Intent(mContext, PercentageResult.class);
                            i.putExtra("UID", mPollDetails.get(position).getUID());
                            i.putExtra("type", mPollDetails.get(position).getPoll_type());
                            mContext.startActivity(i);
                            Toast.makeText(mContext, "This poll has ended!.", Toast.LENGTH_SHORT).show();
                        }
                    }
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
            if (mPollDetails.get(position).isLive() && (Timestamp.now().getSeconds() - mPollDetails.get(position).getTimestamp()) > mPollDetails.get(position).getSeconds()) {
                holder.live.setVisibility(View.GONE);
                fb.getPollsCollection().document(mPollDetails.get(position).getUID()).update("live", false);
                mPollDetails.get(position).setLive(false);
                notifyDataSetChanged();
            } else if (mPollDetails.get(position).isLive()) {
                Log.d("live", "true");
                holder.live.setVisibility(View.VISIBLE);
            } else {
                holder.live.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mPollDetails.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        private CardView cardV;
        private TextView card_type, card_query, card_author, card_date, live;

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
            live = itemView.findViewById(R.id.live);
        }
    }

    private void showcodedialog(int position) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.code_type_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextInputEditText code_type = dialog.findViewById(R.id.code_type);
        MaterialButton ok = dialog.findViewById(R.id.ok);

        dialog.show();
        window.setAttributes(lp);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code_type.getText().toString().isEmpty()) {
                    code_type.setError("Please enter the code to access the poll");
                    code_type.requestFocus();
                } else {
                    if (code_type.getText().toString().equals(mPollDetails.get(position).getPoll_accessID())) {
                        dialog.dismiss();
                        startIntent(mPollDetails.get(position).getUID(), mPollDetails.get(position).getPoll_type());
                    } else {
                        code_type.setError("Incorrect Poll ID");
                        code_type.requestFocus();
                    }

                }
            }
        });
    }

}