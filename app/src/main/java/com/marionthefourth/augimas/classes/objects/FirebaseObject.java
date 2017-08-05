package com.marionthefourth.augimas.classes.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.marionthefourth.augimas.classes.objects.entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.FIREBASE_USER;

public abstract class FirebaseObject implements Serializable, Parcelable {
    private String uid;
    public abstract String getField(final int index);
//    Other Methods
    public final static FirebaseObject getFromFields(final ArrayList<String> fields, final int OBJECT_TYPE) {
        switch (OBJECT_TYPE) {
            case FIREBASE_USER:
                if (fields.size() == 2) {
                    return new User(fields.get(0),fields.get(1));
                } else if(fields.size() == 5) {
                    return new User(fields);
                }
                break;
        }
        return null;
    }
//    Abstract Methods
    public abstract String description();
    public abstract int describeContents();
    public abstract Map<String, String> toMap();
//    Parcel Details
    @Override
    public final void writeToParcel(final Parcel dest, final int flags) { dest.writeSerializable(this); }
//    Class Getters & Setters
    public final String getUID() {
        return uid;
    }
    public final void setUID(final String uid) { this.uid = uid; }
}