package com.PollBuzz.pollbuzz.results;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.PollBuzz.pollbuzz.objects.ClipFunction;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuBaseAdapter;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.HashMap;
import java.util.Map;

public class Multiple_type_result extends AppCompatActivity {
    TextView query;
    LinearLayout group;
    Map<String, Integer> options;
    String key, uid;
    Typeface typeface;
    Dialog dialog;
    FirebaseAuth auth;
    TextView poll_stats;
    FirebaseAuth.AuthStateListener listener;
    Map<String, Object> response;
    Integer integer;
    firebase fb = new firebase();
    PollDetails polldetails;
    ImageView id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_response);
        View view = getSupportActionBar().getCustomView();

        setGlobals(view);
        Intent intent = getIntent();
        getIntentExtras(intent);
        setActionBarFunctionality();
        setAuthStateListener();
        showDialog();
        retriveData(fb);

        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String y = polldetails.getPoll_accessID().toString();
                CustomPowerMenu customPowerMenu = new CustomPowerMenu.Builder<>(Multiple_type_result.this,new IconMenuAdapter())
                        .addItem(new ClipFunction(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_content_copy_black_24dp),y))
                        .setAnimation(MenuAnimation.ELASTIC_CENTER)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .build();
                customPowerMenu.showAsAnchorCenter(view);
            }
        });

    }

    private void retriveData(firebase fb) {

        fb.getPollsCollection()
                .document(key)
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                DocumentSnapshot data = task.getResult();
                                if (data.exists()) {
                                    group.removeAllViews();
                                    polldetails = data.toObject(PollDetails.class);
                                    query.setText(polldetails.getQuestion());
                                    options = polldetails.getMap();
                                    fb.getPollsCollection().document(key)
                                            .collection("Response").document(uid)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot data = task.getResult();
                                                if (data.exists()) {
                                                    response = data.getData();
                                                    setOptions();
                                                }
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            } else {
                                dialog.dismiss();
                            }

                        }
                );
        group.setEnabled(false);
    }

    private void setAuthStateListener() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent i = new Intent(Multiple_type_result.this, LoginSignupActivity.class);
                    startActivity(i);
                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

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

    private void setOptions() {
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            String key = entry.getValue().toString();
            if (options.containsKey(key)) {
                options.remove(key);
                options.put(entry.getValue().toString(), 1);
            }
        }
        for (Map.Entry<String, Integer> entry : options.entrySet()) {
            RadioButton button = new RadioButton(getApplicationContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 20, 5, 20);
            button.setLayoutParams(layoutParams);
            button.setTypeface(typeface);
            button.setText(entry.getKey());
            button.setTextSize(20.0f);
            group.addView(button);
            if (entry.getValue() == 1)
                button.setChecked(true);
            else
                button.setEnabled(false);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton b = (RadioButton) v;
                    if (entry.getValue() == 1) {
                        b.setChecked(true);
                        if (!b.isChecked())
                            b.setChecked(true);
                    } else
                        b.setChecked(false);
                }
            });
        }
        dialog.dismiss();
    }

    private void setGlobals(View view) {
        query = findViewById(R.id.query);
        group = findViewById(R.id.options);
        options = new HashMap<>();
        response = new HashMap<>();
        id = findViewById(R.id.id1);

        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Multiple_type_result.this);
        auth = FirebaseAuth.getInstance();
        poll_stats = view.findViewById(R.id.poll_stats);
        poll_stats.setClickable(true);

    }

    private void setActionBarFunctionality() {
        poll_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Multiple_type_result.this, PercentageResult.class);
                i.putExtra("UID", key);
                i.putExtra("type", "MULTI SELECT");
                startActivity(i);
            }
        });

    }

    private void getIntentExtras(Intent intent) {
        key = intent.getExtras().getString("UID");
        integer = intent.getExtras().getInt("flag");

        if (integer == 1) {
            uid = intent.getExtras().getString("UIDUser");
            poll_stats.setVisibility(View.GONE);
        }
        if (integer == 0) {
            uid = auth.getCurrentUser().getUid();
        }

    }

    public class IconMenuAdapter extends MenuBaseAdapter<ClipFunction> {

        @Override
        public View getView(int index, View view, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();

            if(view == null) {
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
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(item.getTitle());
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", item.getTitle());
                        clipboard.setPrimaryClip(clip);
                    }
                }
            });

            return super.getView(index, view, viewGroup);
        }

    }
}
