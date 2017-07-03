package com.marionthefourth.augimas.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.fragments.BrandingElementFragment;

public final class BrandingElementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branding_element);
        setupActionBar(getSupportActionBar());
        loadExtrasIntoFragment(getSupportFragmentManager(), getIntent().getExtras());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            default:
                transitionBackToPreviousScreen();
                return true;
        }
    }

    private void setupActionBar(final ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
//        setupActionBarRenamingListener(context, actionBar, intent);
    }

    private void transitionBackToPreviousScreen() {
        final Intent brandingElementsIntent = new Intent(this, BrandingElementsActivity.class);
        brandingElementsIntent.putExtra(Constants.Strings.UIDs.TEAM_UID,getIntent().getStringExtra(Constants.Strings.UIDs.TEAM_UID));
        startActivityForResult(brandingElementsIntent, 0);
    }

    private void loadExtrasIntoFragment(FragmentManager fragmentManager, final Bundle extras) {
        final BrandingElementFragment brandingElementFragment = new BrandingElementFragment();
        brandingElementFragment.setArguments(extras);
        fragmentManager.beginTransaction().add(android.R.id.content, brandingElementFragment).commit();
    }
}
