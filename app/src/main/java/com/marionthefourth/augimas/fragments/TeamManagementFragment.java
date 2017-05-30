package com.marionthefourth.augimas.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.TeamMembersAdapter;
import com.marionthefourth.augimas.classes.Constants;
import com.marionthefourth.augimas.classes.Team;
import com.marionthefourth.augimas.classes.User;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.Constants.Ints.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.classes.Constants.Ints.SNACKBAR;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public class TeamManagementFragment extends Fragment {

    public TeamManagementFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_team_management, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.team_member_recycler_view);
        // Set the adapter

        if (recyclerView != null) {
            Context context = view.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            if (PROTOTYPE_MODE) {
                loadPrototypeTeamMembers(view, recyclerView);
            } else {
//                loadChats(view, recyclerView, FirebaseHelper.getCurrentUser());
            }
        }

        AppCompatButton inviteMembers = (AppCompatButton)view.findViewById(R.id.button_invite_member);
        inviteMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInviteMemberDialog(view);
            }
        });


        return view;
    }

    private void loadPrototypeTeamMembers(View view, RecyclerView recyclerView) {
        ArrayList<User> members = new ArrayList<>();
        members.add(new User("Joel", "Bael", User.MemberRole.OWNER));
        members.add(new User("Elizabeth", "Jenkins", User.MemberRole.ADMIN));
        members.add(new User("Will", "Smith", User.MemberRole.EDITOR));
        members.add(new User("Lowe", "Ende", User.MemberRole.CHATTER));
        members.add(new User("Khanverse" ,"Aetion", User.MemberRole.VIEWER));

        Team google = new Team("Google","172351235",members);
        google.setUsername("google");

        TextInputEditText teamName = (TextInputEditText)view.findViewById(R.id.input_team_name);
        teamName.setText(google.getName());

        TextInputEditText teamUsername = (TextInputEditText)view.findViewById(R.id.input_team_username);
        teamUsername.setText(google.getUsername());

        recyclerView.setAdapter(new TeamMembersAdapter(getContext(),google,members));

    }

    public void displayInviteMemberDialog(final View view) {
        // Get FirebaseUser
        final User user = FirebaseHelper.getCurrentUser();

        if (user != null || PROTOTYPE_MODE) {
            // Creating alert Dialog with one Button
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());

            // Setting Dialog Title
            alertDialog.setTitle(view.getContext().getString(R.string.title_invite_member));

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_person_add);

            // Add Username or Email Field
            final TextInputEditText usernameOrEmail = new TextInputEditText(view.getContext());
            final TextInputLayout usernameLayout = new TextInputLayout(view.getContext());
            setupInviteMemberDialogLayout(alertDialog, view, usernameLayout, usernameOrEmail);

            // Setting Positive "Invite" Button
            setupInviteMemberDialogPositiveButton(view, alertDialog, usernameOrEmail, user);

            // Showing Alert Message
            alertDialog.show();
        }
    }

    private void setupInviteMemberDialogPositiveButton(final View view, AlertDialog.Builder alertDialog, TextInputEditText usernameOrEmail, User user) {
        alertDialog.setPositiveButton(R.string.invite, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Constants.FeaturesAvailable.INVITE_TEAM_MEMBER) {

                } else {
                    display(view,SNACKBAR,R.string.feature_unavailable);
                }
            }
        });
    }

    private void setupInviteMemberDialogLayout(final AlertDialog.Builder alertDialog, final View view, final TextInputLayout usernameLayout, final TextInputEditText usernameOrEmail) {
        // Create LinearLayout to add TextInputLayout with Edit Text
        final LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT, GENERAL_PADDING_AMOUNT, GENERAL_PADDING_AMOUNT, GENERAL_PADDING_AMOUNT);

        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        usernameOrEmail.setEnabled(true);
        usernameOrEmail.setHint(view.getContext().getString(R.string.username_or_email_text));
        usernameLayout.addView(usernameOrEmail, 0, lp);
        layout.addView(usernameLayout);
        alertDialog.setView(layout);
    }
}
