package com.augimas.android.classes.objects.content;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.augimas.android.R;
import com.augimas.android.activities.HomeActivity;
import com.augimas.android.backend.Backend;
import com.augimas.android.classes.constants.Constants;
import com.augimas.android.classes.objects.FirebaseContent;
import com.augimas.android.classes.objects.FirebaseEntity;
import com.augimas.android.classes.objects.FirebaseObject;
import com.augimas.android.classes.objects.communication.Channel;
import com.augimas.android.classes.objects.communication.Chat;
import com.augimas.android.classes.objects.communication.Message;
import com.augimas.android.classes.objects.entities.Team;
import com.augimas.android.classes.objects.entities.User;
import com.augimas.android.fragments.BrandingElementFragment;
import com.augimas.android.fragments.TeamManagementFragment;
import com.augimas.android.helpers.FragmentHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.augimas.android.backend.Backend.getCurrentUser;
import static com.augimas.android.classes.constants.Constants.Ints.DEFAULT_ID;

public final class RecentActivity extends FirebaseContent {
    @Exclude
    private FirebaseObject subject;
    @Exclude
    private FirebaseObject object;
    @Exclude
    private FirebaseEntity.EntityRole roleObject;
    @Exclude
    private FirebaseEntity.EntityStatus statusObject;
    private String extraString = "";
    private String extraString2 = "";
    private String teamNameString = "";
    private String objectUID;
    private String subjectUID;
    private String messageText;
    private int senderType;
    private String timestamp;
    private String header;
    private ArrayList<String> seenUIDs = new ArrayList<>();
    private ArrayList<String> receiverUIDs = new ArrayList<>();
    private ActivityVerbType verbType = ActivityVerbType.DEFAULT;
    private ActivityObjectType objectType = ActivityObjectType.DEFAULT;
    private ActivitySubjectType subjectType = ActivitySubjectType.DEFAULT;
    private ActivityObjectType extraObjectType = ActivityObjectType.DEFAULT;

    public RecentActivity(FirebaseObject subject, FirebaseObject object, ActivityVerbType verbType, String teamNameString) {
        this(subject,object,verbType);
        setTeamNameString(teamNameString);
        setMessage();
    }
    public RecentActivity(final FirebaseObject subject, final FirebaseObject object, final ActivityVerbType verbType, final String teamNameString, final String extraString) {
        this(subject,object,verbType,teamNameString);
        setExtraString(extraString);
        setMessage();
    }
    public RecentActivity(final FirebaseObject subject, final FirebaseObject object, final ActivityVerbType verbType, final String teamNameString, final String extraString, final String extraString2) {
        this(subject,object,verbType,teamNameString,extraString);
        setExtraString2(extraString2);
        setMessage();
    }
    public RecentActivity(final FirebaseObject subject, final FirebaseObject object, final ActivityVerbType verbType, final String teamNameString, final int senderType) {
        this(subject,object,verbType,teamNameString);
        setSenderType(senderType);
        setMessage();
    }

