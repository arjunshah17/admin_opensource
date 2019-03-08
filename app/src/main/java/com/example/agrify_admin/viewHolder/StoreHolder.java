package com.example.agrify_admin.viewHolder;

import android.app.Activity;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrify_admin.GlideApp;
import com.example.agrify_admin.MainActivity;
import com.example.agrify_admin.R;
import com.example.agrify_admin.adapter.StoreAdapter;
import com.example.agrify_admin.databinding.ItemStoreProductBinding;
import com.example.agrify_admin.model.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.varunest.sparkbutton.SparkEventListener;

import es.dmoral.toasty.Toasty;


public class StoreHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    public ItemStoreProductBinding binding;
public FirebaseFirestore db;
public FirebaseAuth auth;

    public StoreHolder(@NonNull ItemStoreProductBinding item) {
        super(item.getRoot());
        binding = item;
    }


    public void bind(final DocumentSnapshot snapshot,
                     final StoreAdapter.OnStoreSelectedListener listener, final Activity activity) {
        auth=FirebaseAuth.getInstance();
db=FirebaseFirestore.getInstance();
        final Store store = snapshot.toObject(Store.class);
        Resources resources = itemView.getResources();

        binding.setStore(store);
        if(store.getPrice()==0)
        {
            binding.price.setText("no seller is selling");
        }
        else {
            binding.price.setText("start from ₹" + String.valueOf(store.getPrice()) + "/" + store.getUnit());
        }
        // Load image
        if (activity != null) {
            GlideApp.with(activity)
                    .load(store.getProductImageUrl())
                    .into(binding.productImage);
        }
        binding.wiseButton.setEventListener(new SparkEventListener() {


            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState) {
                    Toasty.success(activity, store.getName() + " added to wishlist", Toasty.LENGTH_SHORT).show();
                } else {
                    Toasty.info(activity, store.getName() + " remove from wishlist", Toasty.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        // Click listener
itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                  View SharedView=  binding.productImage;

                    listener.onStoreSelected(snapshot,SharedView);
                }
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("select an option");

       menu.add(getAdapterPosition(),1,1,"edit");
       menu.add(getAdapterPosition(),2,2,"remove");


    }
}