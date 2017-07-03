package com.marionthefourth.augimas.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.fragments.BrandingElementsFragment;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;

public final class BrandingElementsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branding_elements);

        FirebaseHelper.getReference(getApplicationContext(),R.string.firebase_users_directory).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && currentUser.getType().equals(FirebaseEntity.EntityType.US)) {
                        setupActionBar(getApplicationContext(),getSupportActionBar());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadExtrasIntoFragment(getSupportFragmentManager(), getIntent().getExtras());

    }

    private void loadExtrasIntoFragment(FragmentManager supportFragmentManager, Bundle extras) {
        final BrandingElementsFragment brandingElementsFragment = new BrandingElementsFragment();
        brandingElementsFragment.setArguments(extras);
        supportFragmentManager.beginTransaction().add(android.R.id.content, brandingElementsFragment).commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar(final Context context, final ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
//        setupActionBarRenamingListener(context, actionBar, intent);
    }

}
