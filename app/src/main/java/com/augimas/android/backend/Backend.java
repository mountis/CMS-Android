package com.augimas.android.backend;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.augimas.android.R;
import com.augimas.android.activities.HomeActivity;
import com.augimas.android.activities.SignInActivity;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseObject;
import com.augimas.android.classes.objects.communication.Channel;
import com.augimas.android.classes.objects.communication.Chat;
import com.augimas.android.classes.objects.communication.Message;
import com.augimas.android.classes.objects.content.BrandingElement;
import com.augimas.android.classes.objects.content.RecentActivity;
import com.augimas.android.classes.objects.entities.Device;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.helpers.FragmentHelper;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import static android.content.ContentValues.TAG;
import static com.augimas.android.classes.constants.Constants.Bools.PROTOTYPE_MODE;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.SNACKBAR;
import static com.augimas.android.classes.constants.Constants.Ints.Views.Widgets.IDs.TOAST;
import static com.augimas.android.helpers.FragmentHelper.buildProgressDialog;
import static com.augimas.android.helpers.FragmentHelper.display;

public final class Backend {
//    Sign In/Out/Up & Re-Authenticate Methods
    public static void signIn(final User user, final View view, final Activity activity) {
        final Context context = view.getContext();
        final DatabaseReference usersRef = getReference(R.string.firebase_users_directory, activity);
        final ProgressDialog loadingProgress = buildProgressDialog(R.string.progress_signing_in, view);
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
                                if (newUser.getUsername().equals(user.getUsername()) || newUser.getEmail().equals(user.getUsername())) {
                                    // Ensure no one is signed in
                                    if (getCurrentUser() != null && user.getTeamUID().equals("")) {
                                        signOut((AppCompatActivity) context, false);
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
                                                        display(SNACKBAR, R.string.error_incorrect_signin_detail_1, view);
                                                    } else {
                                                        user.setEmail(newUser.getEmail());
                                                        user.setUID(newUser.getUID());

                                                        display(TOAST, R.string.success_signin, view);

                                                        if (!user.getTeamUID().equals("")) {
                                                            Backend.subscribeTo(Constants.Strings.UIDs.TEAM_UID,user.getTeamUID());
                                                        }

                                                        OneSignal.syncHashedEmail(user.getEmail());
                                                        Backend.subscribeTo(Constants.Strings.UIDs.USER_UID,user.getUID());

                                                        final Intent homeIntent = new Intent(context, HomeActivity.class);
                                                        context.startActivity(homeIntent);
                                                    }

                                                    loadingProgress.dismiss();

                                                }
                                            });
                                } else {
                                    loadingProgress.dismiss();
                                    display(SNACKBAR, R.string.error_incorrect_signin_general, view);
                                }
                            }
                        } else {
                            loadingProgress.dismiss();
                            display(SNACKBAR, R.string.error_incorrect_signin_general, view);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {loadingProgress.dismiss();}
                });
            } else {
                loadingProgress.dismiss();
            }
        }
    }
    public static void signOut(final Activity appCompatActivity, final boolean shouldCloseActivity) {
        if (Constants.Bools.FeaturesAvailable.SIGN_OUT) {
            FirebaseAuth.getInstance().signOut();
            display(TOAST, R.string.success_signout, appCompatActivity.findViewById(R.id.content).getRootView());
        } else {
            display(SNACKBAR, R.string.feature_unavailable, appCompatActivity.findViewById(R.id.content).getRootView());
        }

        if (shouldCloseActivity) {
            final Intent intent = new Intent(appCompatActivity, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appCompatActivity.startActivity(intent);
        }

    }
    public static void signUp(final User user, final View view, final ProgressDialog loadingProgress, final Activity activity) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    display(SNACKBAR, R.string.error_incorrect_signup_general, view);
                } else {
                    final String resultUID = task.getResult().getUser().getUid();
                    // Make new child reference from users reference with given resultUID
//                    final DatabaseReference newUserReference = usersRef.child(resultUID);
                    user.setUID(resultUID);

                    Backend.create(user, activity);
                    Backend.signIn(user, view, activity);
                    OneSignal.syncHashedEmail(user.getEmail());
                    Backend.subscribeTo(Constants.Strings.UIDs.USER_UID,user.getUID());

                    display(SNACKBAR, R.string.success_signup, view);
                }
                loadingProgress.dismiss();
            }


        });
    }
    public static void reAuthenticate(final User user, final String currentPassword, final String newPassword, final ProgressDialog loadingProgress, final DialogInterface dialog, final View containingView) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(),currentPassword);
        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Update Password
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingProgress.dismiss();
                                    if (task.isSuccessful()) {
                                        display(SNACKBAR, R.string.success_password_updated, containingView);
                                    } else {
                                        display(SNACKBAR, R.string.error_password_updated, task.getResult().toString(), containingView);
                                    }
                                }
                            });
                } else {
                    display(SNACKBAR, R.string.error_incorrect_password, containingView);
                }

                loadingProgress.dismiss();
                dialog.dismiss();
            }
        });
    }
