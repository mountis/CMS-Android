package com.marionthefourth.augimas.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.activities.SignInActivity;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseObject;
import com.marionthefourth.augimas.classes.objects.communication.Channel;
import com.marionthefourth.augimas.classes.objects.communication.Chat;
import com.marionthefourth.augimas.classes.objects.communication.Message;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;
import com.marionthefourth.augimas.classes.objects.notifications.Notification;

import java.text.DateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static com.marionthefourth.augimas.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.marionthefourth.augimas.helpers.FragmentHelper.build;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class FirebaseHelper {

    public static void signin(final View view, final User user) {
        final Context context = view.getContext();
        final DatabaseReference usersRef = getReference(context, R.string.firebase_users_directory);
        final ProgressDialog loadingProgress = build(view, R.string.progress_signing_in);
        // To dismiss the dialog

        if (PROTOTYPE_MODE) {
            final Intent homeIntent = new Intent(context, HomeActivity.class);
            context.startActivity(homeIntent);
        } else {
            if (user != null) {
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot userReference : dataSnapshot.getChildren()) {
                                // Get the User instance from the user reference
                                final User newUser = new User(userReference);
                                // Check to see if the User instance's Session ID matches the current Session ID
                                if (newUser != null && user != null) {
                                    if (newUser.getUsername().equals(user.getUsername()) || newUser.getEmail().equals(user.getUsername())) {
                                        // Ensure no one is signed in
                                        if (getCurrentUser() != null && user.getTeamUID().equals("")) {
                                            logout((AppCompatActivity) context, false);
                                        }

                                        // Sign In
                                        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        user.setEmail(newUser.getEmail());

                                        mAuth.signInWithEmailAndPassword(newUser.getEmail(), user.getPassword())
                                                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@android.support.annotation.NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                                        // If sign in fails, display a message to the user. If sign in succeeds
                                                        // the auth state listener will be notified and logic to handle the
                                                        // signed in user can be handled in the listener.
                                                        if (!task.isSuccessful()) {
                                                            Log.w(TAG, "signInWithEmail", task.getException());
                                                            loadingProgress.dismiss();
                                                            display(view, SNACKBAR, R.string.error_incorrect_signin_detail_1);
                                                        } else {
                                                            user.setEmail(newUser.getEmail());
                                                            user.setUID(newUser.getUID());
                                                            loadingProgress.dismiss();

                                                            display(view, TOAST, R.string.success_signin);

                                                            final Intent homeIntent = new Intent(context, HomeActivity.class);
                                                            context.startActivity(homeIntent);
                                                        }
                                                    }
                                                });
                                    }
                                }

                            }
                        } else {
                            loadingProgress.dismiss();
                            display(view, SNACKBAR, R.string.error_incorrect_signin_general);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }

    }
    public static DatabaseReference getReference(final Context context, final int reference) {
        final String CURRENT_REFERENCE = context.getResources().getString(reference);
        switch (reference) {
            case R.string.firebase_url_directory:
                return FirebaseDatabase.getInstance().getReference();
            case R.string.firebase_users_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_branding_element_contents_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_chats_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_branding_elements_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_teams_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_messages_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_notifications_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_channels_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            default:
                return FirebaseDatabase.getInstance().getReference();

        }
    }
    public static void logout(final AppCompatActivity appCompatActivity, final boolean shouldCloseActivity) {
        if (Constants.Bools.FeaturesAvailable.SIGN_OUT) {
            FirebaseAuth.getInstance().signOut();
            display(appCompatActivity.findViewById(R.id.content).getRootView(), TOAST,R.string.success_signout);
        } else {
            display(appCompatActivity.findViewById(R.id.content).getRootView(),SNACKBAR,R.string.feature_unavailable);
        }

        if (shouldCloseActivity) {
            final Intent intent = new Intent(appCompatActivity, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appCompatActivity.startActivity(intent);
        }

    }
    public static User getCurrentUser() {
        final User user = new User();
        final com.google.firebase.auth.FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            user.setEmail(firebaseUser.getEmail());
            user.setUID(firebaseUser.getUid());
            return user;
        }

        return null;
    }

    public static void send(final Context context, final Notification notification) {
        save(context,notification);
    }

    public static void sendNotification(final Context context, final FirebaseObject subject, final Notification.NotificationVerbType verbType, final FirebaseObject object) {
        final Notification notification = new Notification(subject,object,verbType);



        save(context,notification);

    }

    public static void save(final Context context, final FirebaseObject firebaseObject) {
        String key;
        DatabaseReference myRef;
        DatabaseReference itemRef;

        if (firebaseObject instanceof User) {
            myRef = getReference(context,R.string.firebase_users_directory);
            itemRef = myRef.push();
            final User userItem = (User)firebaseObject;
            key = itemRef.getKey();
            userItem.setUID(key);
            itemRef.setValue(userItem.toMap());
        } else if (firebaseObject instanceof Chat) {
            myRef = getReference(context,R.string.firebase_chats_directory);
            itemRef = myRef.push();
            final Chat chatItem = (Chat)firebaseObject;
            key = itemRef.getKey();
            chatItem.setUID(key);
            itemRef.setValue(chatItem.toMap());
        } else if (firebaseObject instanceof BrandingElement) {
            myRef = getReference(context,R.string.firebase_branding_elements_directory);
            itemRef = myRef.push();
            final BrandingElement brandingElementItem = (BrandingElement)firebaseObject;
            key = itemRef.getKey();
            brandingElementItem.setUID(key);
            itemRef.setValue(brandingElementItem.toMap());
        } else if (firebaseObject instanceof Team) {
            myRef = getReference(context,R.string.firebase_teams_directory);
            itemRef = myRef.push();
            final Team teamItem = (Team)firebaseObject;
            key = itemRef.getKey();
            teamItem.setUID(key);
            itemRef.setValue(teamItem.toMap());
        } else if (firebaseObject instanceof Message) {
            myRef = getReference(context,R.string.firebase_messages_directory);
            itemRef = myRef.push();
            final Message messageItem = (Message)firebaseObject;
            key = itemRef.getKey();
            messageItem.setUID(key);
            messageItem.setTimestamp(DateFormat.getDateTimeInstance().format(new Date()));
            itemRef.setValue(messageItem.toMap());
            return;
        } else if (firebaseObject instanceof Notification) {
            myRef = getReference(context,R.string.firebase_notifications_directory);
            itemRef = myRef.push();
            final Notification notificationItem = (Notification)firebaseObject;
            key = itemRef.getKey();
            notificationItem.setUID(key);
            itemRef.setValue(notificationItem.toMap());
        } else if (firebaseObject instanceof Channel) {
            myRef = getReference(context,R.string.firebase_channels_directory);
            itemRef = myRef.push();
            final Channel channelItem = (Channel)firebaseObject;
            key = itemRef.getKey();
            channelItem.setUID(key);
            itemRef.setValue(channelItem.toMap());
        }

        Log.i("","Saved item");
    }
    public static void update(Context context, FirebaseObject firebaseObject){
        DatabaseReference myRef = null;
        DatabaseReference itemRef;
        if (firebaseObject instanceof User) {
            myRef = getReference(context,R.string.firebase_users_directory);
        } else if (firebaseObject instanceof Team) {
            myRef = getReference(context,R.string.firebase_teams_directory);
        } else if (firebaseObject instanceof BrandingElement) {
            myRef = getReference(context,R.string.firebase_branding_element_contents_directory);
        } else if (firebaseObject instanceof Chat) {
            myRef = getReference(context,R.string.firebase_chats_directory);
        } else if (firebaseObject instanceof Message) {
            myRef = getReference(context,R.string.firebase_messages_directory);
        } else if (firebaseObject instanceof Notification) {
            myRef = getReference(context,R.string.firebase_notifications_directory);
        } else if (firebaseObject instanceof Channel) {
            myRef = getReference(context,R.string.firebase_channels_directory);
        }

        if (myRef != null) {
            itemRef = myRef.child(firebaseObject.getUID());
            itemRef.setValue((firebaseObject).toMap());
        }
    }

    public static void delete(final Context context, FirebaseObject firebaseObject) {

        int directory = R.string.firebase_database_url;

        if (firebaseObject instanceof User) {
            directory = R.string.firebase_users_directory;
        } else if (firebaseObject instanceof Chat) {
            directory = R.string.firebase_chats_directory;
        } else if (firebaseObject instanceof Message) {
            directory = R.string.firebase_messages_directory;
        } else if (firebaseObject instanceof BrandingElement) {
            directory = R.string.firebase_branding_element_contents_directory;
        } else if (firebaseObject instanceof Team) {
            directory = R.string.firebase_teams_directory;
        } else if (firebaseObject instanceof Notification){
            directory = R.string.firebase_notifications_directory;
        }

        getReference(
                context,
                directory
        ).child(firebaseObject.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void printIDToken() {
        Log.d("FCM", "Instance ID: " + FirebaseInstanceId.getInstance().getToken());

    }

}