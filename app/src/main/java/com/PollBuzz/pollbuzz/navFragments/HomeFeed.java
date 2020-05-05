package com.PollBuzz.pollbuzz.navFragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.Utils.firebase;
import com.PollBuzz.pollbuzz.adapters.HomePageAdapter;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.responses.Image_type_responses;
import com.PollBuzz.pollbuzz.responses.Multiple_type_response;
import com.PollBuzz.pollbuzz.responses.Ranking_type_response;
import com.PollBuzz.pollbuzz.responses.Single_type_response;
import com.PollBuzz.pollbuzz.results.PercentageResult;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kinda.alert.KAlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class HomeFeed extends Fragment implements HomePageAdapter.okClicked {
    private ArrayList<PollDetails> arrayList;
    private ShimmerRecyclerView recyclerView;
    private com.PollBuzz.pollbuzz.adapters.HomePageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private firebase fb;
    private LayoutAnimationController controller;
    private MaterialTextView viewed;
    private EditText id_search_edittext;
    private ImageButton search, check, back1, back2, id_search, id_search_back;
    private String name = "";
    private Button search_button, id_search_button;
    private TextView starting, ending;
    private TextInputEditText search_type;
    private DocumentSnapshot lastIndex;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private LinearLayout search_layout, date_layout, linear_search, linear_id_search;
    private Date date = Calendar.getInstance().getTime();
    private Boolean flagFirst = true, flagFetch = true;
    private Calendar c = Calendar.getInstance();
    private int mYear = c.get(Calendar.YEAR);
    private int mMonth = c.get(Calendar.MONTH);
    private int mDay = c.get(Calendar.DAY_OF_MONTH);

    final String formatteddate = dateFormat.format(date);
    private int currentFlag = 0;
    private KAlertDialog dialog;
    public static HomeFeed newInstance(String param1, String param2) {
        HomeFeed fragment = new HomeFeed();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
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
                if (!arrayList.isEmpty() && layoutManager.findLastVisibleItemPosition() == arrayList.size() - 11 && flagFetch && !flagFirst) {
                    flagFetch = false;
                    if (currentFlag == 0)
                        getData(currentFlag, "", null, null);
                }
            }
        });

        search.setOnClickListener(view -> {
            closeKeyboard();
            showPopup(view);
        });

        back1.setOnClickListener(view -> {
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
        });

        back2.setOnClickListener(view -> {
            closeKeyboard();
            flagFirst = true;
            arrayList.clear();
            adapter.notifyDataSetChanged();
            lastIndex = null;
            currentFlag = 0;
            getData(0, "", null, null);
            date_layout.setVisibility(View.GONE);
            recyclerView.showShimmerAdapter();
            starting.setText("");
            starting.setHint("Starting Date");
            ending.setText("");
            ending.setHint("Ending Date");
        });

        search_type.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!search_type.getText().toString().isEmpty()) {
                    closeKeyboard();
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                    currentFlag = 1;
                    flagFirst = true;
                    lastIndex = null;
                    name = search_type.getText().toString();
                    getData(1, name, null, null);
                } else
                    Toast.makeText(getContext(), "Please enter the author name", Toast.LENGTH_LONG).show();
            }
            return false;
        });

        search_button.setOnClickListener(view -> {
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
            } else
                Toast.makeText(getContext(), "Please enter the author name", Toast.LENGTH_LONG).show();
        });

        starting.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            String date = day + "-" + (month + 1) + "-" + year;
                            starting.setText(date);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        ending.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            String date = day + "-" + (month + 1) + "-" + year;
                            ending.setText(date);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        check.setOnClickListener(v -> {
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
        });

        id_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_search.setVisibility(View.GONE);
                linear_id_search.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInRight).duration(700).playOn(linear_id_search);
            }
        });

        id_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.SlideOutRight).duration(700).playOn(linear_id_search);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linear_id_search.setVisibility(View.INVISIBLE);
                        linear_search.setVisibility(View.VISIBLE);
                        id_search_edittext.setText("");
                    }
                }, 700);

            }
        });

        id_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                if (id_search_edittext.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter the poll ID", Toast.LENGTH_SHORT).show();
                } else {
                    String poll_id = id_search_edittext.getText().toString().trim();
                    fb.getPollsCollection().whereEqualTo("poll_accessID", poll_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PollDetails pollDetails = null;
                                for (QueryDocumentSnapshot q : task.getResult()) {
                                    pollDetails = q.toObject(PollDetails.class);
                                    pollDetails.setUID(q.getId());
                                }
                                if (pollDetails != null) {
                                    showDialog();
                                    PollDetails finalPollDetails = pollDetails;
                                    fb.getPollsCollection().document(pollDetails.getUID()).collection("Response").document(fb.getUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document1 = task.getResult();
                                                if (document1 != null) {
                                                    if (!document1.exists()) {
                                                        dialog.dismissWithAnimation();
                                                        GotoActivity(finalPollDetails);
                                                        linear_id_search.setVisibility(View.INVISIBLE);
                                                        linear_search.setVisibility(View.VISIBLE);
                                                        id_search_edittext.setText("");
                                                    }
                                                    else {
                                                        dialog.dismissWithAnimation();
                                                        Toast.makeText(getContext(),"You have already voted once.",Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(getContext(), PercentageResult.class);
                                                        intent.putExtra("UID", finalPollDetails.getUID());
                                                        intent.putExtra("type", finalPollDetails.getPoll_type());
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    Toast toast = Toast.makeText(getContext(), "The poll ID is invalid!", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }

                            }
                        }
                    });
                }
            }
        });
    }

    private void GotoActivity(PollDetails pollDetails) {
        String uid = pollDetails.getUID();
        Intent intent;
        switch (pollDetails.getPoll_type()) {
            case "SINGLE CHOICE":
                intent = new Intent(getContext(), Single_type_response.class);
                break;
            case "MULTI SELECT":
                intent = new Intent(getContext(), Multiple_type_response.class);
                break;
            case "RANKED":
                intent = new Intent(getContext(), Ranking_type_response.class);
                break;
            case "PICTURE BASED":
                intent = new Intent(getContext(), Image_type_responses.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + pollDetails.getPoll_type());
        }
        intent.putExtra("UID", uid);
        getContext().startActivity(intent);
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

    private void getArrayListByDate(Date start, Date end, QueryDocumentSnapshot id, boolean islast) throws ParseException {
        if (end == null)
            end = dateFormat.parse(formatteddate);
        else if (start == null)
            start = dateFormat.parse("21-03-2020");
        PollDetails pollDetails = id.toObject(PollDetails.class);
        if (pollDetails.getCreated_date().compareTo(start) >= 0 && pollDetails.getCreated_date().compareTo(end) <= 0) {
            Log.d("okay", "fitted");
            addToRecyclerView(id, islast);
            Log.d("HomeFeedSize1", Integer.toString(arrayList.size()));
        } else {
            if (recyclerView.getActualAdapter() != adapter)
                recyclerView.hideShimmerAdapter();
            if (adapter.getItemCount() == 0) {
                recyclerView.hideShimmerAdapter();
                viewed.setVisibility(View.VISIBLE);
                viewed.setText("You have no unvoted polls created in that date span.");
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
                        limit(20).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            int z = 0;
                            for (QueryDocumentSnapshot dS : task.getResult()) {
                                z++;
                                if (z == task.getResult().size())
                                    addToRecyclerView(dS, true);
                                else
                                    addToRecyclerView(dS, false);
                                lastIndex = dS;
                            }
                        } else {
                            flagFetch = false;
                            recyclerView.hideShimmerAdapter();
                            viewed.setText("There are no polls around...");
                            viewed.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("hello", task.getException().toString());
                        recyclerView.hideShimmerAdapter();
                        Toast.makeText(HomeFeed.this.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                fb.getPollsCollection()
                        .orderBy("timestamp", Query.Direction.DESCENDING).
                        startAfter(lastIndex).limit(20).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot dS : task.getResult()) {
                                addToRecyclerView(dS, false);
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
        } else {
            recyclerView.showShimmerAdapter();
            fb.getPollsCollection()
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d("HomeFeedEmpty", "" + task.getResult().size());
                        if (!task.getResult().isEmpty()) {
                            int z = 0;
                            for (QueryDocumentSnapshot dS : task.getResult()) {
                                z++;
                                PollDetails pollDetails = dS.toObject(PollDetails.class);
                                if (flagi == 1) {
                                    if (adapter.getItemCount() == 0) {
                                        recyclerView.showShimmerAdapter();
                                    }
                                    if (pollDetails.getAuthor_lc().contains(name.toLowerCase().trim())) {
                                        if (z == task.getResult().size())
                                            addToRecyclerView(dS, true);
                                        else {
                                            addToRecyclerView(dS, false);
                                        }
                                    } else {
                                        recyclerView.hideShimmerAdapter();
                                        if (adapter.getItemCount() == 0) {
                                            viewed.setText("You have no unvoted polls for " + name + ".");
                                            viewed.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } else if (flagi == 2) {
                                    try {
                                        if (z == task.getResult().size())
                                            getArrayListByDate(start, end, dS, true);
                                        else
                                            getArrayListByDate(start, end, dS, false);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            flagFetch = false;
                            recyclerView.hideShimmerAdapter();
                            viewed.setText("There are no polls around...");
                            viewed.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("hello", task.getException().toString());
                        recyclerView.hideShimmerAdapter();
                        Toast.makeText(HomeFeed.this.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addToRecyclerView(QueryDocumentSnapshot dS, boolean isLast) {
        viewed.setVisibility(View.GONE);
        PollDetails polldetails = dS.toObject(PollDetails.class);
        polldetails.setUID(dS.getId());
        fb.getPollsCollection().document(dS.getId()).collection("Response").document(fb.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().exists()) {
                        fb.getUsersCollection().document(dS.get("authorUID").toString()).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getResult() != null) {
                                if (task1.getResult().get("pic") != null)
                                    polldetails.setPic(task1.getResult().get("pic").toString());
                                else
                                    polldetails.setPic(null);
                                polldetails.setUsername(task1.getResult().get("username").toString());
                                arrayList.add(polldetails);
                                Log.d("ArraySize", arrayList.size() + "");
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
                                Toast.makeText(HomeFeed.this.getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (flagFirst && isLast) {
                            recyclerView.hideShimmerAdapter();
                            flagFirst = false;
                            viewed.setText("There are no polls around...");
                            viewed.setVisibility(View.VISIBLE);
                        }
                    }
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
        adapter = new HomePageAdapter(getContext(), arrayList, this);
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
        id_search = view.findViewById(R.id.id_search);
        YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(view.findViewById(R.id.text));
        fb = new firebase();
        linear_search = view.findViewById(R.id.linear_search);
        linear_id_search = view.findViewById(R.id.linear_id_search);
        id_search_edittext = view.findViewById(R.id.id_search_edittext);
        id_search_back = view.findViewById(R.id.id_search_back);
        id_search_button = view.findViewById(R.id.search_pollid);
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.filter, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.by_author:
                        search_layout.setVisibility(View.VISIBLE);
                        date_layout.setVisibility(View.GONE);
                        viewed.setVisibility(View.GONE);
                        return true;
                    case R.id.by_date:
                        date_layout.setVisibility(View.VISIBLE);
                        search_layout.setVisibility(View.GONE);
                        viewed.setVisibility(View.GONE);
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

    private void showDialog() {
        dialog = new KAlertDialog(getContext(), KAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Loading poll...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked() {
        closeKeyboard();
    }
}