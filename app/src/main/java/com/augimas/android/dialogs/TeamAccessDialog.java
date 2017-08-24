package com.augimas.android.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import com.augimas.android.R;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.fragments.BrandingElementsFragment;
import com.augimas.android.fragments.TeamManagementFragment;
import com.augimas.android.helpers.FragmentHelper;

import java.util.ArrayList;

import static com.augimas.android.helpers.FragmentHelper.handleNonSupportFragmentRemoval;

/**
 * Created on 7/24/17.
 */

public final class TeamAccessDialog extends Builder {
//    Dialog Constructor
    public TeamAccessDialog(final Team teamItem, final Activity activity) {
        super(activity);
        setupDialog(teamItem, activity);
    }
//    Dialog Setup Methods
    private void setupDialog(final Team teamItem, final Activity activity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.list_item_full_team, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        setupLayout(teamItem, alertDialog, dialogView, (AppCompatActivity) activity);
        alertDialog.show();
    }
    private void setupLayout(final Team teamItem, final AlertDialog dialogBuilder, final View dialogView, final AppCompatActivity activity) {
        final int[] BUTTON_IDS = new int[] {
                R.id.button_status,
                R.id.button_dashboard,
                R.id.button_chat,
                R.id.button_team
        };

        AppCompatTextView teamName = (AppCompatTextView) dialogView.findViewById(R.id.item_label_team_display_name);
        teamName.setText(teamItem.getName());

        final ArrayList<AppCompatImageButton> buttons = new ArrayList<>();
        for (int i = 0; i < BUTTON_IDS.length; i++) {
            buttons.add((AppCompatImageButton)dialogView.findViewById(BUTTON_IDS[i]));
            final int buttonIndex = i;
            final int finalI = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleNonSupportFragmentRemoval(activity.getFragmentManager());
                    switch (buttonIndex) {
                        case 0:
                            if (teamItem.getStatus() != FirebaseEntity.EntityStatus.APPROVED) {
                                new TeamStatusDialog(teamItem, activity.findViewById(R.id.container), activity);
                            } else {
                                buttons.get(finalI).setVisibility(View.GONE);
                            }
                            break;
                        case 1:
                            // Setup Dashboard Button
                            activity.getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,BrandingElementsFragment.newInstance(teamItem.getUID()),Constants.Strings.Fragments.BRANDING_ELEMENTS).addToBackStack(Constants.Strings.Fragments.TEAMS).commit();
                            break;
                        case 2:
                            // Setup Chat Button
                            FragmentHelper.transitionUserToChatFragment(teamItem, activity, null);
                            break;
                        case 3:
                            // Setup Team Management Button
                            activity.getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container,TeamManagementFragment.newInstance(teamItem),Constants.Strings.Fragments.TEAM_MANAGEMENT).addToBackStack(Constants.Strings.Fragments.TEAMS).commit();
                            break;
                    }
                    dialogBuilder.dismiss();
                }
            });
        }
    }
}