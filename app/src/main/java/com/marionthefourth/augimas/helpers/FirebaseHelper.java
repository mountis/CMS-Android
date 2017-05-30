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
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.activities.HomeActivity;
import com.marionthefourth.augimas.activities.SignInActivity;
import com.marionthefourth.augimas.classes.BrandingElement;
import com.marionthefourth.augimas.classes.Chat;
import com.marionthefourth.augimas.classes.Constants;
import com.marionthefourth.augimas.classes.FirebaseObject;
import com.marionthefourth.augimas.classes.Message;
import com.marionthefourth.augimas.classes.Team;
import com.marionthefourth.augimas.classes.User;

import java.text.DateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static com.marionthefourth.augimas.classes.Constants.Ints.SNACKBAR;
import static com.marionthefourth.augimas.classes.Constants.Ints.TOAST;
import static com.marionthefourth.augimas.helpers.FragmentHelper.build;
import static com.marionthefourth.augimas.helpers.FragmentHelper.display;

public final class FirebaseHelper {

    public static void signin(final View view, final User user) {
        final Context context = view.getContext();
        final DatabaseReference usersRef = getReference(context, R.string.firebase_users_directory);
        final ProgressDialog loadingProgress = build(view, R.string.progress_signing_in);
        // To dismiss the dialog

        if (true) {
            Intent homeIntent = new Intent(context, HomeActivity.class);
            context.startActivity(homeIntent);
        } else {
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
                                    } else {
//                                    FirebaseAuth.getInstance().createCustomToken(uid)
//                                            .addOnSuccessListener(new OnSuccessListener<String>() {
//                                                @Override
//                                                public void onSuccess(String customToken) {
//                                                    // Send token back to client
//                                                    FirebaseAuth.getInstance().signInWithCustomToken(customToken)
//                                                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                                                    Log.d(TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful());
//
//                                                                    // If sign in fails, display a message to the user. If sign in succeeds
//                                                                    // the auth state listener will be notified and logic to handle the
//                                                                    // signed in user can be handled in the listener.
//                                                                    if (!task.isSuccessful()) {
//
//                                                                    } else {
//
//                                                                    }
//                                                                }
//                                                            });
//                                                }
//                                            });
                                    }

                                    // Sign In
                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
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

                                                        // Check if User has JointUID
                                                        if (user.getTeamUID().equals("")) {
                                                            display(view, TOAST, R.string.success_signin);
                                                        } else {
                                                            mergeAccounts(context, user, newUser);
                                                            FirebaseHelper.update(context, (FirebaseObject) newUser);
                                                        }

                                                        Intent homeIntent = new Intent(context, HomeActivity.class);
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
    public static DatabaseReference getReference(Context context, int reference) {
        final String CURRENT_REFERENCE = context.getResources().getString(reference);
        switch (reference) {
            case R.string.firebase_url_directory:
                return FirebaseDatabase.getInstance().getReference();
            case R.string.firebase_users_directory:
                return getReference(
                        context,
                        R.string.firebase_url_directory
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_branding_element_directory:
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
                        R.string.firebase_database_url
                ).child(CURRENT_REFERENCE);
            case R.string.firebase_messages_directory:
                return getReference(
                        context,
                        R.string.firebase_database_url
                ).child(CURRENT_REFERENCE);
            default:
                return FirebaseDatabase.getInstance().getReference();

        }
    }
    public static void logout(AppCompatActivity appCompatActivity, boolean shouldCloseActivity) {
        if (Constants.FeaturesAvailable.SIGN_OUT) {
            FirebaseAuth.getInstance().signOut();
            display(appCompatActivity.findViewById(R.id.content).getRootView(), TOAST,R.string.success_signout);
        } else {
            display(appCompatActivity.findViewById(R.id.content).getRootView(),SNACKBAR,R.string.feature_unavailable);
        }

        if (shouldCloseActivity) {
            Intent intent = new Intent(appCompatActivity, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appCompatActivity.startActivity(intent);
        }

    }
    public static User getCurrentUser() {
        User user = new User();
        com.google.firebase.auth.FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            user.setEmail(firebaseUser.getEmail());
            user.setUID(firebaseUser.getUid());
            return user;
        }

        return null;
    }
    public static void save(Context context,User user,
                            FirebaseObject firebaseContent) {
        DatabaseReference myRef;
        DatabaseReference itemRef;

        if (firebaseContent instanceof User) {
            myRef = getReference(context,R.string.firebase_users_directory);
            itemRef = myRef.push();

            User userItem = (User)firebaseContent;
            String key = itemRef.getKey();
            userItem.setUID(key);
            itemRef.setValue(userItem.toMap());
        } else if (firebaseContent instanceof Chat) {
            myRef = getReference(context,R.string.firebase_chats_directory);
            itemRef = myRef.push();
            Chat chatItem = (Chat)firebaseContent;
            String key = itemRef.getKey();
            chatItem.setUID(key);
            itemRef.setValue(chatItem.toMap());
        } else if (firebaseContent instanceof BrandingElement) {
            myRef = getReference(context,R.string.firebase_branding_element_directory);
            itemRef = myRef.push();
            BrandingElement brandingElementItem = (BrandingElement)firebaseContent;
            String key = itemRef.getKey();
            brandingElementItem.setUID(key);
            itemRef.setValue(brandingElementItem.toMap());
        } else if (firebaseContent instanceof Team) {
            myRef = getReference(context,R.string.firebase_teams_directory);
            itemRef = myRef.push();
            Team teamItem = (Team)firebaseContent;
            String key = itemRef.getKey();
            teamItem.setUID(key);
            user.setTeamUID(key);
            itemRef.setValue(teamItem.toMap());
        } else if (firebaseContent instanceof Message) {
            myRef = getReference(context,R.string.firebase_messages_directory);
            itemRef = myRef.push();
            Message messageItem = (Message)firebaseContent;
            String key = itemRef.getKey();
            messageItem.setUID(key);
            messageItem.setTimestamp(DateFormat.getDateTimeInstance().format(new Date()));
            itemRef.setValue(messageItem.toMap());
            return;
        }

        Log.i("","Saved item");
    }
    public static void update(Context context, FirebaseObject firebaseObject){
        DatabaseReference myRef = null;
        DatabaseReference itemRef = null;
        if (firebaseObject instanceof User) {
            myRef = getReference(context,R.string.firebase_users_directory);
        } else if (firebaseObject instanceof Team) {
            myRef = getReference(context,R.string.firebase_teams_directory);
        } else if (firebaseObject instanceof BrandingElement) {
            myRef = getReference(context,R.string.firebase_branding_element_directory);
        } else if (firebaseObject instanceof Chat) {
            myRef = getReference(context,R.string.firebase_chats_directory);
        } else if (firebaseObject instanceof Message) {
            myRef = getReference(context,R.string.firebase_messages_directory);
        }

        if (myRef != null) {
            itemRef = myRef.child(firebaseObject.getUID());
//            firebaseObject.setUID(itemRef.getKey());
            itemRef.setValue((firebaseObject).toMap());
        }
    }
    public static void delete(final Context context, FirebaseObject firebaseObject) {

        int directory;

        if (firebaseObject instanceof User) {
            directory = R.string.firebase_users_directory;
        } else if (firebaseObject instanceof Chat) {
            directory = R.string.firebase_chats_directory;
        } else if (firebaseObject instanceof Message) {
            directory = R.string.firebase_messages_directory;
        } else if (firebaseObject instanceof BrandingElement) {
            directory = R.string.firebase_branding_element_directory;
        } else if (firebaseObject instanceof Team){
            directory = R.string.firebase_teams_directory;
        } else {
            directory = R.string.firebase_database_url;
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

    public static void mergeAccounts(final Context context, final User mainAccount, final User accountToMerge) {
        // Get JointAccount for accountToMerge (if it has one)
        if (!accountToMerge.getTeamUID().equals("")) {
            FirebaseHelper.getReference(context,R.string.firebase_teams_directory).child(accountToMerge.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Team team = new Team(dataSnapshot);
                    // remove it from the joint account
                    team.removeUser(accountToMerge);

                    // if the joint account has no more accounts left delete it if it does, update the item
                    if (team.hasNoUsers()) {
                        delete(context,team);
                    } else {
                        update(context,team);
                    }

                    update(context,accountToMerge);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            FirebaseHelper.getReference(context,R.string.firebase_teams_directory).child(mainAccount.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Team team = new Team(dataSnapshot);

                    // add it to the joint account
                    team.addUser(accountToMerge);

                    // update the items
                    update(context,team);
                    update(context,accountToMerge);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
