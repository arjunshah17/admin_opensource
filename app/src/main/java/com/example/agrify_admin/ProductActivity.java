package com.example.agrify_admin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.agrify_admin.databinding.ActivityProductBinding;
import com.example.agrify_admin.model.Store;
import com.github.gabrielbb.cutout.CutOut;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;

import es.dmoral.toasty.Toasty;

public class ProductActivity extends AppCompatActivity  {
    ActivityProductBinding binding;
    FirebaseUser firebaseUser;
    Store store;

    String TAG="ProductActivity";
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private Uri mainImageURI = null;
    private boolean isLoaded = false;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    private FirebaseAuth firebaseAuth;
Boolean isChanged=false;
      Query mQuery;
      Boolean isEdit=false;
String ProductId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            binding.productLinearLayout.setBackground(this.getDrawable(R.drawable.store_item_background));//set curve background
        }

        store = new Store();
        firebaseAuth = FirebaseAuth.getInstance();
        this.setSupportActionBar(binding.appBar);

        firebaseUser = firebaseAuth.getCurrentUser();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        if(getIntent().getExtras()!=null && getIntent().getStringExtra("id")!=null)
        {
            isEdit=true;
            binding.appBar.setTitle("edit product");
            ProductId=getIntent().getStringExtra("id");
            initilzeData();

        }
        binding.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        binding.productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(ProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(ProductActivity.this, "Permission granted", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(ProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                         Toasty.error(ProductActivity.this,"permission not granted", Toasty.LENGTH_LONG).show();

                    }
                    BringImagePicker();


                } else {

                    BringImagePicker();

                }
            }
        });





    }

    private void initilzeData() {
       firebaseFirestore.collection("store").document(ProductId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
              store= documentSnapshot.toObject(Store.class);
              binding.setStore(store);
               GlideApp.with(ProductActivity.this)
                       .load(store.getProductImageUrl())
                       .into(binding.productImageView);

               binding.catSpinner.setSelection(store.getValFromCategory(getApplicationContext()));
           }
       });

    }

    private void uploadData() {

        store.setCategory(binding.catSpinner.getSelectedItem().toString());
        store.setName(binding.productName.getText().toString());




        if (isChanged ) {
dataLoading(true);






            final StorageReference ref = storageReference.child("storeProductImage").child(store.getName() + ".jpg");
            UploadTask image_path = (UploadTask) ref.putFile(mainImageURI);//uploaded image in cloud

            Task<Uri> urlTask=image_path.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        store.setProductImageUrl(downloadUri.toString());
                        store.setCategory(binding.catSpinner.getSelectedItem().toString());
                        store.setName(binding.productName.getText().toString());
                        store.setDes(binding.productdesTextview.getText().toString());

                        firebaseFirestore.collection("store").document(store.getName()).set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dataLoading(false);
                                    Toasty.success(ProductActivity.this, "product added", Toasty.LENGTH_LONG).show();
                                    startActivity(new Intent(ProductActivity.this, MainActivity.class));
                                } else {
                                    Toasty.error(ProductActivity.this, "error in uploading data", Toasty.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });




        }
        else
        {
            Toasty.info(ProductActivity.this,"upload image", Toasty.LENGTH_SHORT).show();
        }

    }


    private void BringImagePicker() {
        CutOut.activity().start(this);   //open cv image library
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CutOut.CUTOUT_ACTIVITY_REQUEST_CODE) {

            switch (resultCode) {
                case Activity.RESULT_OK:
                    mainImageURI = CutOut.getUri(data);
                    Log.i("onactresultresult", "mainurl:" + mainImageURI.toString());

                    binding.productImageView.setImageURI(mainImageURI);

                    isChanged = true;


                    break;
                case CutOut.CUTOUT_ACTIVITY_RESULT_ERROR_CODE:
                    Exception ex = CutOut.getError(data);
                    break;
                default:
               Toasty.info(ProductActivity.this,"user cancel image", Toasty.LENGTH_SHORT).show();
            }
        }
    }
        void dataLoading(boolean state)
        {
            if (state)
            {
                binding.loaderLayout.setVisibility(View.GONE);
                binding.loaderProgressBar.setVisibility(View.VISIBLE);
            }
            else {
                binding.loaderProgressBar.setVisibility(View.GONE);
                binding.loaderLayout.setVisibility(View.VISIBLE);

            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_activitytoolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //add the function to perform here
        DocumentReference defRef  =firebaseFirestore.collection("store").document(binding.productName.getText().toString().toLowerCase());
        defRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toasty.error(ProductActivity.this,"data already exists", Toasty.LENGTH_LONG).show();
                    } else {
                        uploadData();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return (true);

    }

    }

