package com.PollBuzz.pollbuzz.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.navFragments.ProfileFeed;
import com.PollBuzz.pollbuzz.polls.Single_type_poll;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.PollBuzz.pollbuzz.Utils.firebase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import dmax.dialog.SpotsDialog;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.HomeViewHolder> {

    private ArrayList<PollDetails> mPollDetails;
    private Context mContext;
    FirebaseAnalytics mFirebaseAnalytics;
    firebase fb;
    ArrayList<String> authors = new ArrayList<>();
    SpotsDialog dialog;
    okClicked callBack;
    int flag=0;
    String status="Active";

    public HomePageAdapter(Context mContext, ArrayList<PollDetails> mPollDetails, okClicked callBack) {
        this.mContext = mContext;
        this.mPollDetails = mPollDetails;
        this.callBack=callBack;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        dialog = new SpotsDialog(mContext, R.style.Custom);
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

        holder.cardV.setOnClickListener(view -> {
            Log.d("CardId",mPollDetails.get(position).getUID());
            if (status.equals("Active")) {
                Bundle bundle = new Bundle();
                bundle.putString("user_id", fb.getUserId());
                bundle.putString("poll_id", mPollDetails.get(position).getUID());
                bundle.putString("timestamp", Timestamp.now().toDate().toString());
                mFirebaseAnalytics.logEvent("home_card_vote_clicked", bundle);
//                if (holder.live.getVisibility() == View.VISIBLE) {
//                    showcodedialog(position);
//                } else
                    startIntent(mPollDetails.get(position).getUID(), mPollDetails.get(position).getPoll_type(),holder.card_date.getText().toString().trim(),holder.card_status.getText().toString().trim());
            } if (status.equals("Expired")) {
                Toast.makeText(mContext,"The poll is expired.\nRedirecting you to the poll's result.",Toast.LENGTH_LONG).show();
                Intent i = new Intent(mContext, PercentageResult.class);
                i.putExtra("UID", mPollDetails.get(position).getUID());
                i.putExtra("type", mPollDetails.get(position).getPoll_type());
                mContext.startActivity(i);
            }

        });
        holder.pPicArea.setOnClickListener(v -> {
            Log.d("CardId",mPollDetails.get(position).getUID());
            Bundle bundle = new Bundle();
            bundle.putString("user_id", fb.getUserId());
            bundle.putString("poll_id", mPollDetails.get(position).getUID());
            bundle.putString("timestamp", Timestamp.now().toDate().toString());
            mFirebaseAnalytics.logEvent("home_card_user_clicked", bundle);
            Log.d("HomeAdapter", mPollDetails.get(position).getAuthorUID());
            Fragment profileFeed = ProfileFeed.newInstance(mPollDetails.get(position).getAuthorUID());
            FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
            fm.beginTransaction().add(R.id.container, profileFeed, "profile").addToBackStack("profile").commit();
        });
        holder.menu_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(holder,v,position);
            }
        });
       /* holder.fav_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CardId",mPollDetails.get(position).getUID());
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
                                            Toast.makeText(mContext, mPollDetails.get(position).getAuthor() + " removed from favourite authors", Toast.LENGTH_LONG).show();
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
                                            Log.d("Favourite","Unsubscribing");
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(mPollDetails.get(position).getAuthorUID())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.d("Favourite","Unsubscribed");
                                                            if (task.isSuccessful()) {
                                                                Log.d("UnSubscribedFrom", mPollDetails.get(position).getAuthorUID());

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
                                                    Toast.makeText(mContext, "Failed " + mPollDetails.get(position).getAuthor() + " removing from favourite authors", Toast.LENGTH_LONG).show();
                                                }
                                            });


                                }
                            } else {
                                //Log.d(TAG, "Document does not exist!")
                                Map<String, String> map = new HashMap<>();
                                map.put("Username", (mPollDetails.get(position).getAuthor()));
                                fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //dialog1.dismissWithAnimation();
                                        Toast.makeText(mContext, mPollDetails.get(position).getAuthor() + " added to your favourite authors", Toast.LENGTH_LONG).show();
                                        holder.cardV.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                                        holder.fav_author.setImageResource(R.drawable.ic_star_gold_24dp);
                                        notifyDataSetChanged();
                                        FirebaseMessaging.getInstance().subscribeToTopic(mPollDetails.get(position).getAuthorUID())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("SubscribedTo", mPollDetails.get(position).getAuthorUID());

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
                                                Toast.makeText(mContext, "Failed to add " + mPollDetails.get(position).getAuthor() + " to your favourite authors", Toast.LENGTH_LONG).show();
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
    public void showCodeDialog(int position) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.poll_id_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView code = dialog.findViewById(R.id.code);
        final ImageButton copy = dialog.findViewById(R.id.clip_image);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(mContext, "Copied to clip board", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", code.getText());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }
            }
        });
        dialog.setCancelable(true);

       code.setText(mPollDetails.get(position).getPoll_accessID());

        dialog.show();
        window.setAttributes(lp);

        ImageView shareButton = dialog.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String shareBody = mPollDetails.get(position).getPoll_accessID();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    mContext.startActivity(Intent.createChooser(sharingIntent, "Share link via"));
                } catch (IllegalStateException e) {
                    Toast.makeText(mContext, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void showPopup(HomeViewHolder holder, View v, int position) {
        PopupMenu popup = new PopupMenu(mContext, v);
        MenuInflater inflater = popup.getMenuInflater();

        if(flag==0)
            inflater.inflate(R.menu.menu_home, popup.getMenu());
        else
            inflater.inflate(R.menu.menu_home_1, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.poll_id:
                          showCodeDialog(position);
                        return true;
                    case R.id.follow:
                        dialog.show();
                         if(flag==0)
                         {

                             Map<String, String> map = new HashMap<>();
                             map.put("Username", (mPollDetails.get(position).getAuthor()));
                             fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     //dialog1.dismissWithAnimation();
                                     Toast.makeText(mContext, mPollDetails.get(position).getAuthor() + " added to your favourite authors", Toast.LENGTH_LONG).show();
                                     holder.following.setVisibility(View.VISIBLE);
                                     holder.profilePic.setBackgroundResource(R.drawable.green_boundary_pic);
                                     flag=1;
                                     notifyDataSetChanged();
                                     FirebaseMessaging.getInstance().subscribeToTopic(mPollDetails.get(position).getAuthorUID())
                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if (task.isSuccessful()) {
                                                         Log.d("SubscribedTo", mPollDetails.get(position).getAuthorUID());

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
                                             Toast.makeText(mContext, "Failed to add " + mPollDetails.get(position).getAuthor() + " to your favourite authors", Toast.LENGTH_LONG).show();
                                         }
                                     });

                         }
                         else
                         {

                             fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     //dialog1.dismissWithAnimation();
                                     Toast.makeText(mContext, mPollDetails.get(position).getAuthor() + " removed from favourite authors", Toast.LENGTH_LONG).show();
                                     holder.following.setVisibility(View.GONE);
                                     holder.profilePic.setBackgroundResource(R.drawable.voter_item_outline);
                                     flag=0;
                                     notifyDataSetChanged();
                                            /*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                    if(task.isSuccessful()){
                                                        Log.d("InstanceId",task.getResult().getToken());
                                                    }
                                                }
                                            });*/
                                     Log.d("Favourite","Unsubscribing");
                                     FirebaseMessaging.getInstance().unsubscribeFromTopic(mPollDetails.get(position).getAuthorUID())
                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     Log.d("Favourite","Unsubscribed");
                                                     if (task.isSuccessful()) {
                                                         Log.d("UnSubscribedFrom", mPollDetails.get(position).getAuthorUID());

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
                                             Toast.makeText(mContext, "Failed " + mPollDetails.get(position).getAuthor() + " removing from favourite authors", Toast.LENGTH_LONG).show();
                                         }
                                     });

                         }

                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
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
                        callBack.onItemClicked();
                        //startIntent(mPollDetails.get(position).getUID(), mPollDetails.get(position).getPoll_type());
                    } else {
                        code_type.setError("Incorrect Poll ID");
                        code_type.requestFocus();
                    }

                }
            }
        });
    }

    private void setData(@NonNull HomeViewHolder holder, int position) {
        try {

            holder.live.setVisibility(View.GONE);
            //holder.vote_count.setText(mPollDetails.get(position).getPollcount());
            Log.d("Votes",String.valueOf(mPollDetails.get(position).getPollcount()));
            holder.vote_counter.setText(String.valueOf(mPollDetails.get(position).getPollcount()));

                fb.getUserDocument().collection("Favourite Authors").document(mPollDetails.get(position).getAuthorUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                holder.following.setVisibility(View.VISIBLE);
                                holder.profilePic.setBackgroundResource(R.drawable.green_boundary_pic);
                                flag=1;

                            } else {
                                holder.following.setVisibility(View.GONE);
                                holder.profilePic.setBackgroundResource(R.drawable.voter_item_outline);
                                flag=0;


                            }
                        } else {

                        }
                    }
                });

                //holder.vote_count.setText(mPollDetails.get(position).getPollcount());
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = Calendar.getInstance().getTime();
            if (mPollDetails.get(position).getQuestion() != null)
                holder.card_query.setText(mPollDetails.get(position).getQuestion().trim());
            if (mPollDetails.get(position).getUsername() != null)
                holder.card_author.setText((mPollDetails.get(position).getUsername().trim()));
            if (mPollDetails.get(position).getCreated_date() != null) {
                String date = df.format(mPollDetails.get(position).getCreated_date());
                Log.d("date_new",Long.toString(((date1.getTime() - mPollDetails.get(position).getCreated_date().getTime())/(60*60*1000))));
                Log.d("time",Long.toString(((date1.getTime())/(60*60*1000*24))));
                Log.d("create",Long.toString(((mPollDetails.get(position).getCreated_date().getTime())/(60*60*1000))));
                Log.d("hrs ago",Long.toString(((Timestamp.now().toDate().getTime() - mPollDetails.get(position).getCreated_date().getTime()))/86400000));
                long y = ((Timestamp.now().toDate().getTime() - mPollDetails.get(position).getCreated_date().getTime())*24)/86400000;
                long y1 = ((Timestamp.now().toDate().getTime() - mPollDetails.get(position).getCreated_date().getTime()))/86400000;
                if(y1<=0) {
                    if (y > 0)
                        holder.card_date.setText("• " + y + " hr ago");
                    else
                        holder.card_date.setText("• few minutes ago");
                }
                else{
                    if(y1 == 1)
                     holder.card_date.setText("• " + y1 + " day ago");
                     else
                        holder.card_date.setText("• " + y1 + " days ago");
                }
            }
            Date date = Calendar.getInstance().getTime();
            if (mPollDetails.get(position).isLive() && (Timestamp.now().getSeconds() - mPollDetails.get(position).getTimestamp()) > mPollDetails.get(position).getSeconds()) {
                holder.card_status.setText("• Expired");
                status="Expired";
                holder.live.setVisibility(View.GONE);
                fb.getPollsCollection().document(mPollDetails.get(position).getUID()).update("live",false);
                mPollDetails.get(position).setLive(false);
                notifyDataSetChanged();
            } else if (mPollDetails.get(position).isLive()) {
                Log.d("live", "true");
                holder.live.setVisibility(View.VISIBLE);
                if(mPollDetails.get(position).getSeconds() == Long.MAX_VALUE)
                {
                    holder.card_status.setText("• " + "Custom");
                }
                else {
                    long x=mPollDetails.get(position).getSeconds()-Timestamp.now().getSeconds()+mPollDetails.get(position).getTimestamp();
                    holder.card_status.setText("• " + x + " seconds left");
                }
                status="Active";
            } else {
                holder.live.setVisibility(View.GONE);
                if (mPollDetails.get(position).getExpiry_date() != null && mPollDetails.get(position).getExpiry_date().compareTo(date) >= 0)
                {
                    Date one=mPollDetails.get(position).getExpiry_date();
                    long x =  (one.getTime()-date.getTime())/86400000;
                    if(x>0) {
                        if(x == 1){
                            holder.card_status.setText("• " + x + " day left");
                        }
                        else
                        holder.card_status.setText("• " + x + " days left");
                    }
                    else
                        holder.card_status.setText("• Expires Today" );
                    status="Active";
                }

                else
                {
                    holder.card_status.setText("• Expired");
                    status="Expired";
                }
            }
            if (mPollDetails.get(position).getPic() == null) {
                Log.d("NoPic", String.valueOf(position));
                holder.profilePic.setImageResource(R.drawable.ic_person_black_24dp);
            } else {
                Glide.with(mContext)
                        .load(mPollDetails.get(position).getPic())
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(holder.profilePic);
            }


        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }

    }

    private void startIntent(String uid, String pollType,String card_date,String card_status) {
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
        intent.putExtra("card_date",card_date);
        intent.putExtra("card_status",card_status);

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
        private TextView  card_query, card_author, card_date, card_status, live,vote_counter;
        private ImageView profilePic,following,menu_home;
        private CardView cardV;


        private HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            setGlobals(itemView);
        }

        private void setGlobals(@NonNull View itemView) {

            card_query = itemView.findViewById(R.id.card_query);
            card_author = itemView.findViewById(R.id.card_author);
            card_date = itemView.findViewById(R.id.card_date);
            card_status = itemView.findViewById(R.id.card_status);
            pPicArea = itemView.findViewById(R.id.profileArea);
            profilePic = itemView.findViewById(R.id.pPic);
            live = itemView.findViewById(R.id.live);
            cardV = itemView.findViewById(R.id.cardV);
            following=itemView.findViewById(R.id.following);
            menu_home=itemView.findViewById(R.id.menu_home);
            vote_counter=(TextView)itemView.findViewById(R.id.vote_count_no);
        }
    }
    public interface okClicked {
        void onItemClicked();
    }
}