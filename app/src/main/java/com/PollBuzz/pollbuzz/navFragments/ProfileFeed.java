package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import com.PollBuzz.pollbuzz.LoginSignup.LoginSignupActivity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.PollDetails;
import com.PollBuzz.pollbuzz.PollList;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.ProfileFeedAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.ImagePickerActivity;
import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileFeed extends Fragment {
    private MaterialTextView Uname;
    private ImageView pPic;
    private ImageButton edit;
    private Uri uri;
    private ShimmerRecyclerView profileRV;
    private ProfileFeedAdapter mAdapter;
    private ArrayList<PollDetails> mArrayList;
    private firebase fb;
    private FloatingActionButton fab;
    private LayoutAnimationController controller;

    public ProfileFeed() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setGlobals(view);
        setListeners();
        getData();
    }

    private void setListeners() {
        fab.setOnClickListener(view1 -> {
            Intent i = new Intent(getContext(), PollList.class);
            startActivity(i);
        });
        edit.setOnClickListener(view1 -> {
            try {
                Dexter.withActivity(getActivity())
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
    }

    private void getData() {
        fb.getUserDocument().collection("Created").get().addOnCompleteListener(task -> {
            profileRV.hideShimmerAdapter();
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot dS : task.getResult()) {
                    if (dS.get("pollId") != null)
                        fb.getPollsCollection().document(dS.get("pollId").toString()).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getResult() != null) {
                                DocumentSnapshot dS1 = task1.getResult();
                                if (dS1.exists()) {
                                    addToRecyclerView(dS1);
                                }
                            } else {
                                Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
    }

    private void addToRecyclerView(DocumentSnapshot dS1) {
        PollDetails polldetails = dS1.toObject(PollDetails.class);
        polldetails.setUID(dS1.getId());
        mArrayList.add(polldetails);
        profileRV.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        profileRV.scheduleLayoutAnimation();
    }

    private void setGlobals(@NonNull View view) {
        setHasOptionsMenu(true);
        Toolbar toolbar = view.findViewById(R.id.htab_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        if (((MainActivity) getActivity()).getSupportActionBar() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            onBackArrowPressed(toolbar);
        }
        fb = new firebase();
        Uname = view.findViewById(R.id.username);
        edit = view.findViewById(R.id.edit);
        pPic = view.findViewById(R.id.profilePic);
        profileRV =(ShimmerRecyclerView) view.findViewById(R.id.profileRV);
        profileRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        profileRV.setLayoutManager(linearLayoutManager);
        mArrayList = new ArrayList<>();
        mAdapter = new ProfileFeedAdapter(getContext(), mArrayList);
        profileRV.setAdapter(mAdapter);
        profileRV.showShimmerAdapter();
        fab = view.findViewById(R.id.fab);
        Uname.setText(Utils.helper.getusernamePref(getContext()));
        loadProfilePic(Utils.helper.getpPicPref(getContext()), false);
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.animation_down_to_up);
    }

    private void onBackArrowPressed(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(view1 -> {
            if (getFragmentManager() != null) {
                int index = getFragmentManager().getBackStackEntryCount() - 1;
                FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(index);
                String tag = backEntry.getName();
                getFragmentManager().beginTransaction().hide(ProfileFeed.this)
                        .show(getFragmentManager().findFragmentByTag(tag))
                        .commit();
                if (tag.equals("1")) {
                    MainActivity.bottomBar.setActiveItem(0);
                    MainActivity.active = MainActivity.fragment1;
                } else if (tag.equals("2")) {
                    MainActivity.bottomBar.setActiveItem(1);
                    MainActivity.active = MainActivity.fragment2;
                }
            }
        });
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getContext(), new ImagePickerActivity.PickerOptionListener() {
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
                defaultIntent();
            }
        });
    }

    private void defaultIntent() {
        if (fb.getUser().getPhotoUrl() != null)
            loadProfilePic(fb.getUser().getPhotoUrl().toString(), true);
        else
            loadProfilePic(null, true);
        fb.getStorageReference().child("images/" + fb.getUserId()).delete();
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, 100);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    // loading profile image from local cache

                    addToStorage(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addToStorage(Uri uri) {
        StorageReference mRef = fb.getStorageReference().child("images/" + fb.getUserId());
        Bitmap bmp = null;
        byte[] compressedImage = compressImage(uri);
        if (compressedImage != null) {
            mRef.putBytes(compressedImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        mRef.getDownloadUrl().addOnSuccessListener((Uri uri1) -> {
                            if (uri1 != null)
                                loadProfilePic(uri1.toString(), true);
                            else loadProfilePic(null, true);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        exception.printStackTrace();
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Exception", exception.toString());
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    });
        }
        deleteCache();
    }

    private void loadProfilePic(String url, Boolean update) {
        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .transform(new CircleCrop())
                    .into(pPic);
            if (update) {
                Utils.helper.setpPicPref(getContext(), url);
                fb.getUserDocument().update("pic", url);
            }
        } else {
            pPic.setImageResource(R.drawable.ic_person_black_24dp);
            if (update) {
                Utils.helper.setpPicPref(getContext(), null);
                fb.getUserDocument().update("pic", null);
            }
        }
    }

    private void deleteCache() {
        deleteDir(getContext().getCacheDir());
        deleteDir(getContext().getExternalCacheDir());
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private static boolean deleteDir(File dir) {
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

    private byte[] compressImage(Uri uri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Exception", e.toString());
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
            Utils.helper.removeProfileSetUpPref(getContext());
        fb.signOut();
        Intent i = new Intent(getActivity(), LoginSignupActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

    }
}

