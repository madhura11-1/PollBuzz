package com.PollBuzz.pollbuzz.polls;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kinda.alert.KAlertDialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import Utils.firebase;
import Utils.helper;
import cn.pedant.SweetAlert.SweetAlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Ranking_type_poll extends AppCompatActivity {
    Button add;
    LinearLayout group;
    String name;
    TextInputEditText question_ranking;
    MaterialButton post_ranking;
    int c;
    RadioButton b;
    TextView page_title;
    ImageButton home,logout;
    Date date = Calendar.getInstance().getTime();
    firebase fb;
    KAlertDialog dialog;
    RadioButton option1,option2;
    private ArrayList<String> uniqueoptions=new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    TextView expiry;
    Calendar cal = Calendar.getInstance();
    Date default_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGlobals();
        setActionBarFunctionality();

        final String formatteddate = dateFormat.format(date);
        setListeners(formatteddate);
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Ranking_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Ranking_type_poll.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }
    private void setListeners(String formatteddate) {
        add.setOnClickListener(v -> {
            closeKeyboard();
            final RadioButton button = new RadioButton(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                   LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 20, 0, 20);
            button.setLayoutParams(params);
            String t = "Option" + (c + 1);
            showDialog(Ranking_type_poll.this, button, 0);
            button.setTag(t.toLowerCase());
            group.addView(button);
            group.setVisibility(View.VISIBLE);
            registerForContextMenu(button);
            button.setOnClickListener(v1 -> {
                v1.showContextMenu();
                button.setChecked(false);
            });


        });
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                v.showContextMenu();
                option1.setChecked(false);
            }
        });
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                v.showContextMenu();
                option2.setChecked(false);
            }
        });
        post_ranking.setOnClickListener(view -> {
            closeKeyboard();
           if (question_ranking.getText().toString().isEmpty()) {
                question_ranking.setError("Please enter the question");
                question_ranking.requestFocus();
            } else {

               if(expiry.getText().toString().isEmpty())
               {
                   expiry.setText(dateFormat.format(default_date));
                   addToDatabase(formatteddate);
               }
               else
               {
                   try {
                       if(dateFormat.parse(expiry.getText().toString()).compareTo(dateFormat.parse(formatteddate))>=0){
                           Calendar cali = Calendar.getInstance();
                           int year = cali.get(Calendar.YEAR);
                           int month = cali.get(Calendar.MONTH)+1;
                           int day = cali.get(Calendar.DAY_OF_MONTH)+1;
                           String sday = Integer.toString(day);
                           String smonth = Integer.toString(month);
                           String sint = Integer.toString(year);
                           expiry.setText(sday+"-"+smonth+"-"+sint);
                           addToDatabase(formatteddate);
                       }

                   } catch (ParseException e) {
                       e.printStackTrace();
                   }
               }
            }

        });
        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Ranking_type_poll.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String date=day+"-"+(month+1)+"-"+year;
                                expiry.setText(date);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
    }

    private void addToDatabase(String formatteddate) {
        try {
            String key;
            showDialog();
            post_ranking.setEnabled(false);
            if (fb.getUser() != null) {
                PollDetails polldetails = new PollDetails();
                polldetails.setQuestion(question_ranking.getText().toString().trim());
                polldetails.setCreated_date(dateFormat.parse(formatteddate));
                polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
                polldetails.setAuthorUID(fb.getUserId());
                polldetails.setTimestamp(Timestamp.now().getSeconds());
                    polldetails.setExpiry_date(dateFormat.parse(expiry.getText().toString()));
                Map<String,Object> option=new HashMap<>();
                Map<String,Integer> ranks=new HashMap<>();
                for(int i=0;i<group.getChildCount();i++)
                {
                    ranks.put(String.valueOf(i+1),0);
                }
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton v = (RadioButton) group.getChildAt(i);
                    map.put(v.getText().toString().trim(), 0);
                    option.put(v.getText().toString(),ranks);
                }
                polldetails.setMap(map);
                polldetails.setPoll_type("RANKED");
                CollectionReference docCreated = fb.getUserDocument().collection("Created");
                DocumentReference doc = fb.getPollsCollection().document();
                 key=doc.getId();
                doc.set(polldetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                dialog.dismissWithAnimation();
                                Map<String, Object> m = new HashMap<>();
                                m.put("pollId", doc.getId());
                                m.put("timestamp", Timestamp.now().getSeconds());
                                docCreated.document().set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Ranking_type_poll.this, "Your data added Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Ranking_type_poll.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(Ranking_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismissWithAnimation();
                                            post_ranking.setEnabled(true);
                                        }
                                    }
                                });
                                DocumentReference ref= fb.getPollsCollection().document(key).collection("OptionsCount").document("count");
                                fb.getPollsCollection().document(key).collection("OptionsCount").document("count").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful())
                                        {

                                            DocumentSnapshot result=task.getResult();
                                            if(!result.exists())
                                            {
                                                ref.set(option);
                                            }
                                        }



                                    }
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Ranking_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show();
                            post_ranking.setEnabled(true);
                            dialog.dismissWithAnimation();
                        });
            }

        }catch (Exception e){
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void setGlobals() {
        setContentView(R.layout.activity_ranking_type);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        fb = new firebase();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        page_title = view.findViewById(R.id.page_title);
        group = findViewById(R.id.options);
        add = findViewById(R.id.add);
        c = group.getChildCount();
        question_ranking = findViewById(R.id.question_ranking);
        post_ranking = findViewById(R.id.post_ranking);
        dialog=new KAlertDialog(Ranking_type_poll.this,SweetAlertDialog.PROGRESS_TYPE);
        option1=findViewById(R.id.option1);
        option2=findViewById(R.id.option2);
        uniqueoptions.add("Option 1");
        uniqueoptions.add("Option 2");
        registerForContextMenu(option1);
        registerForContextMenu(option2);
        expiry=findViewById(R.id.expiry_date);

        if (group.getChildCount() == 0)
            group.setVisibility(View.INVISIBLE);
    }

    private void showDialog() {
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Uploading your poll");
        dialog.setCancelable(false);
        dialog.show();
    }

    public void showDialog(Activity activity, final RadioButton button,int flag){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_name_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        final TextInputLayout text =  dialog.findViewById(R.id.name);
        if(flag == 1 && b.getText() != null){
            text.getEditText().setText(b.getText().toString().trim());
        }

        dialog.setCancelable(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                String t=text.getEditText().getText().toString().trim();
                if( flag==0)
                {
                    group.removeView(button);
                }

            }
        });

        dialog.show();
        window.setAttributes(lp);
        Button dialogButton = (Button) dialog.findViewById(R.id.done);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=text.getEditText().getText().toString();
                if(name.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please Enter the option name", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!doesContain(name))
                    {
                        uniqueoptions.remove(button.getText().toString());
                        uniqueoptions.add(name);
                        button.setText(name);
                        Toast.makeText(getApplicationContext(),"Option Added",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"The option is already added",Toast.LENGTH_LONG).show();
                        if(flag==0)
                            group.removeView(button);

                        dialog.dismiss();

                    }

                }

            }
        });





    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.radiobutton_menu, menu);
        b=(RadioButton)v;
        menu.setHeaderTitle("Select The Action");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getItemId()==R.id.edit){
            showDialog(Ranking_type_poll.this,b,1);
        }
        else if(item.getItemId()==R.id.delete){
            group.removeView(b);
            uniqueoptions.remove(b.getText().toString());
            if(group.getChildCount()==0)
                group.setVisibility(View.INVISIBLE);
        }else{
            return false;
        }
        return true;
    }
    private boolean doesContain(String word)
    {
        for(int i=0;i<uniqueoptions.size();i++)
        {
            if(uniqueoptions.get(i).equalsIgnoreCase(word))
                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        uniqueoptions.clear();
        uniqueoptions.add("Option 1");
        uniqueoptions.add("Option 2");
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH,7);
        default_date=cal.getTime();
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}