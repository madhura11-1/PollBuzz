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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Utils.firebase;
import dmax.dialog.SpotsDialog;

public class FavouritePageAdapter extends RecyclerView.Adapter<FavouritePageAdapter.HomeViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    private Context mContext;
    FirebaseAnalytics mFirebaseAnalytics;
    firebase fb;
    ArrayList<String> authors=new ArrayList<>();
    SpotsDialog dialog;

    public FavouritePageAdapter(Context mContext, ArrayList<PollDetails> mPollDetails) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        dialog= new SpotsDialog(mContext,R.style.Custom);
        dialog.setCancelable(false);
        setHasStableIds(true);
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
                if(holder.live.getVisibility() == View.VISIBLE){
                    showcodedialog(mPollDetails.get(position).getUID(),position);
                }
                else
                    startIntent(mPollDetails.get(position).getUID(), mPollDetails.get(position).getPoll_type());
            }
            else
            {
                Intent i=new Intent(mContext, PercentageResult.class);
                i.putExtra("UID",mPollDetails.get(position).getUID());
                i.putExtra("type",mPollDetails.get(position).getPoll_type());
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
        /*holder.fav_author.setOnClickListener(new View.OnClickListener() {
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
                                    //Log.d(TAG, "Document exists!");
                                    fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //dialog1.dismissWithAnimation();


                                            Toast.makeText(mContext,mPollDetails.get(position).getAuthor()+" removed from favourite authors",Toast.LENGTH_LONG).show();
                                            holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                                            holder.fav_author.setImageResource(R.drawable.ic_star_border_white_24dp);
                                            notifyDataSetChanged();
                                            *//*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if(task.isSuccessful()){
                                                        Log.d("InstanceId",task.getResult().getToken());
                                                    }
                                                }
                                            });*//*
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(mPollDetails.get(position).getAuthorUID())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.d("UnSubscribedFrom",mPollDetails.get(position).getAuthorUID());

                                                            }
                                                            dialog.dismiss();
                                                        }
                                                    });
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // dialog1.dismissWithAnimation();
                                                    dialog.dismiss();
//                                                    holder.fav_author.setEnabled(true);
                                                    Toast.makeText(mContext,"Failed "+mPollDetails.get(position).getAuthor()+" removing from favourite authors",Toast.LENGTH_LONG).show();
                                                }
                                            });


                                }
                            } else {
                                //Log.d(TAG, "Document does not exist!");

//
                                Map<String,String> map=new HashMap<>();
                                map.put("Username",(mPollDetails.get(position).getAuthor()));
                                fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //dialog1.dismissWithAnimation();

//
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
                                                        dialog.dismiss();
                                                    }
                                                });
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //dialog1.dismissWithAnimation();
                                                dialog.dismiss();
//                                                holder.fav_author.setEnabled(true);
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
        });*/
    }

    private void showcodedialog(String uid, int position) {

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
                if(code_type.getText().toString().isEmpty()){
                    code_type.setError("Please enter the code to access the poll");
                    code_type.requestFocus();
                }
                else {
                    if(code_type.getText().toString().equals(mPollDetails.get(position).getPoll_accessID())){
                        dialog.dismiss();
                        startIntent(mPollDetails.get(position).getUID(), mPollDetails.get(position).getPoll_type());
                    }
                    else{
                        code_type.setError("Incorrect Poll ID");
                        code_type.requestFocus();
                    }
                }
            }
        });
    }
    private void setData(@NonNull HomeViewHolder holder, int position) {
        try {
            holder.fav_author.setImageResource(R.drawable.ic_star_border_white_24dp);
            holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.fav_author.setVisibility(View.GONE);
            holder.live.setVisibility(View.GONE);
            /*if(fb.getUserId().equals(mPollDetails.get(position).getAuthorUID()))
            {
                holder.fav_author.setVisibility(View.GONE);
            }
            else
            {
                fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                holder.fav_author.setImageResource(R.drawable.ic_star_gold_24dp);
                                holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    holder.fav_author.setTooltipText("Remove from favourite Authors");
                                }
                            } else {
                                holder.fav_author.setImageResource(R.drawable.ic_star_border_white_24dp);
                                holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    holder.fav_author.setTooltipText("Add to favourite authors");
                                }
                            }
                        } else {

                        }
                    }
                });
            }*/
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
            if (mPollDetails.get(position).isLive() && (Timestamp.now().getSeconds() - mPollDetails.get(position).getTimestamp()) > mPollDetails.get(position).getSeconds()) {
                holder.card_status.setText("Expired");
                holder.live.setVisibility(View.GONE);
                fb.getPollsCollection().document(mPollDetails.get(position).getUID()).update("live",false);
                mPollDetails.get(position).setLive(false);
                notifyDataSetChanged();
            } else if (mPollDetails.get(position).isLive()) {
                Log.d("live", "true");
                holder.live.setVisibility(View.VISIBLE);
                holder.card_status.setText("Active");
            } else {
                holder.live.setVisibility(View.GONE);
                if (mPollDetails.get(position).getExpiry_date() != null && mPollDetails.get(position).getExpiry_date().compareTo(date) >= 0)
                    holder.card_status.setText("Active");
                else
                    holder.card_status.setText("Expired");
            }
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout pPicArea;
        private LinearLayout voteArea;
        private ImageButton fav_author;
        private TextView card_type, card_query, card_author, card_date,card_status,live;
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
            fav_author.setVisibility(View.GONE);
            live = itemView.findViewById(R.id.live);
            cardV=itemView.findViewById(R.id.cardV);

        }
    }
}