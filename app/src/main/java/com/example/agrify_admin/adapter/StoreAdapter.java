package com.example.agrify_admin.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.agrify_admin.R;
import com.example.agrify_admin.databinding.ItemStoreProductBinding;
import com.example.agrify_admin.viewHolder.StoreHolder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class StoreAdapter extends FirestoreAdapter<StoreHolder> {
    Activity activity;
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


        return new StoreHolder(itemStoreProductBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener,activity);
    }
}
