package com.marionthefourth.augimas.dialogs;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.adapters.TeamsAdapter;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.helpers.FirebaseHelper;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;
import static com.marionthefourth.augimas.helpers.FirebaseHelper.update;

public final class TeamStatusDialog extends AlertDialog.Builder {
    public TeamStatusDialog(final View containingView, TeamsAdapter.ViewHolder holder) {
        super(containingView.getContext());
        setupDialog(containingView,holder);
    }

    private void setupDialog(View view, TeamsAdapter.ViewHolder holder) {
        // Setting Dialog Title
        setTitle(getContext().getString(R.string.title_team_status_updater));

        // Add Button Fields
        ArrayList<AppCompatButton> buttons = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            buttons.add(new AppCompatButton(getContext()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupButtons(buttons,holder);
        }

        setupLayout(view,buttons);

        // Showing Alert Message
        show();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupButtons(final ArrayList<AppCompatButton> buttons, final TeamsAdapter.ViewHolder holder) {
        for (int i = 0; i < buttons.size();i++) {
            switch (i) {
                case 0:
                    buttons.get(i).setText(FirebaseEntity.EntityStatus.APPROVED.toVerb());
                    break;
                case 1:
                    buttons.get(i).setText(FirebaseEntity.EntityStatus.BLOCKED.toVerb());
                    break;
            }

            final int finalI = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    FirebaseHelper.getReference(getContext(),R.string.firebase_teams_directory).child(holder.teamItem.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final Team teamItem = new Team(dataSnapshot);
                                teamItem.setStatus(FirebaseEntity.EntityStatus.getVerbStatus(finalI));
                                update(getContext(),teamItem);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }

    }

    private void setupLayout(final View view, final ArrayList<AppCompatButton> buttons) {

        final LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        for (int i = 0; i < buttons.size();i++) {
            layout.addView(buttons.get(i));
        }
        setView(layout);
    }
}
