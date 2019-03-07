package com.example.agrify_admin.ProductActivity;

import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.example.agrify_admin.R;
import com.example.agrify_admin.databinding.ProductImageSteperBinding;
import com.google.android.material.button.MaterialButton;

import java.util.zip.Inflater;

import ernestoyaquello.com.verticalstepperform.Step;

public class ProductImage extends Step<Boolean> {
       ProductImageSteperBinding binding;
     private Boolean  ValidFlag=false;

    protected ProductImage(String title) {
        super(title);
    }
    void setValidFlag()
    {
        ValidFlag=true;
    }
    @Override
    public Boolean getStepData() {

        if(binding.productImage.getVisibility()==View.VISIBLE)
        {
            return true;
        }
        else return false;

    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return null;
    }

    @Override
    public void restoreStepData(Boolean data) {

    }

    @Override
    protected IsDataValid isStepDataValid(Boolean stepData) {
        String message="upload image";
        if(stepData)
        {
            message="";

        }
        return new IsDataValid(stepData,message);
    }

    @Override
    protected View createStepContentLayout() {
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.product_image_steper,null,false);
binding.productUploadButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

    }
});
        return binding.getRoot() ;
    }

    @Override
    protected void onStepOpened(boolean animated) {

    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }
}
