package com.PollBuzz.pollbuzz.responses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.transition.Slide;
import androidx.transition.Visibility;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.adapters.HomePageAdapter;
import com.PollBuzz.pollbuzz.objects.ClipFunction;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kinda.alert.KAlertDialog;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuBaseAdapter;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.PollBuzz.pollbuzz.Utils.firebase;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Single_type_response extends AppCompatActivity {

    TextView query, author,card_date,card_status,vote_count,live;
    RadioGroup group;
    Map<String, Integer> options;
    String key;
    Typeface typeface;
    Dialog dialog;
    MaterialButton submit;
    Map<String, Object> response;
    String resp,passed_date,passed_status;
    firebase fb;
    PollDetails polldetails;
    Map<String, Integer> update;
    KAlertDialog dialog1;
    ImageButton share;
    ImageView profile_pic,following,menuhome;
    SpotsDialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_overall);
        View view = getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setGlobals(view);
        showDialog();
        retrieveData();
        setListeners();
    }

    private void setListeners() {

        menuhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resp == null) {
                    Toast.makeText(Single_type_response.this, "Please select a option...", Toast.LENGTH_SHORT).show();
                } else
                    submitResponse(fb);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("timestamp", Timestamp.now().toDate().toString());
                    bundle.putString("UID", key);
                    FirebaseAnalytics.getInstance(Single_type_response.this).logEvent("share_link", bundle);
                    int type = 0;
                    String shareBody = "https://pollbuzz.com/share/" + type + key;
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share link via"));
                } catch (IllegalStateException e) {
                    Toast.makeText(Single_type_response.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
  /*      fav_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog2.show();
                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                {

                                    fav_author.setEnabled(false);
                                    //Log.d(TAG, "Document exists!");
                                    fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //dialog1.dismissWithAnimation();
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(polldetails.getAuthorUID())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("UnSubscribedFrom", polldetails.getAuthorUID());

                                                            }
                                                            dialog2.dismiss();
                                                            fav_author.setEnabled(true);
                                                            Toast.makeText(getApplicationContext(), polldetails.getAuthor() + " removed from favourite authors", Toast.LENGTH_LONG).show();
                                                            fav_author.setImageResource(R.drawable.ic_star_border_dark_24dp);
                                                        }
                                                    });
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // dialog1.dismissWithAnimation();
                                                    dialog2.dismiss();
                                                    fav_author.setEnabled(true);
                                                    Toast.makeText(getApplicationContext(), "Failed " + polldetails.getAuthor() + " removing from favourite authors", Toast.LENGTH_LONG).show();
                                                }
                                            });


                                }
                            } else {
                                //Log.d(TAG, "Document does not exist!");
                                fav_author.setEnabled(false);
                                Map<String, String> map = new HashMap<>();
                                map.put("Username", (polldetails.getAuthor()));
                                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //dialog1.dismissWithAnimation();
                                        FirebaseMessaging.getInstance().subscribeToTopic(polldetails.getAuthorUID())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("SubscribedTo", polldetails.getAuthorUID());

                                                        }
                                                        dialog2.dismiss();
                                                        fav_author.setEnabled(true);
                                                        Toast.makeText(getApplicationContext(), polldetails.getAuthor() + " added to your favourite authors", Toast.LENGTH_LONG).show();
                                                        fav_author.setImageResource(R.drawable.ic_star_gold_24dp);
                                                    }
                                                });
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //dialog1.dismissWithAnimation();
                                                dialog2.dismiss();
                                                fav_author.setEnabled(true);
                                                Toast.makeText(getApplicationContext(), "Failed to add " + polldetails.getAuthor() + " to your favourite authors", Toast.LENGTH_LONG).show();
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

       /* id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String y = polldetails.getPoll_accessID().toString();

                CustomPowerMenu customPowerMenu = new CustomPowerMenu.Builder<>(Single_type_response.this, new IconMenuAdapter())
                        .addItem(new ClipFunction(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_content_copy_black_24dp), y))
                        .setAnimation(MenuAnimation.ELASTIC_CENTER)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .build();
                customPowerMenu.showAsAnchorCenter(view);
            }
        });*/
    }

    private void showPopup(View v) {
            PopupMenu popup = new PopupMenu(Single_type_response.this, v);
            MenuInflater inflater = popup.getMenuInflater();
            if(following.getVisibility() == View.INVISIBLE)
                inflater.inflate(R.menu.menu_home, popup.getMenu());
            if(following.getVisibility() == View.VISIBLE)
                inflater.inflate(R.menu.menu_home_1, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.poll_id:
                            showCodeDialog();
                            return true;
                        case R.id.follow:
                            dialog2.show();
                            if(following.getVisibility() == View.INVISIBLE){
                                Map<String, String> map = new HashMap<>();
                                map.put("Username", (polldetails.getAuthor()));
                                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //dialog1.dismissWithAnimation();
                                        FirebaseMessaging.getInstance().subscribeToTopic(polldetails.getAuthorUID())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            dialog2.dismiss();
                                                            following.setVisibility(View.VISIBLE);
                                                            Toast.makeText(getApplicationContext(), polldetails.getAuthor() + " added to your favourite authors", Toast.LENGTH_LONG).show();

                                                            Log.d("SubscribedTo", polldetails.getAuthorUID());

                                                        }

                                                    }
                                                });
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //dialog1.dismissWithAnimation();
                                                dialog2.dismiss();
                                                Toast.makeText(getApplicationContext(), "Failed to add " + polldetails.getAuthor() + " to your favourite authors", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                            else{

                                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //dialog1.dismissWithAnimation();
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(polldetails.getAuthorUID())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("UnSubscribedFrom", polldetails.getAuthorUID());
                                                            dialog2.dismiss();
                                                            following.setVisibility(View.GONE);
                                                            Toast.makeText(getApplicationContext(), polldetails.getAuthor() + " removed from favourite authors", Toast.LENGTH_LONG).show();

                                                        }

                                                    }
                                                });
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // dialog1.dismissWithAnimation();
                                                dialog2.dismiss();
                                                Toast.makeText(getApplicationContext(), "Failed " + polldetails.getAuthor() + " removing from favourite authors", Toast.LENGTH_LONG).show();
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

    public void showCodeDialog() {
        final Dialog dialog = new Dialog(Single_type_response.this);
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
                Toast toast = Toast.makeText(Single_type_response.this, "Copied to clip board", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) Single_type_response.this.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", code.getText());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }
            }
        });
        dialog.setCancelable(true);

        code.setText(polldetails.getPoll_accessID());

        dialog.show();
        window.setAttributes(lp);

        ImageView shareButton = dialog.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String shareBody = polldetails.getPoll_accessID();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    Single_type_response.this.startActivity(Intent.createChooser(sharingIntent, "Share link via"));
                } catch (IllegalStateException e) {
                    Toast.makeText(Single_type_response.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void submitResponse(firebase fb) {
        showKAlertDialog();
        Integer i = polldetails.getPollcount();
        i++;
        response.put("option", resp);
        Integer p = update.get(resp);
        p++;
        update.put(resp, p);
        fb.getPollsCollection().document(key).update("pollcount", i);
        fb.getPollsCollection().document(key).update("map", update);
        response.put("timestamp", Timestamp.now().getSeconds());

        fb.getPollsCollection().document(key).collection("Response").document(fb.getUserId()).set(response).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> mapi = new HashMap<>();
                    mapi.put("pollId", fb.getUserId());
                    mapi.put("timestamp", Timestamp.now().getSeconds());
                    fb.getUserDocument().collection("Voted").document(key).set(mapi)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Single_type_response.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(Single_type_response.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog1.dismissWithAnimation();
                                    Toast.makeText(Single_type_response.this, "Unable to submit.\nPlease try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Single_type_response.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retrieveData() {
        fb.getPollsCollection()
                .document(key)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot data = task.getResult();
                        if (data != null && data.exists()) {
                            group.removeAllViews();
                            polldetails = data.toObject(PollDetails.class);
                            query.setText(polldetails.getQuestion());
                            options = polldetails.getMap();
                            author.setText(polldetails.getAuthor());
                            card_status.setText(passed_status);
                            card_date.setText(passed_date);
                            if(polldetails.getPic() == null) {
                                profile_pic.setImageResource(R.drawable.ic_person_black_24dp);
                            } else {
                                Glide.with(getApplicationContext())
                                        .load(polldetails.getPic())
                                        .transform(new CircleCrop())
                                        .placeholder(R.drawable.ic_person_black_24dp)
                                        .into(profile_pic);
                            }
                            if(polldetails.isLive()){
                                live.setVisibility(View.VISIBLE);
                            }

                                fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document = task1.getResult();
                                        if (document != null) {
                                            if (document.exists()) {
                                                following.setVisibility(View.VISIBLE);
                                            } else {
                                                following.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    } else {
                                        Toast.makeText(Single_type_response.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                RadioButton button = new RadioButton(getApplicationContext());
                                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(0, 0, 0, 20);
                                button.setPadding(15,20,15,20);
                                button.setLayoutParams(layoutParams);
                                button.setTypeface(typeface);
                                button.setText(entry.getKey());
                                update.put(entry.getKey(), entry.getValue());
                                button.setTextSize(14.0f);
                                button.setTextColor(Color.parseColor("#958ba2"));
                                button.setBackgroundResource(R.drawable.border_gsignin);
                                group.addView(button);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RadioButton b = (RadioButton) v;
                                        if (b.isChecked())
                                            resp = b.getText().toString();
                                    }
                                });
                            }

                            dialog.dismiss();
                            Date date = Calendar.getInstance().getTime();
                            if (polldetails != null) {
                                if (polldetails.isLivePoll()) {
                                    if (polldetails.isLive() && (Timestamp.now().getSeconds() - polldetails.getTimestamp()) > polldetails.getSeconds()) {
                                        polldetails.setLive(false);
                                        fb.getPollsCollection().document(key).update("live", false);
                                        showExpiredDialog();
                                    } else if (!polldetails.isLive()) {
                                        showExpiredDialog();
                                    }
                                } else if (polldetails.getExpiry_date() != null && (polldetails.getExpiry_date().compareTo(date) < 0)) {
                                    Intent intent = new Intent(Single_type_response.this, PercentageResult.class);
                                    intent.putExtra("UID", key);
                                    intent.putExtra("type", "SINGLE CHOICE");
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        } else {
                            finish();
                            Toast.makeText(Single_type_response.this, "This url does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");
        passed_date = intent.getExtras().getString("card_date");
        passed_status = intent.getExtras().getString("card_status");
        Log.d("SingleType", key);
    }


    private void setGlobals(View view) {

        profile_pic = findViewById(R.id.pPic);
        author = findViewById(R.id.author);
        submit = findViewById(R.id.submit);
        query = findViewById(R.id.query);
        card_date = findViewById(R.id.card_date);
        card_status = findViewById(R.id.card_status);
        vote_count = findViewById(R.id.vote_count_no);
        live = findViewById(R.id.live);
        share = view.findViewById(R.id.share);
        group = findViewById(R.id.options);
        following = findViewById(R.id.following);
        menuhome = findViewById(R.id.menu_home);
        options = new HashMap<>();
        response = new HashMap<>();
        update = new HashMap<>();
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.roboto);
        dialog = new Dialog(Single_type_response.this);
        fb = new firebase();
        dialog1 = new KAlertDialog(Single_type_response.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog2 = new SpotsDialog(Single_type_response.this, R.style.Custom);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void showDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);
    }

    private void showKAlertDialog() {
        dialog1.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog1.setTitleText("Uploading your response");
        dialog1.setCancelable(false);
        dialog1.show();
    }

    public class IconMenuAdapter extends MenuBaseAdapter<ClipFunction> {

        @Override
        public View getView(int index, View view, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.copy_clipboard, viewGroup, false);
            }

            ClipFunction item = (ClipFunction) getItem(index);
            final ImageView icon = view.findViewById(R.id.clip_image);
            icon.setImageDrawable(item.getIcon());
            final TextView title = view.findViewById(R.id.clip_id);
            title.setText(item.getTitle());

            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast toast = Toast.makeText(context, "Copied to clip board", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", item.getTitle());
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                    }
                }
            });

            return super.getView(index, view, viewGroup);
        }

    }
    private  void showExpiredDialog()
    {
        Dialog dialog=new Dialog(Single_type_response.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.expired_live_poll_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);
        Button ok=dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(Single_type_response.this, PercentageResult.class);
                i.putExtra("UID", key);
                i.putExtra("type", "SINGLE CHOICE");
                startActivity(i);
                finish();

            }
        });
    }
}
