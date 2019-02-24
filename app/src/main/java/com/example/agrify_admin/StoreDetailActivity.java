package com.example.agrify_admin;

import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.agrify_admin.adapter.SellerAdapter;
import com.example.agrify_admin.databinding.ActivityStoreDetailBinding;
import com.example.agrify_admin.model.Store;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

public class StoreDetailActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {
    private static final String TAG = "StoreDetail";
    public static final String KEY_STORE_ID = "key_store_id";


    private FirebaseFirestore mFirestore;
    private DocumentReference mStoreRef;
    private ListenerRegistration mStoreRegistration;
    ActivityStoreDetailBinding bind;
    Query sellerQuery;
    SellerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_store_detail);
        bind.productDes.setMovementMethod(new ScrollingMovementMethod());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            bind.acitvityDetailLayout.setBackground(this.getDrawable(R.drawable.store_item_background));//set curve background
        }

        String storeId = getIntent().getExtras().getString(KEY_STORE_ID);
        if (storeId == null) {
            throw new IllegalArgumentException("Must pass extra " + storeId);
        }

        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mStoreRef = mFirestore.collection("store").document(storeId);
        getSellerList();


        bind.appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
        mStoreRegistration = mStoreRef.addSnapshotListener(this);
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        StoreLoaded(snapshot.toObject(Store.class));
    }

    private void StoreLoaded(Store store) {
        bind.setStore(store);
        GlideApp.with(this)
                .load(store.getProductImageUrl())
                .into(bind.productImageUrl);
        bind.appBar.setTitle(store.getName());
        stateLoading(false);

    }


    void getSellerList() {
        stateLoading(true);
        sellerQuery = mStoreRef
                .collection("sellerList")
                .orderBy("name", Query.Direction.DESCENDING);
        mAdapter = new SellerAdapter(sellerQuery, this);
        bind.sellerListRecycleView.setLayoutManager(new LinearLayoutManager(this));
        bind.sellerListRecycleView.setAdapter(mAdapter);


    }

    private void stateLoading(boolean stateStatus) {
        if (stateStatus) {
            bind.acitvityDetailLayout.setVisibility(View.GONE);
            bind.progressLoading.setVisibility(View.VISIBLE);
        } else {
            bind.acitvityDetailLayout.setVisibility(View.VISIBLE);
            bind.progressLoading.setVisibility(View.GONE);
        }
    }

}
