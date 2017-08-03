// Marion Rucker
// APD2 - C201703
// Constants.java

package com.marionthefourth.augimas.classes.constants;

public final class Constants {

    public static final class Strings {

        public static final CharSequence QUESTIONNAIRE_KEY = "questionnaire";

        public static final class UIDs {
            public static final String UID = "uid";
            public static final String CHAT_UID = "chatUID";
            public static final String SENDER_UID = "senderUID";
            public static final String OBJECT_UID = "objectUID";
            public static final String SUBJECT_UID = "subjectUID";
            public static final String MESSAGE_UID = "messageUID";
            public static final String TEAM_UID = "teamUID";
            public static final String USER_UID = "userUID";
            public static final String TEAM_UIDS = "teamUIDs";
            public static final String MEMBER_UIDS = "memberUIDS";
            public static final String RECEIVER_UID = "receiverUID";
            public static final String CHANNEL_UID = "channelUID";
            public static final String BRANDING_ELEMENT_UID = "brandingElementUID";
        }

        // Firebase Object Fields
        public static final class Fields {
            public static final String EMAIL = "email";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String NICKNAME = "nickname";
            public static final String FULL_NAME = "fullname";
            public static final String USERNAME_OR_EMAIL = "username_or_email";
            public static final String TEAM_NAME = "teamName";
            public static final String BRANDING_ELEMENT_TYPE = "elementType";
            public static final String BRANDING_ELEMENT_STATUS = "elementStatus";
            public static final String OBJECT_TYPE = "objectType";
            public static final String SUBJECT_TYPE = "subjectType";
            public static final String VERB_TYPE = "verbType";
            public static final String ENTITY_ROLE = "role";
            public static final String ENTITY_STATUS = "status";
            public static final String ENTITY_TYPE = "type";
            public static final String COMMUNICATION_TYPE  = "type";
            public static final String TEXT = "text";
            public static final String DATE = "date";
            public static final String TIME = "time";
            public static final String TITLE = "title";
            public static final String BRANDING_ELEMENT_HEADER = "header";
            public static final String CONTENTS = "contents";
            public static final String FRAGMENT = "fragment";
        }

        public static final class BrandingTypes {
            public static final String DOMAIN_NAME = "domainName";
            public static final String SOCIAL_MEDIA_NAME = "socialMediaName";
            public static final String MISSION_STATEMENT = "missionStatement";
        }

        public static final class Questionnaire {
            public static final class Questions {
                public static final String QUESTION_01 = "How satisfied overall were you with the UI/Layout of this screen?";
                public static final String QUESTION_02 = "Do you have any suggestions for the UI/Layout of the app?";
                public static final String QUESTION_03 = "Was there anything that stood out to you that you liked or wanted to see more of throughout the app?";
                public static final String QUESTION_04 = "Did you have any issues with this screen? If so please list it below.";
                public static final String QUESTION_05 = "How satisfied overall were you with your User Experience on this screen?";
                public static final String QUESTION_06 = "Are there any additional features you want available on this screen?";

            }

            public static final class Screens {
                public static final String SCREEN_01 = "Sign In";
                public static final String SCREEN_02 = "Sign Up";
                public static final String SCREEN_03 = "Home";
                public static final String SCREEN_04 = "Dashboard";
                public static final String SCREEN_05 = "Chats";
                public static final String SCREEN_06 = "Notifications";
                public static final String SCREEN_07 = "Settings";
                public static final String SCREEN_08 = "Team Management";
                public static final String SCREEN_09 = "Branding Element";
            }
        }

        // Firebase
        public static final String USER = "user";
        public static final String BRANDING_ELEMENT = "brandingElement";
        public static final String TEAM = "team";
        public static final String SORT = "sort";
        public static final String DESCRIPTION = "description";

        // Firebase Detail Modes
        public static final String DETAIL_MODE = "detailMode";
        public static final String DETAIL_MODE_UPDATE = "update";
        public static final String DETAIL_MODE_CREATE = "create";
        public static final String DETAIL_MODE_LINK_ACCOUNT = "link";

        // Home Sections
        public static final String CHATS = "Chats";
        public static final String CONTACTS = "Contacts";
        public static final String REMINDERS = "Reminders";
        /* Unused */
        public static final String NOTIFICATIONS = "Notifications";

        // Preference Items
        public static final String BUILD_VERSION = "0.1";
        public static final String SIGN_OUT = "Sign Out";
        public static final String SWITCH_ACCOUNTS = "Switch Accounts";
        public static final String MANAGE_TEAM = "Manage Team";

        public static final String CHANGE_PASSWORD = "Change Password";

        // Preference Key Items
        public static final String SIGN_OUT_KEY = "signout";
        public static final String MANAGE_TEAM_KEY = "manageTeam";
        public static final String UPDATE_ACCOUNT_INFO_KEY = "updateAccountInfo";
        public static final String BUILD_VERSION_KEY = "buildInformation";

