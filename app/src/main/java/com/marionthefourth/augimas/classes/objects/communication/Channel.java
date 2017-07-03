package com.marionthefourth.augimas.classes.objects.communication;

import android.os.Parcel;

import com.google.firebase.database.DataSnapshot;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseCommunication;

import java.util.HashMap;
import java.util.Map;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.Fields.FULL_NAME;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.UIDs.CHAT_UID;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.UIDs.UID;

public final class Channel extends FirebaseCommunication {

    private String name         = "";
    private String chatUID      = "";

    public Channel(){}

    public Channel(Chat chat) {
        this();
        setChatUID(chat.getUID());
    }

    public Channel(Chat chat, String name) {
        this(chat);
        setName(name);
    }

    public Channel(Parcel in) {
        final Channel channel = (Channel)in.readSerializable();
        setUID(channel.getUID());
        setName(channel.getName());
        setChatUID(channel.getChatUID());
    }

    public Channel(final DataSnapshot chatReference) {
        if (chatReference.hasChild(Constants.Strings.UIDs.CHAT_UID)) {
            setChatUID(chatReference.child(Constants.Strings.UIDs.CHAT_UID).getValue().toString());
        }

        if (chatReference.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(chatReference.child(Constants.Strings.UIDs.UID).getValue().toString());
        }

        if (chatReference.hasChild(Constants.Strings.Fields.FULL_NAME)) {
            setName(chatReference.child(Constants.Strings.Fields.FULL_NAME).getValue().toString());
        }

    }

    @Override
    public String getField(int index) {
        switch (index) {
            case UID: return getUID();
            case FULL_NAME: return getName();
            case CHAT_UID: return getChatUID();
            default: return null;
        }
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

        if (!getName().equals("")) {
            result.put(Constants.Strings.Fields.FULL_NAME, getName());
        }

        if (!getUID().equals("")) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }

        if (!getChatUID().equals("")) {
            result.put(Constants.Strings.UIDs.CHAT_UID, getChatUID());
        }

        return result;
    }

    public static final Creator CREATOR = new Creator() {
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };

    public String getName() { return name; }
    public String getChatUID() { return chatUID; }
    public void setName(String name) { this.name = name; }
    public void setChatUID(String chatUID) { this.chatUID = chatUID; }
}