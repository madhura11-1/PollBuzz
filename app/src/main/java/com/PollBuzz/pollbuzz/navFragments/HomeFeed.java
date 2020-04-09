package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.polls.Image_type_poll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.HomePageAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import Utils.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFeed extends Fragment {
    private ArrayList<PollDetails> arrayList;
    private ShimmerRecyclerView recyclerView;
    private com.PollBuzz.pollbuzz.adapters.HomePageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private firebase fb;
    private LayoutAnimationController controller;
    MaterialTextView viewed;
    private ImageButton search;
    private DocumentSnapshot lastIndex;
    Date date = Calendar.getInstance().getTime();
    Boolean flagFirst = true, flagFetch = true;

    public HomeFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_feed, container, false);
        setGlobals(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("Size", String.valueOf(arrayList.size()) + " " + String.valueOf(layoutManager.findLastVisibleItemPosition()));
                if (!arrayList.isEmpty() && layoutManager.findLastVisibleItemPosition() == arrayList.size() - 11 && flagFetch && !flagFirst) {
                    flagFetch = false;
                    getData();
                }
            }
        });
        getData();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
/*        search_type.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                arrayList.clear();
                adapter.notifyDataSetChanged();
                if(!charSequence.toString().isEmpty()){
                    getData(1,charSequence.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });*/

    }

    private void getData() {
        if (lastIndex == null) {
            fb.getPollsCollection()
                    .orderBy("timestamp", Query.Direction.DESCENDING).
                    limit(20).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            viewed.setVisibility(View.VISIBLE);
                            for (QueryDocumentSnapshot dS : task.getResult()) {
                                addToRecyclerView(dS);
                                lastIndex = dS;
                            }
                        } else {
                            flagFetch = false;
                            recyclerView.hideShimmerAdapter();
                            viewed.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("hello",task
                                .getException().toString());
                        recyclerView.hideShimmerAdapter();
                        Toast.makeText(HomeFeed.this.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            fb.getPollsCollection()
                    .orderBy("timestamp", Query.Direction.DESCENDING).
                    startAfter(lastIndex).limit(20).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                            addToRecyclerView(dS);
                            lastIndex = dS;
                        }
                    } else {
                        flagFetch = false;
                    }
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void addToRecyclerView(QueryDocumentSnapshot dS) {
        PollDetails polldetails = dS.toObject(PollDetails.class);
        polldetails.setUID(dS.getId());
        fb.getPollsCollection().document(dS.getId()).collection("Response").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Boolean flag = Boolean.TRUE;
                for (QueryDocumentSnapshot dS1 : task.getResult()) {
                    if (dS1.getId().equals(fb.getUserId())) {
                        flag = Boolean.FALSE;
                        if (flagFirst)
                            recyclerView.hideShimmerAdapter();
                        break;
                    }
                }
                if (flag) {
                    if (polldetails.getExpiry_date().compareTo(date) >= 0) {
                        fb.getUsersCollection().document(dS.get("authorUID").toString()).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getResult() != null) {
                                if (task1.getResult().get("pic") != null)
                                    polldetails.setPic(task1.getResult().get("pic").toString());
                                else
                                    polldetails.setPic(null);
                                polldetails.setUsername(task1.getResult().get("username").toString());
                                arrayList.add(polldetails);
                                Collections.sort(arrayList, (pollDetails, t1) -> Long.compare(t1.getTimestamp(), pollDetails.getTimestamp()));
                                viewed.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                                flagFetch = true;
                                if (flagFirst) {
                                    recyclerView.hideShimmerAdapter();
                                    recyclerView.scheduleLayoutAnimation();
                                    flagFirst = false;
                                }
                            } else {
                                Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void setGlobals(@NonNull View view) {
        arrayList = new ArrayList<>();
        viewed = view.findViewById(R.id.viewed);
        search = view.findViewById(R.id.search);
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.animation_down_to_up);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        adapter = new HomePageAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.showShimmerAdapter();
        YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(view.findViewById(R.id.text));
        fb = new firebase();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.option, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.filter:
//                        Intent i = new Intent(getActivity(), FilterActivity.class);
//                        startActivity(i);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().add(R.id.container,new FilterActivity(),"Filter").hide(fm.findFragmentByTag("0")).addToBackStack("Filter").commit();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
    }


}