package com.PollBuzz.pollbuzz.navFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.FavouriteAuthorAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;


public class FavouriteAuthorFeed extends Fragment {
    private ArrayList<Map<String, Object>> arrayList;
    private ShimmerRecyclerView recyclerView;
    FavouriteAuthorAdapter adapter;
    firebase fb;
    private LinearLayoutManager layoutManager;


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
       View view= inflater.inflate(R.layout.fragment_favourite_author_feed, container, false);
       setGlobals(view);
       return view;
        
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();

    }

    private void getData() {
        fb.getUserDocument().collection("Favourite Authors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult()!=null)
                    for(DocumentSnapshot documentSnapshot: task.getResult())
                    {
                        String id=documentSnapshot.getId();
                        Map<String,Object> map=new HashMap<>();
                        map=documentSnapshot.getData();
                        Log.d("username",map.get("Username").toString());
                       Log.d("doc id",id);
                        fb.getUsersCollection().document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot documentSnapshot1=task.getResult();
                                Map<String,Object> map=new HashMap<>();
                                map=(Map)documentSnapshot1.getData();
                                Log.d("username",map.get("username").toString());
                                map.put("UID",id);
                                arrayList.add(map);
                                recyclerView.hideShimmerAdapter();
                                adapter.notifyDataSetChanged();
                            }
                        });


                    }


            }
        });
    }

    private void setGlobals(View view) {
        arrayList = new ArrayList<>();
        recyclerView=view.findViewById(R.id.recyclerview);
        adapter=new FavouriteAuthorAdapter(getContext(),arrayList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        fb=new firebase();
    }
}
