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
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.fragments.ChatFragment;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.helpers.FirebaseHelper.getCurrentUser;

public final class ChatActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ChatActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupActionBar(getSupportActionBar());

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new ChatActivity.SectionsPagerAdapter(getSupportFragmentManager());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        setupTabItemTitles(tabLayout);

    }

    private ArrayList<String> getChannelUIDsFromIntent() {
        final ArrayList<String> channelUIDs = new ArrayList<>();
        channelUIDs.add(getIntent().getStringExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.US.toInt(false)));
        channelUIDs.add(getIntent().getStringExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.THEM.toInt(false)));
        return channelUIDs;
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

    private void setupActionBar(final ActionBar actionBar) {
        setActionBarTitle(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setActionBarTitle(ActionBar actionBar) {
        actionBar.setTitle("Chat");
    }

    private void setupTabItemTitles(final TabLayout tabLayout) {
        // Get TeamUIDs from the Intent

        // Get Current User
        // Get Current User & Current Team
        setTabText(tabLayout);
    }

    private void setTabText(final TabLayout tabLayout) {
        final ArrayList<String> teamUIDs = getTeamUIDsFromIntent();

        FirebaseHelper.getReference(this,R.string.firebase_users_directory).child(getCurrentUser().getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final User currentUser = new User(dataSnapshot);
                    if (currentUser != null && !currentUser.getTeamUID().equals("") && currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.VIEWER)) {
                        // Get Current Team
                        FirebaseHelper.getReference(ChatActivity.this,R.string.firebase_teams_directory).child(currentUser.getTeamUID()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final Team currentTeam = new Team(dataSnapshot);
                                    if (currentTeam != null) {
                                        tabLayout.getTabAt(FirebaseEntity.EntityType.US.toInt(false)).setText(currentTeam.getName());

                                        // get other Team
                                        setTabTextToOtherTeamName(tabLayout,teamUIDs);
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
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO - Display Empty Chat
            }
        });
    }

    private ArrayList<String> getTeamUIDsFromIntent() {
        final ArrayList<String> teamUIDs = new ArrayList<>();
        teamUIDs.add(getIntent().getStringExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.US.toInt(false)));
        teamUIDs.add(getIntent().getStringExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.THEM.toInt(false)));
        return teamUIDs;
    }

    private void setTabTextToOtherTeamName(final TabLayout tabLayout, ArrayList<String> teamUIDs) {
        // Pull the Team from Firebase matching the other Team UID
        FirebaseHelper.getReference(this,R.string.firebase_teams_directory).child(teamUIDs.get(FirebaseEntity.EntityType.THEM.toInt(false))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Team teamItem = new Team(dataSnapshot);
                    if (teamItem != null) {
                        // Set the First Tab Name To the Other Team's Name
                        tabLayout.getTabAt(FirebaseEntity.EntityType.THEM.toInt(false)).setText(teamItem.getName());
                    }
                } else {
                    // TODO - Display Error, Couldn't Get Team Name
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO - Display Error, Couldn't Get Team Name
            }
        });
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
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