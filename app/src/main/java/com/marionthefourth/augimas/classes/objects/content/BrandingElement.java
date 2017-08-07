package com.marionthefourth.augimas.classes.objects.content;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.res.ResourcesCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.classes.constants.Constants;
import com.marionthefourth.augimas.classes.objects.FirebaseContent;
import com.marionthefourth.augimas.classes.objects.content.branding_elements.Branding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.DEFAULT_ID;
import static com.marionthefourth.augimas.classes.constants.Constants.Strings.NO_VALUE;

public class BrandingElement extends FirebaseContent {
    private ElementType type = ElementType.DEFAULT;
    private ElementStatus status = ElementStatus.DEFAULT;
    private String header = ElementType.DEFAULT.toString();
    @Exclude
    private ArrayList<String> contents = new ArrayList<>();
    @Exclude
    private ArrayList<String> data = new ArrayList<>();
//    Class Enums
    public enum ElementType {
        DOMAIN_NAME,SOCIAL_MEDIA_NAME,MISSION_STATEMENT,TARGET_AUDIENCE,STYLE_GUIDE,LOGO,PRODUCTS_SERVICES,DEFAULT;

        @Override
        public String toString() {
            String elementName = super.toString();

            if (elementName.indexOf("_") == -1) {
                return elementName.substring(0,1) + elementName.substring(1).toLowerCase();
            } else {
                final ArrayList<String> words = new ArrayList<>();

                do {
                    if (words.size() != 0) {
                        elementName = elementName.substring(elementName.indexOf("_")+1);
                    }

                    if (elementName.contains("_")) {
                        words.add(elementName.substring(0,elementName.indexOf("_")));
                    } else {
                        words.add(elementName);

                    }
                } while (elementName.contains("_"));


                String finalElement = "";
                for (int j = 0; j < words.size(); j++) {
                    finalElement += words.get(j).substring(0,1) + words.get(j).substring(1).toLowerCase();

                    if (j != words.size()-1) {
                        finalElement += " ";
                    }
                }

                return finalElement;

            }

        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch(this) {
                    case DOMAIN_NAME: return Constants.Ints.BrandingElementTypes.IDs.DOMAIN_NAME;
                    case SOCIAL_MEDIA_NAME: return Constants.Ints.BrandingElementTypes.IDs.SOCIAL_MEDIA_NAME;
                    case MISSION_STATEMENT: return Constants.Ints.BrandingElementTypes.IDs.MISSION_STATEMENT;
                    case TARGET_AUDIENCE: return Constants.Ints.BrandingElementTypes.IDs.TARGET_AUDIENCE;
                    case STYLE_GUIDE: return Constants.Ints.BrandingElementTypes.IDs.STYLE_GUIDE;
                    case LOGO: return Constants.Ints.BrandingElementTypes.IDs.LOGO;
                    case PRODUCTS_SERVICES: return Constants.Ints.BrandingElementTypes.IDs.PRODUCTS_SERVICES;
                    default: return DEFAULT_ID;
                }
            } else {
                switch(this) {
                    case DOMAIN_NAME: return Constants.Ints.BrandingElementTypes.Indices.DOMAIN_NAME;
                    case SOCIAL_MEDIA_NAME: return Constants.Ints.BrandingElementTypes.Indices.SOCIAL_MEDIA_NAME;
                    case MISSION_STATEMENT: return Constants.Ints.BrandingElementTypes.Indices.MISSION_STATEMENT;
                    case TARGET_AUDIENCE: return Constants.Ints.BrandingElementTypes.Indices.TARGET_AUDIENCE;
                    case STYLE_GUIDE: return Constants.Ints.BrandingElementTypes.Indices.STYLE_GUIDE;
                    case LOGO: return Constants.Ints.BrandingElementTypes.Indices.LOGO;
                    case PRODUCTS_SERVICES: return Constants.Ints.BrandingElementTypes.Indices.PRODUCTS_SERVICES;
                    default: return DEFAULT_ID;
                }
            }
        }

        public static ElementType getType(final String type) {
            for (int i = 0; i < getNumberOfElementTypes(); i++) {
                if (type.equals(getType(i).toString()) || type.equals(getType(i).toMapStyleString())) {
                    return getAllElementTypes().get(i);
                }
            }
            return null;
        }

