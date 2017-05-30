package com.marionthefourth.augimas.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import com.marionthefourth.augimas.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class BrandingElement extends FirebaseContent {

    private String header = "Domain Name";
    private ElementType type = ElementType.DEFAULT;
    private ElementStatus status = ElementStatus.DEFAULT;

    public BrandingElement() {}

    public BrandingElement(ElementType type) {
        this();
        setType(type);
        setHeader(getElementTypeString(type));
    }

    public BrandingElement(ElementType type,ElementStatus status) {
        this(type);
        setStatus(status);
    }

    public BrandingElement(Parcel in) {
        BrandingElement brandingElement = (BrandingElement)in.readSerializable();
        setType(brandingElement.getType());
        setHeader(getElementTypeString(brandingElement.getType()));
    }

    public ElementStatus getStatus() {
        return status;
    }

    public void setStatus(ElementStatus status) {
        this.status = status;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public static String getElementTypeString(ElementType type) {
        switch (type) {
            case DOMAIN_NAME:
                return "Domain Name";
            case SOCIAL_MEDIA_NAME:
                return "Social Media Name";
            case MISSION_STATEMENT:
                return "Mission Statement";
            default:
                return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getElementStatusImage(Context context, ElementStatus status) {
        switch (status) {
            case GOOD:
                return context.getDrawable(R.drawable.ic_check_circle);
            case BAD:
                return context.getDrawable(R.drawable.ic_highlight_off);
            case CHECK:
                return context.getDrawable(R.drawable.ic_help_outline);
            default:
                return context.getDrawable(R.drawable.ic_group_work);
        }
    }

    public enum ElementType {
        DOMAIN_NAME,SOCIAL_MEDIA_NAME,MISSION_STATEMENT,DEFAULT
    }

    public enum ElementStatus {
        GOOD,BAD,CHECK,NOT_STARTED,DEFAULT
    }



    private ArrayList<String> contents = new ArrayList<>();


    @Override
    public String getField(int index) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BrandingElement createFromParcel(Parcel in) {
            return new BrandingElement(in);
        }

        public BrandingElement[] newArray(int size) {
            return new BrandingElement[size];
        }
    };

    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        if (getUID() != null) {
            result.put(Constants.Strings.UID, getUID());
        }

        return result;
    }

    @Override
    public String description() {
        return null;
    }


}
