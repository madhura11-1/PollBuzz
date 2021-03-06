package com.PollBuzz.pollbuzz.results;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.PollBuzz.pollbuzz.BuildConfig;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.PollBuzz.pollbuzz.objects.ClipFunction;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.MenuBaseAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PercentageResult extends AppCompatActivity {

    TextView question_percentage, date_percentage;

    firebase fb;
    String uid, type;
    Map<String, Integer> map;
    LinearLayout linearLayout;
    public static int total;
    Dialog dialog;
    MaterialButton result, pie_charts, selfVote, custom_stop;
    TextView vote_count, status;
    Typeface typeface;
    public static Map<String, Integer> data = new HashMap<>();
    public static String question;
    Boolean flagVoted = true;
    ImageView shareImage, sharePoll;
    PollDetails pollDetails;
    ImageView id;
    Boolean isAuthor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percentage_result);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        Intent intent = getIntent();
        setGlobals(view);
        getIntentExtras(intent);
        retrievedata(fb);
        setListeners();
    }

    private void setListeners() {
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PercentageResult.this, ResultActivity.class);
                intent.putExtra("UID", uid);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
        pie_charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PercentageResult.this, PieChartActivity.class);
                startActivity(i);
            }
        });
        selfVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (type) {
                    case "SINGLE CHOICE":
                        intent = new Intent(getApplicationContext(), Single_type_response.class);
                        break;
                    case "MULTI SELECT":
                        intent = new Intent(getApplicationContext(), Multiple_type_response.class);
                        break;
                    case "RANKED":
                        intent = new Intent(getApplicationContext(), Ranking_type_response.class);
                        break;
                    case "PICTURE BASED":
                        intent = new Intent(getApplicationContext(), Image_type_responses.class);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }
                intent.putExtra("UID", uid);
                startActivity(intent);
            }
        });

        shareImage.setOnClickListener(v -> {
            shareImage.setEnabled(false);
            try {
                Dexter.withActivity(this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    takeAndShareScreenShot();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrashlytics.getInstance().log(e.getMessage());
            }
        });

        sharePoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("timestamp", Timestamp.now().toDate().toString());
                    bundle.putString("UID", uid);
                    FirebaseAnalytics.getInstance(PercentageResult.this).logEvent("share_link", bundle);
                    int type = getType();
                    String shareBody = "https://pollbuzz.com/share/" + type + uid;
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share link via"));
                } catch (IllegalStateException e) {
                    Toast.makeText(PercentageResult.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        custom_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pollDetails.setLive(false);
                fb.getPollsCollection().document(uid).update("live", false)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    status.setText("Status : Expired");
                                    Toast.makeText(PercentageResult.this, "Your Live Poll has been Ended", Toast.LENGTH_SHORT).show();
                                    custom_stop.setVisibility(View.GONE);
                                }
                                else
                                {
                                    Toast.makeText(PercentageResult.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String y = pollDetails.getPoll_accessID().toString();
                CustomPowerMenu customPowerMenu = new CustomPowerMenu.Builder<>(PercentageResult.this,new IconMenuAdapter())
                        .addItem(new ClipFunction(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_content_copy_black_24dp),y))
                        .setAnimation(MenuAnimation.ELASTIC_CENTER)
                        .setMenuRadius(10f)
                        .setMenuShadow(10f)
                        .build();
                customPowerMenu.showAsAnchorCenter(view);
            }
        });
    }

    private int getType() throws IllegalStateException {
        switch (type) {
            case "SINGLE CHOICE":
                return 0;
            case "MULTI SELECT":
                return 1;
            case "RANKED":
                return 2;
            case "PICTURE BASED":
                return 3;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("Go to settings", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void retrievedata(firebase fb) {
        showDialog();
        fb.getPollsCollection().document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            pollDetails = documentSnapshot.toObject(PollDetails.class);
                            if (pollDetails != null) {
                                if (pollDetails.getAuthorUID().equals(fb.getUserId())) {
                                    isAuthor = true;
                                    result.setVisibility(View.VISIBLE);
                                    selfVote.setVisibility(View.VISIBLE);
                                }
                                question_percentage.setText(pollDetails.getQuestion());
                                question = pollDetails.getQuestion();
                                int l = "at 00:00:00 UTC+5:30".length();
                                String date = pollDetails.getCreated_date().toString();
                                String d = "Created on: " + date.substring(0, date.length() - l - 3);
                                date_percentage.setText(d);
                                map = pollDetails.getMap();
                                Date date1 = Calendar.getInstance().getTime();
                                if (!pollDetails.isLive()) {
                                    if (pollDetails.getExpiry_date() != null && pollDetails.getExpiry_date().compareTo(date1) >= 0)
                                        status.setText("Status : Active");
                                    else {
                                        status.setText("Status : Expired");
                                        selfVote.setBackgroundColor(getResources().getColor(R.color.grey));
                                        selfVote.setEnabled(false);
                                    }
                                } else {
                                    if (Timestamp.now().getSeconds() - pollDetails.getTimestamp() > pollDetails.getSeconds()) {
                                        status.setText("Status : Expired");
                                        selfVote.setBackgroundColor(getResources().getColor(R.color.grey));
                                        selfVote.setEnabled(false);
                                        custom_stop.setVisibility(View.GONE);
                                    }
                                    else {
                                        status.setText("Status : Active");
                                        if(isAuthor && pollDetails.getSeconds() == Long.MAX_VALUE)
                                            custom_stop.setVisibility(View.VISIBLE);
                                        else
                                            custom_stop.setVisibility(View.GONE);
                                    }
                                }
                                total = pollDetails.getPollcount();
                                String vote = "Total Voters:" + pollDetails.getPollcount();
                                vote_count.setText(vote);
                                fb.getPollsCollection().document(uid).collection("Response").get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful() && task1.getResult() != null) {
                                        if (!task1.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot dS1 : task1.getResult()) {
                                                if (dS1.getId().equals(fb.getUserId())) {
                                                    flagVoted = false;
                                                    break;
                                                }
                                            }
                                        } else flagVoted = true;
                                        if (!flagVoted) {
                                            selfVote.setBackgroundColor(getResources().getColor(R.color.grey));
                                            selfVote.setEnabled(false);
                                        }
                                    }
                                    setProgressbar(map);
                                });
                            }
                        }
                    } else {
                        Log.d("PercentageResult", Objects.requireNonNull(task.getException().getMessage()));
                    }
                });
    }

    private void setProgressbar(Map<String, Integer> mapip) {

        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mapip.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> map.put(x.getKey(), x.getValue()));
        }
        dialog.dismiss();
        linearLayout.removeAllViews();
        data.clear();
        if (type.equals("PICTURE BASED")) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                Integer per;
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                layoutParams.setMargins(30, 10, 10, 10);
                imageView.setLayoutParams(layoutParams);
                loadProfilePic(imageView, entry.getKey());
                LinearLayout linearLayout1 = new LinearLayout(getApplicationContext());
                linearLayout1.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(30, 40, 10, 40);
                linearLayout1.setLayoutParams(layoutParams1);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(30, 10, 10, 10);
                RoundCornerProgressBar progressBar = new RoundCornerProgressBar(PercentageResult.this, null, android.R.attr.progressBarStyleHorizontal);
                //progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                progressBar.setProgressColor(Color.parseColor("#56d2c2"));
                progressBar.setProgressBackgroundColor(getResources().getColor(R.color.grey));
                LinearLayout wrap_bar = new LinearLayout(getApplicationContext());
                wrap_bar.setOrientation(LinearLayout.VERTICAL);
                progressBar.setPadding(5, 20, 10, 20);
                progressBar.setRadius(20);
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams3.setMargins(10, 30, 10, 30);
                wrap_bar.setLayoutParams(layoutParams2);
                wrap_bar.setBackgroundColor(getResources().getColor(R.color.grey));
                ;
                progressBar.setLayoutParams(layoutParams3);
                progressBar.setScaleY(15);

                TextView textView = new TextView(this);
                TextView voted_by = new TextView(this);
                textView.setTypeface(typeface);
                voted_by.setTypeface(typeface);
                int v_by;
                if (total != 0) {
                    per = (int) ((entry.getValue() / Double.valueOf(total)) * 100);
                    textView.setText(per + "%");
                    progressBar.setProgress(per);
                    v_by = entry.getValue();
                    String text = "Voted by: " + v_by;
                    voted_by.setText(text);

                } else {
                    per = 0;
                    textView.setText(per + "%");
                    String text = "Voted by: " + 0;
                    voted_by.setText(text);
                    progressBar.setProgress(per);

                }
                data.put(entry.getKey(), entry.getValue());
                textView.setLayoutParams(layoutParams2);
                voted_by.setLayoutParams(layoutParams2);
                textView.setTextSize(20.0f);
                voted_by.setTextSize(20.0f);
                textView.setTextColor(getResources().getColor(R.color.black));
                voted_by.setTextColor(getResources().getColor(R.color.black));
                linearLayout1.addView(imageView);
                linearLayout1.addView(textView);
                linearLayout1.addView(wrap_bar);
                wrap_bar.addView(progressBar);
                linearLayout1.addView(voted_by);
                linearLayout.addView(linearLayout1);

            }

        } else if (type.equals("RANKED")) {

            fb.getPollsCollection()
                    .document(uid)
                    .collection("OptionsCount")
                    .document("count")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Map<String, Object> map1 = task.getResult().getData();
                            sortRanking(map1);

                        }
                    });


        } else {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                LinearLayout linearLayout1 = new LinearLayout(getApplicationContext());
                linearLayout1.setOrientation(LinearLayout.VERTICAL);
                RoundCornerProgressBar progressBar = new RoundCornerProgressBar(PercentageResult.this, null, android.R.attr.progressBarStyleHorizontal);
                //progressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                progressBar.setProgressColor(Color.parseColor("#56d2c2"));
                progressBar.setProgressBackgroundColor(getResources().getColor(R.color.grey));
                LinearLayout wrap_bar = new LinearLayout(getApplicationContext());
                wrap_bar.setOrientation(LinearLayout.VERTICAL);
                progressBar.setPadding(5, 20, 10, 20);
                progressBar.setRadius(20);
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams3.setMargins(10, 30, 10, 30);


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 10, 20);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(30, 40, 10, 40);
                linearLayout1.setLayoutParams(layoutParams1);
                wrap_bar.setLayoutParams(layoutParams);
                wrap_bar.setBackgroundColor(getResources().getColor(R.color.grey));
                progressBar.setLayoutParams(layoutParams3);
                progressBar.setScaleY(15);
                TextView textView = new TextView(this);
                TextView voted_by = new TextView(this);
                int v_by;
                Integer per;
                Log.d("option", entry.getKey());
                if (total != 0) {
                    per = (int) ((entry.getValue() / Double.valueOf(total)) * 100);
                    v_by = entry.getValue();
                    String text = "Voted by: " + v_by;
                    voted_by.setText(text);
                    textView.setText(entry.getKey() + " - " + per + "%");
                    progressBar.setProgress(per);
                } else {
                    per = 0;
                    textView.setText(entry.getKey() + " - " + per + "%");
                    String text = "Voted by: " + 0;
                    voted_by.setText(text);
                    progressBar.setProgress(per);
                }
                data.put(entry.getKey(), entry.getValue());
                textView.setLayoutParams(layoutParams);
                voted_by.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.START);
                voted_by.setGravity(Gravity.START);
                textView.setTextSize(20.0f);
                voted_by.setTextSize(20.0f);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTypeface(typeface);
                voted_by.setTypeface(typeface);
                voted_by.setTextColor(getResources().getColor(R.color.black));
                linearLayout1.addView(textView);
                wrap_bar.addView(progressBar);
                linearLayout1.addView(wrap_bar);
                linearLayout1.addView(voted_by);
                linearLayout.addView(linearLayout1);
            }
        }
    }

    private void sortRanking(Map<String, Object> map1) {
        Map<String, Long> map2 = new HashMap<>();
        int r = map1.size();
        int c = r + 1;
        long arr[][] = new long[r][c];
        String names[] = new String[r];
        int i = 0;

        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            map2 = (Map<String, Long>) entry.getValue();
            names[i] = entry.getKey();
            arr[i][0] = i;
            for (int j = 1; j < c; j++) {
                arr[i][j] = map2.get(String.valueOf(j));

            }
            i++;

        }
        for (int k = c - 1; k > 0; k--) {
            final int j = k;
            Arrays.sort(arr, new Comparator<long[]>() {

                @Override
                // Compare values according to columns
                public int compare(final long[] entry1,
                                   final long[] entry2) {

                    // To sort in descending order revert
                    // the '>' Operator
                    if (entry1[j] <= entry2[j]) {
                        if (j == 1)
                            return 1;
                        else {
                            int ans = check(entry1, entry2, j - 1);
                            return ans;
                            /*if(entry2[j-1]>entry1[j-1])
                                return 1;
                            else if(entry1[j-1]==entry2[j-1])
                            {
                                if(j>1)
                                {
                                    int ans=check(entry1,entry2,j-1);
                                    return ans;
                                }
                                return 0;
                            }
                            else
                                return -1;*/
                        }
                    } else
                        return -1;
                }
            });
        }
        System.out.println(arr);
        LinearLayout linearLayout1 = new LinearLayout(getApplicationContext());
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 100, 10, 10);
        linearLayout1.setLayoutParams(layoutParams);
        for (i = 0; i < r; i++) {
            LinearLayout linearLayout2 = new LinearLayout(getApplicationContext());
            linearLayout2.setOrientation(LinearLayout.VERTICAL);
            layoutParams.setMargins(12, 12, 10, 12);
            linearLayout2.setLayoutParams(layoutParams);
            TextView tV_main = new TextView(getApplicationContext());
            int index = (int) arr[i][0];
            tV_main.setText(names[index]);
            linearLayout1.addView(tV_main);

            for (int j = 1; j < c; j++) {
                TextView tV = new TextView(getApplicationContext());
                tV.setText("Priority " + j + " : " + arr[i][j]);
                linearLayout2.addView(tV);
            }
            linearLayout1.addView(linearLayout2);
        }
        linearLayout.addView(linearLayout1);

    }

    public static int check(long arr1[], long arr2[], int k) {
        if (k > 0) {
            if (arr1[k] <= arr2[k])
                if (k == 1)
                    return 1;
                else
                    check(arr1, arr2, k - 1);
            else
                return -1;


        }
        return -1;
    }

    private void loadProfilePic(ImageView view, String url) {
        if (url != null) {
            Glide.with(this)
                    .load(url)
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

    private void getIntentExtras(Intent intent) {
        uid = intent.getExtras().getString("UID");
        type = intent.getExtras().getString("type");
        if (type != null && type.equals("RANKED")) pie_charts.setVisibility(View.GONE);
        if (intent.getBooleanExtra("selfVote", false)) {
            selfVote.setVisibility(View.VISIBLE);
        }
    }

    private void setGlobals(View view) {
        linearLayout = findViewById(R.id.percentage);
        fb = new firebase();
        question_percentage = findViewById(R.id.question_percentage);
        date_percentage = findViewById(R.id.date_percentage);
        result = findViewById(R.id.result);
        selfVote = findViewById(R.id.selfVote);
        map = new HashMap<>();
        vote_count = findViewById(R.id.vote_count);
        dialog = new Dialog(PercentageResult.this);
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.maven_pro);
        pie_charts = findViewById(R.id.pie);
        shareImage = findViewById(R.id.share_button);
        sharePoll = findViewById(R.id.share_poll);
        status = findViewById(R.id.status);
        custom_stop = findViewById(R.id.custom_stop);
        id = findViewById(R.id.id1);

    }

    private void takeAndShareScreenShot() {
        try {
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots.jpeg";
            File file = new File(mPath);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            shareImage.setEnabled(true);
            shareScreenshot(file);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void shareScreenshot(File imageFile) {
        Bundle bundle = new Bundle();
        bundle.putString("timestamp", Timestamp.now().toDate().toString());
        bundle.putString("UID", uid);
        FirebaseAnalytics.getInstance(this).logEvent("share_screenshot", bundle);
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri imageuri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageuri = Uri.fromFile(imageFile);
        } else {
            imageuri = FileProvider.getUriForFile(PercentageResult.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    imageFile);
        }
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, "Result for " + question_percentage.getText());
        intent.putExtra(Intent.EXTRA_STREAM, imageuri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(intent, "Share Result Using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No sharing app is installed in your phone!", Toast.LENGTH_SHORT).show();
        }
        deleteCache();
    }

    private void deleteCache() {
        deleteDir(getApplicationContext().getCacheDir());
        deleteDir(getApplicationContext().getExternalCacheDir());
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
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
