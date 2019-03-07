package com.example.agrify_admin.ProductActivity;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.TextViewBindingAdapter;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.agrify_admin.R;
import com.example.agrify_admin.databinding.ProductDetailsSteperBinding;

import ernestoyaquello.com.verticalstepperform.Step;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class ProductDetails extends Step<Boolean> implements TextViewBindingAdapter.OnTextChanged, TextWatcher {
    Boolean validFlag;
    Activity activity;
    AwesomeValidation mAwesomeValidation ;
    ProductDetailsSteperBinding binding;

    protected ProductDetails(String title ,Activity activity) {
        super(title);
        this.activity=activity;


    }

    @Override
    public Boolean getStepData() {
        return false;
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
        String message="enter correct data";
        if(stepData)
        {
            message="";
        }
        return new IsDataValid(stepData,message);
    }

    @Override
    protected View createStepContentLayout() {

        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.product_details_steper,null,false);
        mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(activity,binding.productName.getId(), RegexTemplate.NOT_EMPTY,R.string.product_empty);
        mAwesomeValidation.addValidation(activity,binding.productDec.getId(), RegexTemplate.NOT_EMPTY,R.string.product_empty);
        mAwesomeValidation.addValidation(activity,binding.productUnit.getId(), RegexTemplate.NOT_EMPTY,R.string.product_empty);
 binding.productName.addTextChangedListener(this);
 binding.productDec.addTextChangedListener(this);
 binding.productUnit.addTextChangedListener(this);

        return binding.getRoot();
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


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      markAsCompletedOrUncompleted(true);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
