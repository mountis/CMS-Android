package com.marionthefourth.augimas.classes.objects.content.branding_elements;

import com.marionthefourth.augimas.classes.constants.Constants;

import java.util.ArrayList;

import static com.marionthefourth.augimas.classes.constants.Constants.Ints.DEFAULT_ID;

public final class Branding {

    public enum TLD {
        COM,NET,ME,ORG,US,INFO,LA,ASIA,BIZ,TV,UK,WS,NU,NYC,OKINAWA,ONL,NETWORK,NINJA,DEFAULT;

        @Override
        public String toString() {
            return "." + super.toString().toLowerCase();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this) {
                    case COM:               return Constants.Ints.TLDs.IDs.COM;
                    case NET:               return Constants.Ints.TLDs.IDs.NET;
                    case ME:                return Constants.Ints.TLDs.IDs.ME;
                    case ORG:               return Constants.Ints.TLDs.IDs.ORG;
                    case US:                return Constants.Ints.TLDs.IDs.US;
                    case INFO:              return Constants.Ints.TLDs.IDs.INFO;
                    case LA:                return Constants.Ints.TLDs.IDs.LA;
                    case ASIA:              return Constants.Ints.TLDs.IDs.ASIA;
                    case BIZ:               return Constants.Ints.TLDs.IDs.BIZ;
                    case TV:                return Constants.Ints.TLDs.IDs.TV;
                    case UK:                return Constants.Ints.TLDs.IDs.UK;
                    case WS:                return Constants.Ints.TLDs.IDs.WS;
                    case NU:                return Constants.Ints.TLDs.IDs.NU;
                    case NYC:               return Constants.Ints.TLDs.IDs.NYC;
                    case OKINAWA:           return Constants.Ints.TLDs.IDs.OKINAWA;
                    case ONL:               return Constants.Ints.TLDs.IDs.ONL;
                    case NETWORK:           return Constants.Ints.TLDs.IDs.NETWORK;
                    case NINJA:             return Constants.Ints.TLDs.IDs.NINJA;
                    default:                return DEFAULT_ID;
                }
            } else {
                switch (this) {
                    case COM:               return Constants.Ints.TLDs.Indices.COM;
                    case NET:               return Constants.Ints.TLDs.Indices.NET;
                    case ME:                return Constants.Ints.TLDs.Indices.ME;
                    case ORG:               return Constants.Ints.TLDs.Indices.ORG;
                    case US:                return Constants.Ints.TLDs.Indices.US;
                    case INFO:              return Constants.Ints.TLDs.Indices.INFO;
                    case LA:                return Constants.Ints.TLDs.Indices.LA;
                    case ASIA:              return Constants.Ints.TLDs.Indices.ASIA;
                    case BIZ:               return Constants.Ints.TLDs.Indices.BIZ;
                    case TV:                return Constants.Ints.TLDs.Indices.TV;
                    case UK:                return Constants.Ints.TLDs.Indices.UK;
                    case WS:                return Constants.Ints.TLDs.Indices.WS;
                    case NU:                return Constants.Ints.TLDs.Indices.NU;
                    case NYC:               return Constants.Ints.TLDs.Indices.NYC;
                    case OKINAWA:           return Constants.Ints.TLDs.Indices.OKINAWA;
                    case ONL:               return Constants.Ints.TLDs.Indices.ONL;
                    case NETWORK:           return Constants.Ints.TLDs.Indices.NETWORK;
                    case NINJA:             return Constants.Ints.TLDs.Indices.NINJA;
                    default:                return DEFAULT_ID;
                }
            }

        }

        public static TLD getTLD(String tld) {
            for (int i = 0; i < getNumberOfTLDs(); i++) {
                if (tld.equals(getTLD(i).toString()) || tld.equals(getTLD(i).toMapStyleString())) {
                    return getTLD(i);
                }
            }

            return DEFAULT;
        }

        public static TLD getTLD(int tld) {
            switch (tld) {
                case Constants.Ints.TLDs.IDs.COM:
                case Constants.Ints.TLDs.Indices.COM:
                    return COM;
                case Constants.Ints.TLDs.IDs.NET:
                case Constants.Ints.TLDs.Indices.NET:
                    return NET;
                case Constants.Ints.TLDs.IDs.ME:
                case Constants.Ints.TLDs.Indices.ME:
                    return ME;
                case Constants.Ints.TLDs.IDs.ORG:
                case Constants.Ints.TLDs.Indices.ORG:
                    return ORG;
                case Constants.Ints.TLDs.IDs.US:
                case Constants.Ints.TLDs.Indices.US:
                    return US;
                case Constants.Ints.TLDs.IDs.INFO:
                case Constants.Ints.TLDs.Indices.INFO:
                    return INFO;
                case Constants.Ints.TLDs.IDs.LA:
                case Constants.Ints.TLDs.Indices.LA:
                    return LA;
                case Constants.Ints.TLDs.IDs.BIZ:
                case Constants.Ints.TLDs.Indices.BIZ:
                    return BIZ;
                case Constants.Ints.TLDs.IDs.TV:
                case Constants.Ints.TLDs.Indices.TV:
                    return TV;
                case Constants.Ints.TLDs.IDs.UK:
                case Constants.Ints.TLDs.Indices.UK:
                    return UK;
                case Constants.Ints.TLDs.IDs.WS:
                case Constants.Ints.TLDs.Indices.WS:
                    return WS;
                case Constants.Ints.TLDs.IDs.NU:
                case Constants.Ints.TLDs.Indices.NU:
                    return NU;
                case Constants.Ints.TLDs.IDs.NYC:
                case Constants.Ints.TLDs.Indices.NYC:
                    return NYC;
                case Constants.Ints.TLDs.IDs.OKINAWA:
                case Constants.Ints.TLDs.Indices.OKINAWA:
                    return OKINAWA;
                case Constants.Ints.TLDs.IDs.ONL:
                case Constants.Ints.TLDs.Indices.ONL:
                    return ONL;
                case Constants.Ints.TLDs.IDs.NETWORK:
                case Constants.Ints.TLDs.Indices.NETWORK:
                    return NETWORK;
                case Constants.Ints.TLDs.IDs.NINJA:
                case Constants.Ints.TLDs.Indices.NINJA:
                    return NINJA;
                default:
                    return DEFAULT;

            }

        }

        public static ArrayList<TLD> getAllTLDs() {
            final ArrayList<TLD> tlds = new ArrayList<>();
            for (int i = 0; i < getNumberOfTLDs(); i++) {
                tlds.add(getTLD(i));
            }

            return tlds;
        }

        public static int getNumberOfTLDs() {
            return 18;
        }
    }

    public enum Service {
        FACEBOOK,TWITTER,INSTAGRAM,REDDIT,GAB,LINKEDIN,PINTEREST,SNAPCHAT,TUMBLR,VIBER,WECHAT,WEIBO,YOUTUBE,DEFAULT;

        @Override
        public String toString() {
            return super.toString().substring(0,1) + super.toString().substring(1).toLowerCase();
        }

        public String toMapStyleString() {
            return String.valueOf(this.toInt(true));
        }

        public int toInt(boolean mapStyle) {
            if (mapStyle) {
                switch (this) {
                    case FACEBOOK:      return Constants.Ints.Services.IDs.FACEBOOK;
                    case TWITTER:       return Constants.Ints.Services.IDs.TWITTER;
                    case INSTAGRAM:     return Constants.Ints.Services.IDs.INSTAGRAM;
                    case REDDIT:        return Constants.Ints.Services.IDs.REDDIT;
                    case GAB:           return Constants.Ints.Services.IDs.GAB;
                    case LINKEDIN:      return Constants.Ints.Services.IDs.LINKEDIN;
                    case PINTEREST:     return Constants.Ints.Services.IDs.PINTEREST;
                    case SNAPCHAT:      return Constants.Ints.Services.IDs.SNAPCHAT;
                    case TUMBLR:        return Constants.Ints.Services.IDs.TUMBLR;
                    case VIBER:         return Constants.Ints.Services.IDs.VIBER;
                    case WECHAT:        return Constants.Ints.Services.IDs.WECHAT;
                    case WEIBO:         return Constants.Ints.Services.IDs.WEIBO;
                    case YOUTUBE:       return Constants.Ints.Services.IDs.YOUTUBE;
                    case DEFAULT:
                        break;
                    default:            return DEFAULT_ID;
                }
            } else {
                switch (this) {
                    case FACEBOOK:      return Constants.Ints.Services.Indices.FACEBOOK;
                    case TWITTER:       return Constants.Ints.Services.Indices.TWITTER;
                    case INSTAGRAM:     return Constants.Ints.Services.Indices.INSTAGRAM;
                    case REDDIT:        return Constants.Ints.Services.Indices.REDDIT;
                    case GAB:           return Constants.Ints.Services.Indices.GAB;
                    case LINKEDIN:      return Constants.Ints.Services.Indices.LINKEDIN;
                    case PINTEREST:     return Constants.Ints.Services.Indices.PINTEREST;
                    case SNAPCHAT:      return Constants.Ints.Services.Indices.SNAPCHAT;
                    case TUMBLR:        return Constants.Ints.Services.Indices.TUMBLR;
                    case VIBER:         return Constants.Ints.Services.Indices.VIBER;
                    case WECHAT:        return Constants.Ints.Services.Indices.WECHAT;
                    case WEIBO:         return Constants.Ints.Services.Indices.WEIBO;
                    case YOUTUBE:       return Constants.Ints.Services.Indices.YOUTUBE;
                    default:            return DEFAULT_ID;
                }
            }

            return DEFAULT_ID;
        }

        public static Service getService(int service) {
            switch (service) {
                case Constants.Ints.Services.Indices.FACEBOOK: return FACEBOOK;
                case Constants.Ints.Services.Indices.TWITTER: return TWITTER;
                case Constants.Ints.Services.Indices.INSTAGRAM: return INSTAGRAM;
                case Constants.Ints.Services.Indices.REDDIT: return REDDIT;
                case Constants.Ints.Services.Indices.GAB: return GAB;
                case Constants.Ints.Services.Indices.LINKEDIN: return LINKEDIN;
                case Constants.Ints.Services.Indices.PINTEREST: return PINTEREST;
                case Constants.Ints.Services.Indices.SNAPCHAT: return SNAPCHAT;
                case Constants.Ints.Services.Indices.TUMBLR: return TUMBLR;
                case Constants.Ints.Services.Indices.VIBER: return VIBER;
                case Constants.Ints.Services.Indices.WECHAT: return WECHAT;
                case Constants.Ints.Services.Indices.WEIBO: return WEIBO;
                case Constants.Ints.Services.Indices.YOUTUBE: return YOUTUBE;
                default: return DEFAULT;
            }
        }

        public static Service getService(String service) {
            for (int i = 0; i < getNumberOfServices(); i++) {
                if (service.equals(getService(i)) || service.equals(getService(i).toMapStyleString())) {
                    return getService(i);
                }
            }

            return DEFAULT;
        }

        public static ArrayList<Service> getAllServices() {
            final ArrayList<Service> services = new ArrayList<>();
            for (int i = 0; i < getNumberOfServices(); i++) {
                services.add(getService(i));
            }

            return services;
        }

        public static int getNumberOfServices() {
            return 13;
        }

    }
}