package com.example.agrify_admin.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify_admin.ProductActivity;
import com.example.agrify_admin.R;
import com.example.agrify_admin.databinding.ItemStoreProductBinding;
import com.example.agrify_admin.model.Store;
import com.example.agrify_admin.viewHolder.StoreHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import es.dmoral.toasty.Toasty;

public class StoreAdapter extends FirestoreAdapter<StoreHolder>  {
    Activity activity;

WriteBatch batch;
FirebaseFirestore firebaseFirestore;

    public interface OnStoreSelectedListener {

        void onStoreSelected(DocumentSnapshot store, View SharedView);

    }
    private OnStoreSelectedListener mListener;

    public StoreAdapter(Query query ,OnStoreSelectedListener listener,Activity activity) {
        super(query);
       mListener=listener;
        this.activity=activity;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemStoreProductBinding itemStoreProductBinding = DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.item_store_product, parent, false);

firebaseFirestore=FirebaseFirestore.getInstance();
batch=firebaseFirestore.batch();
        return new StoreHolder(itemStoreProductBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHolder holder, int position) {

        holder.bind(getSnapshot(position), mListener,activity);
    }

    public void delete(int pos)
    {
        firebaseFirestore.collection("store").document(getSnapshot(pos).getId()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentReference docRef;
                    DocumentReference userProduct;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i("id",document.getId());
                      docRef=firebaseFirestore.collection("wishlist").document(document.getId()).collection("wishlist").document(getSnapshot(pos).getId());
                      
                     batch.delete(docRef);
                     userProduct=firebaseFirestore.collection("store").document(getSnapshot(pos).getId()).collection("wishlist").document(document.getId());
                     batch.delete(userProduct);


                    }
                    firebaseFirestore.collection("store").document(getSnapshot(pos).getId()).collection("sellerList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    DocumentReference ref=document.getDocumentReference("userId");
                                    batch.delete(document.getReference());
                                    batch.delete(ref);

                                }

                            }
                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    getSnapshot(pos).getReference().delete();
                                    notifyDataSetChanged();
                                }
                            });

                        }
                    });




                } else {

                    getSnapshot(pos).getReference().delete();
                    notifyDataSetChanged();
                }
            }
        });



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
