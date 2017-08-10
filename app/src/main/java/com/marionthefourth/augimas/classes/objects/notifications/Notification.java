package com.marionthefourth.augimas.classes.objects.notifications;

import android.os.Bundle;
import android.os.Parcel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseContent;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.FirebaseObject;
import com.marionthefourth.augimas.classes.objects.communication.Message;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.DEFAULT_ID;
import static com.marionthefourth.augimas.classes.objects.notifications.Notification.NotificationObjectType.BRANDING_ELEMENT;

public final class Notification extends FirebaseContent {
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
    private ArrayList<String> receiverUIDs = new ArrayList<>();
    private NotificationVerbType verbType = NotificationVerbType.DEFAULT;
    private NotificationObjectType objectType = NotificationObjectType.DEFAULT;
    private NotificationSubjectType subjectType = NotificationSubjectType.DEFAULT;
    private NotificationObjectType extraObjectType = NotificationObjectType.DEFAULT;

    public Notification(FirebaseObject subject, FirebaseObject object, NotificationVerbType verbType, String teamNameString) {
        this(subject,object,verbType);
        setTeamNameString(teamNameString);
        setMessage();
    }

    public Notification(final FirebaseObject subject, final FirebaseObject object, final NotificationVerbType verbType, final String teamNameString, final String extraString) {
        this(subject,object,verbType,extraString);
        setExtraString(extraString);
        setMessage();
    }

    public Notification(final FirebaseObject subject, final FirebaseObject object, final NotificationVerbType verbType, final String teamNameString, final String extraString, final String extraString2) {
        this(subject,object,verbType,teamNameString,extraString);
        setExtraString2(extraString2);
        setMessage();
    }

    //    Class Enums
    public enum NotificationSubjectType {
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

        public static NotificationSubjectType getType(int type) {
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

        public static NotificationSubjectType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationSubjectTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<NotificationSubjectType> getAllNotificationSubjectTypes() {
            final ArrayList<NotificationSubjectType> notificationSubjectTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationSubjectTypes(); i++) {
                notificationSubjectTypes.add(getType(i));
            }

            return notificationSubjectTypes;
        }

        public static int getNumberOfNotificationSubjectTypes() {
            return 3;
        }

    }
    public enum NotificationVerbType {
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