    //    Class Enums
    public enum ActivitySubjectType {
        MEMBER, TEAM, DEFAULT;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this) {
                    case TEAM:      return Constants.Ints.NotificationTypes.Subjects.IDs.MEMBER;
                    case MEMBER:    return Constants.Ints.NotificationTypes.Subjects.IDs.TEAM;
                    default:        return DEFAULT_ID;
                }
            } else {
                switch (this) {
                    case TEAM:      return Constants.Ints.NotificationTypes.Subjects.Indices.TEAM;
                    case MEMBER:    return Constants.Ints.NotificationTypes.Subjects.Indices.MEMBER;
                    default:        return DEFAULT_ID;
                }
            }
        }

        public static ActivitySubjectType getType(int type) {
            switch (type) {
                case Constants.Ints.NotificationTypes.Subjects.IDs.MEMBER:
                case Constants.Ints.NotificationTypes.Subjects.Indices.MEMBER:
                    return MEMBER;
                case Constants.Ints.NotificationTypes.Subjects.IDs.TEAM:
                case Constants.Ints.NotificationTypes.Subjects.Indices.TEAM:
                    return TEAM;
                default: return DEFAULT;
            }
        }

        public static ActivitySubjectType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationSubjectTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<ActivitySubjectType> getAllNotificationSubjectTypes() {
            final ArrayList<ActivitySubjectType> activitySubjectTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationSubjectTypes(); i++) {
                activitySubjectTypes.add(getType(i));
            }

            return activitySubjectTypes;
        }

        public static int getNumberOfNotificationSubjectTypes() {
            return 3;
        }

    }
    public enum ActivityVerbType {
        ADD, APPROVE, AWAIT, CREATE , CHAT, DISAPPROVE, INVITE, JOIN, LEFT, RECEIVE,
        REQUEST, REQUEST_ACCESS, REQUEST_APPROVAL, REQUEST_JOIN, UPDATE, UPDATE_ROLE, UPDATE_USERNAME,
        UPDATE_TEAM_NAME, DEFAULT, BLOCK, REMOVE;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public static ActivityVerbType toVerbType(BrandingElement.ElementStatus status) {
            switch (status) {
                case APPROVED:
                    return ActivityVerbType.APPROVE;
                case AWAITING:
                    return ActivityVerbType.AWAIT;
                case INCOMPLETE:
                    return ActivityVerbType.DISAPPROVE;
                case NONE:
                    return ActivityVerbType.AWAIT;
                default:
                    return ActivityVerbType.DEFAULT;

            }

        }
        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch(this) {
                    case ADD:               return Constants.Ints.NotificationTypes.Verbs.IDs.ADD;
                    case APPROVE:           return Constants.Ints.NotificationTypes.Verbs.IDs.APPROVE;
                    case BLOCK:             return Constants.Ints.NotificationTypes.Verbs.IDs.BLOCK;
                    case AWAIT:             return Constants.Ints.NotificationTypes.Verbs.IDs.AWAIT;
                    case CHAT:              return Constants.Ints.NotificationTypes.Verbs.IDs.CHAT;
                    case CREATE:            return Constants.Ints.NotificationTypes.Verbs.IDs.CREATE;
                    case DISAPPROVE:        return Constants.Ints.NotificationTypes.Verbs.IDs.DISAPPROVE;
                    case INVITE:            return Constants.Ints.NotificationTypes.Verbs.IDs.INVITE;
                    case JOIN:              return Constants.Ints.NotificationTypes.Verbs.IDs.JOIN;
                    case LEFT:              return Constants.Ints.NotificationTypes.Verbs.IDs.LEFT;
                    case RECEIVE:           return Constants.Ints.NotificationTypes.Verbs.IDs.RECEIVE;
                    case REQUEST:           return Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST;
                    case REQUEST_ACCESS:    return Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST_ACCESS;
                    case REQUEST_APPROVAL:  return Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST_APPROVAL;
                    case REQUEST_JOIN:      return Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST_JOIN;
                    case UPDATE:            return Constants.Ints.NotificationTypes.Verbs.IDs.UPDATE;
                    case UPDATE_ROLE:       return Constants.Ints.NotificationTypes.Verbs.IDs.UPDATE_ROLE;
                    case UPDATE_USERNAME:   return Constants.Ints.NotificationTypes.Verbs.IDs.UPDATE_USERNAME;
                    case UPDATE_TEAM_NAME:  return Constants.Ints.NotificationTypes.Verbs.IDs.UPDATE_TEAM_NAME;
                    case REMOVE:            return Constants.Ints.NotificationTypes.Verbs.IDs.REMOVE;
                    default:                return DEFAULT_ID;
                }
            } else {
                switch(this) {
                    case ADD:               return Constants.Ints.NotificationTypes.Verbs.Indices.ADD;
                    case APPROVE:           return Constants.Ints.NotificationTypes.Verbs.Indices.APPROVE;
                    case BLOCK:             return Constants.Ints.NotificationTypes.Verbs.Indices.BLOCK;
                    case AWAIT:             return Constants.Ints.NotificationTypes.Verbs.Indices.AWAIT;
                    case CHAT:              return Constants.Ints.NotificationTypes.Verbs.Indices.CHAT;
                    case CREATE:            return Constants.Ints.NotificationTypes.Verbs.Indices.CREATE;
                    case DISAPPROVE:        return Constants.Ints.NotificationTypes.Verbs.Indices.DISAPPROVE;
                    case INVITE:            return Constants.Ints.NotificationTypes.Verbs.Indices.INVITE;
                    case JOIN:              return Constants.Ints.NotificationTypes.Verbs.Indices.JOIN;
                    case LEFT:              return Constants.Ints.NotificationTypes.Verbs.Indices.LEFT;
                    case RECEIVE:           return Constants.Ints.NotificationTypes.Verbs.Indices.RECEIVE;
                    case REQUEST:           return Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST;
                    case REQUEST_ACCESS:    return Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST_ACCESS;
                    case REQUEST_APPROVAL:  return Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST_APPROVAL;
                    case REQUEST_JOIN:      return Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST_JOIN;
                    case UPDATE:            return Constants.Ints.NotificationTypes.Verbs.Indices.UPDATE;
                    case UPDATE_ROLE:       return Constants.Ints.NotificationTypes.Verbs.Indices.UPDATE_ROLE;
                    case UPDATE_USERNAME:   return Constants.Ints.NotificationTypes.Verbs.Indices.UPDATE_USERNAME;
                    case UPDATE_TEAM_NAME:  return Constants.Ints.NotificationTypes.Verbs.Indices.UPDATE_TEAM_NAME;
                    case REMOVE:            return Constants.Ints.NotificationTypes.Verbs.Indices.REMOVE;
                    default:                return DEFAULT_ID;
                }
            }
        }

        public static ActivityVerbType getType(int type) {
            switch (type) {
                case Constants.Ints.NotificationTypes.Verbs.IDs.ADD:
                case Constants.Ints.NotificationTypes.Verbs.Indices.ADD:
                    return ADD;
                case Constants.Ints.NotificationTypes.Verbs.IDs.APPROVE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.APPROVE:
                    return APPROVE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.AWAIT:
                case Constants.Ints.NotificationTypes.Verbs.Indices.AWAIT:
                    return AWAIT;
                case Constants.Ints.NotificationTypes.Verbs.IDs.BLOCK:
                case Constants.Ints.NotificationTypes.Verbs.Indices.BLOCK:
                    return BLOCK;
                case Constants.Ints.NotificationTypes.Verbs.IDs.CREATE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.CREATE:
                    return CREATE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.CHAT:
                case Constants.Ints.NotificationTypes.Verbs.Indices.CHAT:
                    return CHAT;
                case Constants.Ints.NotificationTypes.Verbs.IDs.DISAPPROVE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.DISAPPROVE:
                    return DISAPPROVE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.INVITE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.INVITE:
                    return INVITE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.JOIN:
                case Constants.Ints.NotificationTypes.Verbs.Indices.JOIN:
                    return JOIN;
                case Constants.Ints.NotificationTypes.Verbs.IDs.LEFT:
                case Constants.Ints.NotificationTypes.Verbs.Indices.LEFT:
                    return LEFT;
                case Constants.Ints.NotificationTypes.Verbs.IDs.RECEIVE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.RECEIVE:
                    return RECEIVE;
                case Constants.Ints.NotificationTypes.Verbs.IDs.REQUEST:
                case Constants.Ints.NotificationTypes.Verbs.Indices.REQUEST:
                    return REQUEST;
                case Constants.Ints.NotificationTypes.Verbs.IDs.UPDATE:
                case Constants.Ints.NotificationTypes.Verbs.Indices.UPDATE:
                    return UPDATE;
                default: return DEFAULT;
            }
        }

        public static ActivityVerbType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationVerbTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<ActivityVerbType> getAllNotificationVerbTypes() {
            final ArrayList<ActivityVerbType> activityVerbTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationVerbTypes(); i++) {
                activityVerbTypes.add(getType(i));
            }

            return activityVerbTypes;
        }

        public static int getNumberOfNotificationVerbTypes() {
            return 16;
        }

    }
    public enum ActivityObjectType {
        BRANDING_ELEMENT, MESSAGE, MEMBER, TEAM, DEFAULT;

        @Override
        public String toString() {
            return super.toString();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this){
                    case BRANDING_ELEMENT:  return Constants.Ints.NotificationTypes.Objects.IDs.BRANDING_ELEMENT;
                    case MESSAGE:              return Constants.Ints.NotificationTypes.Objects.IDs.MESSAGE;
                    case MEMBER:            return Constants.Ints.NotificationTypes.Objects.IDs.MEMBER;
                    case TEAM:              return Constants.Ints.NotificationTypes.Objects.IDs.TEAM;
                    default:                return DEFAULT_ID;
                }
            } else {
                switch (this){
                    case BRANDING_ELEMENT:  return Constants.Ints.NotificationTypes.Objects.Indices.BRANDING_ELEMENT;
                    case MESSAGE:              return Constants.Ints.NotificationTypes.Objects.Indices.MESSAGE;
                    case MEMBER:            return Constants.Ints.NotificationTypes.Objects.Indices.MEMBER;
                    case TEAM:              return Constants.Ints.NotificationTypes.Objects.Indices.TEAM;
                    default:                return DEFAULT_ID;
                }
            }
        }

        public static ActivityObjectType getType(int type) {
            switch (type) {
                case Constants.Ints.NotificationTypes.Objects.IDs.BRANDING_ELEMENT:
                case Constants.Ints.NotificationTypes.Objects.Indices.BRANDING_ELEMENT:
                    return BRANDING_ELEMENT;
                case Constants.Ints.NotificationTypes.Objects.IDs.MESSAGE:
                case Constants.Ints.NotificationTypes.Objects.Indices.MESSAGE:
                    return MESSAGE;
                case Constants.Ints.NotificationTypes.Objects.IDs.MEMBER:
                case Constants.Ints.NotificationTypes.Objects.Indices.MEMBER:
                    return MEMBER;
                case Constants.Ints.NotificationTypes.Objects.IDs.TEAM:
                case Constants.Ints.NotificationTypes.Objects.Indices.TEAM:
                    return TEAM;
                    default: return DEFAULT;
            }
        }

        public static ActivityObjectType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationObjectTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<ActivityObjectType> getAllNotificationObjectTypes() {
            final ArrayList<ActivityObjectType> activityObjectTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationObjectTypes(); i++) {
                activityObjectTypes.add(getType(i));
            }

            return activityObjectTypes;
        }

        public static int getNumberOfNotificationObjectTypes() {
            return 5;
        }
    }
    //    RecentActivity Constructors
    public RecentActivity() { }
    public RecentActivity(final Parcel in) {
        final RecentActivity recentActivity = (RecentActivity) in.readSerializable();
        setUID(recentActivity.getUID());
        setObject(recentActivity.getObject());
        setSubject(recentActivity.getSubject());
        setVerbType(recentActivity.getVerbType());
        setReceiverUIDs(recentActivity.getReceiverUIDs());
    }
    public RecentActivity(final DataSnapshot nRef) {
        this();
        if (nRef.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(nRef.child(Constants.Strings.UIDs.UID).getValue().toString());
        }
        if (nRef.hasChild(Constants.Strings.Fields.SUBJECT_TYPE)) {
            setSubjectType(ActivitySubjectType.getType(nRef.child(Constants.Strings.Fields.SUBJECT_TYPE).getValue().toString()));
        }
        if (nRef.hasChild(Constants.Strings.Fields.VERB_TYPE)) {
            setVerbType(ActivityVerbType.getType(nRef.child(Constants.Strings.Fields.VERB_TYPE).getValue().toString()));
        }
        if (nRef.hasChild(Constants.Strings.Fields.OBJECT_TYPE)) {
            setObjectType(ActivityObjectType.getType(nRef.child(Constants.Strings.Fields.OBJECT_TYPE).getValue().toString()));
        }
        if (nRef.hasChild(Constants.Strings.UIDs.SUBJECT_UID)) {
            setSubjectUID(nRef.child(Constants.Strings.UIDs.SUBJECT_UID).getValue().toString());
        }
        if (nRef.hasChild(Constants.Strings.UIDs.OBJECT_UID)) {
            setObjectUID(nRef.child(Constants.Strings.UIDs.OBJECT_UID).getValue().toString());
        }
        if (nRef.hasChild(Constants.Strings.Fields.MESSAGE)) {
            setMessageText(nRef.child(Constants.Strings.Fields.MESSAGE).getValue().toString());
        }
        if (nRef.hasChild(Constants.Strings.Fields.HEADER)) {
            setHeader(nRef.child(Constants.Strings.Fields.HEADER).getValue().toString());
        }
        if (nRef.hasChild(Constants.Strings.Fields.TIMESTAMP)) {
            setTimestamp(nRef.child(Constants.Strings.Fields.TIMESTAMP).getValue().toString());
        }

        int index = 0;
        while (nRef.hasChild(Constants.Strings.UIDs.RECEIVER_UID + index)) {
            getReceiverUIDs().add(nRef.child(Constants.Strings.UIDs.RECEIVER_UID + index).getValue().toString());
            index++;
        }
    }
    public static ArrayList<RecentActivity> toArrayList(DataSnapshot notificationReferences) {
        final ArrayList<RecentActivity> recentActivities = new ArrayList<>();
        for(DataSnapshot notificationReference:notificationReferences.getChildren()) {
            recentActivities.add(new RecentActivity(notificationReference));
        }
        return recentActivities;
    }
    public RecentActivity(final FirebaseObject subject, final ActivityVerbType vType) {
        setVerbType(vType);
        setSubject(subject);
    }
    public RecentActivity(final FirebaseObject subject, final FirebaseObject object, final ActivityVerbType vType) {
        this(subject,vType);
        setObject(object);
        setMessage();
    }
    public RecentActivity(final FirebaseObject subject, final FirebaseObject object, final ActivityVerbType vType, FirebaseEntity.EntityRole roleObject, String teamNameString) {
        this(subject,object,vType);
        setRoleObject(roleObject);
        setTeamNameString(teamNameString);
        setMessage();
    }
    public RecentActivity(final FirebaseObject subject, final FirebaseObject object, final ActivityVerbType vType, FirebaseEntity.EntityStatus statusObject, String extraString) {
        this(subject,object,vType);
        setStatusObject(statusObject);
        setExtraString(extraString);
        setMessage();
    }

    public RecentActivity(final ActivitySubjectType sType, final ActivityVerbType vType, final ActivityObjectType oType){
        setVerbType(vType);
        setObjectType(oType);
        setSubjectType(sType);
    }
    public RecentActivity(final FirebaseObject subject, final ActivityVerbType vType, final BrandingElement.ElementType brandingElementType) {
        this(subject,vType);
    }
