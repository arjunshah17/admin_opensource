package com.example.agrify_admin.ProductActivity;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.agrify_admin.R;


import ernestoyaquello.com.verticalstepperform.Step;

public class Category extends Step<String> {

    Spinner spinner;

    protected Category(String title) {
        super(title);

    }

    @Override
    public String getStepData() {
        //   isStepDataValid(spinner.getSelectedItem().toString().toLowerCase());

        return spinner.getSelectedItem().toString().toLowerCase();

    }

    @Override
    public String getStepDataAsHumanReadableString() {
        return null;
    }

    @Override
    public void restoreStepData(String data) {
        spinner.setSelection(getValFromCategory(getContext(), data));

    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        boolean isCatValid = true;
        String errorMessage;
        if (stepData.equals("select category")) {
            isCatValid = false;
        }
        errorMessage = !isCatValid ? "select correct Category" : "";


        return new IsDataValid(isCatValid, errorMessage);

    }

    @Override
    protected View createStepContentLayout() {
        spinner = new Spinner(getContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_spinner, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return spinner;
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

    public int getValFromCategory(Context context, String category) {
        String[] arr = context.getResources().getStringArray(R.array.categories_names);
        for (int i = 0; i < arr.length; i++) {
            if (category.equals(arr[i])) {
                return i;
            }
        }
        return 0;
    }
}
