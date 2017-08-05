package com.marionthefourth.augimas.classes.objects.entities;

import com.google.firebase.database.DataSnapshot;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Device extends FirebaseObject {
    private String token = "";
//    Device Constructors
    public Device(String deviceToken) {
        super();
        setToken(deviceToken);
    }
    public Device(DataSnapshot deviceSnapshot) {
        if (deviceSnapshot.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(deviceSnapshot.child(Constants.Strings.UIDs.UID).getValue().toString());
        }
        if (deviceSnapshot.hasChild(Constants.Strings.Fields.TOKEN)) {
            setToken(deviceSnapshot.child(Constants.Strings.Fields.TOKEN).getValue().toString());
        }
    }
    public static ArrayList<Device> toArrayList(DataSnapshot deviceReferences) {
        final ArrayList<Device> devices = new ArrayList<>();
        for(DataSnapshot deviceReference:deviceReferences.getChildren()) {
            devices.add(new Device(deviceReference));
        }
        return devices;
    }
//    Other Methods
    @Override
    public String getField(int index) {
        return null;
    }
    @Override
    public String description() {
        return null;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }
        if (getToken() != null) {
            result.put(Constants.Strings.Fields.TOKEN, getToken());
        }
        return result;
    }
//    Class Getters & Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}