//    Functional Methods
    public Bundle toBundle() {
    final Bundle bundle = new Bundle();
    String entityTeamUID;
    String navigationDirection;
    switch(getSubjectType()) {
        case MEMBER:
            entityTeamUID = ((User) getSubject()).getTeamUID();
            break;
        case TEAM:
        default:
            entityTeamUID = getSubject().getUID();
            break;
    }
    switch (getObjectType()) {
        case BRANDING_ELEMENT:
            navigationDirection = Constants.Strings.Fragments.BRANDING_ELEMENTS;
            break;
        case MESSAGE:
            navigationDirection = Constants.Strings.Fragments.CHAT;
            break;
        case MEMBER:
        case TEAM:
        default:
            navigationDirection = Constants.Strings.Fragments.TEAM_MANAGEMENT;
            break;

    }

    bundle.putString(Constants.Strings.Fields.MESSAGE, setMessage());
    bundle.putString(Constants.Strings.UIDs.TEAM_UID,entityTeamUID);
    bundle.putString(Constants.Strings.Fields.FRAGMENT,navigationDirection);

    return bundle;
}
    public JSONObject toNotificationJSON(final String toUID, final String senderUID, String header) {
        JSONObject upstreamJSON = new JSONObject();
        try {
            upstreamJSON.put(Constants.Strings.Server.Fields.APP_ID,Constants.Strings.Server.OneSignal.APP_ID);
//            Add Filters
            JSONArray filters = new JSONArray();
            JSONObject filter = new JSONObject();
            filter.put(Constants.Strings.Server.Fields.FIELD,Constants.Strings.Server.Fields.TAG);
            filter.put(Constants.Strings.Server.Fields.KEY,Constants.Strings.UIDs.TEAM_UID);
            filter.put(Constants.Strings.Server.Fields.RELATION,Constants.Strings.Server.Fields.EQUALS);
            filter.put(Constants.Strings.Server.Fields.VALUE,toUID);
            filters.put(filter);
            upstreamJSON.put(Constants.Strings.Server.Fields.FILTERS,filters);
//            Add Data
            JSONObject data = new JSONObject();
            data.put(Constants.Strings.Server.Fields.MESSAGE, setMessage());
            data.put(Constants.Strings.Fields.FRAGMENT,getObjectTypeString());
            data.put(Constants.Strings.UIDs.SENDER_UID,senderUID);
            data.put(Constants.Strings.Fields.HEADER,header);
            data.put(Constants.Strings.UIDs.RECENT_ACTIVITY_UID,getUID());
            upstreamJSON.put(Constants.Strings.Server.Fields.DATA,data);
//            Add Language
            JSONObject englishMessage = new JSONObject();
            englishMessage.put(Constants.Strings.Server.Fields.ENGLISH,Constants.Strings.Server.Fields.ENGLISH_MESSAGE);
            upstreamJSON.put(Constants.Strings.Server.Fields.CONTENTS,englishMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return upstreamJSON;

        /* This is the previous user's JSON Format */
        /*
        String strJsonBody = "{"
        + "\"app_id\": \"7432468f-5504-4ffb-813b-88f9a45dc575\","

        + "\"filters\": [{\"field\": \"tag\", \"key\": \"teamUID\", \"relation\": \"=\", \"value\": \"" + teamUID + "\"}],"

        + "\"data\": {\"foo\": \"bar\"},"
        + "\"contents\": {\"en\": \"English Message\"}"
        + "}";
        */
    }
    public void navigate(final HomeActivity activity) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final User currentUser = new User(dataSnapshot);

                    final BottomNavigationView navigation = activity.navigation;
                    switch (getObjectType()) {
                        case BRANDING_ELEMENT:
                            Backend.getReference(R.string.firebase_branding_elements_directory,activity).child(getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final BrandingElement brandingElement = new BrandingElement(dataSnapshot);
                                        navigation.getMenu().getItem(0).setChecked(true);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(Constants.Strings.BRANDING_ELEMENT,brandingElement);
                                        bundle.putSerializable(Constants.Strings.UIDs.BRANDING_ELEMENT_UID,brandingElement.getUID());
                                        (activity)
                                                .getSupportFragmentManager()
                                                .beginTransaction()
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                .replace(R.id.container, BrandingElementFragment.newInstance(bundle),Constants.Strings.Fragments.BRANDING_ELEMENT)
                                                .commit();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                            break;
                        case MESSAGE:
                            Backend.getReference(R.string.firebase_messages_directory,activity).child(getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final Message messageItem = new Message(dataSnapshot);
                                    if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                        navigation.getMenu().getItem(0).setChecked(true);
                                    } else {
                                        navigation.getMenu().getItem(2).setChecked(true);
                                    }
                                    // Get Sender UID to find out what team the sender was if the host
                                    // Get the Current User UID to pass in to get the Chat
                                    if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                        Backend.getReference(R.string.firebase_channels_directory,activity).child(messageItem.getChannelUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot channelSnapshot) {
                                                if (channelSnapshot.exists()) {
                                                    final Channel currentChannel = new Channel(channelSnapshot);
                                                    Backend.getReference(R.string.firebase_chats_directory,activity).child(currentChannel.getChatUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot chatSnapshot) {
                                                            if (chatSnapshot.exists()) {
                                                                final Chat currentChat = new Chat(chatSnapshot);
                                                                final String teamUID;
                                                                activity.setShouldHandleNavigation(true);
                                                                if (currentChat.getTeamUIDs().get(0).equals(currentUser.getTeamUID())) {
                                                                    teamUID = currentChat.getTeamUIDs().get(1);
                                                                } else {
                                                                    teamUID = currentChat.getTeamUIDs().get(0);
                                                                }

                                                                Backend.getReference(R.string.firebase_teams_directory,activity).child(teamUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(final DataSnapshot teamSnapshot) {
                                                                        if (teamSnapshot.exists()) {
                                                                            FragmentHelper.transitionUserToChatFragment(new Team(teamSnapshot),activity, messageItem.getChannelUID());
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {}
                                                                });

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {}
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });

                                    } else {
                                        FragmentHelper.transitionClientUserToChatFragment(currentUser,activity, messageItem.getChannelUID());
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                            break;
                        case MEMBER:
                            Backend.getReference(R.string.firebase_users_directory,activity).child(getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final User userObjectElement = new User(dataSnapshot);

                                    Backend.getReference(R.string.firebase_teams_directory,activity).child(userObjectElement.getTeamUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            final Team teamElement = new Team(dataSnapshot);
                                            activity.setShouldHandleNavigation(false);
                                            if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                                if (userObjectElement.getType() == FirebaseEntity.EntityType.HOST) {
                                                    navigation.getMenu().getItem(3).setChecked(true);
                                                } else {
                                                    navigation.getMenu().getItem(0).setChecked(true);
                                                }
                                            } else {
                                                navigation.getMenu().getItem(3).setChecked(true);
                                            }

                                            (activity)
                                                    .getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                    .replace(R.id.container, TeamManagementFragment.newInstance(teamElement),Constants.Strings.Fragments.TEAM_MANAGEMENT)
                                                    .commit();
                                            activity.setShouldHandleNavigation(true);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                            break;
                        case TEAM:
                            Backend.getReference(R.string.firebase_teams_directory,activity).child(getObjectUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final Team teamElement = new Team(dataSnapshot);

                                    activity.setShouldHandleNavigation(false);
                                    if (currentUser.getTeamUID().equals(teamElement.getUID())) {
                                        navigation.getMenu().getItem(3).setChecked(true);
                                    } else {
                                        navigation.getMenu().getItem(0).setChecked(true);
                                    }

                                    (activity)
                                            .getSupportFragmentManager()
                                            .beginTransaction()
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                            .replace(R.id.container, TeamManagementFragment.newInstance(teamElement),Constants.Strings.Fragments.TEAMS)
                                            .commit();
                                    activity.setShouldHandleNavigation(true);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                            break;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }
        if (getTimestamp() != null) {
            result.put(Constants.Strings.Fields.TIMESTAMP,getTimestamp());
        }
        if (getHeader() != null) {
            result.put(Constants.Strings.Fields.HEADER,getHeader());
        }
        if (getObject() != null) {
            result.put(Constants.Strings.UIDs.OBJECT_UID, getObjectUID());
        }
        if (getSubject() != null) {
            result.put(Constants.Strings.UIDs.SUBJECT_UID, getSubjectUID());
        }
        if (getObjectType() != null) {
            result.put(Constants.Strings.Fields.OBJECT_TYPE, getObjectType().toMapStyleString());
        }
        if (getSubjectType() != null) {
            result.put(Constants.Strings.Fields.SUBJECT_TYPE, getSubjectType().toMapStyleString());
        }
        if (getVerbType() != null) {
            result.put(Constants.Strings.Fields.VERB_TYPE, getVerbType().toMapStyleString());
        }
        if (getReceiverUIDs() != null) {
            for (int i = 0; i < getReceiverUIDs().size(); i++) {
                result.put(Constants.Strings.UIDs.RECEIVER_UID+i, getReceiverUIDs().get(i));
            }
        }
        if (getMessageText() != null) {
            result.put(Constants.Strings.Fields.MESSAGE,getMessageText());
        }
        return result;
    }
    public boolean goesToUID(String uid) {
        for (int i = 0; i < getReceiverUIDs().size(); i++) {
            if (getReceiverUIDs().get(i).equals(uid)) {
                return true;
            }
        }
        return false;
    }
    //    Parcel Details
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RecentActivity createFromParcel(Parcel in) {
            return new RecentActivity(in);
        }
        public RecentActivity[] newArray(int size) {
            return new RecentActivity[size];
        }
    };
//    Other Methods
    @Override
    public String getField(int index) {
        return null;
    }
    @Override
    public String description() {
        return null;
    }
    //    Class Getters & Setters
    private String getObjectTypeString() {
        switch (getObjectType()) {
            case BRANDING_ELEMENT:
                return Constants.Strings.Fragments.BRANDING_ELEMENTS;
            case MESSAGE:
                return Constants.Strings.Fragments.CHAT;
            case MEMBER:
                return Constants.Strings.Fragments.TEAM_MANAGEMENT;
            case TEAM:
                return Constants.Strings.Fragments.HOME;
            default:
                return null;
        }
    }
    public boolean hasBeenSeenBy(final User user) {
        for(String seenUID:getSeenUIDs()) {
            if (seenUID.equals(user.getUID())) return true;
        }

        return false;
    }
    public String setMessage() {
        String subjectPart = "";
        String verbPart = "";
        String objectPart = "";

        if (getSubject() != null && getObject() != null) {
            switch (subjectType) {
                case MEMBER:
                case TEAM:
                    subjectPart = ((FirebaseEntity)getSubject()).getName() + " ";
                    break;
                default:
                    subjectPart = "[subjectPart] ";
            }

            switch (objectType) {
                case BRANDING_ELEMENT:
                    String objectType = ((BrandingElement) getObject()).getType().toString() + " Section.";
                    if (getTeamNameString() != null && !getTeamNameString().equals("")) {
                        String teamName = getTeamNameString();
                        if (teamName.endsWith("s")) {
                            teamName += "' ";
                        } else {
                            teamName += "'s ";
                        }
                        switch (verbType) {
                            case ADD:
                                if (extraString != null && !extraString.equals("")) {
                                    setMessageText(subjectPart + "added " + extraString + " to " + teamName + objectType);
                                } else {
                                    switch(((BrandingElement)object).getType()) {
                                        case BRAND_STYLE:
                                            setMessageText(subjectPart + "added a color to " + teamName + objectType);
                                            break;
                                        case LOGO:
                                            setMessageText(subjectPart + "added a logo in " + teamName + objectType);
                                            break;
                                        case TARGET_AUDIENCE:
                                            setMessageText(subjectPart + "added a target audience in " + teamName + objectType);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            case APPROVE:
                                setMessageText(subjectPart + "approved " + teamName + objectType);
                                break;
                            case UPDATE:
                                if (extraString != null && extraString2 != null && !extraString.equals("") && !extraString2.equals("")) {
                                    setMessageText(subjectPart + "changed " + extraString + " to " + extraString2 + " in " + teamName + objectType);
                                } else {
                                    switch(((BrandingElement)object).getType()) {
                                        case BRAND_STYLE:
                                            setMessageText(subjectPart + "replaced a color to " + teamName + objectType);
                                            break;
                                        case LOGO:
                                            setMessageText(subjectPart + "replaced a logo in " + teamName + objectType);
                                            break;
                                        case TARGET_AUDIENCE:
                                            setMessageText(subjectPart + "replaced a target audience in " + teamName + objectType);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            case AWAIT:
                                setMessageText(subjectPart + "is awaiting approval of " + teamName + objectType);
                                break;
                            case DISAPPROVE:
                                setMessageText(subjectPart + "disapproved the " + teamName + objectType);
                                break;
                            case REMOVE:
                                if (extraString != null && !extraString.equals("")) {
                                    switch(((BrandingElement)object).getType()) {
                                        case BRAND_STYLE:
                                            setMessageText(subjectPart + "removed a brand style from " + teamName + objectType);
                                            break;
                                        case TARGET_AUDIENCE:
                                            setMessageText(subjectPart + "removed a target audience from " + teamName + objectType);
                                            break;
                                        case LOGO:
                                            setMessageText(subjectPart + "removed a logo from " + teamName + objectType);
                                        default:
                                            setMessageText(subjectPart + "removed " + extraString + " from " + teamName + objectType);
                                            break;
                                    }
                                } else {
                                    switch(((BrandingElement)object).getType()) {
                                        case BRAND_STYLE:
                                            setMessageText(subjectPart + "removed a color from " + teamName + objectType);
                                            break;
                                        case LOGO:
                                            setMessageText(subjectPart + "removed a logo from " + teamName + objectType);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            default:
                                return "missing [objectType]";
                        }
                    } else {
                        switch (verbType) {
                            case ADD:
                                switch(((BrandingElement)object).getType()) {
                                    case BRAND_STYLE:
                                        setMessageText(subjectPart + "added a color to the " + objectType);
                                        break;
                                    case LOGO:
                                        setMessageText(subjectPart + "added a logo in the " + objectType);
                                        break;
                                    case TARGET_AUDIENCE:
                                        setMessageText(subjectPart + "added a target audience in the " + objectType);
                                        break;
                                    default:
                                        setMessageText(subjectPart + "added " + extraString + " to the " + objectType);
                                        break;
                                }
                                break;
                            case APPROVE:
                                setMessageText(subjectPart + "approved the " + objectType);
                                break;
                            case UPDATE:
                                if (extraString != null && extraString2 != null && !extraString.equals("") && !extraString2.equals("")) {
                                    setMessageText(subjectPart + "changed " + extraString + " to " + extraString2 + " in the " + objectType);
                                } else {
                                    switch(((BrandingElement)object).getType()) {
                                        case BRAND_STYLE:
                                            setMessageText(subjectPart + "updated a color in the " + objectType);
                                            break;
                                        case LOGO:
                                            setMessageText(subjectPart + "updated a logo in the " + objectType);
                                            break;
                                        case TARGET_AUDIENCE:
                                            setMessageText(subjectPart + "updated a target audience in the " + objectType);
                                            break;
                                        default:
                                            setMessageText(subjectPart + "added " + extraString + " to the " + objectType);
                                            break;
                                    }
                                }
                                break;
                            case AWAIT:
                                setMessageText(subjectPart + "is awaiting approval of the " + objectType);
                                break;
                            case DISAPPROVE:
                                setMessageText(subjectPart + "disapproved the current " + objectType);
                                break;
                            case REMOVE:
                                if (extraString != null && !extraString.equals("")) {
                                    switch(((BrandingElement)object).getType()) {
                                        case BRAND_STYLE:
                                            setMessageText(subjectPart + "removed a brand style from " + objectType);
                                            break;
                                        case TARGET_AUDIENCE:
                                            setMessageText(subjectPart + "removed a target audience from " + objectType);
                                            break;
                                        case LOGO:
                                            setMessageText(subjectPart + "removed a logo from " + objectType);
                                        default:
                                            setMessageText(subjectPart + "removed " + extraString + " from the " + objectType);
                                            break;
                                    }
                                } else {
                                    switch(((BrandingElement)object).getType()) {
                                        case BRAND_STYLE:
                                            setMessageText(subjectPart + "removed a color from " + objectType);
                                            break;
                                        case TARGET_AUDIENCE:
                                            setMessageText(subjectPart + "removed a target audience from " + objectType);
                                            break;
                                        case LOGO:
                                            setMessageText(subjectPart + "removed a logo from " + objectType);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                            default:
                                return "missing [objectType]";
                        }
                    }

                    break;
                case MESSAGE:
                    if (getTeamNameString() != null && !getTeamNameString().equals("")) {
                        String teamName = getTeamNameString();
                        if (getSenderType() != 0) {
                            if (getSenderType() == Constants.Ints.Sender.TO) {
                                setMessageText(subjectPart + "said, \"" + ((Message) object).getText() + "\" to " + teamName + ".");
                            } else {
                                setMessageText(subjectPart + "from " + teamName + " said, \"" + ((Message) object).getText() + "\"");
                            }
                        }
                    } else {
                        setMessageText(subjectPart + "said, \"" + ((Message) object).getText() + "\"");
                    }
                    break;
                case MEMBER:
                    objectPart = ((User) getObject()).getName();
                    if (roleObject != null) {
                        String objectName = "";
                        if (objectPart.endsWith("s")) {
                            objectName += objectPart + "' ";
                        } else {
                            objectName += objectPart + "'s ";
                        }
                        if (getTeamNameString() != null && !getTeamNameString().equals("")) {
                            setMessageText(subjectPart + "updated " + objectName + "role in " + getTeamNameString() + " to " + roleObject.toString() + ".");
                        } else {
                            setMessageText(subjectPart + "updated " + objectName + "role to " + roleObject.toString() + ".");
                        }
                        break;
                    } else if (statusObject != null) {
                        if (getTeamNameString() != null && !getTeamNameString().equals("")) {
                            final String teamEndString = getTeamNameString() + "!";
                            switch(verbType) {
                                case APPROVE:
                                    setMessageText(subjectPart + "accepted " + objectPart + " into " + teamEndString);
                                    break;
                                case INVITE:
                                    setMessageText(subjectPart + "invited " + objectPart + " to " + teamEndString);
                                    break;
                                case BLOCK:
                                    setMessageText(subjectPart + "blocked " + objectPart + " from " + teamEndString);
                                    break;
                                default:
                                    return "missing [objectType]";
                            }
                        } else {
                            switch(verbType) {
                                case ADD:
                                    setMessageText(subjectPart + "added " + objectPart + " to the team!");
                                    break;
                                case APPROVE:
                                    setMessageText(subjectPart + "accepted " + objectPart + " into the team!");
                                    break;
                                case INVITE:
                                    if (getTeamNameString() != null && !getTeamNameString().equals("")) {
                                        final String teamName = getTeamNameString();
                                        if (getExtraString() != null && !getExtraString().equals("")) {
                                            setMessageText(subjectPart + "from " + getExtraString() + ", invited " + objectPart + " to " + teamName + ".");
                                        } else {
                                            setMessageText(subjectPart + "invited " + objectPart + " to " + teamName + ".");
                                        }
                                    } else {
                                        setMessageText(subjectPart + "invited " + objectPart + " to the team!");
                                    }
                                    break;
                                case BLOCK:
                                    setMessageText(subjectPart + "blocked " + objectPart + " from the team!");
                                    break;
                                default:
                                    return "missing [objectType]";
                            }
                        }
                    } else {
                        switch(verbType) {
                            case ADD:
                                setMessageText(subjectPart + "added " + objectPart + " to the team!");
                                break;
                            case APPROVE:
                                setMessageText(subjectPart + "accepted " + objectPart + " into the team!");
                                break;
                            case INVITE:
                                if (getTeamNameString() != null && !getTeamNameString().equals("")) {
                                    final String teamName = getTeamNameString();
                                    if (getExtraString() != null && !getExtraString().equals("")) {
                                        setMessageText(subjectPart + "from " + getExtraString() + ", invited " + objectPart + " to " + teamName + ".");
                                    } else {
                                        setMessageText(subjectPart + "invited " + objectPart + " to " + teamName + ".");
                                    }
                                } else {
                                    setMessageText(subjectPart + "invited " + objectPart + " to the team!");
                                }
                                break;
                            case BLOCK:
                                setMessageText(subjectPart + "blocked " + objectPart + " from the team!");
                                break;
                            default:
                                return "missing [objectType]";
                        }
                    }
                    break;
                case TEAM:
                    objectPart = ((FirebaseEntity)getObject()).getName();
                    if (statusObject != null) {
                        switch(verbType) {
                            case APPROVE:
                                setMessageText(subjectPart + "accepted " + objectPart + "!");
                                break;
                            case INVITE:
                                setMessageText(subjectPart + "invited " + objectPart + "!");
                                break;
                            case BLOCK:
                                setMessageText(subjectPart + "blocked " + objectPart + "!");
                                break;
                        }
                    } else {
                        if (extraString != null && !extraString.equals("")) {
                            switch(verbType) {
                                case UPDATE_USERNAME:
                                    setMessageText(subjectPart + "changed " + objectPart + "'s username to " + extraString + ".");
                                    break;
                                case UPDATE_TEAM_NAME:
                                    setMessageText(subjectPart + "changed " + objectPart + "'s name to " + extraString + ".");
                                    break;
                            }
                        } else {
                            switch (verbType) {
                                case APPROVE:
                                    setMessageText(subjectPart + "approved " + objectPart + ".");
                                    break;
                                case BLOCK:
                                    setMessageText(subjectPart + "blocked " + objectPart + ".");
                                    break;
                                case CREATE:
                                    setMessageText(subjectPart + "created " + objectPart + ".");
                                    break;
                                case JOIN:
                                    setMessageText(subjectPart + "joined " + objectPart + ".");
                                    break;
                                case LEFT:
                                    setMessageText(subjectPart + "left " + objectPart + ".");
                                    break;
                                case REQUEST_JOIN:
                                    setMessageText(subjectPart + "is requesting to join " + objectPart + ".");
                                    break;
                                case UPDATE_USERNAME:
                                    if (subject instanceof Team && subjectUID.equals(objectUID)) {
                                        setMessageText(subjectPart + "changed their username to " + getTeamNameString() + ".");
                                    } else {
                                        setMessageText(subjectPart + "changed " + objectPart + "'s username to " + getTeamNameString() + ".");
                                    }
                                    break;
                                case UPDATE_TEAM_NAME:
                                    if (subject instanceof Team && subjectUID.equals(objectUID)) {
                                        setMessageText(subjectPart + "changed their name to " + getTeamNameString() + ".");
                                    } else {
                                        setMessageText(subjectPart + "changed " + objectPart + "'s name to " + getTeamNameString() + ".");
                                    }
                                    break;
                            }
                        }
                    }

                    break;
                default:
                    return " missing [objectPart]";
            }

            return getMessageText();
        } else {
            setMessageText(subjectPart + " " + verbPart + " " + objectPart + ".");
            return getMessageText();
        }

    }
    public ActivityObjectType getExtraObjectType() {
        return extraObjectType;
    }
    public void addReceiverUID(final FirebaseObject object) {
        if (getReceiverUIDs().size() > 0) {
            for(String uid:getReceiverUIDs()) {
                if (object instanceof FirebaseEntity) {
                    if (object instanceof User) {
                        if (uid.equals(((User) object).getTeamUID())) return;
                    } else if (object instanceof Team) {
                        if (uid.equals(object.getUID())) return;
                    }
                } else if (object instanceof FirebaseContent) {
                    if (object instanceof BrandingElement) {
                        if (uid.equals(((BrandingElement) object).getTeamUID())) return;
                    }
                }
            }
        }

        if (object instanceof FirebaseEntity) {
            if (object instanceof User) {
                getReceiverUIDs().add(((User) object).getTeamUID());
            } else if (object instanceof Team) {
                getReceiverUIDs().add(object.getUID());
            }
        } else if (object instanceof FirebaseContent) {
            if (object instanceof BrandingElement) {
                getReceiverUIDs().add(((BrandingElement) object).getTeamUID());
            }
        }
    }
    public void addReceiverUID(final String toUID) {
        if (getReceiverUIDs().size() > 0) {
            for(String uid:getReceiverUIDs()) {
                if(uid.equals(toUID)) return;
            }
        }

        getReceiverUIDs().add(toUID);
    }
    public void addSeenUID(final User user) {
        if (getSeenUIDs().size() > 0) {
            for (final String seenUID : getSeenUIDs()) {
                if (seenUID.equals(user.getUID())) return;
            }
        }

        getSeenUIDs().add(user.getUID());
    }
    public String getObjectUID() { return objectUID; }
    private void setObjectUID(final String objectUID) { this.objectUID = objectUID; }
    private FirebaseObject getObject() { return object; }
    public ActivityObjectType setObject(final FirebaseObject object) {
        this.object = object;
        if (object != null) {
            setObjectUID(object.getUID());
        }
        if (object instanceof Message) {
            setObjectType(ActivityObjectType.MESSAGE);
        } else if (object instanceof BrandingElement) {
            setObjectType(ActivityObjectType.BRANDING_ELEMENT);
        } else if (object instanceof User) {
            setObjectType(ActivityObjectType.MEMBER);
        } else if (object instanceof Team) {
            setObjectType(ActivityObjectType.TEAM);
        } else {
            return ActivityObjectType.DEFAULT;
        }

        return getObjectType();
    }
    public String getSubjectUID() { return subjectUID; }
    private void setSubjectUID(final String subjectUID) { this.subjectUID = subjectUID; }
    private FirebaseObject getSubject() { return subject; }
    public ActivitySubjectType setSubject(final FirebaseObject subject) {
        this.subject = subject;
        setSubjectUID(subject.getUID());
        if (subject instanceof User) {
            setSubjectType(ActivitySubjectType.MEMBER);
        } else if (subject instanceof Team) {
            setSubjectType(ActivitySubjectType.TEAM);
        } else {
            return ActivitySubjectType.DEFAULT;
        }

        return getSubjectType();
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public ArrayList<String> getSeenUIDs() {
        return seenUIDs;
    }
    public void setSeenUIDs(ArrayList<String> seenUIDs) {
        this.seenUIDs = seenUIDs;
    }
    private int getSenderType() {
        return senderType;
    }
    private void setSenderType(int senderType) {
        this.senderType = senderType;
    }
    public String getExtraString2() {
        return extraString2;
    }
    private void setExtraString2(String extraString2) {
        this.extraString2 = extraString2;
    }
    public FirebaseEntity.EntityRole getRoleObject() {
        return roleObject;
    }
    private void setRoleObject(FirebaseEntity.EntityRole roleObject) {
        this.roleObject = roleObject;
    }
    public FirebaseEntity.EntityStatus getStatusObject() {
        return statusObject;
    }
    private void setStatusObject(FirebaseEntity.EntityStatus statusObject) {
        this.statusObject = statusObject;
    }
    private String getExtraString() {
        return extraString;
    }
    public void setExtraString(String extraString) {
        this.extraString = extraString;
    }
    private String getTeamNameString() {
        return teamNameString;
    }
    private void setTeamNameString(String teamNameString) {
        this.teamNameString = teamNameString;
    }
    public String getMessageText() {
        return messageText;
    }
    private void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    private ActivityVerbType getVerbType() { return verbType; }
    private void setVerbType(final ActivityVerbType verbType) { this.verbType = verbType; }
    private ArrayList<String> getReceiverUIDs() { return receiverUIDs; }
    private void setReceiverUIDs(final ArrayList<String> receiverUIDs) { this.receiverUIDs = receiverUIDs; }
    public ActivityObjectType getObjectType() { return objectType; }
    private void setObjectType(final ActivityObjectType objectType) { this.objectType = objectType; }
    public ActivitySubjectType getSubjectType() { return subjectType; }
    private void setSubjectType(final ActivitySubjectType subjectType) { this.subjectType = subjectType; }
}