        public static final String ADMIN_ACCESS_CODE = "N3V3R-0N3-0F-7H3M";
        public static final String ADMIN_REQUEST_CODE = "1-0F-U$";
        public static final String ADMIN_TEAM_NAME = "Augimas";
        public static final String ADMIN_TEAM_USERNAME = "augimas";

        public static final String NO_VALUE = "0x81BAC";
        public static final String YES_VALUE = "0xA4BEF";

        public static final class Fragments {
            public static final String SETTINGS = "Settings";
            public static final String SIGN_IN = "Sign In";
            public static final String SIGN_UP = "Sign Up";
            public static final String HOME = "Home";
            public static final String DASHBOARD = "Dashboard";
            public static final String CHAT = "Chat";
            public static final String CHAT_LIST = "Chat List";
            public static final String BRANDING_ELEMENT = "Branding Element";
            public static final String BRANDING_ELEMENTS = "Branding Elements";
            public static final String NOTIFICATIONS = "Notifications";
            public static final String TEAM_MANAGEMENT = "Team Management";
            public static final String TEAMS = "Teams";
            public static final String QUESTIONNAIRE = "Questionnaire";

        }

    }

    public static final class Ints {
        public static final class UIDs {
            public static final int UID                 = 10;
            public static final int TEAM_UID            = 14;
            public static final int CHAT_UID            = 16;
        }

        public static final class EntityTypes {
            public static final class Indices {
                public static final int US = 0;
                public static final int THEM = 1;
            }
            public static final class IDs {
                public static final int US = 0x3A2B7;
                public static final int THEM = 0x2F1A6;
            }
        }
        public static final class EntityRoles {
            public static final class Indices {
                public static final int OWNER            = 0;
                public static final int ADMIN            = 1;
                public static final int EDITOR           = 2;
                public static final int CHATTER          = 3;
                public static final int VIEWER           = 4;
                public static final int NONE             = 5;
            }
            public static final class IDs {
                public static final int OWNER = 0x3B2F8;
                public static final int ADMIN = 0x2A1E7;
                public static final int EDITOR = 0x1F0D6;
                public static final int CHATTER = 0x9E9C5;
                public static final int VIEWER = 0x8D8B4;
                public static final int NONE = 0x7C7A3;
            }
        }
        public static final class EntityStatii {
            public static final class Indices {
                public static final int APPROVED = 0;
                public static final int AWAITING = 1;
                public static final int NONE = 2;
                public static final int BLOCKED = 3;
            }
            public static final class IDs {
                public static final int APPROVED    = 0x7B6F2;
                public static final int AWAITING    = 0x6A5E1;
                public static final int NONE        = 0x9D7E2;
                public static final int BLOCKED     = 0x5F4D9;
            }
        }
        public static final class CommunicationTypes {
            public static final class Indices {
                public static final int A = 0;
                public static final int B = 1;
                public static final int C = 2;
            }
            public static final class IDs {
                public static final int A = 0x518FA;
                public static final int B = 0x697EB;
                public static final int C = 0x786DC;
            }

        }
        public static final class CommunicationTines {
            public static final class Indices {
                public static final int ONE     = 0;
                public static final int TWO     = 1;
                public static final int THREE   = 2;
            }
            public static final class IDs {
                public static final int ONE     = 0x871BA;
                public static final int TWO     = 0x680BA;
                public static final int THREE   = 0x599BA;
            }
        }
        public static final class BrandingElementStatii {
            public static final class Indices {
                public static final int APPROVED    = 0;
                public static final int AWAITING    = 1;
                public static final int INCOMPLETE  = 2;
                public static final int NONE        = 3;
            }
            public static final class IDs {
                public static final int APPROVED    = 0x7B6F2;
                public static final int AWAITING    = 0x6A5E1;
                public static final int INCOMPLETE  = 0x9D7A8;
                public static final int NONE        = 0x9D7E2;
            }
        }
        public static final class NotificationTypes {
            public static final class Verbs {
                public static final class IDs{
                    public static final int ADD                 = 0x941BAF84;
                    public static final int APPROVE             = 0x853ACF93;
                    public static final int AWAIT               = 0x765FEF02;
                    public static final int CREATE              = 0x123ABC91;
                    public static final int DISAPPROVE          = 0x677EBF11;
                    public static final int INVITE              = 0x589DDF24;
                    public static final int JOIN                = 0x126FCF60;
                    public static final int LEFT                = 0x23F6CF60;
                    public static final int RECEIVE             = 0x490CFF33;
                    public static final int REQUEST             = 0x302BAF42;
                    public static final int REQUEST_ACCESS      = 0x464FCF61;
                    public static final int REQUEST_APPROVAL    = 0x404FFF84;
                    public static final int REQUEST_JOIN        = 0x194BEF93;
                    public static final int UPDATE              = 0x214ABF51;

                }
                public static final class Indices {
                    public static final int ADD                 =  0;
                    public static final int APPROVE             =  1;
                    public static final int AWAIT               =  2;
                    public static final int DISAPPROVE          =  3;
                    public static final int CREATE              =  4;
                    public static final int INVITE              =  5;
                    public static final int JOIN                =  6;
                    public static final int LEFT                =  7;
                    public static final int RECEIVE             =  8;
                    public static final int REQUEST             =  9;
                    public static final int REQUEST_ACCESS      = 10;
                    public static final int REQUEST_APPROVAL    = 11;
                    public static final int REQUEST_JOIN        = 12;
                    public static final int UPDATE              = 13;
                }
            }
            public static final class Subjects {
                public static final class IDs{
                    public static final int MEMBER         = 0x513BAF84;
                    public static final int TEAM           = 0x724ACF93;
                }
                public static final class Indices {
                    public static final int MEMBER          = 0;
                    public static final int TEAM            = 1;
                }
            }
            public static final class Objects {
                public static final class IDs{
                    public static final int BRANDING_ELEMENT    = 0x51AB5F84;
                    public static final int CHAT                = 0x63BBA783;
                    public static final int MEMBER              = 0x73BBF713;
                    public static final int TEAM                = 0x263B6F8A;
                }
                public static final class Indices {
                    public static final int BRANDING_ELEMENT    = 0;
                    public static final int CHAT                = 1;
                    public static final int MEMBER              = 2;
                    public static final int TEAM                = 3;
                }
            }

        }

