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
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

public class Multiple_type_poll extends AppCompatActivity {
    Button add;
    MaterialButton post_multi;
    TextInputEditText question_multi;
    LinearLayout group;
    String name, expirydate;
    int c, flagm = 0, yeari, monthi, dayi;
    long sec;
    RadioButton b;
    Date date = Calendar.getInstance().getTime();
    firebase fb;
    TextView text1;
    ImageButton home, logout;
    KAlertDialog dialog;
    RadioButton option1, option2;
    ArrayList<String> uniqueoptions = new ArrayList<>();
    TextView expiry;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Calendar cal = Calendar.getInstance();
    Date default_date;
    MaterialSpinner materialSpinner;
    String alpha_numeric;
    ToggleButton toggleButton;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGlobals();
        setActionBarFunctionality();
        final String formatteddate = dateFormat.format(date);
        setListeners(formatteddate);
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
            Intent i = new Intent(Multiple_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut(this);
        });
    }

    private void setListeners(String formatteddate) {

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

        post_multi.setOnClickListener(view -> {
            closeKeyboard();
            if (question_multi.getText().toString().isEmpty()) {
                question_multi.setError("Please enter the question");
                question_multi.requestFocus();
            } else if (group.getChildCount() < 2) {
                Toast.makeText(Multiple_type_poll.this, "Please add at least two options", Toast.LENGTH_SHORT).show();
            } else if (group.getChildCount() > 12) {
                Toast.makeText(getApplicationContext(), "Maximum of 12 options allowed\nDelete some options", Toast.LENGTH_LONG).show();
            } else {

                if (expiry.getVisibility() == View.VISIBLE) {

                    if (expiry.getText().toString().isEmpty()) {
                        expiry.setText(dateFormat.format(default_date));
                        expirydate = dateFormat.format(default_date);
                        addToDatabase(formatteddate);
                    } else {
                        try {
                            if (dateFormat.parse(expiry.getText().toString()).compareTo(dateFormat.parse(formatteddate)) >= 0) {
                                String sday = Integer.toString(dayi + 1);
                                String smonth = Integer.toString(monthi);
                                String sint = Integer.toString(yeari);
                                expirydate = (sday + "-" + smonth + "-" + sint);
                                addToDatabase(formatteddate);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (materialSpinner.getVisibility() == View.VISIBLE) {
                    addToDatabase(formatteddate);
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(Multiple_type_poll.this,
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
                polldetails.setPoll_type("MULTI SELECT");
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
                    alpha_numeric = alpha_numeric(4);
                    polldetails.setPoll_accessID("PB#" + alpha_numeric);
                    polldetails.setExpiry_date(dateFormat.parse(expirydate));
                }
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
                            Map<String, Object> m = new HashMap<>();
                            m.put("pollId", doc.getId());
                            m.put("timestamp", Timestamp.now().getSeconds());
                            docCreated.document().set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (flagm == 1) {
                                            dialog.dismissWithAnimation();
                                            showDialog(Multiple_type_poll.this, doc);
                                        } else {
                                            MediaType mediaType = MediaType.parse("application/json");
                                            JSONObject obj = new JSONObject(), notification = new JSONObject(), data = new JSONObject();
                                            try {
                                                data.put("type", "MULTI SELECT");
                                                data.put("username", helper.getusernamePref(Multiple_type_poll.this));
                                                data.put("pollId", doc.getId());
                                                data.put("title", polldetails.getQuestion());
                                                if (helper.getpPicPref(Multiple_type_poll.this) != null)
                                                    data.put("profilePic", helper.getpPicPref(Multiple_type_poll.this));
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
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(Multiple_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                                                            dialog.dismissWithAnimation();
                                                        }
                                                    });
                                                    Intent intent = new Intent(Multiple_type_poll.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(Multiple_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismissWithAnimation();
                                        post_multi.setEnabled(true);
                                    }
                                }
                            });
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
        dialog = new KAlertDialog(Multiple_type_poll.this, SweetAlertDialog.PROGRESS_TYPE);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        registerForContextMenu(option1);
        registerForContextMenu(option2);
        uniqueoptions.add("Option 1");
        uniqueoptions.add("Option 2");
        text1 = findViewById(R.id.text1);
        expiry = findViewById(R.id.expiry_date);
        toggleButton = findViewById(R.id.toggle);
        materialSpinner = (MaterialSpinner) findViewById(R.id.spinner);
        materialSpinner.setItems("30", "60", "90", "Custom Stop");

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
        Toast.makeText(activity, doc.getId(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Multiple_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Multiple_type_poll.this, MainActivity.class);
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
                                        sharecode(code.getText().toString().trim());
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

    private void sharecode(String code) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Access Code for the Live poll :\n" + code);
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
        dialogButton.setOnClickListener(v -> {
            name = text.getEditText().getText().toString();
            if (name.isEmpty()) {
                text.setError("Please enter this");
                text.requestFocus();
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
            showDialog(Multiple_type_poll.this, b, 1);
        } else if (item.getItemId() == R.id.delete) {
            group.removeView(b);
            if (group.getChildCount() == 0)
                group.setVisibility(View.INVISIBLE);
        } else {
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