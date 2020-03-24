package com.PollBuzz.pollbuzz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Multiple_type_response extends AppCompatActivity {
    TextView title, query;
    LinearLayout group;
    FirebaseFirestore db;
    CollectionReference ref;
    Map<String,Integer> options;
    String key;
    Typeface typeface;
    Dialog dialog;
    FirebaseAuth auth;
    ImageButton home,logout;
    FirebaseAuth.AuthStateListener listener;
    Button submit;
    int c;
    Map<String,Integer> response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_type_response);
        setContentView(R.layout.activity_single_type_response);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        c=0;
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Multiple_type_response.this, MainActivity.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
            }
        });
        title=findViewById(R.id.title);
        submit=findViewById(R.id.submit);
        query=findViewById(R.id.query);
        group=findViewById(R.id.options);
        db=FirebaseFirestore.getInstance();
        options=new HashMap<>();
        response=new HashMap<>();
        typeface= ResourcesCompat.getFont(getApplicationContext(),R.font.didact_gothic);
        dialog=new Dialog(Multiple_type_response.this);
        //showDialog();
        auth = FirebaseAuth.getInstance();
        listener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null)
                {
                    Intent i=new Intent(Multiple_type_response.this, Login_Signup_Activity.class);
                    startActivity(i);
                }

            }
        };
        db.collection("Polls").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {



                if (task.isSuccessful()) {

                    DocumentSnapshot data = task.getResult();
                    if(data.exists())
                    {   group.removeAllViews();
                    dialog.dismiss();
                    Polldetails polldetails=data.toObject(Polldetails.class);
                    title.setText(polldetails.getTitle());
                    title.setPaintFlags(title.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    query.setText(polldetails.getQuestion());
                    options=polldetails.getMap();

                    for(Map.Entry<String,Integer> entry : options.entrySet())
                    {
                        RadioButton button=new RadioButton(getApplicationContext());
                        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(5,20,5,20);
                        button.setLayoutParams(layoutParams);
                        button.setTypeface(typeface);

                        button.setText(entry.getKey());
                        button.setTextSize(20.0f);
                        group.addView(button);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RadioButton b=(RadioButton)v;
                                if(b.isChecked())
                                    response.put(b.getText().toString(),0);
                                else
                                    response.remove(b.getText().toString());


                            }
                        });
                    }
                    }
                }

            }
        }

        );

        ref=db.collection("Polls").document(key).collection("Response");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(response);


                ref.document(auth.getCurrentUser().getUid()).set(response);

                db.collection("Users").document(auth.getCurrentUser().getUid()).collection("Voted").document(key).set(response);
                Intent i=new Intent(Multiple_type_response.this,MainActivity.class);
                startActivity(i);


            }
        });




    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);

    }
}
