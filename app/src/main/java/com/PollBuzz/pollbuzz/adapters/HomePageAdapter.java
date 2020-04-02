package com.PollBuzz.pollbuzz.adapters;

import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.responses.Descriptive_type_response;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomeViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    private Context mContext;

    public HomePageAdapter(Context mContext, ArrayList<PollDetails> mPollDetails) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.card_layout_profile_pic, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        setData(holder, position);
        clickListener(holder, position);
    }

    private void clickListener(@NonNull HomeViewHolder holder, int position) {
        holder.voteArea.setOnClickListener(view -> startIntent(mPollDetails.get(position).getUID(), mPollDetails.get(position).getPoll_type()));
        holder.pPicArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ProfileFeed profileFeed=ProfileFeed.newInstance(mPollDetails.get(position).getAuthorUID());
                Fragment profileFeed= ProfileFeed.newInstance(mPollDetails.get(position).getAuthorUID());
                FragmentManager fm=((AppCompatActivity)mContext).getSupportFragmentManager();
                fm.beginTransaction().add(R.id.container,profileFeed,"profile").addToBackStack("profile").commit();
            }
        });
    }

    private void setData(@NonNull HomeViewHolder holder, int position) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            if (mPollDetails.get(position).getPoll_type() != null)
                holder.card_type.setText(mPollDetails.get(position).getPoll_type());
            if (mPollDetails.get(position).getQuestion() != null)
                holder.card_query.setText(mPollDetails.get(position).getQuestion().trim());
            if (mPollDetails.get(position).getUsername() != null)
                holder.card_author.setText((mPollDetails.get(position).getUsername().trim()));
            if (mPollDetails.get(position).getCreated_date() != null)
            {
                String date=df.format(mPollDetails.get(position).getCreated_date());
                holder.card_date.setText(date.trim());
            }
            if(mPollDetails.get(position).getPic()==null){
                Log.d("NoPic",String.valueOf(position));
                holder.profilePic.setImageResource(R.drawable.ic_person_black_24dp);
            } else {
                Glide.with(mContext)
                        .load(mPollDetails.get(position).getPic())
                        .transform(new CircleCrop())
                        .into(holder.profilePic);
            }

        }catch (Exception e){
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void startIntent(String uid, String pollType) {
        Intent intent;
        switch (pollType)
        {
            case "SINGLE CHOICE":
                intent = new Intent(mContext, Single_type_response.class);
                break;
            case "MULTI SELECT":
                intent = new Intent(mContext, Multiple_type_response.class);
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

    @Override
    public int getItemCount() {
        return mPollDetails.size();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout pPicArea;
        private LinearLayout voteArea;
        private TextView card_type, card_query, card_author, card_date;
        private ImageView profilePic;

        private HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            setGlobals(itemView);
        }

        private void setGlobals(@NonNull View itemView) {
            card_type = itemView.findViewById(R.id.card_type);
            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
            pPicArea=itemView.findViewById(R.id.profileArea);
            voteArea=itemView.findViewById(R.id.voteArea);
            profilePic=itemView.findViewById(R.id.pPic);
        }
    }
}