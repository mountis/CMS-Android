package com.marionthefourth.augimas.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public final class Member extends FirebaseObject {

    private String uid;
    private String userUID;
    private String teamUID;

    public Member() {}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getTeamUID() {
        return teamUID;
    }

    public void setTeamUID(String teamUID) {
        this.teamUID = teamUID;
    }

    public Member(String userUID, String teamUID) {
        setUserUID(userUID);
        setTeamUID(teamUID);
    }

    public Member(String userUID, String teamUID, String uid) {
        this(userUID,teamUID);
        setUID(uid);
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public Map<String, String> toMap() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }
}
