package com.PollBuzz.pollbuzz.polls;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.objects.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kinda.alert.KAlertDialog;
import com.zcw.togglebutton.ToggleButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.PollBuzz.pollbuzz.Utils.firebase;
import com.PollBuzz.pollbuzz.Utils.helper;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Single_type_poll extends AppCompatActivity {
    Button add;
    RadioGroup group;
    String name, expirydate;
    int c, flagm = 0, yeari, monthi, dayi;
    long sec;
    RadioButton b;
    TextInputEditText question;
    DatePickerDialog datePickerDialog;
    MaterialButton button;
    TextView text1;
    String alpha_numeric;
    Date date = Calendar.getInstance().getTime();
    firebase fb;
    ImageButton home, logout;
    KAlertDialog dialog;
    RadioButton option1, option2;
    ArrayList<String> uniqueoptions = new ArrayList<>();
    TextView expiry;
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    Calendar cal = Calendar.getInstance();
    Date default_date;
    MaterialSpinner materialSpinner;
    ToggleButton toggleButton;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGlobals();
        setActionBarFunctionality();
        final String formattedDate = df.format(date);
        setListeners(formattedDate);
        TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.toggle), "Live Polls", "You can create a Live poll by enabling the toggle button")
                        .cancelable(true)
                        .outerCircleAlpha(0.50f)
                        .dimColor(R.color.black)
                        .transparentTarget(false)
        );
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Single_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut(this);
        });
    }

    private void setListeners(String formattedDate) {
        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                flagm = 1;
                if (item.equals("Custom Stop"))
                    sec = Long.MAX_VALUE;
                else
                    sec = Long.parseLong(item);
            }
        });

        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    flagm = 1;
                    sec = Long.parseLong("30");
                    materialSpinner.setVisibility(View.VISIBLE);
                    text1.setText("Select your time in sec");
                    expiry.setVisibility(View.GONE);
                } else {
                    flagm = 0;
                    materialSpinner.setVisibility(View.GONE);
                    text1.setText("Set Poll Expiry Date");
                    expiry.setVisibility(View.VISIBLE);
                }
            }
        });

        add.setOnClickListener(v -> {
            question.clearFocus();
            RadioButton button = new RadioButton(getApplicationContext());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 20, 0, 20);
            button.setLayoutParams(params);
            String t = "Option" + (c + 1);
            showDialog(Single_type_poll.this, button, 0);
            button.setTag(t.toLowerCase());
            group.addView(button);
            group.setVisibility(View.VISIBLE);
            registerForContextMenu(button);
        });
        group.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
            question.clearFocus();
            RadioButton button = (RadioButton) findViewById(checkedId);
            button.setChecked(false);
            button.showContextMenu();
            closeKeyboard();
        });
        button.setOnClickListener(view -> {
            question.clearFocus();
            closeKeyboard();
            if (question.getText().toString().isEmpty()) {
                question.setError("Please enter the question");
                question.requestFocus();
            } else if (group.getChildCount() < 2) {
                Toast.makeText(Single_type_poll.this, "Please add atleast two options", Toast.LENGTH_SHORT).show();
            } else if (group.getChildCount() > 12) {
                Toast.makeText(getApplicationContext(), "Maximum of 12 options allowed\nDelete some options", Toast.LENGTH_LONG).show();
            } else {
                if (expiry.getVisibility() == View.VISIBLE) {
                    if (expiry.getText().toString().isEmpty()) {
                        expiry.setText(df.format(default_date));
                        expirydate = df.format(default_date);
                        addToDatabase(formattedDate);
                    } else {
                        try {
                            if (df.parse(expiry.getText().toString()).compareTo(df.parse(formattedDate)) >= 0) {
                                String sday = Integer.toString(dayi + 1);
                                String smonth = Integer.toString(monthi);
                                String sint = Integer.toString(yeari);
                                expirydate = (sday + "-" + smonth + "-" + sint);
                                addToDatabase(formattedDate);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (materialSpinner.getVisibility() == View.VISIBLE) {
                    addToDatabase(formattedDate);
                }
            }
        });
        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question.clearFocus();
                closeKeyboard();
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(Single_type_poll.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String date = day + "-" + (month + 1) + "-" + year;
                                expiry.setText(date);
                                yeari = year;
                                monthi = month + 1;
                                dayi = day;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

    }

    private void addToDatabase(String formattedDate) {
        try {
            showDialog();
            button.setEnabled(false);
            if (fb.getUser() != null) {
                PollDetails polldetails = new PollDetails();
                polldetails.setQuestion(question.getText().toString().trim());
                polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
                polldetails.setAuthor_lc(helper.getusernamePref(getApplicationContext()).toLowerCase());
                polldetails.setAuthorUID(fb.getUserId());
                polldetails.setTimestamp(Timestamp.now().getSeconds());
                if (flagm == 1) {
                    Log.d("yes", "item");
                    polldetails.setLive(true);
                    polldetails.setLivePoll(true);
                    polldetails.setSeconds(sec);
                } else {
                    polldetails.setExpiry_date(df.parse(expirydate));
                    alpha_numeric = alpha_numeric(4);
                    polldetails.setPoll_accessID("PB#" + alpha_numeric);
                }
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton v = (RadioButton) group.getChildAt(i);
                    map.put(v.getText().toString(), 0);
                }
                polldetails.setMap(map);
                polldetails.setPoll_type("SINGLE CHOICE");
                polldetails.setCreated_date(df.parse(formattedDate));
                CollectionReference docCreated = fb.getUserDocument().collection("Created");
                DocumentReference doc = fb.getPollsCollection().document();
                doc.set(polldetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Added", doc.getId());
                                Map<String, Object> m = new HashMap<>();
                                m.put("pollId", doc.getId());
                                m.put("timestamp", Timestamp.now().getSeconds());
                                docCreated.document().set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (flagm == 1) {
                                                dialog.dismissWithAnimation();
                                                showDialog(Single_type_poll.this, doc);
                                            }
                                            MediaType mediaType = MediaType.parse("application/json");
                                            JSONObject obj = new JSONObject(), notification = new JSONObject(), data = new JSONObject();
                                            try {
//                                                notification.put("title", "New poll from your favourite author!");
//                                                notification.put("body", helper.getusernamePref(Single_type_poll.this) + " has a new poll for you.");
                                                data.put("type", "SINGLE CHOICE");
                                                data.put("username", helper.getusernamePref(Single_type_poll.this));
                                                data.put("pollId", doc.getId());
                                                data.put("title", polldetails.getQuestion());
                                                if (helper.getpPicPref(Single_type_poll.this) != null)
                                                    data.put("profilePic", helper.getpPicPref(Single_type_poll.this));
//                                                obj.put("notification", notification);
                                                obj.put("data", data);
                                                obj.put("to", "/topics/" + fb.getUserId());
                                                obj.put("priority", "high");
                                            } catch (JSONException e) {
                                                Log.d("Exception", e.getMessage());
                                            }
                                            Log.d("NotificationBody", obj.toString());
                                            RequestBody body = RequestBody.create(mediaType, obj.toString());
                                            OkHttpClient client = new OkHttpClient();
                                            Request request = new Request.Builder()
                                                    .url("https://fcm.googleapis.com/fcm/send")
                                                    .post(body)
                                                    .addHeader("Authorization", "key=" + getString(R.string.server_key))
                                                    .addHeader("Content-Type", "application/json")
                                                    .build();
                                            Call call = client.newCall(request);
                                            call.enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                                                    if (response.isSuccessful()) {
                                                        Log.d("Response", response.body().string());
                                                        response.close();
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(Single_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                                                            dialog.dismissWithAnimation();
                                                        }
                                                    });
                                                    if (flagm != 1) {
                                                        Intent intent = new Intent(Single_type_poll.this, MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(Single_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismissWithAnimation();
                                            button.setEnabled(true);
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Single_type_poll.this, "Unable to post.Please try again", Toast.LENGTH_SHORT).show();
                            dialog.dismissWithAnimation();
                            button.setEnabled(true);
                        });


            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void setGlobals() {
        setContentView(R.layout.activity_single_type_poll);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        fb = new firebase();
        home = view.findViewById(R.id.home);
        group = findViewById(R.id.options);
        add = findViewById(R.id.add);
        add = findViewById(R.id.add);
        c = group.getChildCount();
        button = findViewById(R.id.post);
        question = findViewById(R.id.question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        uniqueoptions.add("Option 1");
        uniqueoptions.add("Option 2");
        text1 = findViewById(R.id.text1);
        registerForContextMenu(option1);
        registerForContextMenu(option2);
        toggleButton = findViewById(R.id.toggle);
        materialSpinner = (MaterialSpinner) findViewById(R.id.spinner);
        materialSpinner.setItems("30", "60", "90", "Custom Stop");
        expiry = findViewById(R.id.expiry_date);
        if (group.getChildCount() == 0)
            group.setVisibility(View.INVISIBLE);
    }

    public void showDialog(Activity activity, DocumentReference doc) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.code_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView code = dialog.findViewById(R.id.code);
        final ImageButton copy = dialog.findViewById(R.id.clip_image);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Copied to clip board", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", code.getText());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }
            }
        });
        dialog.setCancelable(false);

        alpha_numeric = alpha_numeric(4);
        doc.update("poll_accessID", "PB#" + alpha_numeric);
        code.setText("PB#" + alpha_numeric);

        dialog.show();
        window.setAttributes(lp);

        MaterialButton button = dialog.findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                flagm = 0;
                Intent intent = new Intent(Single_type_poll.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        ImageView shareButton = dialog.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Dexter.withActivity(activity)
                            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        sharecode(code.getText().toString().trim(), doc.getId());
                                    }

                                    if (report.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();
                } catch (Exception e) {
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().log(e.getMessage());
                }
            }
        });
    }

    public static String alpha_numeric(int count) {

        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private void sharecode(String code, String UID) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Access Code for the Live poll :\n" + code + "\n\nOr use the link:\n" + "https://pollbuzz.com/share/0" + UID);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(intent, "Share Code Using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No sharing app is installed in your phone!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("Go to settings", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }


    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showDialog() {
        dialog = new KAlertDialog(Single_type_poll.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Uploading your poll");
        dialog.setCancelable(false);
        dialog.show();
    }


    public void showDialog(Activity activity, final RadioButton button, int flag) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.set_name_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextInputLayout text = dialog.findViewById(R.id.name);
        if (flag == 1 && b.getText() != null) {
            text.getEditText().setText(b.getText().toString().trim());
        }
        text.requestFocus();
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                String t = text.getEditText().getText().toString().trim();
                if (flag == 0) {
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
                name = text.getEditText().getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter the option name", Toast.LENGTH_SHORT).show();
                } else {
                    if (!doesContain(name)) {
                        uniqueoptions.remove(button.getText().toString());
                        uniqueoptions.add(name);
                        button.setText(name);
                        Toast.makeText(getApplicationContext(), "Option Added", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "The option is already added", Toast.LENGTH_LONG).show();
                        if (flag == 0)
                            group.removeView(button);

                        dialog.dismiss();

                    }
                    closeKeyboard();
                }

            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.radiobutton_menu, menu);
        b = (RadioButton) v;
        menu.setHeaderTitle("Select The Action");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            //Toast.makeText(getApplicationContext(),"calling code",Toast.LENGTH_LONG).show();
            showDialog(Single_type_poll.this, b, 1);
        } else if (item.getItemId() == R.id.delete) {
            group.removeView(b);
            uniqueoptions.remove(b.getText().toString());
            if (group.getChildCount() == 0)
                group.setVisibility(View.INVISIBLE);
        } else {
            return false;
        }
        return true;
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean doesContain(String word) {
        for (int i = 0; i < uniqueoptions.size(); i++) {
            if (uniqueoptions.get(i).equalsIgnoreCase(word))
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
        cal.add(Calendar.DAY_OF_MONTH, 7);
        default_date = cal.getTime();
    }
}