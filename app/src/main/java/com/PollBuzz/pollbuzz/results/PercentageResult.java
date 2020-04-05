package com.PollBuzz.pollbuzz.results;

import com.PollBuzz.pollbuzz.BuildConfig;
import com.PollBuzz.pollbuzz.responses.Descriptive_type_response;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import Utils.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

public class PercentageResult extends AppCompatActivity {

    TextView question_percentage, date_percentage;

    firebase fb;
    ImageButton home, logout;
    String uid, type;
    int flag;
    Map<String, Integer> map;
    LinearLayout linearLayout;
    public static Double total;
    Dialog dialog;
    MaterialButton result, pie_charts, selfVote;
    TextView vote_count;
    Typeface typeface;
    public static Map<String, Integer> data = new HashMap<>();
    public static String question;
    Boolean flagVoted = true;
    ImageView shareButton;

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
        setActionBarFunctionality();
        retrievedata(fb);

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
                Log.d("Case", type);
                fb.getPollsCollection().document(uid).collection("Response").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot dS1 : task.getResult()) {
                                if (dS1.getId().equals(fb.getUserId())) {
                                    flagVoted = false;
                                    Toast.makeText(PercentageResult.this, "You have already voted once...", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                        } else flagVoted = true;
                        if (flagVoted) {
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
                    }
                });
            }
        });

        shareButton.setOnClickListener(v -> {
            shareButton.setEnabled(false);
            //Toast.makeText(getApplicationContext(), "Taking screenshot", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                shareButton.setEnabled(true);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                shareButton.setEnabled(true);
            } else {
                takeAndShareScreenShot();
            }
        });
    }

    private void retrievedata(firebase fb) {

        showDialog();
        fb.getPollsCollection().document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            PollDetails pollDetails = documentSnapshot.toObject(PollDetails.class);
                            question_percentage.setText(pollDetails.getQuestion());
                            question = pollDetails.getQuestion();
                            int l="at 00:00:00 UTC+5:30".length();
                            String date=pollDetails.getCreated_date().toString();
                            String d = "Created on: " + date.substring(0,date.length()-l-3);
                            date_percentage.setText(d);
                            map = pollDetails.getMap();
                            total = Double.valueOf(pollDetails.getPollcount());
                            String vote = "Total Voters:" + pollDetails.getPollcount();
                            vote_count.setText(vote);
                            setProgressbar(map);
                        } else
                            Log.d("hello", "hii");
                    }
                });
    }

    private void setProgressbar(Map<String, Integer> mapip) {

        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        mapip.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> map.put(x.getKey(), x.getValue()));
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
                    per = (int) ((entry.getValue() / total) * 100);
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

            /*LinearLayout linearLayout1=new LinearLayout(getApplicationContext());
            linearLayout1.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 100, 10, 10);
            linearLayout1.setLayoutParams(layoutParams);
            fb.getPollsCollection()
                    .document(uid)
                    .collection("OptionsCount")
                    .document("count")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            linearLayout.removeAllViews();
                            Map<String,Object> map1=task.getResult().getData();
                            Map<String, Long> map2 = new HashMap<>();
                            if (map1 != null) {
                                for(Map.Entry<String,Object> entry : map1.entrySet()){
                                    map2=(Map<String,Long>) entry.getValue();
                                    LinearLayout linearLayout2=new LinearLayout(getApplicationContext());
                                    linearLayout2.setOrientation(LinearLayout.VERTICAL);
                                    layoutParams.setMargins(12, 12, 10, 12);
                                    linearLayout2.setLayoutParams(layoutParams);
                                    TextView tV_main =new TextView(getApplicationContext());
                                    tV_main.setText(entry.getKey()+" : ");
                                    tV_main.setTextColor(getResources().getColor(R.color.black));
                                    tV_main.setTextSize(22.0f);
                                    tV_main.setTypeface(typeface);
                                    linearLayout1.addView(tV_main);
                                    for(Map.Entry<String,Long> entry1 : map2.entrySet()){
                                        TextView tV=new TextView(getApplicationContext());
                                        tV.setText("Priority "+entry1.getKey()+ " : " + entry1.getValue());
                                        linearLayout2.addView(tV);
                                        tV.setTextColor(getResources().getColor(R.color.black));
                                        tV.setTextSize(18.0f);
                                        tV.setTypeface(typeface);
                                    }
                                    linearLayout1.addView(linearLayout2);
                                }
                                linearLayout.addView(linearLayout1);
                            }
                        }
                    });*/
            fb.getPollsCollection()
                    .document(uid)
                    .collection("OptionsCount")
                    .document("count")
                    .get()
                    .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Map<String,Object> map1=task.getResult().getData();
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

                    per = (int) ((entry.getValue() / total) * 100);
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
        int r=map1.size();
        int c=r+1;
        long arr[][]=new long[r][c];
        String names[]=new String[r];
        int i=0;

        for(Map.Entry<String,Object> entry : map1.entrySet()){
            map2=(Map<String,Long>) entry.getValue();
            names[i]=entry.getKey();
            arr[i][0]=i;
            for(int j=1;j<c;j++)
           {
               arr[i][j]=map2.get(String.valueOf(j));

            }
            i++;

        }
        for(int k=c-1;k>0;k--)
        {   final int j=k;
            Arrays.sort(arr, new Comparator<long[]>() {

                @Override
                // Compare values according to columns
                public int compare(final long[] entry1,
                                   final long[] entry2) {

                    // To sort in descending order revert
                    // the '>' Operator
                    if (entry1[j] <=entry2[j])
                    {
                        if(j==1)
                            return 1;
                        else
                        {
                            int ans=check(entry1,entry2,j-1);
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
                    }

                    else
                        return -1;
                }
            });
        }
        System.out.println(arr);
        LinearLayout linearLayout1=new LinearLayout(getApplicationContext());
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 100, 10, 10);
        linearLayout1.setLayoutParams(layoutParams);
        for(i=0;i<r;i++)
        {
            LinearLayout linearLayout2=new LinearLayout(getApplicationContext());
            linearLayout2.setOrientation(LinearLayout.VERTICAL);
            layoutParams.setMargins(12, 12, 10, 12);
            linearLayout2.setLayoutParams(layoutParams);
            TextView tV_main =new TextView(getApplicationContext());
            int index=(int)arr[i][0];
            tV_main.setText(names[index]);
            linearLayout1.addView(tV_main);

            for(int j=1;j<c;j++)
            {
                TextView tV=new TextView(getApplicationContext());
                tV.setText("Priority "+j+ " : " + arr[i][j]);
                linearLayout2.addView(tV);
            }
            linearLayout1.addView(linearLayout2);
        }
        linearLayout.addView(linearLayout1);

    }
    public static int check(long arr1[],long arr2[], int k)
    {
        if(k>0)
        {
            if(arr1[k]<=arr2[k])
                if(k==1)
                return 1;
                else
                    check(arr1,arr2,k-1);
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


    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(PercentageResult.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(PercentageResult.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
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
        if(type.equals("RANKED"))
            pie_charts.setVisibility(View.GONE);
        flag = intent.getIntExtra("flag", 0);
        if (flag == 1) {
            result.setVisibility(View.GONE);
            selfVote.setVisibility(View.GONE);
        }

    }

    private void setGlobals(View view) {
        linearLayout = findViewById(R.id.percentage);
        fb = new firebase();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        question_percentage = findViewById(R.id.question_percentage);
        date_percentage = findViewById(R.id.date_percentage);
        result = findViewById(R.id.result);
        selfVote = findViewById(R.id.selfVote);
        map = new HashMap<>();
        vote_count = findViewById(R.id.vote_count);
        dialog = new Dialog(PercentageResult.this);
        typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.maven_pro);
        pie_charts = findViewById(R.id.pie);
        shareButton = findViewById(R.id.share_button);
    }

    @Override
    protected void onStart() {
        super.onStart();

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
            shareButton.setEnabled(true);
            shareScreenshot(file);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void shareScreenshot(File imageFile) {
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
}
