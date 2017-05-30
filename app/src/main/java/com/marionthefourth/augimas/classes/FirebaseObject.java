// Marion Rucker
// APD2 - C201703
// FirebaseObject.java

package com.marionthefourth.augimas.classes;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.Exclude;
import com.marionthefourth.augimas.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static com.marionthefourth.augimas.classes.Constants.Ints.FIREBASE_CONTENT_CONTACT;
import static com.marionthefourth.augimas.classes.Constants.Ints.FIREBASE_USER;

public abstract class FirebaseObject implements Serializable, Parcelable {

    protected String uid;
    @Exclude
    protected int fields;
    @Exclude
    protected int objectType = R.string.firebase_object;

    public abstract String getField(int index);
    public int getFeilds() { return fields; }
    public int getObjectType() { return objectType; }

    public Bundle toBundle(AppCompatActivity appCompatActivity, int FIREBASE_CONTENT) {
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

    public String getUID() {
        return uid;
    }
    public void setUID(String uid) { this.uid = uid; }

    public static String[] stringArrayFromBundle(Bundle bundle, String fieldResources[]) {
        String stringArray[] = new String[fieldResources.length];
        for (int i = 0; i < fieldResources.length;i++) {
            stringArray[i] = bundle.getString(fieldResources[i]);
        }
        return stringArray;
    }

    public static FirebaseObject fromBundle(AppCompatActivity appCompatActivity, Bundle bundle, int FIREBASE_CONTENT) {
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

    public static Bundle toBundle(AppCompatActivity appCompatActivity, ArrayList<FirebaseObject> items) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(appCompatActivity.getResources().getString(R.string.firebase_object),items);
        return bundle;
    }

    public static FirebaseObject getFirebaseObjectFromFields(ArrayList<String> fields, int OBJECT_TYPE) {

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

    public abstract int describeContents();
    public abstract void writeToParcel(Parcel parcel, int i);
    public abstract Map<String, String> toMap();
    public abstract String description();
}