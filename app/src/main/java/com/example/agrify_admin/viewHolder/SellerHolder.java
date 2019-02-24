package com.example.agrify_admin.viewHolder;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify_admin.GlideApp;
import com.example.agrify_admin.databinding.ItemSellerBinding;
import com.example.agrify_admin.model.Seller;
import com.google.firebase.firestore.DocumentSnapshot;

public class SellerHolder extends RecyclerView.ViewHolder {

    ItemSellerBinding binding;
    public SellerHolder(@NonNull ItemSellerBinding item) {
        super(item.getRoot());
        binding = item;
    }
    public void bind(final DocumentSnapshot snapshot, final Activity activity) {

        Seller seller = snapshot.toObject(Seller.class);
        Resources resources = itemView.getResources();

        binding.setSeller(seller);

        // Load image
        if (activity != null) {
            GlideApp.with(activity)
                    .load(seller.getProfilePhotoUrl())
                    .into(binding.profilePhoto);
        }
 final String phoneNumber=seller.getPhone();
        binding.phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                v.getContext().startActivity(intent);
            }
        });
    }



}
