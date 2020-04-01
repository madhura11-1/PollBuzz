package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.VotedFeedAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import Utils.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VotedFeed extends Fragment {
    private ShimmerRecyclerView votedRV;
    private VotedFeedAdapter mAdapter;
    private ArrayList<PollDetails> mArrayList;
    private LinearLayoutManager layoutManager;
    private CollectionReference userVotedRef;
    private firebase fb;
    private LayoutAnimationController controller;
    private MaterialTextView viewed;
    private DocumentSnapshot lastIndex;
    ProgressBar progressBar;
    Boolean flagFirst = true, flagFetch = true;

    public VotedFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_voted_feed, container, false);
        setGlobals(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
        getData();
    }

    private void setListeners() {
        votedRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mArrayList.isEmpty() && layoutManager.findLastVisibleItemPosition() == mArrayList.size() - 1 && flagFetch && !flagFirst) {
                    progressBar.setVisibility(View.VISIBLE);
                    flagFetch = false;
                    getData();
                }
            }
        });
    }

    private void getData() {
        try {
            if (lastIndex == null) {
                userVotedRef.orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(20).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot dS : task.getResult()) {
                                if (dS.exists()) {
                                    long timestamp = (long) dS.get("timestamp");
                                    fb.getPollsCollection().document(dS.getId())
                                            .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null) {
                                            addToRecyclerView(task1.getResult(), timestamp);
                                        }
                                    });
                                }
                                lastIndex = dS;
                            }
                        } else {
                            flagFetch=false;
                            votedRV.hideShimmerAdapter();
                            viewed.setVisibility(View.VISIBLE);
                        }
                    } else {
                        votedRV.hideShimmerAdapter();
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                userVotedRef.orderBy("timestamp", Query.Direction.DESCENDING)
                        .startAfter(lastIndex).limit(20).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot dS : task.getResult()) {
                                if (dS.exists()) {
                                    long timestamp = (long) dS.get("timestamp");
                                    fb.getPollsCollection().document(dS.getId())
                                            .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null) {
                                            addToRecyclerView(task1.getResult(), timestamp);
                                        }
                                    });
                                }
                                lastIndex = dS;
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You have viewed all polls...", Toast.LENGTH_SHORT).show();
                            flagFetch=false;
                        }
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void addToRecyclerView(DocumentSnapshot dS1, long timestamp) {
        try {
            Log.d("TimeStamp", String.valueOf(timestamp));
            PollDetails polldetails = dS1.toObject(PollDetails.class);
            polldetails.setUID(dS1.getId());
            polldetails.setTimestamp(timestamp);
            mArrayList.add(polldetails);
            try {
                Collections.sort(mArrayList, (pollDetails, t1) -> Long.compare(t1.getTimestamp(), pollDetails.getTimestamp()));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(e.getMessage());
            }
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            flagFetch = true;
            if (flagFirst) {
                votedRV.hideShimmerAdapter();
                votedRV.scheduleLayoutAnimation();
                flagFirst = true;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void setGlobals(@NonNull View view) {
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.animation_down_to_up);
        viewed = view.findViewById(R.id.viewed);
        progressBar = view.findViewById(R.id.pBar);
        votedRV = view.findViewById(R.id.votedrecyclerview);
        votedRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        votedRV.setLayoutManager(layoutManager);
        mArrayList = new ArrayList<>();
        mAdapter = new VotedFeedAdapter(getContext(), mArrayList);
        votedRV.setAdapter(mAdapter);
        votedRV.setLayoutAnimation(controller);
        votedRV.showShimmerAdapter();
        fb = new firebase();
        userVotedRef = fb.getUserDocument().collection("Voted");
    }
}