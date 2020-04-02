package com.PollBuzz.pollbuzz.polls;

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
import com.kinda.alert.KAlertDialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.firebase;
import Utils.helper;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Multiple_type_poll extends AppCompatActivity {
    Button add;
    MaterialButton post_multi;
    TextInputEditText question_multi;
    LinearLayout group;
    String name;
    int c;
    RadioButton b;
    Date date = Calendar.getInstance().getTime();
    firebase fb;
    ImageButton home, logout;
    KAlertDialog dialog;
    RadioButton option1,option2;
    ArrayList<String> uniqueoptions=new ArrayList<>();
    TextView expiry;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

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
            Intent i = new Intent(Multiple_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Multiple_type_poll.this, LoginSignupActivity.class);
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
            showDialog(Multiple_type_poll.this, button, 0);
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
                v.showContextMenu();
                option1.setChecked(false);
            }
        });
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
                option2.setChecked(false);
            }
        });

        post_multi.setOnClickListener(view -> {
            closeKeyboard();
           if (question_multi.getText().toString().isEmpty()) {
                question_multi.setError("Please enter the question");
                question_multi.requestFocus();
            } else if (group.getChildCount() <2) {
                Toast.makeText(Multiple_type_poll.this, "Please add at least two options", Toast.LENGTH_SHORT).show();
            } else {

               if(expiry.getText().toString().isEmpty())
                   expiry.setText("No Expiry");
                addToDatabase(formatteddate);
            }
        });
        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Multiple_type_poll.this,
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
    private void showDialog() {
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Uploading your poll");
        dialog.setCancelable(false);
        dialog.show();
    }


    private void addToDatabase(String formatteddate) {
        try {
            showDialog();
            post_multi.setEnabled(false);
            if (fb.getUser() != null) {
                PollDetails polldetails = new PollDetails();
                polldetails.setQuestion(question_multi.getText().toString().trim());
                polldetails.setCreated_date(dateFormat.parse(formatteddate));
                polldetails.setPoll_type("MULTI ANSWER POLL");
                polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
                polldetails.setAuthorUID(fb.getUserId());
                polldetails.setTimestamp(Timestamp.now().getSeconds());
                if(expiry.getText().toString().equals("No Expiry")){
                    polldetails.setExpiry_date(dateFormat.parse("31-12-2020"));
                }
                else{
                    polldetails.setExpiry_date(dateFormat.parse(expiry.getText().toString()));}
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton v = (RadioButton) group.getChildAt(i);
                    map.put(v.getText().toString().trim(), 0);
                }
                polldetails.setMap(map);
                CollectionReference docCreated = fb.getUserDocument().collection("Created");
                DocumentReference doc = fb.getPollsCollection().document();
                doc.set(polldetails)
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismissWithAnimation();
                            Map<String, Object> m = new HashMap<>();
                            m.put("pollId", doc.getId());
                            m.put("timestamp", Timestamp.now().getSeconds());
                            docCreated.document().set(m);
                            Toast.makeText(Multiple_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Multiple_type_poll.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Multiple_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show();
                            dialog.dismissWithAnimation();
                            post_multi.setEnabled(true);
                        });

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void setGlobals() {
        setContentView(R.layout.activity_multiple_type_poll);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        fb = new firebase();
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        group = findViewById(R.id.options);
        add = findViewById(R.id.add);
        c = group.getChildCount();
        post_multi = findViewById(R.id.post_multi);
        question_multi = findViewById(R.id.question_multi);
        dialog=new KAlertDialog(Multiple_type_poll.this,SweetAlertDialog.PROGRESS_TYPE);
        option1=findViewById(R.id.option1);
        option2=findViewById(R.id.option2);
        registerForContextMenu(option1);
        registerForContextMenu(option2);
        uniqueoptions.add("Option 1");
        uniqueoptions.add("Option 2");
        expiry=findViewById(R.id.expiry_date);

        if (group.getChildCount() == 0)
            group.setVisibility(View.INVISIBLE);
    }

    public void showDialog(Activity activity, final RadioButton button, int flag){
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
        dialogButton.setOnClickListener(v -> {
            name = text.getEditText().getText().toString();
            if (name.isEmpty()) {
                text.setError("Please enter this");
                text.requestFocus();
            } else {
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
            showDialog(Multiple_type_poll.this,b,1);
        }
        else if(item.getItemId()==R.id.delete){
            group.removeView(b);
            if(group.getChildCount()==0)
                group.setVisibility(View.INVISIBLE);
        }else{
            return false;
        }
        return true;
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
    }
}