package com.augimas.android.backend.notification;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.augimas.android.backend.Backend;

public final class BackendInstanceIDService extends FirebaseInstanceIdService {
//    Service Method
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        Backend.sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), getApplicationContext());
    }
}