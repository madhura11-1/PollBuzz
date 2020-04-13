package com.PollBuzz.pollbuzz.results;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.VoteDetails;
import com.PollBuzz.pollbuzz.adapters.VoterPageAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Utils.firebase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResultActivity extends AppCompatActivity {

    ShimmerRecyclerView voteRV;
    VoterPageAdapter mPageAdapter;
    List<VoteDetails> mVoteDetailsList;
    ImageButton home, logout;
    TextView page_title;
    String UID, type;
    firebase fb;
    private LayoutAnimationController controller;
    private MaterialTextView viewed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setGlobals();
        Intent intent = getIntent();
        getIntentExtras(intent);
        setActionBarFunctionality();
        retrieveData(fb);
    }


    private void retrieveData(firebase fb) {
        if (UID != null) {
            fb.getPollsCollection().document(UID).collection("Response").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot dS : querySnapshot) {
                            long timestamp = (long) dS.get("timestamp");
                            fb.getUsersCollection().document(dS.getId()).get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null) {
                                            DocumentSnapshot documentSnapshot = task1.getResult();
                                            Object author = documentSnapshot.get("username");
                                            Object imageUrl = documentSnapshot.get("pic");
                                            Log.d("type", author.toString());
                                            VoteDetails voteDetails;
                                            if (imageUrl != null)
                                                voteDetails = new VoteDetails(UID, type, author.toString(), dS.getId(), imageUrl.toString(), timestamp);
                                            else
                                                voteDetails = new VoteDetails(UID, type, author.toString(), dS.getId(), null, timestamp);
                                            mVoteDetailsList.add(voteDetails);
                                            Collections.sort(mVoteDetailsList, new Comparator<VoteDetails>() {
                                                @Override
                                                public int compare(VoteDetails voteDetails, VoteDetails t1) {
                                                    return Long.compare(t1.getTimestamp(), voteDetails.getTimestamp());
                                                }
                                            });
                                            mPageAdapter.notifyDataSetChanged();
                                            voteRV.hideShimmerAdapter();
                                            voteRV.scheduleLayoutAnimation();
                                        }
                                    });
                        }
                    } else {
                        voteRV.hideShimmerAdapter();
                        viewed.setVisibility(View.VISIBLE);
                    }
                } else {
                    voteRV.hideShimmerAdapter();
                    Toast.makeText(ResultActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getIntentExtras(Intent parent) {
        UID = parent.getStringExtra("UID");
        type = parent.getStringExtra("type");
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(ResultActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut(this);
        });
    }

    private void setGlobals() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        home = view.findViewById(R.id.home);
        viewed = findViewById(R.id.viewed);
        logout = view.findViewById(R.id.logout);
        page_title = view.findViewById(R.id.page_title);
        fb = new firebase();
        controller = AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.animation_down_to_up);
        page_title.setText("Results");
        voteRV = findViewById(R.id.voterListRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        voteRV.setLayoutManager(linearLayoutManager);
        mVoteDetailsList = new ArrayList<>();
        mPageAdapter = new VoterPageAdapter(getApplicationContext(), mVoteDetailsList);
        voteRV.setAdapter(mPageAdapter);
        voteRV.setLayoutAnimation(controller);
        voteRV.showShimmerAdapter();
    }
}
