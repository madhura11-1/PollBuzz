package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import com.google.firebase.firestore.QuerySnapshot;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import Utils.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
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
    private TextInputEditText search_type;
    private DocumentSnapshot lastIndex;
    private LinearLayout search_layout,date_layout;
    ProgressBar progressBar;
    TextView starting,ending;
    private  String name="";
    private ImageButton search,check,back1,back2;
    private Button search_button;
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    Date date = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    final String formatteddate = dateFormat.format(date);
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
        getData(0,"",null,null);
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filter, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.by_author:
                        mArrayList.clear();
                        mAdapter.notifyDataSetChanged();
                        lastIndex=null;
                        search_layout.setVisibility(View.VISIBLE);
                        date_layout.setVisibility(View.GONE);
                        viewed.setVisibility(View.GONE);
                        getData(0,"",null,null);
                        return true;
                    case R.id.by_date:
                        mArrayList.clear();
                        mAdapter.notifyDataSetChanged();
                        lastIndex=null;
                        date_layout.setVisibility(View.VISIBLE);
                        search_layout.setVisibility(View.GONE);
                        viewed.setVisibility(View.GONE);
                        getData(0,"",null,null);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
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
                    getData(0,"",null,null);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewed.setVisibility(View.GONE);
                lastIndex = null;
                showPopup(view);
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastIndex = null;
                votedRV.setVisibility(View.VISIBLE);
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();
                name = search_type.getText().toString();
                if(!name.isEmpty()){
                    getData(1,name,null,null);
                }
                else
                    Toast.makeText(getContext(),"PLease enter the author name",Toast.LENGTH_LONG).show();
            }
        });

        starting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String date=day+"-"+(month+1)+"-"+year;
                                starting.setText(date);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        ending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String date=day+"-"+(month+1)+"-"+year;
                                ending.setText(date);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
            });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    viewed.setVisibility(View.GONE);
                    lastIndex = null;
                    if(starting.getText().toString().isEmpty() && ending.getText().toString().isEmpty())
                        Toast.makeText(getContext(),"Please atleast one of the dates",Toast.LENGTH_LONG).show();
                    else {
                        if(!starting.getText().toString().isEmpty() && !ending.getText().toString().isEmpty())
                        {
                            Date start=dateFormat.parse(starting.getText().toString());
                            Date end=dateFormat.parse(ending.getText().toString());
                            if(start.compareTo(end)>0)
                                Toast.makeText(getContext(),"Starting date can't be after the ending date",Toast.LENGTH_LONG).show();
                            else
                            {   mArrayList.clear();
                                mAdapter.notifyDataSetChanged();
                                votedRV.showShimmerAdapter();
                                getData(2,"",dateFormat.parse(starting.getText().toString()),dateFormat.parse(ending.getText().toString()));
                            }
                        }
                        else
                        {   mArrayList.clear();
                            mAdapter.notifyDataSetChanged();
                            votedRV.showShimmerAdapter();
                            if(starting.getText().toString().isEmpty())
                                getData(2,"",null,dateFormat.parse(ending.getText().toString()));
                            else
                                getData(2,"",dateFormat.parse(starting.getText().toString()),null);
                        }

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();
                lastIndex=null;
                getData(0,"",null,null);
                search_layout.setVisibility(View.GONE);
            }
        });
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayList.clear();
                mAdapter.notifyDataSetChanged();
                lastIndex=null;
                getData(0,"",null,null);
                date_layout.setVisibility(View.GONE);
            }
        });
    }

    private void getData(int flagi , String name , Date start , Date end) {
        try {
            if (lastIndex == null) {
                userVotedRef.orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(20).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d("SizeVoted","Size: "+task.getResult().size());
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot dS : task.getResult()) {
                                if (dS.exists()) {
                                    long timestamp = (long) dS.get("timestamp");
                                    if (flagi == 1) {
                                        getArrayListByAuthor(name,dS.getId(),timestamp);
                                    }
                                    else if (flagi == 2){
                                        try {
                                            getArrayListByDate(start,end,timestamp, dS.getId());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else
                                    {
                                        fb.getPollsCollection().document(dS.getId())
                                                .get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful() && task1.getResult() != null) {
                                                addToRecyclerView(task1.getResult(), timestamp);
                                            }
                                        });
                                    }
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
                                    if (flagi == 1) {
                                        getArrayListByAuthor(name, dS.getId(), timestamp);
                                    } else if(flagi == 2){
                                        try {
                                            getArrayListByDate(start,end,timestamp, dS.getId());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                        else
                                     {
                                        fb.getPollsCollection().document(dS.getId())
                                                .get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful() && task1.getResult() != null) {
                                                addToRecyclerView(task1.getResult(), timestamp);
                                            }
                                        });
                                    }
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

    private void getArrayListByDate(Date start, Date end,long timestamp,String docu_id) throws ParseException {
        if(end==null)
            end=dateFormat.parse(formatteddate);
        else
        if(start==null)
            start=dateFormat.parse("21-03-2020");
        fb.getPollsCollection().orderBy("created_date").whereGreaterThanOrEqualTo("created_date",start)
                .whereLessThanOrEqualTo("created_date",end).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        viewed.setVisibility(View.VISIBLE);
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                              if(docu_id.equals(dS.getId()))
                            addToRecyclerView(dS,timestamp);

                        }

                    }
                }
            }
        });
    }

    private void getArrayListByAuthor(String name,String docu_id,long timestamp) {
        fb.getPollsCollection().whereEqualTo("author",name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        viewed.setVisibility(View.VISIBLE);
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                            PollDetails pollDetails = dS.toObject(PollDetails.class);
                            if(docu_id.equals(dS.getId())) {
                                Log.d("name",name);
                                addToRecyclerView(dS,timestamp);
                            }

                        }

                    }
                }

            }
        });

    }

    private void addToRecyclerView(DocumentSnapshot dS1, long timestamp) {
        try {
            PollDetails polldetails = dS1.toObject(PollDetails.class);
            polldetails.setUID(dS1.getId());
            polldetails.setTimestamp(timestamp);
            mArrayList.add(polldetails);
            Log.d("added",Integer.toString(mArrayList.size()));
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
        search_type = view.findViewById(R.id.search_type);
        search_button = view.findViewById(R.id.search_button);
        search_layout = view.findViewById(R.id.type_layout);
        date_layout = view.findViewById(R.id.date_layout);
        fb = new firebase();
        starting = view.findViewById(R.id.starting_date);
        ending = view.findViewById(R.id.ending_date);
        search = view.findViewById(R.id.search);
        check = view.findViewById(R.id.check);
        back1 = view.findViewById(R.id.back1);
        back2 = view.findViewById(R.id.back2);
        userVotedRef = fb.getUserDocument().collection("Voted");
    }
}