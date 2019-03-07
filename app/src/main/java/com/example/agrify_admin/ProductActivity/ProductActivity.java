package com.example.agrify_admin.ProductActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.agrify_admin.GlideApp;
import com.example.agrify_admin.MainActivity;
import com.example.agrify_admin.R;
import com.example.agrify_admin.databinding.ActivityProductBinding;
import com.example.agrify_admin.model.Store;
import com.github.gabrielbb.cutout.CutOut;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;
import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;

public class ProductActivity extends AppCompatActivity implements StepperFormListener {
    ActivityProductBinding binding;
    FirebaseUser firebaseUser;
    Store store;

    String TAG = "ProductActivity";
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private Uri mainImageURI = null;
    private boolean isLoaded = false;
    private StorageReference storageReference;
    private Bitmap compressedImageFile;
    private FirebaseAuth firebaseAuth;
    Boolean isChanged = false;
    Query mQuery;
    Boolean isEdit = false;
    String ProductId;
    String ProductName;
    Category cat = new Category("select Category");
   ProductImage productImage=new ProductImage("upload image");
    ProductDetails productDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

INIT();
        if(getIntent().getExtras()!=null && getIntent().getStringExtra("id")!=null)
        {
            isEdit=true;
            binding.appBar.setTitle("edit product");
            ProductId=getIntent().getStringExtra("id");
            ProductName=getIntent().getStringExtra("name");
            initilzeData();

        }

    }

    private void initilzeData() {
        dataLoading(true);
        firebaseFirestore.collection("store").document(ProductId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dataLoading(false);
                store= documentSnapshot.toObject(Store.class);

             productDetails . binding.setStore(store);
                if(store.getProductImageUrl()!=null) {
                    GlideApp.with(ProductActivity.this)
                            .load(store.getProductImageUrl())
                            .into(productImage.binding.productImage);
                    productImage.binding.productImage.setVisibility(View.VISIBLE);
                    productImage.markAsCompleted(true);
                    productImage.binding.productUploadButton.setText("change image");
                }
              cat.spinner.setSelection(store.getValFromCategory(getApplicationContext()));
            }
        });

    }

    private void INIT() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            binding.productLinearLayout.setBackground(this.getDrawable(R.drawable.store_item_background));//set curve background
        }
        firebaseAuth = FirebaseAuth.getInstance();
        this.setSupportActionBar(binding.appBar);

        firebaseUser = firebaseAuth.getCurrentUser();
        user_id = firebaseAuth.getCurrentUser().getUid();
        productDetails=new ProductDetails("enter product details",this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        binding.stepperForm.setup(this, cat,productImage,productDetails).displayBottomNavigation(false)
                .lastStepNextButtonText("start selling").init();
        binding.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        store=new Store();
productImage.binding.productUploadButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
    uploadImage();
    }
});



    }

    private void uploadImage() {

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


    private void BringImagePicker() {
            CutOut.activity().start(this);   //open cv image library
        }


    @Override
    public void onCompletedForm() {
        store.setName(productDetails.binding.productName.getText().toString().toLowerCase());
        store.setDes(productDetails.binding.productDec.getText().toString());
        store.setUnit(productDetails.binding.productUnit.getText().toString().toLowerCase());
        store.setCategory(cat.spinner.getSelectedItem().toString());


        firebaseFirestore.collection("store").whereEqualTo("name",store.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    if( task.getResult().isEmpty())
                    {
                        uploadData();
                    }
                    else
                    {
                        if(isEdit)
                        {
                            if(ProductName.equals(store.getName()))
                            {
                                uploadData();
                            }
                            else
                            {
                                Toasty.error(ProductActivity.this, "product name is taken by other product ", Toasty.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toasty.error(ProductActivity.this, "data already existed", Toasty.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


    }

    @Override
    public void onCancelledForm() {


    }

    private void uploadData() {


        if (isChanged || isEdit ) {
            dataLoading(true);

            if(isEdit && !isChanged) {

                firebaseFirestore.collection("store").document(ProductId).set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
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



            else{
                final StorageReference ref = storageReference.child("storeProductImage").child(store.getName());
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

                            if(isEdit)
                            {
                                firebaseFirestore.collection("store").document(ProductId).set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                            else {
                                firebaseFirestore.collection("store").document().set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    }
                });




            }}
        else
        {
            Toasty.info(ProductActivity.this,"upload image", Toasty.LENGTH_SHORT).show();
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CutOut.CUTOUT_ACTIVITY_REQUEST_CODE) {

            switch (resultCode) {
                case Activity.RESULT_OK:
                    mainImageURI = CutOut.getUri(data);
                    Log.i("onactresultresult", "mainurl:" + mainImageURI.toString());

                    productImage.binding.productImage.setImageURI(mainImageURI);
                    productImage.binding.productImage.setVisibility(View.VISIBLE);
                     productImage.markAsCompleted(true);


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
}
