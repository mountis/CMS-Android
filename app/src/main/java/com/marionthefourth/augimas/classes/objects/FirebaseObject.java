package com.marionthefourth.augimas.classes.objects;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.Exclude;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.objects.entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.FIREBASE_CONTENT_CONTACT;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.FIREBASE_USER;

public abstract class FirebaseObject implements Serializable, Parcelable {

    private String uid;
    @Exclude
    private int fields;
    @Exclude
    private int objectType = R.string.firebase_object;

    public final int getFields() { return fields; }
    public abstract String getField(final int index);

    public Bundle toBundle(final AppCompatActivity appCompatActivity, final int FIREBASE_CONTENT) {
        Bundle bundle = new Bundle();
        String resource = null;
        switch (FIREBASE_CONTENT) {
            case FIREBASE_USER:
                resource = appCompatActivity.getResources().getString(R.string.firebase_user);
                break;
            case FIREBASE_CONTENT_CONTACT:
//                resource = appCompatActivity.getResources().getString(R.string.firebase_contact);
                break;
            default:
                return null;
        }

        bundle.putSerializable(resource,this);
        return bundle;
    }

    public final String getUID() {
        return uid;
    }
    public final void setUID(final String uid) { this.uid = uid; }

    public final static String[] stringArrayFromBundle(final Bundle bundle, final String fieldResources[]) {
        String stringArray[] = new String[fieldResources.length];
        for (int i = 0; i < fieldResources.length;i++) {
            stringArray[i] = bundle.getString(fieldResources[i]);
        }
        return stringArray;
    }

    public final static FirebaseObject fromBundle(final AppCompatActivity appCompatActivity, final Bundle bundle, final int FIREBASE_CONTENT) {
        String resource = "";
        switch (FIREBASE_CONTENT) {
                case FIREBASE_USER:
                    resource = appCompatActivity.getResources().getString(R.string.firebase_user);
                    break;
                case FIREBASE_CONTENT_CONTACT:
//                    resource = appCompatActivity.getResources().getString(R.string.firebase_contact);
                    return (FirebaseObject)bundle.getSerializable(resource);
                default:
                    return null;
        }

        if (!resource.equals("")) {

        }

        return null;
    }

    public final static Bundle toBundle(final AppCompatActivity appCompatActivity, final ArrayList<FirebaseObject> items) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(appCompatActivity.getResources().getString(R.string.firebase_object),items);
        return bundle;
    }

    public final static FirebaseObject getFirebaseObjectFromFields(final ArrayList<String> fields, final int OBJECT_TYPE) {

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

    public abstract String description();
    public abstract int describeContents();
    public abstract Map<String, String> toMap();

    @Override
    public final void writeToParcel(final Parcel dest, final int flags) { dest.writeSerializable(this); }
}