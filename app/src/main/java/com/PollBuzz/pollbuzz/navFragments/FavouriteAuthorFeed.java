package com.PollBuzz.pollbuzz.navFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.objects.UserDetails;
import com.PollBuzz.pollbuzz.adapters.FavouriteAuthorAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import com.PollBuzz.pollbuzz.Utils.firebase;


public class FavouriteAuthorFeed extends Fragment implements FavouriteAuthorAdapter.itemClicked {
    private List<UserDetails> arrayList;
    private ShimmerRecyclerView recyclerView;
    FavouriteAuthorAdapter adapter;
    MaterialTextView viewed;
    TextInputEditText search;
    int flag = 0;
    firebase fb;
    private LinearLayoutManager layoutManager;
    private boolean flagFirst = true;

    public FavouriteAuthorFeed() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite_author_feed, container, false);
        setGlobals(view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        setListeners();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    search.setHint("Search Author");
                else
                    search.setHint("");
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
                if (!s.toString().isEmpty()) {
                    flag = 1;
                    search.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_back_black_24dp, 0, 0, 0);
                } else {
                    flag = 0;
                    search.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_black_24dp, 0, 0, 0);
                }
            }
        });
        search.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("Points", "value: " + search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width());
                    Log.d("Points", "value: " + event.getRawX() + " " + search.getPaddingLeft());
                    if (event.getRawX() <= (search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width()) + search.getPaddingLeft() + 30) {
                        if (flag == 1) {
                            search.setText("");
                            filter("");
                            closeKeyboard();
                            search.clearFocus();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void getData() {
        fb.getUserDocument().collection("Favourite Authors").get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
            if (task.isSuccessful() && task.getResult() != null)
                if (!task.getResult().isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String id = documentSnapshot.getId();
                        fb.getUsersCollection().document(id).get().addOnCompleteListener(task1 -> {
                            DocumentSnapshot documentSnapshot1 = task1.getResult();
                            if (documentSnapshot1 != null) {
                                UserDetails userDetails = documentSnapshot1.toObject(UserDetails.class);
                                userDetails.setUid(documentSnapshot1.getId());
                                arrayList.add(userDetails);
                                if (flagFirst) {
                                    recyclerView.hideShimmerAdapter();
                                    flagFirst = false;
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    recyclerView.hideShimmerAdapter();
                    viewed.setText("You have no favourite authors.");
                    viewed.setVisibility(View.VISIBLE);
                }
        });
    }

    private void setGlobals(View view) {
        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerview);
        adapter = new FavouriteAuthorAdapter(getContext(), arrayList, this);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.showShimmerAdapter();
        viewed = view.findViewById(R.id.viewed);
        search = view.findViewById(R.id.search);
        fb = new firebase();
        YoYo.with(Techniques.ZoomInDown).duration(1100).playOn(view.findViewById(R.id.text));
    }

    void filter(String text) {
        List<UserDetails> filteredList = new ArrayList<>();
        for (UserDetails details : arrayList) {
            if (details.getUsername().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(details);
            }
        }
        adapter.filterList(filteredList);
        if (filteredList.size() == 0) {
            viewed.setText("No search results for " + text + ".");
            viewed.setVisibility(View.VISIBLE);
        }else{
            viewed.setVisibility(View.GONE);
        }
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onItemClicked() {
        closeKeyboard();
    }
}