        public static final int DEFAULT_ID          = 0x4E3C8;

        public static final class Fields {
            public static final int USERNAME            = 0;
            public static final int USERNAME_OR_EMAIL   = 0;
            public static final int PASSWORD            = 1;
            public static final int CONFIRM_PASSWORD    = 2;
            public static final int EMAIL               = 3;
            public static final int FULL_NAME           = 4;
        }

        public static final int ENTITY_ROLE         = 18;
        public static final int ENTITY_STATUS       = 20;
        public static final int ENTITY_TYPE         = 22;

        // Special Numbers
        public static final class SignificantNumbers {
            public static final int MINIMUM_PASSWORD_COUNT = 6;
            public static final int GENERAL_PADDING_AMOUNT = 24;
        }

        public static final class Views {
            public static final class Buttons {
                public static final class Indices {
                    public static final int SIGN_IN_BUTTON          = 0;
                    public static final int FORGOT_PASSWORD_BUTTON  = 1;
                    public static final int SIGN_UP_TEXT_BUTTON     = 2;
                    public static final int SIGN_UP_BUTTON          = 0;
                    public static final int SIGN_IN_TEXT_BUTTON     = 1;
                }
            }
            public static final class Widgets {
                public static final class IDs {
                    public static final int SNACKBAR                  = 0x5000001;
                    public static final int TOAST                     = 0x5000002;
                    public static final int PROGRESS_DIALOG           = 0x5000003;
                }
                public static final class Indices {

                }
            }
        }

        // Firebase Objects
        public static final int FIREBASE_USER             = 0x0000001;
        public static final int FIREBASE_CONTENT_CONTACT  = 0x0000010;
        public static final int FIREBASE_CONTENT_CHAT     = 0x0000011;
        public static final int FIREBASE_CONTENT_MESSAGE  = 0x0000012;
        public static final int FIREBASE_CONTENT_REMINDER = 0x0000013;

        // Activities & Fragments

        public static final class Activities {
            public static final int SIGN_IN_ACTIVITY          = 0x1000010;
            public static final int SIGN_UP_ACTIVITY          = 0x1000020;
            public static final int HOME_ACTIVITY             = 0x2000110;
            public static final int CONTACTS_ACTIVITY         = 0x2000210;
            public static final int CHATS_ACTIVITY            = 0x2000310;
            public static final int CHAT_ACTIVITY             = 0x3000311;
        }

        public static final class Fragments {
            public static final int SIGN_IN_FRAGMENT          = 0x1000011;
            public static final int SIGN_UP_FRAGMENT          = 0x1000021;
            public static final int HOME_FRAGMENT             = 0x2000120;
            public static final int CONTACTS_FRAGMENT         = 0x2000220;
            public static final int CHATS_FRAGMENT            = 0x2000320;
            public static final int CHAT_FRAGMENT             = 0x3000321;
        }

        // Other Views

