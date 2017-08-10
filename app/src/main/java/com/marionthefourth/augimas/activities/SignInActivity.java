package com.marionthefourth.augimas.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;
import com.onesignal.OneSignal;

public final class SignInActivity extends AppCompatActivity {
//    Activity Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Backend.sendRegistrationToServer(this, FirebaseInstanceId.getInstance().getToken());

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.get(Constants.Strings.UIDs.RECENT_ACTIVITY_UID) != null) {
                Backend.getReference(R.string.title_recent_activities,this).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final RecentActivity recentActivity = new RecentActivity(dataSnapshot);
                            recentActivity.navigate(SignInActivity.this);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}