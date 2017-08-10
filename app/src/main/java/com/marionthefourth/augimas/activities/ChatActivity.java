package com.marionthefourth.augimas.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.fragments.ChatFragment;

import java.util.ArrayList;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;

public final class ChatActivity extends AppCompatActivity {
    //    Activity Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupActionBar(getSupportActionBar());

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setCurrentItem(getIntent().getIntExtra(Constants.Strings.Fields.SELECTED_INDEX,0));

        setupTabItemTitles(tabLayout);
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
//    Action Bar Methods
    private void setupActionBar(final ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setTitle("Chat");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
//    Tab Item Naming Methods
    private void setupTabItemTitles(final TabLayout tabLayout) {
        final ArrayList<String> teamUIDs = getTeamUIDsFromIntent();

        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory, this).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot userSnapshot) {
                    if (userSnapshot.exists()) {
                        final User currentUser = new User(userSnapshot);
                        if (!currentUser.getTeamUID().equals("") && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                            // Get Current Team
                            Backend.getReference(R.string.firebase_teams_directory, ChatActivity.this).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot teamSnapshot) {
                                    if (teamSnapshot.exists()) {
                                        final Team currentTeam = new Team(teamSnapshot);
                                        tabLayout.getTabAt(FirebaseEntity.EntityType.HOST.toInt(false)).setText(currentTeam.getName());
                                        // Get other Team
                                        setTabTextToOtherTeamName(teamUIDs, tabLayout);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // TODO - Display Empty Chat
                                }
                            });
                        }

                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // TODO - Display Empty Chat
                }
            });
        }
    }
    private void setTabTextToOtherTeamName(final ArrayList<String> teamUIDs, final TabLayout tabLayout) {
        Backend.getReference(R.string.firebase_teams_directory, this).child(teamUIDs.get(FirebaseEntity.EntityType.CLIENT.toInt(false))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Team teamItem = new Team(dataSnapshot);
                    // Set the First Tab Name To the Other Team's Name
                    tabLayout.getTabAt(FirebaseEntity.EntityType.CLIENT.toInt(false)).setText(teamItem.getName());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
//    Intent Methods
    private ArrayList<String> getTeamUIDsFromIntent() {
        final ArrayList<String> teamUIDs = new ArrayList<>();
        teamUIDs.add(getIntent().getStringExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.HOST.toInt(false)));
        teamUIDs.add(getIntent().getStringExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.CLIENT.toInt(false)));
        return teamUIDs;
    }
    private ArrayList<String> getChannelUIDsFromIntent() {
        final ArrayList<String> channelUIDs = new ArrayList<>();
        channelUIDs.add(getIntent().getStringExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.HOST.toInt(false)));
        channelUIDs.add(getIntent().getStringExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.CLIENT.toInt(false)));
        return channelUIDs;
    }
//    Section Pager Adapter
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return ChatFragment.newInstance(getChannelUIDsFromIntent().get(position));
        }
        @Override
        public int getCount() {
            return 2;
        }
    }
}