        public static ElementType getType(final int typeIndex) {
            switch (typeIndex) {
                case Constants.Ints.BrandingElementTypes.Indices.DOMAIN_NAME:
                case Constants.Ints.BrandingElementTypes.IDs.DOMAIN_NAME:
                    return DOMAIN_NAME;
                case Constants.Ints.BrandingElementTypes.Indices.SOCIAL_MEDIA_NAME:
                case Constants.Ints.BrandingElementTypes.IDs.SOCIAL_MEDIA_NAME:
                    return SOCIAL_MEDIA_NAME;
                case Constants.Ints.BrandingElementTypes.Indices.MISSION_STATEMENT:
                case Constants.Ints.BrandingElementTypes.IDs.MISSION_STATEMENT:
                    return MISSION_STATEMENT;
                case Constants.Ints.BrandingElementTypes.Indices.TARGET_AUDIENCE:
                case Constants.Ints.BrandingElementTypes.IDs.TARGET_AUDIENCE:
                    return TARGET_AUDIENCE;
                case Constants.Ints.BrandingElementTypes.Indices.STYLE_GUIDE:
                case Constants.Ints.BrandingElementTypes.IDs.STYLE_GUIDE:
                    return STYLE_GUIDE;
                case Constants.Ints.BrandingElementTypes.Indices.LOGO:
                case Constants.Ints.BrandingElementTypes.IDs.LOGO:
                    return LOGO;
                case Constants.Ints.BrandingElementTypes.Indices.PRODUCTS_SERVICES:
                case Constants.Ints.BrandingElementTypes.IDs.PRODUCTS_SERVICES:
                    return PRODUCTS_SERVICES;
                default: return DEFAULT;
            }
        }

        public static ArrayList<ElementType> getAllElementTypes() {
            final ArrayList<ElementType> elementTypes = new ArrayList<>();
            for (int i = 0; i < getNumberOfElementTypes(); i++) {
                elementTypes.add(getType(i));
            }

            return elementTypes;
        }

        public static int getNumberOfElementTypes() {
            return 2;
        }
    }
    public enum ElementStatus {
        APPROVED,AWAITING,INCOMPLETE,NONE,DEFAULT;

        @Override
        public String toString() {
            return super.toString().substring(0,1) + super.toString().substring(1).toLowerCase();
        }

        public String toVerb() {
            switch (this) {
                case APPROVED:
                    return "Approve";
                case AWAITING:
                    return "Awaiting Approval";
                case INCOMPLETE:
                    return "Incomplete";
                case NONE:
                    return "None";
                default:
                    return this.toString();
            }
        }

        public static ElementStatus fromVerb(String verb) {
            switch (verb) {
                case "Approve":
                    return APPROVED;
                case "Awaiting Approval":
                    return AWAITING;
                case "Incomplete":
                    return INCOMPLETE;
                case "None":
                    return NONE;
                default:
                    return DEFAULT;
            }
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this) {
                    case APPROVED: return Constants.Ints.BrandingElementStatii.IDs.APPROVED;
                    case AWAITING: return Constants.Ints.BrandingElementStatii.IDs.AWAITING;
                    case INCOMPLETE: return Constants.Ints.BrandingElementStatii.IDs.INCOMPLETE;
                    case NONE: return Constants.Ints.BrandingElementStatii.IDs.NONE;
                    default: return DEFAULT_ID;
                }
            } else {
                switch (this) {
                    case APPROVED: return Constants.Ints.BrandingElementStatii.Indices.APPROVED;
                    case AWAITING: return Constants.Ints.BrandingElementStatii.Indices.AWAITING;
                    case INCOMPLETE: return Constants.Ints.BrandingElementStatii.Indices.INCOMPLETE;
                    case NONE: return Constants.Ints.BrandingElementStatii.Indices.NONE;
                    default: return DEFAULT_ID;
                }
            }
        }

        public static ElementStatus getStatus(final String status) {
            for (int i = 0; i < getNumberOfElementStatii(); i++) {
                if (status.toLowerCase().equals(getStatus(i).toString().toLowerCase()) || status.toLowerCase().equals(getStatus(i).toMapStyleString().toLowerCase())) {
                    return getAllStatii().get(i);
                }
            }
            return null;
        }

        public static ArrayList<ElementStatus> getAllStatii() {
            final ArrayList<ElementStatus> elementStatii = new ArrayList<>();
            for (int i = 0; i < getNumberOfElementStatii(); i++) {
                elementStatii.add(getStatus(i));
            }

            return elementStatii;
        }

        public static ElementStatus getStatus(final int statusIndex) {
            switch (statusIndex) {
                case Constants.Ints.BrandingElementStatii.Indices.APPROVED:
                case Constants.Ints.BrandingElementStatii.IDs.APPROVED:
                    return APPROVED;
                case Constants.Ints.BrandingElementStatii.Indices.AWAITING:
                case Constants.Ints.BrandingElementStatii.IDs.AWAITING:
                    return AWAITING;
                case Constants.Ints.BrandingElementStatii.Indices.INCOMPLETE:
                case Constants.Ints.BrandingElementStatii.IDs.INCOMPLETE:
                    return INCOMPLETE;
                case Constants.Ints.BrandingElementStatii.Indices.NONE:
                case Constants.Ints.BrandingElementStatii.IDs.NONE:
                    return NONE;
                default: return DEFAULT;
            }
        }

        private static int getNumberOfElementStatii() {
            return 5;
        }

        public Drawable toDrawable(final Context context) {
            int resource;
            switch (this) {
                case APPROVED: resource = R.drawable.ic_check_circle;
                    break;
                case INCOMPLETE: resource = R.drawable.ic_highlight_off;
                    break;
                case AWAITING: resource = R.drawable.ic_help_outline;
                    break;
                default: resource = R.drawable.ic_group_work;
                    break;
            }

            return ResourcesCompat.getDrawable(context.getResources(), resource, null);
        }

    }
