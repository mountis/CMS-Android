package com.augimas.android.classes.objects.communication;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseCommunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.augimas.android.classes.constants.Constants.Ints.Fields.FULL_NAME;
import static com.augimas.android.classes.constants.Constants.Ints.UIDs.CHAT_UID;
import static com.augimas.android.classes.constants.Constants.Ints.UIDs.UID;

public final class Channel extends FirebaseCommunication {
    private String name         = "";
    private String chatUID      = "";
//    Channel Constructors
    public Channel(){}
    public Channel(Chat chat) {
        this();
        setChatUID(chat.getUID());
    }
    public Channel(Parcel in) {
        final Channel channel = (Channel)in.readSerializable();
        setUID(channel.getUID());
        setName(channel.getName());
        setChatUID(channel.getChatUID());
    }
    public Channel(final DataSnapshot channelReference) {
        if (channelReference.hasChild(Constants.Strings.UIDs.CHAT_UID)) {
            setChatUID(channelReference.child(Constants.Strings.UIDs.CHAT_UID).getValue().toString());
        }

        if (channelReference.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(channelReference.child(Constants.Strings.UIDs.UID).getValue().toString());
        }

        if (channelReference.hasChild(Constants.Strings.Fields.FULL_NAME)) {
            setName(channelReference.child(Constants.Strings.Fields.FULL_NAME).getValue().toString());
        }

    }
    public static ArrayList<Channel> toArrayList(DataSnapshot channelReferences) {
        final ArrayList<Channel> channels = new ArrayList<>();
        for(DataSnapshot channelReference:channelReferences.getChildren()) {
            channels.add(new Channel(channelReference));
        }
        return channels;
    }

    public static ArrayList<Channel> toFilteredArrayList(final ArrayList<Channel> channels, String field, String content) {
        final ArrayList<Channel> filteredChannelList = new ArrayList<>();
        for (final Channel channelItem:channels) {
            switch (field) {
                case Constants.Strings.UIDs.CHAT_UID:
                    if (channelItem.getChatUID().equals(content)) filteredChannelList.add(channelItem);
                    break;
                case Constants.Strings.Fields.FULL_NAME:
                    if (!channelItem.getName().equals(content)) filteredChannelList.add(channelItem);
                default:
                    break;
            }
        }
        return filteredChannelList;
    }

    public static ArrayList<Channel> toFilteredArrayList(final ArrayList<Channel> channels, final ArrayList<String> fields, final ArrayList<String> content) {
        final ArrayList<Channel> filteredChannelList = new ArrayList<>();

        if (fields.size() > 0) {
            filteredChannelList.addAll(Channel.toFilteredArrayList(channels,fields.get(0),content.get(0)));
            fields.remove(0);
            content.remove(0);
            return Channel.toFilteredArrayList(filteredChannelList,fields,content);
        }

        return channels;
    }
//    Other Methods
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
    public static ArrayList<String> sortChannels(final ArrayList<Channel> channels) {
        final ArrayList<Channel> sortedChannels = new ArrayList<>(channels.size());
        final ArrayList<String> channelUIDs = new ArrayList<>(channels.size());
        if (channels.size() == 2) {
            if (channels.get(0).getName().equals("")) {
                sortedChannels.add(channels.get(1));
                sortedChannels.add(channels.get(0));
            } else {
                sortedChannels.add(channels.get(0));
                sortedChannels.add(channels.get(1));
            }

            for (int i = 0; i < sortedChannels.size(); i++) {
                channelUIDs.add(sortedChannels.get(i).getUID());
            }
        }

        return channelUIDs;
    }
//    Parcel Details
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
//    Class Getters & Setters
    public String getName() { return name; }
    public String getChatUID() { return chatUID; }
    public void setName(String name) { this.name = name; }
    public void setChatUID(String chatUID) { this.chatUID = chatUID; }
}