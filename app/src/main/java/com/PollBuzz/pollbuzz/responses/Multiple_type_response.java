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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class Multiple_type_response extends AppCompatActivity {
    TextView query, author, card_date, card_status, vote_count, live;
    LinearLayout group;
    Map<String, Integer> options;
    String key, passed_date, passed_status;
    Typeface typeface;
    Dialog dialog;
    Button submit;
    ImageButton share,back_overall;
    firebase fb;
    int c;
    Map<String, Integer> update;
    PollDetails polldetails;
    Map<String, Object> response;
    KAlertDialog dialog1;
    SpotsDialog dialog2;
    ImageView profilePic, menu_home, following;
    int checked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_type_response);
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

        menu_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        submit.setOnClickListener(v -> {
            if (response.size() > 0)
                submitResponse();
            else
                Toast.makeText(Multiple_type_response.this, "Please select atleast one option...", Toast.LENGTH_SHORT).show();
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("timestamp", Timestamp.now().toDate().toString());
                    bundle.putString("UID", key);
                    FirebaseAnalytics.getInstance(Multiple_type_response.this).logEvent("share_link", bundle);
                    int type = 1;
                    String shareBody = "https://pollbuzz.com/share/" + type + key;
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share link via"));
                } catch (IllegalStateException e) {
                    Toast.makeText(Multiple_type_response.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back_overall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Multiple_type_response.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(Multiple_type_response.this, v);
        MenuInflater inflater = popup.getMenuInflater();
        if (following.getVisibility() == View.INVISIBLE)
            inflater.inflate(R.menu.menu_home, popup.getMenu());
        if (following.getVisibility() == View.VISIBLE)
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
                        if (following.getVisibility() == View.INVISIBLE) {
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
                                                        profilePic.setBackgroundResource(R.drawable.green_boundary_pic);
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
                        } else {

                            fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(polldetails.getAuthorUID())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog2.dismiss();
                                                        following.setVisibility(View.INVISIBLE);
                                                        profilePic.setBackgroundResource(R.drawable.voter_item_outline);
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

    private void showCodeDialog() {
        final Dialog dialog = new Dialog(Multiple_type_response.this);
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
                Toast toast = Toast.makeText(Multiple_type_response.this, "Copied to clip board", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) Multiple_type_response.this.getSystemService(Context.CLIPBOARD_SERVICE);
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
                    Multiple_type_response.this.startActivity(Intent.createChooser(sharingIntent, "Share link via"));
                } catch (IllegalStateException e) {
                    Toast.makeText(Multiple_type_response.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");
        passed_date = intent.getExtras().getString("card_date");
        passed_status = intent.getExtras().getString("card_status");
    }

    private void submitResponse() {
        showKAlertDialog();
        Integer p = polldetails.getPollcount();
        p++;
        for (Map.Entry<String, Object> e : response.entrySet()) {
            Integer i = update.get(e.getValue());
            i++;
            update.put(e.getValue().toString(), i);
        }
        fb.getPollsCollection().document(key).update("pollcount", p);
        fb.getPollsCollection().document(key).update("map", update);
        response.put("timestamp", Timestamp.now().getSeconds());
        fb.getPollsCollection().document(key).collection("Response")
                .document(fb.getUserId()).set(response);
        Map<String, Object> mapi = new HashMap<>();
        mapi.put("pollId", fb.getUserId());
        mapi.put("timestamp", Timestamp.now().getSeconds());
        fb.getUserDocument().collection("Voted").document(key).set(mapi)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Multiple_type_response.this, "Successfully submitted your response", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Multiple_type_response.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog1.dismissWithAnimation();
                        Toast.makeText(Multiple_type_response.this, "Unable to submit.\nPlease try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void retrieveData() {
        fb.getPollsCollection()
                .document(key)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot data = task.getResult();
                        if (data != null && data.exists()) {
                            group.removeAllViews();
                            polldetails = data.toObject(PollDetails.class);
                            query.setText(polldetails.getQuestion());
                            options = polldetails.getMap();
                            author.setText(polldetails.getAuthor());
                            vote_count.setText(polldetails.getPollcount().toString());
                            card_date.setText(passed_date);
                            card_status.setText(passed_status);
                            if (polldetails.getPic() == null) {
                                profilePic.setImageResource(R.drawable.ic_person_black_24dp);
                            } else {
                                Glide.with(getApplicationContext())
                                        .load(polldetails.getPic())
                                        .transform(new CircleCrop())
                                        .placeholder(R.drawable.ic_person_black_24dp)
                                        .into(profilePic);
                            }
                            if (polldetails.isLive()) {
                                live.setVisibility(View.VISIBLE);
                            }
                            response.clear();
                            fb.getUserDocument().collection("Favourite Authors").document(polldetails.getAuthorUID()).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document = task1.getResult();
                                    if (document != null) {
                                        if (document.exists()) {
                                            following.setVisibility(View.VISIBLE);
                                            profilePic.setBackgroundResource(R.drawable.green_boundary_pic);
                                        } else {
                                            following.setVisibility(View.INVISIBLE);
                                            profilePic.setBackgroundResource(R.drawable.voter_item_outline);

                                        }
                                    }
                                } else {
                                    Toast.makeText(Multiple_type_response.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            int i = 0;
                            for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                CheckBox button = new CheckBox(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(0, 0, 0, 20);
                                button.setPadding(15, 20, 15, 20);
                                button.setLayoutParams(layoutParams);
                                button.setTypeface(typeface);
                                button.setBackgroundResource(R.drawable.border_gsignin);
                                button.setText(entry.getKey());
                                update.put(entry.getKey(), entry.getValue());
                                button.setTextSize(20.0f);
                                group.addView(button);
                                int finalI = i;
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CheckBox b = (CheckBox) v;
                                        if (b.isChecked()) {
                                            response.put("option" + finalI, b.getText().toString());
                                            checked++;
                                        } else {
                                            response.values().remove(b.getText().toString());
                                            checked--;
                                        }
                                    }
                                });
                                i++;
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
                                        Intent intent = new Intent(Multiple_type_response.this, PercentageResult.class);
                                        intent.putExtra("UID", key);
                                        intent.putExtra("type", "MULTI SELECT");
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            }
                        } else {
                            finish();
                            Toast.makeText(this, "This url does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
        );
    }

    private void setGlobals(View view) {
        c = 0;
        submit = findViewById(R.id.submit);
        query = findViewById(R.id.query);
        group = findViewById(R.id.options);
        options = new HashMap<>();
        response = new HashMap<>();
        update = new HashMap<>();
        share = view.findViewById(R.id.share);
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.roboto);
        dialog = new Dialog(Multiple_type_response.this);
        dialog1 = new KAlertDialog(Multiple_type_response.this, SweetAlertDialog.PROGRESS_TYPE);
        fb = new firebase();
        dialog2 = new SpotsDialog(Multiple_type_response.this, R.style.Custom);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        author = findViewById(R.id.author);
        card_date = findViewById(R.id.card_date);
        card_status = findViewById(R.id.card_status);
        live = findViewById(R.id.live);
        vote_count = findViewById(R.id.vote_count_no);
        profilePic = findViewById(R.id.pPic);
        menu_home = findViewById(R.id.menu_home);
        following = findViewById(R.id.following);
        back_overall = view.findViewById(R.id.back_overall);
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

    private void showExpiredDialog() {
        Dialog dialog = new Dialog(Multiple_type_response.this);
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
        Button ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(Multiple_type_response.this, PercentageResult.class);
                i.putExtra("UID", key);
                i.putExtra("type", "MULTI SELECT");
                startActivity(i);
                finish();

            }
        });


    }

}