//    Branding Element Constructors
    public BrandingElement() {}
    public BrandingElement(final Parcel in) {
        final BrandingElement brandingElement = (BrandingElement)in.readSerializable();
        setType(brandingElement.getType());
        setStatus(brandingElement.getStatus());
        setHeader(brandingElement.getType().toString());
        setTeamUID(brandingElement.getTeamUID());
    }
    public BrandingElement(final ElementType type) {
        this();
        setType(type);
        setHeader(type.toString());
        initContents(type);
    }
    public BrandingElement(final DataSnapshot brandingElementSnapshot) {
        this();
        if (brandingElementSnapshot.hasChild(Constants.Strings.Fields.BRANDING_ELEMENT_TYPE)) {
            setType(ElementType.getType(brandingElementSnapshot.child(Constants.Strings.Fields.BRANDING_ELEMENT_TYPE).getValue().toString()));
            setHeader(ElementType.getType(brandingElementSnapshot.child(Constants.Strings.Fields.BRANDING_ELEMENT_TYPE).getValue().toString()).toString());
        }
        if (brandingElementSnapshot.hasChild(Constants.Strings.Fields.BRANDING_ELEMENT_STATUS)) {
            setStatus(ElementStatus.getStatus(brandingElementSnapshot.child(Constants.Strings.Fields.BRANDING_ELEMENT_STATUS).getValue().toString()));
        }
        if (brandingElementSnapshot.hasChild(Constants.Strings.UIDs.UID)) {
            setUID(brandingElementSnapshot.child(Constants.Strings.UIDs.UID).getValue().toString());
        }
        if (brandingElementSnapshot.hasChild(Constants.Strings.UIDs.TEAM_UID)) {
            setTeamUID(brandingElementSnapshot.child(Constants.Strings.UIDs.TEAM_UID).getValue().toString());
        }

//        initContents(type);
        fillContents(brandingElementSnapshot,false);
    }
    public BrandingElement(final ElementType type, final ElementStatus status) {
        this(type);
        setStatus(status);
    }
    public static ArrayList<BrandingElement> toArrayList(DataSnapshot brandingElementReferences) {
        final ArrayList<BrandingElement> brandingElements = new ArrayList<>();
        for(DataSnapshot brandingElementReference:brandingElementReferences.getChildren()) {
            brandingElements.add(new BrandingElement(brandingElementReference));
        }
        return brandingElements;
    }
