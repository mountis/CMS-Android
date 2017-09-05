package com.augimas.android.helpers;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.augimas.android.R;
import com.augimas.android.activities.ChatActivity;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.communication.Channel;
import com.augimas.android.classes.objects.communication.Chat;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.PROGRESS_DIALOG;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.augimas.android.classes.objects.communication.Channel.sortChannels;

public final class FragmentHelper {
//    Widget Building Methods
    public static ProgressDialog buildProgressDialog(int stringID, View view) {
        final ProgressDialog loadingProgress = new ProgressDialog(view.getContext());
        loadingProgress.setMessage(view.getContext().getString(stringID));
        loadingProgress.setProgressStyle(R.style.AppTheme_ProgressDialog);
        loadingProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        loadingProgress.show();
        return loadingProgress;
    }
    public static ProgressDialog display(final int VIEW_TYPE, final int STRING_ID, final View view) {
        switch (VIEW_TYPE) {
            case SNACKBAR:
                Snackbar.make(view, STRING_ID, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case TOAST:
                Toast.makeText(view.getContext(), view.getContext().getString(STRING_ID), Toast.LENGTH_SHORT).show();
                break;
            case PROGRESS_DIALOG:
                final ProgressDialog loadingProgress = new ProgressDialog(view.getContext());
                loadingProgress.setMessage(view.getContext().getString(STRING_ID));
                loadingProgress.setProgressStyle(R.style.AppTheme_ProgressDialog);
                loadingProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                loadingProgress.show();
                return loadingProgress;
            default:
                return null;
        }
        return null;
    }

    public static ProgressDialog display(final int VIEW_TYPE, final int STRING_ID, final Context context) {
        switch (VIEW_TYPE) {
            case SNACKBAR:
//                Snackbar.make(view, STRING_ID, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case TOAST:
                Toast.makeText(context, context.getString(STRING_ID), Toast.LENGTH_SHORT).show();
                break;
            case PROGRESS_DIALOG:
                final ProgressDialog loadingProgress = new ProgressDialog(context);
                loadingProgress.setMessage(context.getString(STRING_ID));
                loadingProgress.setProgressStyle(R.style.AppTheme_ProgressDialog);
                loadingProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                loadingProgress.show();
                return loadingProgress;
            default:
                return null;
        }
        return null;
    }
    public static void display(final int VIEW_TYPE, final int STRING_ID, final String additionalText, final View view) {
        switch (VIEW_TYPE) {
            case SNACKBAR:
                Snackbar.make(view, STRING_ID + " " + additionalText + ".", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case TOAST:
                Toast.makeText(view.getContext(), view.getContext().getString(STRING_ID) + " " + additionalText + ".", Toast.LENGTH_SHORT).show();
                break;
        }
    }
//    Input Verification Methods
    public final static boolean isValidEmail(String email) {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
}
    public final static boolean isValidEmail(CharSequence target) {
    return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
}
    public static boolean fieldsAreFilled(ArrayList<TextInputEditText> editTexts) {
        for (TextInputEditText input:editTexts) {
            if (input.getText().equals("")) return false;
        }
        return true;
    }
    public static boolean fieldsPassWhitelist(ArrayList<TextInputEditText> inputs) {
        String[] whitelist = {
            "augimas","augimus","augeemas","augeemus"
        };

        for (TextInputEditText input:inputs) {
            for (String filter:whitelist) {
                if (input.getText().toString().toLowerCase().equals(filter)) return false;
            }
        }

        return true;
    }
//    Fragment Transition Methods
    public final static void handleNonSupportFragmentRemoval(final FragmentManager rManager) {
        if (rManager.findFragmentByTag(Constants.Strings.Fragments.SETTINGS) != null) {
            rManager.beginTransaction().setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).remove(rManager.findFragmentByTag(Constants.Strings.Fragments.SETTINGS)).commit();
        }
    }

    public static void transitionUserToChatFragment(final Team teamItem, final Activity activity, final String channelUID) {
        Backend.getReference(R.string.firebase_chats_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (final Chat chatItem:Chat.toArrayList(dataSnapshot)) {
                        if (chatItem.hasTeam(teamItem.getUID())) {
                            Backend.getReference(R.string.firebase_channels_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        ArrayList<String> fields = new ArrayList<>();
                                        fields.add(Constants.Strings.UIDs.CHAT_UID);
                                        fields.add(Constants.Strings.Fields.FULL_NAME);
                                        ArrayList<String> content = new ArrayList<>();
                                        content.add(chatItem.getUID());
                                        content.add(teamItem.getName());
                                        final ArrayList<Channel> channels = Channel.toFilteredArrayList(Channel.toArrayList(dataSnapshot),fields,content);

                                        Backend.getReference(R.string.firebase_users_directory, activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    final User currentUser = new User(dataSnapshot);
                                                    if (currentUser != null && !currentUser.getTeamUID().equals("")) {
                                                        Backend.getReference(R.string.firebase_teams_directory, activity).child(currentUser.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                                    final Team currentTeam = new Team(dataSnapshot);
                                                                    ArrayList<String> channelUIDs = sortChannels(channels);

                                                                    final Intent chatIntent = new Intent(activity, ChatActivity.class);

                                                                    if (channelUID != null && !channelUID.equals("")) {
                                                                        for (int i = 0; i < channelUIDs.size(); i++) {
                                                                            if (channelUIDs.get(i).equals(channelUID)) {
                                                                                chatIntent.putExtra(Constants.Strings.Fields.SELECTED_INDEX,i);
                                                                            }
                                                                        }
                                                                    }

                                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.HOST.toInt(false),currentTeam.getUID());
                                                                    chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.CLIENT.toInt(false),teamItem.getUID());
                                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.HOST.toInt(false),channelUIDs.get(0));
                                                                    chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.CLIENT.toInt(false),channelUIDs.get(1));
                                                                    activity.startActivity(chatIntent);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {}
                                                        });
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static void transitionClientUserToChatFragment(final User currentUser, final Activity activity, final String channelUID) {
        // Get Channel UIDs & Team UIDs
        final ArrayList<String> teamUIDs = new ArrayList<>();
        final ArrayList<Channel> channels = new ArrayList<>();

        teamUIDs.add(currentUser.getTeamUID());

        Backend.getReference(R.string.firebase_chats_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot chatReference:dataSnapshot.getChildren()) {
                        final Chat chatItem = new Chat(chatReference);
                        if (currentUser.isInChat(chatItem)) {
                            // Get Other Team Admin Team UID
                            Backend.getReference(R.string.firebase_teams_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                        for (DataSnapshot teamReference:dataSnapshot.getChildren()) {
                                            final Team teamItem = new Team(teamReference);
                                            if (!currentUser.isInTeam(teamItem) && chatItem.hasTeam(teamItem.getUID())) {
                                                teamUIDs.add(teamItem.getUID());
                                                // Get Channel UIDs
                                                Backend.getReference(R.string.firebase_channels_directory, activity).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                                            for (DataSnapshot channelReference:dataSnapshot.getChildren()) {
                                                                final Channel channelItem = new Channel(channelReference);
                                                                if (channelItem.getChatUID().equals(chatItem.getUID())) {
                                                                    channels.add(channelItem);
                                                                }
                                                            }

                                                            if (channels.size() != 0) {

                                                                ArrayList<String> channelUIDs = sortChannels(channels);

                                                                Intent chatIntent = new Intent(activity,ChatActivity.class);
                                                                for (int i = 0; i < channels.size();i++) {
                                                                    if (!channels.get(i).getName().equals(teamItem.getName())) {
                                                                        if(channels.get(i).getName().equals("")) {
                                                                            if (channelUID.equals(channels.get(i).getUID())) {
                                                                                chatIntent.putExtra(Constants.Strings.Fields.SELECTED_INDEX,FirebaseEntity.EntityType.CLIENT.toInt(false));
                                                                            }
                                                                            chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.CLIENT.toInt(false),channels.get(i).getUID());
                                                                        } else {
                                                                            if (channelUID.equals(channels.get(i).getUID())) {
                                                                                chatIntent.putExtra(Constants.Strings.Fields.SELECTED_INDEX,FirebaseEntity.EntityType.HOST.toInt(false));
                                                                            }
                                                                            chatIntent.putExtra(Constants.Strings.UIDs.CHANNEL_UID + FirebaseEntity.EntityType.HOST.toInt(false),channels.get(i).getUID());
                                                                        }
                                                                    }
                                                                }


                                                                chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.HOST.toInt(false),teamUIDs.get(0));
                                                                chatIntent.putExtra(Constants.Strings.UIDs.TEAM_UIDS + FirebaseEntity.EntityType.CLIENT.toInt(false),teamUIDs.get(1));

                                                                activity.startActivity(chatIntent);

                                                            }
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

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}