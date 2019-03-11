package com.example.agrify_admin.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.agrify_admin.ProductActivity.ProductActivity;
import com.example.agrify_admin.R;
import com.example.agrify_admin.databinding.ItemStoreProductBinding;
import com.example.agrify_admin.viewHolder.StoreHolder;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class StoreAdapter extends FirestoreAdapter<StoreHolder>  {
    Activity activity;

WriteBatch wishListBatch;
WriteBatch SellerBatch;
WriteBatch imageBatch;

FirebaseFirestore firebaseFirestore;
FirebaseStorage storageReference;
    ItemStoreProductBinding itemStoreProductBinding;
    public interface OnStoreSelectedListener {

        void onStoreSelected(DocumentSnapshot store, View SharedView);

    }
    private OnStoreSelectedListener mListener;

    public StoreAdapter(Query query ,OnStoreSelectedListener listener,Activity activity) {
        super(query);
       mListener=listener;
        this.activity=activity;

        firebaseFirestore=FirebaseFirestore.getInstance();
        wishListBatch=firebaseFirestore.batch();
        SellerBatch=firebaseFirestore.batch();
        imageBatch=firebaseFirestore.batch();
        storageReference= FirebaseStorage.getInstance();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        itemStoreProductBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.item_store_product, parent, false);

        return new StoreHolder(itemStoreProductBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHolder holder, int position) {

        holder.bind(getSnapshot(position), mListener,activity);
    }

    public void delete(int pos)
            //does not delete images
    { productDeleting(true);
        storageReference.getReference().child("storeProductImage").child(getSnapshot(pos).getString("name")).child(getSnapshot(pos).getString("name")).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.info(activity,"main image deleted",Toasty.LENGTH_SHORT).show();
            }
        });
    String productName=getSnapshot(pos).getString("name");
        firebaseFirestore.collection("store").document(getSnapshot(pos).getId()).collection("sellerList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot doc:task.getResult())
                    {
                        SellerBatch.delete(Objects.requireNonNull(doc.getDocumentReference("userId")));
                        deleteSellerProductImage(getSnapshot(pos).getString("name"),doc.getDouble("imageCount").intValue(),doc.getId());
                        Log.i("deleted user Id",doc.getDocumentReference("userId").getPath());
                    }

                }

                firebaseFirestore.collection("store").document(getSnapshot(pos).getId()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                DocumentReference docRef = firebaseFirestore.collection("wishlist").document(doc.getId()).collection("wishlist").document(getSnapshot(pos).getId());
                                SellerBatch.delete(docRef);

                                Log.i("wishlistUserId",docRef.getPath());
                            }
                        }


                        SellerBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    FirebaseFunctions mFunctions;
// ...
                                    HashMap<String, Object> storeHash = new HashMap<>();

                                    DocumentReference ref = firebaseFirestore.collection("store").document(getSnapshot(pos).getId());
                                    storeHash.put("path", ref.getPath());

                                    mFunctions = FirebaseFunctions.getInstance();
                                    mFunctions.getHttpsCallable("recursiveDelete").call(storeHash).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                            if (task.isSuccessful()) {
                                                Toasty.info(activity, task.getResult().toString(), Toasty.LENGTH_SHORT).show();
                                            } else {
                                                Toasty.error(activity, task.getException().getLocalizedMessage(), Toasty.LENGTH_SHORT).show();
                                            }
                                            productDeleting(false);
                                        }
                                    });
                                }
                            }
                        });



                    }

                });
            }
        });



Task<Void> ref=storageReference.getReference().child("storeProductImage").child(productName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
    @Override
    public void onSuccess(Void aVoid) {
        Toasty.info(activity,"image deleted",Toasty.LENGTH_SHORT).show();
    }
});

    }

    private void deleteSellerProductImage(String name, int imageCount,String seller_id) {
        for(int count=1;count<=imageCount;count++) {
            storageReference.getReference().child("storeProductImage").child(name).child(seller_id).child(name).child(name + count).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toasty.info(activity,name+"deleted imagest,Toasty.LENGTH_SHORT").show();
                }
            });
        }
    }

    private void productDeleting(Boolean state) {
        if(state)
        {
            itemStoreProductBinding.mainLinearLayout.setVisibility(View.GONE);
            itemStoreProductBinding.animationViewDelete.setVisibility(View.VISIBLE);
            itemStoreProductBinding.animationViewDelete.playAnimation();


        }
        else {
            itemStoreProductBinding.animationViewDelete.setVisibility(View.GONE);
          itemStoreProductBinding.mainLinearLayout.setVisibility(View.VISIBLE);
          itemStoreProductBinding.animationViewDelete.cancelAnimation();

        }
    }

    public void edit(int pos)
    {
          String id=   getSnapshot(pos).getId();
        Intent intent =new Intent(activity, ProductActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("name",getSnapshot(pos).getString("name"));
        activity.startActivity(intent);
    }
}
