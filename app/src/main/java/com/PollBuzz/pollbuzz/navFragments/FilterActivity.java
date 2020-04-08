package com.PollBuzz.pollbuzz.navFragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.HomePageAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.common.base.CharMatcher;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import Utils.firebase;

public class FilterActivity extends Fragment {
    private ArrayList<PollDetails> arrayList;
    private ShimmerRecyclerView recyclerView;
    private com.PollBuzz.pollbuzz.adapters.HomePageAdapter adapter;
    private firebase fb;
    private LayoutAnimationController controller;
    MaterialTextView viewed, viewed2;
    private TextInputEditText search_type;
    private ImageButton search, check;
    private LinearLayout search_layout, date_layout;
    private Button search_button;
    private String name;
    String name_1u,name_u,name_l,name_1s_rl;
    TextView starting, ending;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private LinearLayoutManager layoutManager;
    Date date = Calendar.getInstance().getTime();
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    final String formatteddate = dateFormat.format(date);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_filter,container,false);
        setGlobals(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
    }

    private void setListeners() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });
        search_type.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    closeKeyboard();
                    arrayList.clear();
                    adapter.notifyDataSetChanged();
                    recyclerView.showShimmerAdapter();

                    name = search_type.getText().toString().trim();

                    if (!name.isEmpty()) {
                        //getData(1,name,null,null);
                        name_1u=name.substring(0,1).toUpperCase()+name.substring(1);
                        name_l=name.toLowerCase();
                        name_u=name.toUpperCase();
                        name_1s_rl=name.substring(0,1)+name.substring(1).toLowerCase();
                        getArrayListByAuthor(name);

                        if(CharMatcher.javaUpperCase().matchesAllOf(name))
                        {
                            getArrayListByAuthor(name_l);
                            getArrayListByAuthor(name_1s_rl);
                        }
                        else if(CharMatcher.javaLowerCase().matchesAllOf(name)){
                            getArrayListByAuthor(name_1u);
                            getArrayListByAuthor(name_u);
                        }
                        else
                        {
                            getArrayListByAuthor(name_l);
                            getArrayListByAuthor(name_1s_rl);
                            getArrayListByAuthor(name_1u);
                            getArrayListByAuthor(name_u);
                        }
                    } else
                        Toast.makeText(getContext(), "Please enter the author name", Toast.LENGTH_LONG).show();                    return true;
                }
                return false;
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                arrayList.clear();
                adapter.notifyDataSetChanged();
                recyclerView.showShimmerAdapter();
                name = search_type.getText().toString().trim();
                if (!name.isEmpty()) {
                    //getData(1,name,null,null);
                    name_1u=name.substring(0,1).toUpperCase()+name.substring(1);
                    name_l=name.toLowerCase();
                    name_u=name.toUpperCase();
                    name_1s_rl=name.substring(0,1)+name.substring(1).toLowerCase();
                    getArrayListByAuthor(name);

                    if(CharMatcher.javaUpperCase().matchesAllOf(name))
                    {
                        getArrayListByAuthor(name_l);
                        getArrayListByAuthor(name_1s_rl);
                    }
                    else if(CharMatcher.javaLowerCase().matchesAllOf(name)){
                        getArrayListByAuthor(name_1u);
                        getArrayListByAuthor(name_u);
                    }
                    else
                        {
                            getArrayListByAuthor(name_l);
                            getArrayListByAuthor(name_1s_rl);
                            getArrayListByAuthor(name_1u);
                            getArrayListByAuthor(name_u);
                        }
                }
                else
                    Toast.makeText(getContext(), "Please enter the author name", Toast.LENGTH_LONG).show();
                //search_type.setText("");
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
                                recyclerView.showShimmerAdapter();
                                getArrayListByDate(dateFormat.parse(starting.getText().toString()), dateFormat.parse(ending.getText().toString()));
                                //getData(2,"",dateFormat.parse(starting.getText().toString()),dateFormat.parse(ending.getText().toString()));
                            }
                        } else {
                            arrayList.clear();
                            adapter.notifyDataSetChanged();
                            recyclerView.showShimmerAdapter();
                            if (starting.getText().toString().isEmpty())
                                getArrayListByDate(null, dateFormat.parse(ending.getText().toString()));
                            else
                                getArrayListByDate(dateFormat.parse(starting.getText().toString()), null);


                        }

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getArrayListByAuthor(String name) {
        //.startAt(name.toUpperCase()).endAt(name.toLowerCase()+"\uf8ff")
        fb.getPollsCollection().whereEqualTo("author", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        viewed.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                            addToRecyclerView(dS);
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

    }

    private void getArrayListByDate(Date start, Date end) throws ParseException {
        if (end == null)
            end = dateFormat.parse(formatteddate);
        else if (start == null)
            start = dateFormat.parse("21-03-2020");
        fb.getPollsCollection().orderBy("created_date").whereGreaterThanOrEqualTo("created_date", start)
                .whereLessThanOrEqualTo("created_date", end).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (!task.getResult().isEmpty()) {
                        viewed.setVisibility(View.VISIBLE);
                        arrayList.clear();
                        adapter.notifyDataSetChanged();
                        for (QueryDocumentSnapshot dS : task.getResult()) {
                            addToRecyclerView(dS);

                        }

                    }else{
                        if(arrayList.isEmpty()){
                            recyclerView.hideShimmerAdapter();
                            viewed.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void addToRecyclerView(DocumentSnapshot dS) {
        PollDetails polldetails = dS.toObject(PollDetails.class);
        polldetails.setUID(dS.getId());
        fb.getPollsCollection().document(dS.getId()).collection("Response").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Boolean flag = Boolean.TRUE;
                for (QueryDocumentSnapshot dS1 : task.getResult()) {
                    if (dS1.getId().equals(fb.getUserId())) {
                        flag = Boolean.FALSE;
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
                            Collections.sort(arrayList, (pollDetails, t1) -> Long.compare(t1.getTimestamp(), pollDetails.getTimestamp()));
                            viewed.setVisibility(View.GONE);
                            recyclerView.hideShimmerAdapter();
                            adapter.notifyDataSetChanged();
                            recyclerView.scheduleLayoutAnimation();
                        }
                    });
                }else{
                    if(arrayList.isEmpty()){
                        recyclerView.hideShimmerAdapter();
                        viewed.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void setGlobals(View view) {
        arrayList = new ArrayList<>();
        viewed = view.findViewById(R.id.viewed);
        viewed2 = view.findViewById(R.id.viewed2);
        search_layout = view.findViewById(R.id.type_layout);
        search_layout.setVisibility(View.GONE);
        search = view.findViewById(R.id.search);
        search_type = view.findViewById(R.id.search_type);
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.animation_down_to_up);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        search_button = view.findViewById(R.id.search_button);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        adapter = new HomePageAdapter(getContext(), arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutAnimation(controller);
        YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(view.findViewById(R.id.text));
        fb = new firebase();
        starting = view.findViewById(R.id.starting_date);
        ending = view.findViewById(R.id.ending_date);
        check = view.findViewById(R.id.check);
        date_layout = view.findViewById(R.id.date_layout);
        date_layout.setVisibility(View.GONE);
    }

    public void showPopup(View v) {
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
                        viewed2.setVisibility(View.GONE);
                        return true;
                    case R.id.by_date:
                        date_layout.setVisibility(View.VISIBLE);
                        search_layout.setVisibility(View.GONE);
                        viewed2.setVisibility(View.GONE);
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