//    Init Methods
    private void initContents(ElementType type) {
        if (getContents().size() == 0) {
            getContents().add("");
        }

        switch (type){
            case DOMAIN_NAME:
                if (getContents().size() != Branding.TLD.getNumberOfTLDs()+1) {
                    for (int i = 0; i < Branding.TLD.getNumberOfTLDs(); i++) {
                        getContents().add(NO_VALUE);
                    }
                }
                break;
            case SOCIAL_MEDIA_NAME:
                if (getContents().size() != Branding.Service.getNumberOfServices()+1) {
                    for (int i = 0; i < Branding.Service.getNumberOfServices(); i++) {
                        getContents().add(NO_VALUE);
                    }
                }
                break;
            case MISSION_STATEMENT:
                break;
            case TARGET_AUDIENCE:
                break;
            case STYLE_GUIDE:
                break;
            case LOGO:
                break;
            case PRODUCTS_SERVICES:
                break;
            case DEFAULT:
                break;
        }
    }
    private void fillContents(final DataSnapshot brandingElementSnapshot,boolean regularData) {
        if (regularData) {
            if (contents.size() == 0) {
                return;
            }
        } else {

        }

        int currentIndex = 0;
        switch (getType()) {
            case DOMAIN_NAME:

                while (brandingElementSnapshot.hasChild(Constants.Strings.BrandingTypes.DOMAIN_NAME + currentIndex)) {
                    if (brandingElementSnapshot.hasChild(Constants.Strings.BrandingTypes.DOMAIN_NAME + currentIndex)) {
                        if (regularData) {
                            getContents().set(currentIndex,brandingElementSnapshot.child(Constants.Strings.BrandingTypes.DOMAIN_NAME + currentIndex).getValue().toString());
                        } else {
                            data.add(currentIndex,brandingElementSnapshot.child(Constants.Strings.BrandingTypes.DOMAIN_NAME + currentIndex).getValue().toString());
                        }
                    }
                    currentIndex++;
                }
                break;
            case SOCIAL_MEDIA_NAME:
                while (brandingElementSnapshot.hasChild(Constants.Strings.BrandingTypes.SOCIAL_MEDIA_NAME + currentIndex)) {
                    if (brandingElementSnapshot.hasChild(Constants.Strings.BrandingTypes.SOCIAL_MEDIA_NAME + currentIndex)) {
                        if (regularData) {
                            getContents().set(currentIndex,brandingElementSnapshot.child(Constants.Strings.BrandingTypes.SOCIAL_MEDIA_NAME + currentIndex).getValue().toString());
                        } else {
                            data.add(currentIndex,brandingElementSnapshot.child(Constants.Strings.BrandingTypes.SOCIAL_MEDIA_NAME + currentIndex).getValue().toString());
                        }
                    }
                    currentIndex++;

                }
                break;
            case MISSION_STATEMENT:
                break;
            case TARGET_AUDIENCE:
                break;
            case STYLE_GUIDE:
                break;
            case LOGO:
                break;
            case PRODUCTS_SERVICES:
                break;
            case DEFAULT:
                break;
        }
    }
    @Override
    public Map<String, String> toMap() {
        final HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UIDs.UID, getUID());
        }
        if (!getType().equals(null)) {
            result.put(Constants.Strings.Fields.BRANDING_ELEMENT_TYPE,getType().toMapStyleString());
        }
        if (!getStatus().equals(null)) {
            result.put(Constants.Strings.Fields.BRANDING_ELEMENT_STATUS,getStatus().toMapStyleString());
        }
        if (getTeamUID() != null) {
            result.put(Constants.Strings.UIDs.TEAM_UID,getTeamUID());
        }
        saveContents(result,false);

        return result;
    }
    @Override
    public String description() {
        return null;
    }
    @Override
    public String getField(int index) {
        return null;
    }
    private void saveContents(HashMap<String, String> result,boolean regularData) {
        int currentIndex = 0;
        if (regularData) {
            while (!contents.get(currentIndex).equals("")) {
                switch (getType()) {
                    case DOMAIN_NAME:
                        result.put(Constants.Strings.BrandingTypes.DOMAIN_NAME+currentIndex,contents.get(currentIndex));
                        break;
                    case SOCIAL_MEDIA_NAME:
                        result.put(Constants.Strings.BrandingTypes.SOCIAL_MEDIA_NAME+currentIndex,contents.get(currentIndex));
                        break;
                    case MISSION_STATEMENT:
                        result.put(Constants.Strings.BrandingTypes.MISSION_STATEMENT+currentIndex,contents.get(currentIndex));
                        break;
                    case TARGET_AUDIENCE:
                        break;
                    case STYLE_GUIDE:
                        break;
                    case LOGO:
                        break;
                    case PRODUCTS_SERVICES:
                        break;
                    case DEFAULT:
                        break;
                }
                currentIndex++;

                if (currentIndex == contents.size()) {
                    return;
                }
            }
        } else {
            while (data.size() > currentIndex) {
                switch (getType()) {
                    case DOMAIN_NAME:
                        result.put(Constants.Strings.BrandingTypes.DOMAIN_NAME+currentIndex,data.get(currentIndex));
                        break;
                    case SOCIAL_MEDIA_NAME:
                        result.put(Constants.Strings.BrandingTypes.SOCIAL_MEDIA_NAME+currentIndex,data.get(currentIndex));
                        break;
                    case MISSION_STATEMENT:
                        result.put(Constants.Strings.BrandingTypes.MISSION_STATEMENT+currentIndex,data.get(currentIndex));
                        break;
                    case TARGET_AUDIENCE:
                        break;
                    case STYLE_GUIDE:
                        break;
                    case LOGO:
                        break;
                    case PRODUCTS_SERVICES:
                        break;
                    case DEFAULT:
                        break;
                }
                currentIndex++;

                if (currentIndex == data.size()) {
                    return;
                }
            }
        }

    }
//    Class Getters & Setters

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public String getHeader() {
        return header;
    }
    public void setHeader(final String header) {
        this.header = header;
    }
    public ElementType getType() {
        return type;
    }
    public void setType(final ElementType type) {
        this.type = type;
    }
    public ElementStatus getStatus() { return status; }
    public void setStatus(final ElementStatus status) {
        this.status = status;
    }
    public ArrayList<String> getContents() { return contents; }
//    Parcel Details
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BrandingElement createFromParcel(Parcel in) {
            return new BrandingElement(in);
        }
        public BrandingElement[] newArray(int size) {
            return new BrandingElement[size];
        }
    };
}