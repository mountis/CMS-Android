package com.augimas.android.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;

import com.augimas.android.R;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.augimas.android.backend.Backend.getCurrentUser;

/**
 * Created on 8/25/17.
 */

public final class ConfirmActionDialog extends AlertDialog.Builder {
    public ConfirmActionDialog(final String item, final String extraString, final String extraString2, final BrandingElement element, final Activity activity) {
        super(activity);
        setupDialog(item,extraString,extraString2,element,activity);
    }

    private void setupDialog(final String item, final String extraString, final String extraString2, final BrandingElement element, final Activity activity) {
        setMessage(R.string.confirm_data_deletion);
        setPositiveButton(R.string.yes_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                element.getData().remove(item);
                Backend.update(element, activity);
                sendBrandingElementNotification(element, RecentActivity.ActivityVerbType.REMOVE, extraString, extraString2, activity);
            }
        });
        setNegativeButton(R.string.no_text,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        show();
    }

    private void sendBrandingElementNotification(final BrandingElement brandingName, final RecentActivity.ActivityVerbType verbType, final String extraString, final String extraString2, final Activity activity) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayMap<FirebaseEntity.EntityType,Team> teamMap = Team.toClientAndHostTeamMap(dataSnapshot,brandingName.getTeamUID());
                    Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final User currentUser = new User(dataSnapshot);

                            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                                final RecentActivity hostRecentActivity;
                                final RecentActivity clientRecentActivity;

                                if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                    hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName(), extraString);
                                    clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType, "",extraString);
                                } else {
                                    hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType, "",extraString);
                                    clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType,"", extraString);
                                }

                                Backend.sendUpstreamNotification(hostRecentActivity,teamMap.get(FirebaseEntity.EntityType.HOST).getUID(),currentUser.getUID(),brandingName.getType().toString(),activity, true);
                                Backend.sendUpstreamNotification(clientRecentActivity,teamMap.get(FirebaseEntity.EntityType.CLIENT).getUID(),currentUser.getUID(),brandingName.getType().toString(),activity, true);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
}