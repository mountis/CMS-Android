package com.marionthefourth.augimas.classes;

import android.os.Parcel;

import java.util.Map;

/**
 * Created by MGR4 on 5/26/17.
 */

public final class Notification extends FirebaseContent {

    private String message;
    private Team onTeam;

    public Notification(Parcel in) {
        Notification notification = (Notification) in.readSerializable();
        this.message = notification.getMessage();
        this.onTeam = notification.getOnTeam();
    }

    public Notification(String message, Team team){
        this.message = message;
        this.onTeam = team;
    }

    public String getNotificationText() {
        return onTeam.getName() + " " + message;
    }

    public static final Creator CREATOR = new Creator() {
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Team getOnTeam() {
        return onTeam;
    }

    public void setOnTeam(Team onTeam) {
        this.onTeam = onTeam;
    }

    @Override
    public String getField(int index) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @Override
    public Map<String, String> toMap() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }
}