        public static final class TLDs {
            public static final class Indices {
                public static final int COM     = 0;
                public static final int NET     = 1;
                public static final int ME      = 2;
                public static final int ORG     = 3;
                public static final int US      = 4;
                public static final int INFO    = 5;
                public static final int LA      = 6;
                public static final int ASIA    = 7;
                public static final int BIZ     = 8;
                public static final int TV      = 9;
                public static final int UK      = 10;
                public static final int WS      = 11;
                public static final int NU      = 12;
                public static final int NYC     = 13;
                public static final int OKINAWA = 14;
                public static final int ONL     = 15;
                public static final int NETWORK = 16;
                public static final int NINJA   = 17;
            }
            public static final class IDs {
                public static final int COM = 0x0005312B;
                public static final int NET = 0x0004430A;
                public static final int ME = 0x0003558B;
                public static final int ORG = 0x0002676C;
                public static final int US = 0x0001794B;
                public static final int INFO = 0x0000802E;
                public static final int LA = 0x0009929D;
                public static final int ASIA = 0x0008047F;
                public static final int BIZ = 0x0007166A;
                public static final int TV = 0x0006285C;
                public static final int UK = 0x0005314B;
                public static final int WS = 0x0004433E;
                public static final int NU = 0x0003552F;
                public static final int NYC = 0x0002671B;
                public static final int OKINAWA = 0x00017944;
                public static final int ONL = 0x00008071;
                public static final int NETWORK = 0x00099296;
                public static final int NINJA = 0x00080458;
            }
        }

        public static final class Services {
            public static final class Indices {
                public static final int FACEBOOK    = 0;
                public static final int TWITTER     = 1;
                public static final int INSTAGRAM   = 2;
                public static final int REDDIT      = 3;
                public static final int GAB         = 4;
                public static final int LINKEDIN    = 5;
                public static final int PINTEREST   = 6;
                public static final int SNAPCHAT    = 7;
                public static final int TUMBLR      = 8;
                public static final int VIBER       = 9;
                public static final int WECHAT      = 10;
                public static final int WEIBO       = 11;
                public static final int YOUTUBE     = 12;
            }
            public static final class IDs {
                public static final int FACEBOOK    = 0x0A1941FD;
                public static final int TWITTER     = 0x0B2860AB;
                public static final int INSTAGRAM   = 0x0C37895A;
                public static final int REDDIT      = 0x0D46057F;
                public static final int GAB         = 0x0140124C;
                public static final int LINKEDIN    = 0x05105AAB;
                public static final int PINTEREST   = 0x013591CA;
                public static final int SNAPCHAT    = 0x013A5013;
                public static final int TUMBLR      = 0x20395BCA;
                public static final int VIBER       = 0x1059129F;
                public static final int WECHAT      = 0xAB1529CA;
                public static final int WEIBO       = 0xABC959EF;
                public static final int YOUTUBE     = 0XBCDEF941;
            }
        }
        public static final class BrandingElementTypes {
            public static final class Indices {
                public static final int DOMAIN_NAME         = 0;
                public static final int SOCIAL_MEDIA_NAME   = 1;
                public static final int MISSION_STATEMENT   = 2;
                public static final int TARGET_AUDIENCE     = 3;
                public static final int STYLE_GUIDE         = 4;
                public static final int LOGO                = 5;
                public static final int PRODUCTS_SERVICES   = 6;
            }
            public static final class IDs {
                public static final int DOMAIN_NAME = 0x827BABE;
                public static final int SOCIAL_MEDIA_NAME = 0x736ABBC;
                public static final int MISSION_STATEMENT = 0x644FCBA;
                public static final int TARGET_AUDIENCE = 0x552EDBF;
                public static final int STYLE_GUIDE = 0x460DEBD;
                public static final int LOGO = 0x378CFBB;
                public static final int PRODUCTS_SERVICES = 0x289BABA;
            }
        }

    }

    public static final class Bools {
        public static final boolean PROTOTYPE_MODE = false;

        public static final class FeaturesAvailable {
            public static final boolean SIGN_UP = true;
            public static final boolean SIGN_IN = true;
            public static final boolean RECOVER_PASSWORD = true;
            public static final boolean CHANGE_PASSWORD = true;
            public static final boolean INVITE_TEAM_MEMBER = true;
            public static final boolean UPDATE_TEAM_MEMBER_ROLE = true;
            public static final boolean DISPLAY_DASHBOARD = true;
            public static final boolean SIGN_OUT = true;
            public static final boolean REQUEST_ADMIN_ROLE = true;
            public static final boolean SEND_CHAT_MESSAGE = true;
            public static final boolean UPDATE_TEAM_STATUS = true;
            public static final boolean DISPLAY_CHATS = true;
            public static final boolean DISPLAY_NOTIFICATIONS = true;
            public static final boolean DISPLAY_QUESTIONNAIRE = false;
            public static final boolean DISPLAY_SETTINGS = true;
        }
    }
}