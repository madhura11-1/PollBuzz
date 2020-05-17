package com.PollBuzz.pollbuzz.responses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.objects.ClipFunction;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.PollBuzz.pollbuzz.Utils.firebase;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class Image_type_responses extends AppCompatActivity {

    TextView query, author,card_status,card_date,live,vote_count;
    ImageView image1, image2,menu_button,profile_pic,following;
    RadioGroup group;
    RadioButton b1, b2;
    MaterialButton submit;
    Map<String, Integer> options, update;
    Map<String, Object> response;
    String key, imageoption1, imageoption2,passed_date,passes_status;
    Typeface typeface;
    Dialog dialog;
    firebase fb;
    ImageButton share,back;
    PollDetails polldetails;
    KAlertDialog dialog1;
    SpotsDialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_type_responses);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_overall);
        View view = getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setGlobals(view);
        setListeners();
        showDialog();
        retrieveData();
    }

    private void setListeners() {
        b1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    b2.setChecked(false);
                }
            }
        });
        b2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    b1.setChecked(false);
                }
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b1.setChecked(true);
                b2.setChecked(false);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b2.setChecked(true);
                b1.setChecked(false);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("timestamp", Timestamp.now().toDate().toString());
                    bundle.putString("UID", key);
                    FirebaseAnalytics.getInstance(Image_type_responses.this).logEvent("share_link", bundle);
                    int type = 3;
                    String shareBody = "https://pollbuzz.com/share/" + type + key;
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share link via"));
                } catch (IllegalStateException e) {
                    Toast.makeText(Image_type_responses.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Image_type_responses.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(b1.isChecked() || b2.isChecked())) {
                    Toast.makeText(Image_type_responses.this, "Please section a option...", Toast.LENGTH_SHORT).show();
                } else
                    submitResponse();

            }
        });

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
    }
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(Image_type_responses.this, v);
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
                                    FirebaseMessaging.getInstance().subscribeToTopic(polldetails.getAuthorUID())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog2.dismiss();
                                                        following.setVisibility(View.VISIBLE);
                                                        profile_pic.setBackgroundResource(R.drawable.green_boundary_pic);
                                                        Toast.makeText(getApplicationContext(), polldetails.getAuthor() + " added to your favourite authors", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog2.dismiss();
                                            Toast.makeText(getApplicationContext(), "Failed to add " + polldetails.getAuthor() + " to your favourite authors", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        else{

                            fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(polldetails.getAuthorUID())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("UnSubscribedFrom", polldetails.getAuthorUID());
                                                        dialog2.dismiss();
                                                        following.setVisibility(View.INVISIBLE);
                                                        profile_pic.setBackgroundResource(R.drawable.voter_item_outline);
                                                        Toast.makeText(getApplicationContext(), polldetails.getAuthor() + " removed from favourite authors", Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            });
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
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
        final Dialog dialog = new Dialog(Image_type_responses.this);
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
                Toast toast = Toast.makeText(Image_type_responses.this, "Copied to clip board", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) Image_type_responses.this.getSystemService(Context.CLIPBOARD_SERVICE);
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
                    Image_type_responses.this.startActivity(Intent.createChooser(sharingIntent, "Share link via"));
                } catch (IllegalStateException e) {
                    Toast.makeText(Image_type_responses.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void submitResponse() {

        showKAlertDialog();
        Integer i = polldetails.getPollcount();
        i++;
        Integer p = 0;
        fb.getPollsCollection().document(key).update("pollcount", i);
        if (b1.isChecked()) {
            response.put("option", b1.getText().toString().trim());
            p = update.get(imageoption1);
            p++;
            update.put(imageoption1, p);
        }
        if (b2.isChecked()) {
            response.put("option", b2.getText().toString().trim());
            p = update.get(imageoption2);
            p++;
            update.put(imageoption2, p);
        }
        fb.getPollsCollection().document(key).update("map", update);
        if (fb.getUser() != null) {
            response.put("timestamp", Timestamp.now().getSeconds());
            fb.getPollsCollection().document(key).collection("Response")
                    .document(fb.getUserId()).set(response).addOnSuccessListener(aVoid -> {
                Map<String, Object> mapi = new HashMap<>();
                mapi.put("pollId", fb.getUserId());
                mapi.put("timestamp", Timestamp.now().getSeconds());
                fb.getUsersCollection().document(fb.getUserId()).collection("Voted").document(key).set(mapi).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog1.dismissWithAnimation();
                            Toast.makeText(Image_type_responses.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                            Intent i1 = new Intent(Image_type_responses.this, MainActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i1);
                        } else {
                            dialog1.dismissWithAnimation();
                            Toast.makeText(Image_type_responses.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog1.dismissWithAnimation();
                            Toast.makeText(Image_type_responses.this, "Unable to submit.\nPlease try again ", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void retrieveData() {
        fb.getPollsCollection().document(key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                polldetails = snapshot.toObject(PollDetails.class);
                                query.setText(polldetails.getQuestion().trim());
                                options = polldetails.getMap();
                                author.setText(polldetails.getAuthor());
                                card_status.setText(passes_status);
                                card_date.setText(passed_date);
                                vote_count.setText(polldetails.getPollcount().toString());
                                fb.getUsersCollection().document(polldetails.getAuthorUID()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                String url = documentSnapshot.getString("pic");
                                                if(url != null) {
                                                    Glide.with(getApplicationContext())
                                                            .load(url)
                                                            .transform(new CircleCrop())
                                                            .placeholder(R.drawable.ic_person_black_24dp)
                                                            .into(profile_pic);
                                                }
                                                else{
                                                    profile_pic.setImageResource(R.drawable.ic_person_black_24dp);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Image_type_responses.this, "Cannot Load your profile pic", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                if(polldetails.isLive()){
                                    live.setVisibility(View.VISIBLE);
                                }
                                    fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    following.setVisibility(View.VISIBLE);
                                                    profile_pic.setBackgroundResource(R.drawable.green_boundary_pic);
                                                } else {
                                                    following.setVisibility(View.INVISIBLE);
                                                    profile_pic.setBackgroundResource(R.drawable.voter_item_outline);
                                                }
                                            } else {
                                                Toast.makeText(Image_type_responses.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                int i = 0;
                                for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                    update.put(entry.getKey(), entry.getValue());
                                    if (i == 0) {
                                        loadProfilePic(image1, entry.getKey());
                                        imageoption1 = entry.getKey();
                                    }
                                    if (i == 1) {
                                        loadProfilePic(image2, entry.getKey());
                                        imageoption2 = entry.getKey();
                                    }
                                    i++;

                                }
                                dialog.dismiss();
                                Date date = Calendar.getInstance().getTime();
                                if (polldetails != null) {
                                    if (polldetails.isLivePoll()) {
                                        if (polldetails.isLive() && (Timestamp.now().getSeconds() - polldetails.getTimestamp()) > polldetails.getSeconds()) {
                                            polldetails.setLive(false);
                                            fb.getPollsCollection().document(key).update("live", false);
                                            showExpiredDialog();
                                        } else if (!polldetails.isLive())
                                            showExpiredDialog();
                                    } else if (polldetails.getExpiry_date() != null && (polldetails.getExpiry_date().compareTo(date) < 0)) {
                                        Intent intent = new Intent(Image_type_responses.this, PercentageResult.class);
                                        intent.putExtra("UID", key);
                                        intent.putExtra("type", "PICTURE BASED");
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            } else {
                                finish();
                                Toast.makeText(Image_type_responses.this, "This url does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");
        passed_date = intent.getExtras().getString("card_date");
        passes_status = intent.getExtras().getString("card_status");
    }

    private void setGlobals(View view) {

        group = findViewById(R.id.options);
        options = new HashMap<>();
        response = new HashMap<>();
        update = new HashMap<>();
        following = findViewById(R.id.following);
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Image_type_responses.this);
        query = findViewById(R.id.query);
        submit = findViewById(R.id.submit);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        b1 = findViewById(R.id.option1);
        b2 = findViewById(R.id.option2);
        fb = new firebase();
        dialog1 = new KAlertDialog(Image_type_responses.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog2 = new SpotsDialog(Image_type_responses.this, R.style.Custom);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        author = findViewById(R.id.author);
        live = findViewById(R.id.live);
        share = view.findViewById(R.id.share);
        back = view.findViewById(R.id.back_overall);
        card_date = findViewById(R.id.card_date);
        card_status = findViewById(R.id.card_status);
        menu_button = findViewById(R.id.menu_home);
        profile_pic = findViewById(R.id.pPic);
        vote_count = findViewById(R.id.vote_count_no);
    }


    private void loadProfilePic(ImageView view, String url) {
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .transform(new RoundedCorners(50))
                    .into(view);
        } else {
            view.setImageResource(R.drawable.place_holder);
        }
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
    private  void showExpiredDialog()
    {
        Dialog dialog=new Dialog(Image_type_responses.this);
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
                Intent i = new Intent(Image_type_responses.this, PercentageResult.class);
                i.putExtra("UID", key);
                i.putExtra("type", "PICTURE BASED");
                startActivity(i);
                finish();

            }
        });



    }
}
