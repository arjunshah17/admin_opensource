package com.example.agrify_admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.agrify_admin.fragments.sliderFirst;
import com.example.agrify_admin.fragments.sliderSecond;
import com.github.paolorotolo.appintro.AppIntro2;

public class introSlider extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addSlide(new sliderFirst());
        addSlide(new sliderSecond());
        showSkipButton(true);
        setProgressButtonEnabled(true);
        setTheme(R.style.AppTheme);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.

    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
