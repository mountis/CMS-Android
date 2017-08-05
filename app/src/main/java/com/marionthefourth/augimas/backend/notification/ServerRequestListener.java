package com.marionthefourth.augimas.backend.notification;

/**
 * Request Listener Interface. It is just to handle the HTTP request error
 */

public interface ServerRequestListener {

    void onComplete();

    void onError(String message);
}
