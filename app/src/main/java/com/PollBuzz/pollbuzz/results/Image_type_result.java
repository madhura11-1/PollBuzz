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

import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.PollBuzz.pollbuzz.objects.ClipFunction;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuBaseAdapter;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.HashMap;
import java.util.Map;

public class Image_type_result extends AppCompatActivity {

    MaterialTextView query;
    LinearLayout group;
    firebase fb = new firebase();
    RadioButton b1, b2;
    ImageView image1, image2;
    String key, uid;
    Typeface typeface;
    Dialog dialog;
    TextView poll_stats;
    Map<String, Object> response;
    Map<String, Integer> options;
    Integer integer;
    PollDetails pollDetails;
    ImageView id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_type_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_response);
        View view = getSupportActionBar().getCustomView();


        setGlobals(view);
        Intent intent = getIntent();
        getIntentExtras(intent);
        setActionBarFunctionality();
        showDialog();
        retriveData(fb);

        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String y = pollDetails.getPoll_accessID().toString();
                CustomPowerMenu customPowerMenu = new CustomPowerMenu.Builder<>(Image_type_result.this,new IconMenuAdapter())
                        .addItem(new ClipFunction(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_content_copy_black_24dp),y))
                        .setAnimation(MenuAnimation.ELASTIC_CENTER)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .build();
                customPowerMenu.showAsAnchorCenter(view);
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
            uid = fb.getUserId();
        }

    }

    private void retriveData(firebase fb) {

        fb.getPollsCollection().document(key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                pollDetails = documentSnapshot.toObject(PollDetails.class);
                                query.setText(pollDetails.getQuestion());
                                options = pollDetails.getMap();
                                int i = 0;
                                for (Map.Entry<String, Integer> entry : options.entrySet()) {
                                    if (i == 0) {
                                        loadProfilePic(image1, entry.getKey());
                                    }
                                    if (i == 1) {
                                        loadProfilePic(image2, entry.getKey());
                                    }
                                    i++;
                                }

                                fb.getPollsCollection().document(key).collection("Response").document(uid).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    response = documentSnapshot.getData();
                                                    if (response.containsValue("Option 2")) {
                                                        b2.setChecked(true);
                                                    } else
                                                        b1.setChecked(true);

                                                }


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Image_type_result.this, "Failed to access your response", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                        dialog.dismiss();
                    }
                });
        b1.setEnabled(false);
        b2.setEnabled(false);
    }

    private void setGlobals(View view) {
        poll_stats = view.findViewById(R.id.poll_stats);
        group = findViewById(R.id.options);
        response = new HashMap<>();
        options = new HashMap<>();
        query = findViewById(R.id.query_imageresult);
        group = findViewById(R.id.options);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        b1 = findViewById(R.id.option1);
        b2 = findViewById(R.id.option2);
        id = findViewById(R.id.id1);


        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.didact_gothic);
        dialog = new Dialog(Image_type_result.this);
    }

    private void setActionBarFunctionality() {
        poll_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Image_type_result.this, PercentageResult.class);
                i.putExtra("UID", key);
                i.putExtra("type", "PICTURE BASED");
                startActivity(i);

            }
        });

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