//    Reference Methods
    public static DatabaseReference getReference(final int reference, final Context context) {
        final String CURRENT_REFERENCE = context.getResources().getString(reference);
        switch (reference) {
            case R.string.firebase_url_directory:
                return FirebaseDatabase.getInstance().getReference();
            default: return getReference(
                    R.string.firebase_url_directory, context
            ).child(CURRENT_REFERENCE);
        }
    }
    public static void setConnectionListener(final Activity activity) {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(Constants.Strings.Firebase.General.CONNECTED);
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    System.out.println("connected");
                    // Send Notification Stating Reconnection
                } else {
                    // Send Notification Stating Connection Lost
                    final int flags = Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK;
                    final int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
                    final Intent intent = new Intent(activity,HomeActivity.class);
                    intent.addFlags(flags);

                    final PendingIntent pendingIntent = PendingIntent.getActivity(activity, uniqueInt, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity)
                            .setSmallIcon(R.drawable.filled_logo)
                            .setContentTitle("Connection Lost")
                            .setContentText("You appear to not be connected to the network. You will not be able to access the features of the application without it.")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0, notificationBuilder.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    //    Data CUD Methods
    public static void update(final FirebaseObject firebaseObject, final Activity activity){
        DatabaseReference myRef = null;
        DatabaseReference itemRef;
        if (firebaseObject instanceof User) {
            myRef = getReference(R.string.firebase_users_directory, activity);
        } else if (firebaseObject instanceof Team) {
            myRef = getReference(R.string.firebase_teams_directory, activity);
        } else if (firebaseObject instanceof BrandingElement) {
            myRef = getReference(R.string.firebase_branding_element_contents_directory, activity);
        } else if (firebaseObject instanceof Chat) {
            myRef = getReference(R.string.firebase_chats_directory, activity);
        } else if (firebaseObject instanceof Message) {
            myRef = getReference(R.string.firebase_messages_directory, activity);
        } else if (firebaseObject instanceof RecentActivity) {
            myRef = getReference(R.string.firebase_recent_activities_directory, activity);
        } else if (firebaseObject instanceof Channel) {
            myRef = getReference(R.string.firebase_channels_directory, activity);
        }

        if (myRef != null) {
            itemRef = myRef.child(firebaseObject.getUID());
            itemRef.setValue(firebaseObject.toMap());
        }
    }
    public static void delete(final FirebaseObject firebaseObject, final Activity activity) {

        int directory = R.string.firebase_url_directory;

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
        } else if (firebaseObject instanceof RecentActivity){
            directory = R.string.firebase_recent_activities_directory;
        } else if (firebaseObject instanceof Channel) {
            directory = R.string.firebase_channels_directory;
        }

        getReference(
                directory, activity
        ).child(firebaseObject.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
    public static void create(final FirebaseObject firebaseObject, final Context context) {
        String key;
        DatabaseReference myRef = null;
        DatabaseReference itemRef;

        if (firebaseObject instanceof User) {
            myRef = getReference(R.string.firebase_users_directory, context);
            itemRef = myRef.child(firebaseObject.getUID());
            itemRef.setValue(firebaseObject.toMap());
            return;
        } else if (firebaseObject instanceof Chat) {
            myRef = getReference(R.string.firebase_chats_directory, context);
        } else if (firebaseObject instanceof BrandingElement) {
            myRef = getReference(R.string.firebase_branding_elements_directory, context);
        } else if (firebaseObject instanceof Team) {
            myRef = getReference(R.string.firebase_teams_directory, context);
        } else if (firebaseObject instanceof Message) {
            myRef = getReference(R.string.firebase_messages_directory, context);
        } else if (firebaseObject instanceof RecentActivity) {
            myRef = getReference(R.string.firebase_recent_activities_directory, context);
        } else if (firebaseObject instanceof Channel) {
            myRef = getReference(R.string.firebase_channels_directory, context);
        } else if (firebaseObject instanceof Device) {
            myRef = getReference(R.string.firebase_devices_directory, context);
        }

        if (myRef != null) {
            itemRef = myRef.push();
            key = itemRef.getKey();
            firebaseObject.setUID(key);
            itemRef.setValue(firebaseObject.toMap());
        }

    }
//    RecentActivity Methods
    public static void sendUpstreamNotification(final RecentActivity recentActivity, final String receivingTeamUID, final String senderUID, final String header, final Activity activity, boolean shouldSave) {
        recentActivity.addReceiverUID(receivingTeamUID);
        if (shouldSave) {
            recentActivity.setHeader(header);
            recentActivity.setTimestamp(new Date().toString());
            create(recentActivity, activity);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        String jsonResponse;

                        URL url = new URL(Constants.Strings.Server.OneSignal.NOTIFICATION_URL);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic ZGUxMDg2ZmUtZThiZC00YzVjLTkwODktYTdlZmQ2MDhhYjZj");
                        con.setRequestMethod("POST");

                        JSONObject upstreamJSON = recentActivity.toNotificationJSON(receivingTeamUID,senderUID,header);

                        String strJsonBody = upstreamJSON.toString();

                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        final Scanner scanner;
                        final InputStream source;
                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            source = con.getInputStream();
                        } else {
                            source = con.getErrorStream();
                        }
                        scanner = new Scanner(source, "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                        System.out.println("jsonResponse:\n" + jsonResponse);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
    public static RecentActivity sendNotification(final FirebaseObject object, final FirebaseObject subject, final RecentActivity.ActivityVerbType verbType, final Activity activity) {
        final RecentActivity recentActivity = new RecentActivity(subject,object,verbType);
        create(recentActivity, activity);
        return recentActivity;
    }
//    Subscription Methods
    public static void unSubscribeFrom(final String uid) {
        OneSignal.deleteTag(uid);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(uid);
    }
    public static void subscribeTo(final String tag, final String uid) {
        OneSignal.sendTag(tag,uid);
        FirebaseMessaging.getInstance().subscribeToTopic(uid);
    }
//    Other Methods
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
    public static void sendRegistrationToServer(final String token, final Context context) {
        Backend.getReference(R.string.firebase_devices_directory, context).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot deviceSnapshot:dataSnapshot.getChildren()) {
                        final Device device = new Device(deviceSnapshot);
                        if (device.getToken().equals(token)) {
                            return;
                        }
                    }

                    final Device currentDevice = new Device(token);
//                    Backend.create(context,currentDevice);
//                    Backend.subscribeTo(Constants.Strings.UIDs.DEVICE_UID,token);
                } else {
                    final Device currentDevice = new Device(token);
//                    Backend.create(context,currentDevice);
//                    Backend.subscribeTo(Constants.Strings.UIDs.DEVICE_UID,token);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
//    Updating Account Information Methods [Unused]
    public static void updateEmail(final String email, final View view, final Activity activity) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (FragmentHelper.isValidEmail(email)) {
            if (user != null) {
                user.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Pull User Info to update their email info
                                    getReference(R.string.firebase_users_directory, activity).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final User currentUser = new User(dataSnapshot);
                                            currentUser.setEmail(email);
                                            OneSignal.syncHashedEmail(email);
                                            update(currentUser, activity);
                                            FragmentHelper.display(SNACKBAR,R.string.email_updated,view);

                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                    Log.d(TAG, "User email address updated.");
                                } else {
                                    FragmentHelper.display(SNACKBAR,R.string.error_invalid_email,view);
                                }
                            }
                        });
            }
        }
    }
    public static void setPersistenceEnabled(boolean value) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(value);
    }
}