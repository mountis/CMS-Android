package com.marionthefourth.augimas.classes.objects.communication;

import android.os.Parcel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseCommunication;
import com.marionthefourth.augimas.classes.objects.entities.User;

import java.util.HashMap;
import java.util.Map;

public final class Message extends FirebaseCommunication {

    private String text         = "";
    private String channelUID   = "";
    private String senderUID    = "";
    private String timestamp    = "";
    @Exclude
    private Channel channel;

    public Message() {}

    public Message(final Channel channel, final User sender, final String text) {
        setText(text);
        setChannelUID(channel.getUID());
        setSenderUID(sender.getUID());
//        setType(chat.getType());
    }

    public Message(final String channelUID, final String senderUID, final String text) {
        setText(text);
        setChannelUID(channelUID);
        setSenderUID(senderUID);
    }

    public Message(final Parcel in) {
        final Message message = (Message)in.readSerializable();
        setUID(message.getUID());
        setText(message.getText());
        setChannelUID(message.getChannelUID());
        setSenderUID(message.getSenderUID());
        setType(message.getType());
    }

    public Message(final DataSnapshot messageReference) {
        if (messageReference.hasChild(Constants.Strings.UIDs.CHANNEL_UID)) {
            setChannelUID(messageReference.child(Constants.Strings.UIDs.CHANNEL_UID).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(messageReference.child(Constants.Strings.UIDs.UID).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.Fields.TEXT)) {
            setText(messageReference.child(Constants.Strings.Fields.TEXT).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.UIDs.SENDER_UID)) {
            setSenderUID(messageReference.child(Constants.Strings.UIDs.SENDER_UID).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.Fields.TIME)) {
            setTimestamp(messageReference.child(Constants.Strings.Fields.TIME).getValue().toString());
        }

        if (messageReference.hasChild(Constants.Strings.Fields.COMMUNICATION_TYPE)) {
            setType(CommunicationType.getType(messageReference.child(Constants.Strings.Fields.COMMUNICATION_TYPE).getValue().toString()));
        }
    }

    public String getText() {
        return text;
    }
    public String getChannelUID() {
        return channelUID;
    }
    public String getSenderUID() {
        return senderUID;
    }
    public String getTimestamp() { return timestamp; }
    public void setText(String text) {
        this.text = text;
    }
    public void setChannelUID(String channelUID) {
        this.channelUID = channelUID;
    }
    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isFromChat(final Chat chat) {
        return chat.getUID().equals(getChannelUID());
    }

    @Override
    public String getField(final int index) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();

        if (!getText().equals("")) {
            result.put(Constants.Strings.Fields.TEXT, getText());
        }

        if (!getChannelUID().equals("")) {
            result.put(Constants.Strings.UIDs.CHANNEL_UID, getChannelUID());
        }

        if (!getSenderUID().equals("")) {
            result.put(Constants.Strings.UIDs.SENDER_UID, getSenderUID());
        }

        if (!getUID().equals("")) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }

        if (!getTimestamp().equals("") && getTimestamp().toString().length() > 0) {
            result.put(Constants.Strings.Fields.TIME, getTimestamp());
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

}