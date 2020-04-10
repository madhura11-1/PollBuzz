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
import com.google.common.base.CharMatcher;
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

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
    private ImageButton search, check, back1, back2;
    private String name = "";
    private Button search_button;
    private TextView starting, ending;
    private TextInputEditText search_type;
    private DocumentSnapshot lastIndex;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private LinearLayout search_layout, date_layout;
    Date date = Calendar.getInstance().getTime();
    Boolean flagFirst = true, flagFetch = true;
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    final String formatteddate = dateFormat.format(date);
    int currentFlag = 0;

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
        setListeners();
        getData(0, "", null, null);

    }

    private void setListeners() {
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
                    if (currentFlag == 0)
                        getData(currentFlag, "", null, null);
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewed.setVisibility(View.GONE);
                closeKeyboard();
                showPopup(view);
            }
        });

        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                flagFirst = true;
                arrayList.clear();
                adapter.notifyDataSetChanged();
                lastIndex = null;
                currentFlag = 0;
                getData(0, "", null, null);
                search_layout.setVisibility(View.GONE);
                recyclerView.showShimmerAdapter();
                search_type.setText("");
            }
        });
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                flagFirst = true;
                arrayList.clear();
                adapter.notifyDataSetChanged();
                lastIndex = null;
                currentFlag = 0;
                getData(0, "", null, null);
                date_layout.setVisibility(View.GONE);
                recyclerView.showShimmerAdapter();
                starting.setHint("Starting Date");
                ending.setHint("Ending Date");
            }
        });
        search_type.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!search_type.getText().toString().isEmpty()) {
                        closeKeyboard();
                        //recyclerView.showShimmerAdapter();
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        currentFlag = 1;
                        flagFirst = true;
                        lastIndex = null;
                        name = search_type.getText().toString();
                        getData(1, name, null, null);
//                        recyclerView.setVisibility(View.VISIBLE);
//                        arrayList.clear();
//                        adapter.notifyDataSetChanged();
                    } else
                        Toast.makeText(getContext(), "Please enter the author name", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!search_type.getText().toString().isEmpty()) {
                    closeKeyboard();
                    recyclerView.showShimmerAdapter();
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                    currentFlag = 1;
                    flagFirst = true;
                    lastIndex = null;
                    name = search_type.getText().toString();
                    viewed.setVisibility(View.GONE);
                    getData(1, name, null, null);
//                        recyclerView.setVisibility(View.VISIBLE);
//                        arrayList.clear();
//                        adapter.notifyDataSetChanged();
                } else
                    Toast.makeText(getContext(), "Please enter the author name", Toast.LENGTH_LONG).show();
            }
        });

        starting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String date = day + "-" + (month + 1) + "-" + year;
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
                                String date = day + "-" + (month + 1) + "-" + year;
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
                    flagFirst = true;
                    viewed.setVisibility(View.GONE);
                    lastIndex = null;
                    if (starting.getText().toString().isEmpty() && ending.getText().toString().isEmpty())
                        Toast.makeText(getContext(), "Please atleast choose one of the dates", Toast.LENGTH_LONG).show();
                    else {
                        if (!starting.getText().toString().isEmpty() && !ending.getText().toString().isEmpty()) {
                            Date start = dateFormat.parse(starting.getText().toString());
                            Date end = dateFormat.parse(ending.getText().toString());
                            if (start.compareTo(end) > 0)
                                Toast.makeText(getContext(), "Starting date can't be after the ending date", Toast.LENGTH_LONG).show();
                            else {
                                arrayList.clear();
                                adapter.notifyDataSetChanged();
                                //recyclerView.showShimmerAdapter();
                                currentFlag = 2;
                                viewed.setVisibility(View.GONE);
                                getData(2, "", dateFormat.parse(starting.getText().toString()), dateFormat.parse(ending.getText().toString()));
                            }
                        } else {
                            arrayList.clear();
                            adapter.notifyDataSetChanged();
                            recyclerView.showShimmerAdapter();
                            viewed.setVisibility(View.GONE);
                            if (starting.getText().toString().isEmpty())
                                getData(2, "", null, dateFormat.parse(ending.getText().toString()));
                            else
                                getData(2, "", dateFormat.parse(starting.getText().toString()), null);
                            currentFlag = 2;

                        }

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

/*    private void getArrayListByAuthor(String name,String id,long timestamp) {
        fb.getPollsCollection().whereEqualTo("author", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        viewed.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                            if(id.equals(dS.getId()))
                            addToRecyclerView(dS,timestamp);
                        }
                    }
                    else{
                        if(arrayList.isEmpty()){
                            recyclerView.hideShimmerAdapter();
                            viewed.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
        });

    }*/

    private void getArrayListByDate(Date start, Date end, long timestamp, QueryDocumentSnapshot id) throws ParseException {
        if (end == null)
            end = dateFormat.parse(formatteddate);
        else if (start == null)
            start = dateFormat.parse("21-03-2020");
        PollDetails pollDetails = id.toObject(PollDetails.class);
        if (pollDetails.getCreated_date().compareTo(start) >= 0 && pollDetails.getCreated_date().compareTo(end) <= 0) {
            Log.d("okay", "fitted");
            addToRecyclerView(id, pollDetails.getTimestamp());
            Log.d("HomeFeedSize1", Integer.toString(arrayList.size()));
        }
        else{
            recyclerView.hideShimmerAdapter();
            if(adapter.getItemCount()==0)
            {
                viewed.setVisibility(View.VISIBLE);
                viewed.setText("You have voted all active polls created in that date span.");
            }

        }

    /*    fb.getPollsCollection().orderBy("created_date").whereGreaterThanOrEqualTo("created_date", start)
                .whereLessThanOrEqualTo("created_date", end).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        viewed.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                                addToRecyclerView(dS, timestamp);

                        }

                    }else{
                        if(arrayList.isEmpty()){
                            recyclerView.hideShimmerAdapter();
                            viewed.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });*/
    }

    private void getData(int flagi, String name, Date start, Date end) {
        if (flagi == 0) {
            if (lastIndex == null) {
                fb.getPollsCollection()
                        .orderBy("timestamp", Query.Direction.DESCENDING).
                        limit(20).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d("HomeFeedEmpty", "" + task.getResult().size());
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot dS : task.getResult()) {
                                    long timestamp = (long) dS.get("timestamp");
                                    addToRecyclerView(dS, timestamp);
                                    lastIndex = dS;
                                }
                            } else {
                                flagFetch = false;
                                recyclerView.hideShimmerAdapter();
                                viewed.setVisibility(View.VISIBLE);
                                viewed.setText("You have voted all active polls.");



                            }
                        } else {
                            Log.d("hello", task
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
                                long timestamp = (long) dS.get("timestamp");
                                addToRecyclerView(dS, timestamp);
                                lastIndex = dS;
                            }
                        } else {
                            flagFetch = false;
                            //Toast.makeText(getContext(), "You have viewed all polls...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            if (lastIndex == null) {
                recyclerView.showShimmerAdapter();
                fb.getPollsCollection()
                        .orderBy("timestamp", Query.Direction.DESCENDING).
                        limit(20).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d("HomeFeedEmpty", "" + task.getResult().size());
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot dS : task.getResult()) {
                                    PollDetails pollDetails = dS.toObject(PollDetails.class);
                                    long timestamp = (long) dS.get("timestamp");
                                    if (flagi == 1) {
                                        if (pollDetails.getAuthor_lc().contains(name.toLowerCase().trim())) {
                                            addToRecyclerView(dS, pollDetails.getTimestamp());
                                            recyclerView.hideShimmerAdapter();
                                        }
                                        else{
                                            recyclerView.hideShimmerAdapter();



                                        }
                                    } else if (flagi == 2) {
                                        try {
                                            Log.d("name", pollDetails.getAuthor());
                                            getArrayListByDate(start, end, timestamp, dS);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    lastIndex = dS;
                                }
                            } else {
                                flagFetch = false;
                                recyclerView.hideShimmerAdapter();
                                viewed.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("hello", task
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
                                PollDetails pollDetails = dS.toObject(PollDetails.class);
                                long timestamp = (long) dS.get("timestamp");
                                if (flagi == 1) {
                                    if (pollDetails.getAuthor_lc().contains(name.toLowerCase().trim()))
                                        addToRecyclerView(dS, pollDetails.getTimestamp());
                                    //  getArrayListByAuthor(name, dS.getId(), timestamp);
                                } else if (flagi == 2) {
                                    try {
                                        Log.d("name", pollDetails.getAuthor());
                                        getArrayListByDate(start, end, timestamp, dS);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                lastIndex = dS;
                            }
                        } else {
                            flagFetch = false;
                            //Toast.makeText(getContext(), "You have viewed all polls...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        flagFetch = false;
                    }
                });
            }
        }
    }


    private void addToRecyclerView(QueryDocumentSnapshot dS, long timestamp) {
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
                        fb.getUsersCollection().document(dS.get("authorUID").toString()).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getResult() != null) {
                                if (task1.getResult().get("pic") != null)
                                    polldetails.setPic(task1.getResult().get("pic").toString());
                                else
                                    polldetails.setPic(null);
                                polldetails.setUsername(task1.getResult().get("username").toString());
                                arrayList.add(polldetails);
                               // Log.d("HomeFeedSize2", Integer.toString(arrayList.size()));
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
        });
    }

    private void setGlobals(@NonNull View view) {
        arrayList = new ArrayList<>();
        viewed = view.findViewById(R.id.viewed);
        search = view.findViewById(R.id.search);
        search_layout = view.findViewById(R.id.type_layout);
        date_layout = view.findViewById(R.id.date_layout);
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
        check = view.findViewById(R.id.check);
        search_button = view.findViewById(R.id.search_button);
        starting = view.findViewById(R.id.starting_date);
        ending = view.findViewById(R.id.ending_date);
        back1 = view.findViewById(R.id.back1);
        back2 = view.findViewById(R.id.back2);
        search_type = view.findViewById(R.id.search_type);
        YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(view.findViewById(R.id.text));
        fb = new firebase();
        //viewed.setVisibility(View.GONE);
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
//                        arrayList.clear();
//                        adapter.notifyDataSetChanged();
//                        lastIndex = null;
                        search_layout.setVisibility(View.VISIBLE);
                        date_layout.setVisibility(View.GONE);
                        viewed.setVisibility(View.GONE);
//                        getData(0, "", null, null);
                        return true;
                    case R.id.by_date:
//                        arrayList.clear();
//                        adapter.notifyDataSetChanged();
//                        lastIndex = null;
                        date_layout.setVisibility(View.VISIBLE);
                        search_layout.setVisibility(View.GONE);
                        viewed.setVisibility(View.GONE);
//                        getData(0, "", null, null);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
    }

    private void closeKeyboard() {
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }


}