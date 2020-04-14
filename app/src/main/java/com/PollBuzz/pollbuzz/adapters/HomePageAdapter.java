package com.PollBuzz.pollbuzz.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import dmax.dialog.SpotsDialog;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomeViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    private Context mContext;
    FirebaseAnalytics mFirebaseAnalytics;
    firebase fb;
    ArrayList<String> authors=new ArrayList<>();
    SpotsDialog dialog;

    public HomePageAdapter(Context mContext, ArrayList<PollDetails> mPollDetails) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        dialog= new SpotsDialog(mContext,R.style.Custom);
        //dialog.create();
        fb = new firebase();
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
        holder.fav_author.setImageResource(R.drawable.ic_star_border_white_24dp);
        holder.voteArea.setOnClickListener(view -> {
            if(holder.card_status.getText().toString().equals("Active"))
            {
                Bundle bundle = new Bundle();
                bundle.putString("user_id", fb.getUserId());
                bundle.putString("poll_id", mPollDetails.get(position).getUID());
                bundle.putString("timestamp", Timestamp.now().toDate().toString());
                mFirebaseAnalytics.logEvent("home_card_vote_clicked", bundle);
                startIntent(mPollDetails.get(position).getUID(), mPollDetails.get(position).getPoll_type());
            }
            else
            {
               Intent i=new Intent(mContext, PercentageResult.class);
               i.putExtra("UID",mPollDetails.get(position).getUID());
               i.putExtra("type",mPollDetails.get(position).getPoll_type());
               firebase fb=new firebase();
               if(fb.getAuth().getCurrentUser().getUid().equals(mPollDetails.get(position).getAuthorUID()))
               i.putExtra("flag",2);
               else
               i.putExtra("flag",3);
               mContext.startActivity(i);
            }

        });
        holder.pPicArea.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("user_id", fb.getUserId());
            bundle.putString("poll_id", mPollDetails.get(position).getUID());
            bundle.putString("timestamp", Timestamp.now().toDate().toString());
            mFirebaseAnalytics.logEvent("home_card_user_clicked", bundle);
            Log.d("HomeAdapter",mPollDetails.get(position).getAuthorUID());
            Fragment profileFeed =ProfileFeed.newInstance(mPollDetails.get(position).getAuthorUID());
            FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
            fm.beginTransaction().add(R.id.container, profileFeed, "profile").addToBackStack("profile").commit();
        });
        holder.fav_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                {
                                holder.fav_author.setEnabled(false);
                                    //Log.d(TAG, "Document exists!");
                                    fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //dialog1.dismissWithAnimation();
                                            dialog.dismiss();
                                            holder.fav_author.setEnabled(true);
                                          Toast.makeText(mContext,mPollDetails.get(position).getAuthor()+" removed from favourite authors",Toast.LENGTH_LONG).show();
                                            holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                                            holder.fav_author.setImageResource(R.drawable.ic_star_border_white_24dp);
                                            notifyDataSetChanged();
                                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if(task.isSuccessful()){
                                                        Log.d("InstanceId",task.getResult().getToken());
                                                    }
                                                }
                                            });
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(mPollDetails.get(position).getAuthorUID())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.d("UnSubscribedFrom",mPollDetails.get(position).getAuthorUID());
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                   // dialog1.dismissWithAnimation();
                                                    dialog.dismiss();
                                                    holder.fav_author.setEnabled(true);
                                                    Toast.makeText(mContext,"Failed "+mPollDetails.get(position).getAuthor()+" removing from favourite authors",Toast.LENGTH_LONG).show();
                                                }
                                            });


                                }
                            } else {
                                //Log.d(TAG, "Document does not exist!");

                                holder.fav_author.setEnabled(false);
                                Map<String,String> map=new HashMap<>();
                                map.put("Username",(mPollDetails.get(position).getAuthor()));
                                fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //dialog1.dismissWithAnimation();
                                        dialog.dismiss();
                                        holder.fav_author.setEnabled(true);
                                        Toast.makeText(mContext,mPollDetails.get(position).getAuthor()+" added to your favourite authors",Toast.LENGTH_LONG).show();
                                        holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                                        holder.fav_author.setImageResource(R.drawable.ic_star_gold_24dp);
                                        notifyDataSetChanged();
                                        FirebaseMessaging.getInstance().subscribeToTopic(mPollDetails.get(position).getAuthorUID())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Log.d("SubscribedTo",mPollDetails.get(position).getAuthorUID());
                                                        }
                                                    }
                                                });
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //dialog1.dismissWithAnimation();
                                                dialog.dismiss();
                                                holder.fav_author.setEnabled(true);
                                                Toast.makeText(mContext,"Failed to add "+mPollDetails.get(position).getAuthor()+" to your favourite authors",Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            //Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });
            }
        });
    }

    private void setData(@NonNull HomeViewHolder holder, int position) {
        try {
            fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            holder.fav_author.setImageResource(R.drawable.ic_star_gold_24dp);
                            holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                            holder.fav_author.setTooltipText("Remove from favourite Authors");
                        } else {
                            holder.fav_author.setImageResource(R.drawable.ic_star_border_white_24dp);
                            holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                            holder.fav_author.setTooltipText("Add to favourite authors");
                        }
                    } else {

                    }
                }
            });
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            if (mPollDetails.get(position).getPoll_type() != null)
                holder.card_type.setText(mPollDetails.get(position).getPoll_type());
            if (mPollDetails.get(position).getQuestion() != null)
                holder.card_query.setText(mPollDetails.get(position).getQuestion().trim());
            if (mPollDetails.get(position).getUsername() != null)
                holder.card_author.setText((mPollDetails.get(position).getUsername().trim()));
            if (mPollDetails.get(position).getCreated_date() != null) {
                String date = df.format(mPollDetails.get(position).getCreated_date());
                holder.card_date.setText(date.trim());
            }
            Date date = Calendar.getInstance().getTime();
            if(mPollDetails.get(position).getExpiry_date().compareTo(date) >= 0)
                holder.card_status.setText("Active");
            else
                holder.card_status.setText("Expired");
            if (mPollDetails.get(position).getPic() == null) {
                Log.d("NoPic", String.valueOf(position));
                holder.profilePic.setImageResource(R.drawable.ic_person_black_24dp);
            } else {
                Glide.with(mContext)
                        .load(mPollDetails.get(position).getPic())
                        .transform(new CircleCrop())
                        .into(holder.profilePic);
            }

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
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
        private ImageButton fav_author;
        private TextView card_type, card_query, card_author, card_date,card_status;
        private ImageView profilePic;
        private CardView cardV;

        private HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            setGlobals(itemView);
        }

        private void setGlobals(@NonNull View itemView) {
            card_type = itemView.findViewById(R.id.card_type);
            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
            card_status=itemView.findViewById(R.id.card_status);
            pPicArea = itemView.findViewById(R.id.profileArea);
            voteArea = itemView.findViewById(R.id.voteArea);
            profilePic = itemView.findViewById(R.id.pPic);
            fav_author=itemView.findViewById(R.id.fav_author);
            cardV=itemView.findViewById(R.id.cardV);
        }
    }
}