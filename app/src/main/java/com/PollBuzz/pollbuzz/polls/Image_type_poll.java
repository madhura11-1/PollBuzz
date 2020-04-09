package com.PollBuzz.pollbuzz.polls;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.R;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kinda.alert.KAlertDialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.ImagePickerActivity;
import Utils.firebase;
import Utils.helper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Image_type_poll extends AppCompatActivity {

    Button add;
    LinearLayout l1, l2, group;
    ImageButton home, logout;
    Uri uri1, uri2;
    ImageView view1, view2;
    RadioButton b1, b2;
    MaterialButton post_image;
    TextInputEditText question_image;
    firebase fb;
    Date date = Calendar.getInstance().getTime();
    Calendar cal = Calendar.getInstance();
    Date default_date;



    @Override
    protected void onStart() {
        super.onStart();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH,7);
        default_date=cal.getTime();

    }

    private int requestCode = 0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    final String formatteddate = dateFormat.format(date);
    KAlertDialog dialog;
    TextView expiry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGlobals();
        setActionBarFunctionality();
        registerForContextMenu(b1);
        registerForContextMenu(b1);
        setListeners();
    }

    private void setListeners() {
        b1.setOnClickListener(v -> {
            closeKeyboard();
            v.showContextMenu();
            b1.setChecked(false);
            try {
                requestCode = 100;
                Dexter.withActivity(Image_type_poll.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
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
        });
        b2.setOnClickListener(v -> {
            closeKeyboard();
            v.showContextMenu();
            b2.setChecked(false);
            try {
                requestCode = 200;
                Dexter.withActivity(Image_type_poll.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
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
            }
        });
        post_image.setOnClickListener(view -> {
            closeKeyboard();
            if (question_image.getText().toString().isEmpty()) {
                question_image.setError("Please enter the question");
                question_image.requestFocus();
            } else {

                if(expiry.getText().toString().isEmpty())
                {
                    expiry.setText(dateFormat.format(default_date));
                    addToStorage();
                }

                else{
                    addToStorage();

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(Image_type_poll.this,
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

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(Image_type_poll.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut();
            Intent i = new Intent(Image_type_poll.this, LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
    }

    private void setGlobals() {
        setContentView(R.layout.activity_image_type_poll);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        fb = new firebase();
        expiry=findViewById(R.id.expiry_date);
        l1 = findViewById(R.id.l1);
        group = findViewById(R.id.options);
        add = findViewById(R.id.add);
        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        b1 = findViewById(R.id.option1);
        b2 = findViewById(R.id.option2);
        view1 = findViewById(R.id.image1);
        view2 = findViewById(R.id.image2);
        home = view.findViewById(R.id.home);
        logout = view.findViewById(R.id.logout);
        post_image = findViewById(R.id.post_imagetype);
        question_image = findViewById(R.id.question_imagetype);
        dialog=new KAlertDialog(Image_type_poll.this,SweetAlertDialog.PROGRESS_TYPE);
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }

            @Override
            public void defaultPic() {
                if (requestCode == 100)
                    loadProfilePic(view1, null);
                else
                    loadProfilePic(view2, null);
            }
        });
    }
    private void showDialog() {
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        dialog.setTitleText("Uploading your poll");
        dialog.setCancelable(false);
        dialog.show();
    }


    private void launchCameraIntent() {
        Intent intent = new Intent(Image_type_poll.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, requestCode);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(Image_type_poll.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                uri1 = data.getParcelableExtra("path");
                loadProfilePic(view1, uri1.toString());
            } else {
                uri2 = data.getParcelableExtra("path");
                loadProfilePic(view2, uri2.toString());
            }
        }
    }

    private void loadProfilePic(ImageView view, String url) {
        try {
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (url != null) {
                Glide.with(this)
                        .load(url)
                        .into(view);
            } else {
                view.setImageResource(R.drawable.place_holder);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void addToStorage() {
        try {
            showDialog();
            post_image.setEnabled(false);
            PollDetails polldetails = new PollDetails();
            polldetails.setQuestion(question_image.getText().toString().trim());
            polldetails.setCreated_date(dateFormat.parse(formatteddate));
            polldetails.setPoll_type("PICTURE BASED");
                polldetails.setExpiry_date(dateFormat.parse(expiry.getText().toString()));
            polldetails.setAuthor(helper.getusernamePref(getApplicationContext()));
            polldetails.setAuthor_lc(helper.getusernamePref(getApplicationContext()).toLowerCase());
            polldetails.setAuthorUID(fb.getUserId());
            polldetails.setTimestamp(Timestamp.now().getSeconds());
            Map<String, Integer> map = new HashMap<>();
            String uri1String = uri1.toString().replace("\\", "");
            StorageReference mRef = fb.getStorageReference().child("polls/" + fb.getUserId() + "/" + uri1String + "/option1");
            Log.d("ImagePath", uri1.toString());
            byte[] compressedImage = compressImage(uri1);
            if (compressedImage != null) {
                mRef.putBytes(compressedImage)
                        .addOnSuccessListener(taskSnapshot -> {
                            mRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imagePath = uri.toString();
                                Log.d("ImagePath", imagePath);
                                map.put(imagePath, 0);
                                String uri2String = uri2.toString().replace("\\", "");
                                StorageReference mRef1 = fb.getStorageReference().child("polls/" + fb.getUserId() + "/" + uri2String + "/option2");
                                Log.d("ImagePath", uri2.toString());
                                byte[] compressedImage1 = compressImage(uri2);
                                if (compressedImage1 != null) {
                                    mRef1.putBytes(compressedImage1)
                                            .addOnSuccessListener(taskSnapshot1 -> {
                                                dialog.dismissWithAnimation();
                                                mRef1.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                                    String imagePath1 = uri1.toString();
                                                    Log.d("ImagePath", imagePath1);
                                                    map.put(imagePath1, 0);
                                                    polldetails.setMap(map);
                                                    addToDatabase(polldetails);
                                                }).addOnFailureListener(exception -> {
                                                    exception.printStackTrace();
                                                    Log.d("Exception", exception.toString());
                                                });
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    exception.printStackTrace();
                                                    Log.d("Exception", exception.toString());
                                                    dialog.dismissWithAnimation();
                                                    post_image.setEnabled(true);
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                }
                                            });
                                }
                            });
                        });
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void addToDatabase(PollDetails polldetails) {
        try {
            fb.getPollsCollection().add(polldetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            deleteCache();
                            Map<String, Object> m = new HashMap<>();
                            m.put("pollId", task.getResult().getId());
                            m.put("timestamp", Timestamp.now().getSeconds());
                            fb.getUserDocument().collection("Created").document().set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Image_type_poll.this, "Your data added successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(Image_type_poll.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(Image_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(Image_type_poll.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Exception", task.getException().toString());
                        }
                    });
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().log(e.getMessage());
        }
    }

    private void deleteCache() {
        deleteDir(getApplicationContext().getCacheDir());
        deleteDir(getApplicationContext().getExternalCacheDir());
    }

    private byte[] compressImage(Uri uri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Exception", e.toString());
            FirebaseCrashlytics.getInstance().log(e.getMessage());
            return null;
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Image_type_poll.this);
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

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


