package com.marionthefourth.augimas.classes;

import android.os.Parcel;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public final class Message extends FirebaseContent {

    private String text;
    private String chatUID;
    private String senderUID;
    private String timestamp;

    public Message() {}

    public Message(Chat chat, User user, String text) {
        setSenderUID(user.getUID());
        setChatUID(chat.getUID());
        setText(text);
    }

    public Message(Parcel in) {
        Message message = (Message)in.readSerializable();
        setChatUID(message.getChatUID());
        setUID(message.getUID());
        setSenderUID(message.getSenderUID());
        setText(message.getText());
    }

    public Message(DataSnapshot messageReference) {
        if (messageReference.hasChild(Constants.Strings.CHAT_UID)) {
            setChatUID(messageReference.child(Constants.Strings.CHAT_UID).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.UID)) {
            setUID(messageReference.child(Constants.Strings.UID).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.TEXT)) {
            setText(messageReference.child(Constants.Strings.TEXT).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.SENDER_UID)) {
            setSenderUID(messageReference.child(Constants.Strings.SENDER_UID).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.TIME)) {
            setTimestamp(messageReference.child(Constants.Strings.TIME).getValue().toString());
        }
    }

    public boolean isFromChat(Chat chat) {
        return chat.getUID().equals(getChatUID());
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
    public void writeToParcel(Parcel dest, int flags) { dest.writeSerializable(this);}

    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();

        if (!getText().equals("")) {
            result.put(Constants.Strings.TEXT, getText());
        }

        if (!getChatUID().equals("")) {
            result.put(Constants.Strings.CHAT_UID, getChatUID());
        }

        if (!getSenderUID().equals("")) {
            result.put(Constants.Strings.SENDER_UID, getSenderUID());
        }

        if (!getUID().equals("")) {
            result.put(Constants.Strings.UID, getUID());
        }

        if (!getTimestamp().equals("") && getTimestamp().toString().length() > 0) {
            result.put(Constants.Strings.TIME, getTimestamp());
        }

        return result;
    }

    @Override
    public String description() {
        return null;
    }

    public static final Creator CREATOR = new Creator() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getText() {
        return text;
    }
    public String getChatUID() {
        return chatUID;
    }
    public String getSenderUID() {
        return senderUID;
    }
    public String getTimestamp() { return timestamp; }
    public void setText(String text) {
        this.text = text;
    }
    public void setChatUID(String chatUID) {
        this.chatUID = chatUID;
    }
    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

}