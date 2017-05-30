package com.marionthefourth.augimas.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.fragments.BrandingElementFragment;

public final class BrandingElementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branding_element);
        loadExtrasIntoFragment(getSupportFragmentManager(),savedInstanceState,getIntent().getExtras());
    }

    private void loadExtrasIntoFragment(FragmentManager fragmentManager, final Bundle savedInstanceState, final Bundle extras) {
        BrandingElementFragment brandingElementFragment = new BrandingElementFragment();
        brandingElementFragment.setArguments(extras);
        fragmentManager.beginTransaction().add(android.R.id.content, brandingElementFragment).commit();
    }
}