        public static NotificationVerbType toVerbType(BrandingElement.ElementStatus status) {
            switch (status) {
                case APPROVED:
                    return NotificationVerbType.APPROVE;
                case AWAITING:
                    return NotificationVerbType.AWAIT;
                case INCOMPLETE:
                    return NotificationVerbType.DISAPPROVE;
                case NONE:
                    return NotificationVerbType.AWAIT;
                default:
                    return NotificationVerbType.DEFAULT;

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

        public static NotificationVerbType getType(int type) {
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

        public static NotificationVerbType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationVerbTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<NotificationVerbType> getAllNotificationVerbTypes() {
            final ArrayList<NotificationVerbType> notificationVerbTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationVerbTypes(); i++) {
                notificationVerbTypes.add(getType(i));
            }

            return notificationVerbTypes;
        }

        public static int getNumberOfNotificationVerbTypes() {
            return 16;
        }

    }
    public enum NotificationObjectType {
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

        public static NotificationObjectType getType(int type) {
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

        public static NotificationObjectType getType(String type) {
            for (int i = 0; i < getNumberOfNotificationObjectTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getType(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<NotificationObjectType> getAllNotificationObjectTypes() {
            final ArrayList<NotificationObjectType> notificationObjectTypes = new ArrayList<>();
            for (int i = 0 ; i < getNumberOfNotificationObjectTypes(); i++) {
                notificationObjectTypes.add(getType(i));
            }

            return notificationObjectTypes;
        }

        public static int getNumberOfNotificationObjectTypes() {
            return 5;
        }
    }
    //    Notification Constructors
    public Notification() { }
    public Notification(final Parcel in) {
        final Notification notification = (Notification) in.readSerializable();
        setUID(notification.getUID());
        setObject(notification.getObject());
        setSubject(notification.getSubject());
        setVerbType(notification.getVerbType());
        setReceiverUIDs(notification.getReceiverUIDs());
    }
    public Notification(final DataSnapshot nRef) {
        this();
        if (nRef.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(nRef.child(Constants.Strings.UIDs.UID).getValue().toString());
        }
        if (nRef.hasChild(Constants.Strings.Fields.SUBJECT_TYPE)) {
            setSubjectType(NotificationSubjectType.getType(nRef.child(Constants.Strings.Fields.SUBJECT_TYPE).getValue().toString()));
        }
        if (nRef.hasChild(Constants.Strings.Fields.VERB_TYPE)) {
            setVerbType(NotificationVerbType.getType(nRef.child(Constants.Strings.Fields.VERB_TYPE).getValue().toString()));
        }
        if (nRef.hasChild(Constants.Strings.Fields.OBJECT_TYPE)) {
            setObjectType(NotificationObjectType.getType(nRef.child(Constants.Strings.Fields.OBJECT_TYPE).getValue().toString()));
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

        int index = 0;
        while (nRef.hasChild(Constants.Strings.UIDs.RECEIVER_UID + index)) {
            getReceiverUIDs().add(nRef.child(Constants.Strings.UIDs.RECEIVER_UID + index).getValue().toString());
            index++;
        }
    }
    public static ArrayList<Notification> toArrayList(DataSnapshot notificationReferences) {
        final ArrayList<Notification> notifications = new ArrayList<>();
        for(DataSnapshot notificationReference:notificationReferences.getChildren()) {
            notifications.add(new Notification(notificationReference));
        }
        return notifications;
    }
    public Notification(final FirebaseObject subject, final NotificationVerbType vType) {
        setVerbType(vType);
        setSubject(subject);
    }
    public Notification(final FirebaseObject subject, final FirebaseObject object, final NotificationVerbType vType) {
        this(subject,vType);
        setObject(object);
        setMessage();
    }
    public Notification(final FirebaseObject subject, final FirebaseObject object, final NotificationVerbType vType, FirebaseEntity.EntityRole roleObject, String teamNameString) {
        this(subject,object,vType);
        setRoleObject(roleObject);
        setTeamNameString(teamNameString);
        setMessage();
    }
    public Notification(final FirebaseObject subject, final FirebaseObject object, final NotificationVerbType vType, FirebaseEntity.EntityStatus statusObject, String extraString) {
        this(subject,object,vType);
        setStatusObject(statusObject);
        setExtraString(extraString);
        setMessage();
    }

    public Notification(final NotificationSubjectType sType, final NotificationVerbType vType, final NotificationObjectType oType){
        setVerbType(vType);
        setObjectType(oType);
        setSubjectType(sType);
    }
    public Notification(final FirebaseObject subject, final NotificationVerbType vType, final BrandingElement.ElementType brandingElementType) {
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
    public JSONObject toUpstreamJSON(final String toTeamUID, final String senderUID, String header) {
        JSONObject upstreamJSON = new JSONObject();
        try {
            upstreamJSON.put(Constants.Strings.Server.Fields.APP_ID,Constants.Strings.Server.OneSignal.APP_ID);
//            Add Filters
            JSONArray filters = new JSONArray();
            JSONObject filter = new JSONObject();
            filter.put(Constants.Strings.Server.Fields.FIELD,Constants.Strings.Server.Fields.TAG);
            filter.put("key",Constants.Strings.UIDs.TEAM_UID);
            filter.put(Constants.Strings.Server.Fields.RELATION,Constants.Strings.Server.Fields.EQUALS);
            filter.put(Constants.Strings.Server.Fields.VALUE,toTeamUID);
            filters.put(filter);
            upstreamJSON.put(Constants.Strings.Server.Fields.FILTERS,filters);
//            Add Data
            JSONObject data = new JSONObject();
            data.put(Constants.Strings.Server.Fields.MESSAGE, setMessage());
            data.put(Constants.Strings.Fields.FRAGMENT,getObjectTypeString());
            data.put(Constants.Strings.UIDs.SENDER_UID,senderUID);
            data.put(Constants.Strings.Fields.HEADER,"");
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
    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UIDs.UID, getUID());
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
    public static final Creator CREATOR = new Creator() {
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }
        public Notification[] newArray(int size) {
            return new Notification[size];
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
    public String getObjectTypeString() {
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

    public String getRelativeMessage(final String relativeUID) {
        String subjectPart = "";
        String verbPart = "";
        String objectPart = "";
        String extraObjectPart = "";

        if (getSubject() != null && getObject() != null) {
            switch (subjectType) {
                case MEMBER:
                case TEAM:
                    if (subjectUID.equals(relativeUID)) {
                        subjectPart = "You ";
                    } else {
                        subjectPart = ((FirebaseEntity)getSubject()).getName() + " ";
                    }
                    break;
                default:
                    subjectPart = "[subjectPart] ";
            }

            switch (objectType) {
                case BRANDING_ELEMENT:
                    String objectType = ((BrandingElement) getObject()).getType().toString() + ".";
                    switch (verbType) {
                        case ADD:
                            setMessageText(subjectPart + "added a new item to " + objectType);
                            break;
                        case APPROVE:
                            setMessageText(subjectPart + "approved the " + objectType);
                            break;
                        case UPDATE:
                            setMessageText(subjectPart + "updated a " + objectType + " item.");
                            break;
                        case AWAIT:
                            if (subjectUID.equals(relativeUID)) {
                                setMessageText(subjectPart + "are awaiting approval of the " + objectType);
                            } else {
                                setMessageText(subjectPart + "is awaiting approval of the " + objectType);
                            }
                            break;
                        case DISAPPROVE:
                            setMessageText(subjectPart + "disapproved the current " + objectType);
                            break;
                        case REMOVE:
                            setMessageText(subjectPart + "removed an item from the " + objectType);
                            break;
                        default:
                            return "missing [objectType]";
                    }
                    break;
                case MESSAGE:
                    setMessageText(subjectPart + "said, \"" + ((Message) object).getText() + "\"");
                    break;
                case MEMBER:
                    if (roleObject != null) {
                        setMessageText(subjectPart + "updated " + objectPart + "'s role to " + roleObject.toString() + ".");
                    } else if (statusObject != null) {
                        switch(verbType) {
                            case APPROVE:
                                setMessageText(subjectPart + "accepted " + objectPart + " into the team!");
                                break;
                            case INVITE:
                                setMessageText(subjectPart + "invited " + objectPart + " to the team!");
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
                                setMessageText(subjectPart + "accepted " + objectPart + " into the team!");
                                break;
                            case INVITE:
                                setMessageText(subjectPart + "invited " + objectPart + " to the team!");
                                break;
                            case BLOCK:
                                setMessageText(subjectPart + "blocked " + objectPart + " from the team!");
                                break;
                        }
                    } else {
                        if (extraString != null) {
                            switch(verbType) {
                                case UPDATE_USERNAME:
                                    setMessageText(subjectPart + "changed " + objectPart + "'s username to " + extraString);
                                    break;
                                case UPDATE_TEAM_NAME:
                                    setMessageText(subjectPart + "changed " + objectPart + "'s name to " + extraString);
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
                                }
                                break;
                            case APPROVE:
                                setMessageText(subjectPart + "approved " + teamName + objectType);
                                break;
                            case UPDATE:
                                if (extraString != null && extraString2 != null && !extraString.equals("") && !extraString2.equals("")) {
                                    setMessageText(subjectPart + "changed " + extraString + " to " + extraString2 + " in " + teamName + objectType);
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
                                    setMessageText(subjectPart + "removed " + extraString + " from " + teamName + objectType);
                                }
                                break;
                            default:
                                return "missing [objectType]";
                        }
                    } else {
                        switch (verbType) {
                            case ADD:
                                setMessageText(subjectPart + "added " + extraString + " to the " + objectType);
                                break;
                            case APPROVE:
                                setMessageText(subjectPart + "approved the " + objectType);
                                break;
                            case UPDATE:
                                if (extraString != null && extraString2 != null && !extraString.equals("") && !extraString2.equals("")) {
                                    setMessageText(subjectPart + "changed " + extraString + " to " + extraString2 + " in the " + objectType);
                                }
                                break;
                            case AWAIT:
                                setMessageText(subjectPart + "is awaiting approval of the " + objectType);
                                break;
                            case DISAPPROVE:
                                setMessageText(subjectPart + "disapproved the current " + objectType);
                                break;
                            case REMOVE:
                                setMessageText(subjectPart + "removed " + extraString + " from the " + objectType);
                                break;
                            default:
                                return "missing [objectType]";
                        }
                    }

                    break;
                case MESSAGE:
                    if (getTeamNameString() != null && !getTeamNameString().equals("")) {
                        String teamName = getTeamNameString();
                        setMessageText(subjectPart + " from " + teamName + "said, \"" + ((Message) object).getText() + "\"");
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
                                case APPROVE:
                                    setMessageText(subjectPart + "accepted " + objectPart + " into the team!");
                                    break;
                                case INVITE:
                                    setMessageText(subjectPart + "invited " + objectPart + " to the team!");
                                    break;
                                case BLOCK:
                                    setMessageText(subjectPart + "blocked " + objectPart + " from the team!");
                                    break;
                                default:
                                    return "missing [objectType]";
                            }
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
                                    setMessageText(subjectPart + "changed " + objectPart + "'s username to " + extraString);
                                    break;
                                case UPDATE_TEAM_NAME:
                                    setMessageText(subjectPart + "changed " + objectPart + "'s name to " + extraString);
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

    public NotificationObjectType getExtraObjectType() {
        return extraObjectType;
    }

    public void setExtraObjectType(NotificationObjectType extraObjectType) {
        this.extraObjectType = extraObjectType;
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

    private void setReceiverUIDs(FirebaseObject subject, FirebaseObject object, NotificationVerbType vType) {
        if (subject instanceof FirebaseEntity) {
            if (subject instanceof User) {
                getReceiverUIDs().add(((User) subject).getTeamUID());
                if (object instanceof FirebaseEntity) {
                    if (object instanceof User) {
                        getReceiverUIDs().add(((User) object).getTeamUID());
                    } else if (object instanceof Team) {
                        getReceiverUIDs().add(object.getUID());
                    } else if (object instanceof BrandingElement) {
                        getReceiverUIDs().add(((BrandingElement) object).getTeamUID());
                    }
                } else if (object instanceof FirebaseContent) {
                    if (object instanceof BrandingElement) {
                        getReceiverUIDs().add(((BrandingElement) object).getTeamUID());
                    }
                }
            } else if (subject instanceof Team){
                getReceiverUIDs().add(object.getUID());
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
        }

        if (object instanceof FirebaseEntity) {
            if (subject instanceof User) {

            } else  if (subject instanceof Team){
                getReceiverUIDs().add(subject.getUID());
            }
        } else if (object instanceof FirebaseContent) {

        }
    }
    public String getObjectUID() { return objectUID; }
    public void setObjectUID(final String objectUID) { this.objectUID = objectUID; }
    public FirebaseObject getObject() { return object; }
    public NotificationObjectType setObject(final FirebaseObject object) {
        this.object = object;
        if (object != null) {
            setObjectUID(object.getUID());
        }
        if (object instanceof Message) {
            setObjectType(NotificationObjectType.MESSAGE);
        } else if (object instanceof BrandingElement) {
            setObjectType(BRANDING_ELEMENT);
        } else if (object instanceof User) {
            setObjectType(NotificationObjectType.MEMBER);
        } else if (object instanceof Team) {
            setObjectType(NotificationObjectType.TEAM);
        } else {
            return NotificationObjectType.DEFAULT;
        }

        return getObjectType();
    }
    public String getSubjectUID() { return subjectUID; }
    public void setSubjectUID(final String subjectUID) { this.subjectUID = subjectUID; }
    public FirebaseObject getSubject() { return subject; }
    public NotificationSubjectType setSubject(final FirebaseObject subject) {
        this.subject = subject;
        setSubjectUID(subject.getUID());
        if (subject instanceof User) {
            setSubjectType(NotificationSubjectType.MEMBER);
        } else if (subject instanceof Team) {
            setSubjectType(NotificationSubjectType.TEAM);
        } else {
            return NotificationSubjectType.DEFAULT;
        }

        return getSubjectType();
    }


    public String getExtraString2() {
        return extraString2;
    }
    public void setExtraString2(String extraString2) {
        this.extraString2 = extraString2;
    }
    public FirebaseEntity.EntityRole getRoleObject() {
        return roleObject;
    }
    public void setRoleObject(FirebaseEntity.EntityRole roleObject) {
        this.roleObject = roleObject;
    }
    public FirebaseEntity.EntityStatus getStatusObject() {
        return statusObject;
    }
    public void setStatusObject(FirebaseEntity.EntityStatus statusObject) {
        this.statusObject = statusObject;
    }
    public String getExtraString() {
        return extraString;
    }
    public void setExtraString(String extraString) {
        this.extraString = extraString;
    }
    public String getTeamNameString() {
        return teamNameString;
    }
    public void setTeamNameString(String teamNameString) {
        this.teamNameString = teamNameString;
    }
    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    public NotificationVerbType getVerbType() { return verbType; }
    public void setVerbType(final NotificationVerbType verbType) { this.verbType = verbType; }
    public ArrayList<String> getReceiverUIDs() { return receiverUIDs; }
    public void setReceiverUIDs(final ArrayList<String> receiverUIDs) { this.receiverUIDs = receiverUIDs; }
    public NotificationObjectType getObjectType() { return objectType; }
    public void setObjectType(final NotificationObjectType objectType) { this.objectType = objectType; }
    public NotificationSubjectType getSubjectType() { return subjectType; }
    public void setSubjectType(final NotificationSubjectType subjectType) { this.subjectType = subjectType; }
}