package com.marionthefourth.augimas.backend.notification;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.marionthefourth.augimas.backend.Backend;

public final class BackendInstanceIDService extends FirebaseInstanceIdService {
//    Service Method
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        Backend.sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), getApplicationContext());
    }
}