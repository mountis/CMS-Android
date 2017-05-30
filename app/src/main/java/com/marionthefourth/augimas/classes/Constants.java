// Marion Rucker
// APD2 - C201703
// Constants.java

package com.marionthefourth.augimas.classes;

public final class Constants {

    public static final class Strings {

        // Firebase UID Fields
        public static final String UID = "uid";
        public static final String TO_UID = "toUID";
        public static final String USER_UID = "userUID";
        public static final String FROM_UID = "fromUID";
        public static final String CHAT_UID = "chatUID";
        public static final String JOINT_UID = "jointUID";
        public static final String SENDER_UID = "senderUID";
        public static final String CONTACT_UID = "contactUID";
        public static final String MESSAGE_UID = "messageUID";
        public static final String TEAM_UID = "teamUID";
        public static final String MEMBER_UIDS = "memberUIDS";
        public static final String MEMBER_A_UID = "memberAUID";
        public static final String MEMBER_B_UID = "memberBUID";

        // Firebase Object Fields
        public static final String EMAIL = "email";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String NICKNAME = "nickname";
        public static final String FULL_NAME = "fullname";
        public static final String USERNAME_OR_EMAIL = "username_or_email";
        public static final String TEAM_ROLE = "teamRole";


        // Firebase
        public static final String USER = "user";
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
        public static final String CHANGE_PASSWORD_KEY = "changePassword";
        public static final String BUILD_VERSION_KEY = "buildInformation";

        //  Firebase Fields
        public static final String TEXT = "text";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String TITLE = "title";
        public static final String BRANDING_ELEMENT_HEADER = "header";
    }

    public static final class Ints {

        // Firebase Object Fields
        public static final int USERNAME            = 0;
        public static final int USERNAME_OR_EMAIL   = 0;
        public static final int PASSWORD            = 1;
        public static final int CONFIRM_PASSWORD    = 2;
        public static final int EMAIL               = 3;
        public static final int FULL_NAME           = 4;
        public static final int UID                 = 10;
        public static final int TEAM_UID            = 14;


        // Special Numbers
        public static final int MINIMUM_PASSWORD_COUNT = 6;
        public static final int GENERAL_PADDING_AMOUNT = 15;

        // Buttons
        public static final int SIGN_IN_BUTTON = 0;
        public static final int FORGOT_PASSWORD_BUTTON = 1;
        public static final int SIGN_UP_TEXT_BUTTON = 2;

        public static final int SIGN_UP_BUTTON = 0;
        public static final int SIGN_IN_TEXT_BUTTON = 1;

        // Firebase Objects
        public static final int FIREBASE_USER             = 0x0000001;
        public static final int FIREBASE_CONTENT_CONTACT  = 0x0000010;
        public static final int FIREBASE_CONTENT_CHAT     = 0x0000011;
        public static final int FIREBASE_CONTENT_MESSAGE  = 0x0000012;
        public static final int FIREBASE_CONTENT_REMINDER = 0x0000013;

        // Activities & Fragments
        public static final int SIGN_IN_ACTIVITY          = 0x1000010;
        public static final int SIGN_IN_FRAGMENT          = 0x1000011;
        public static final int SIGN_UP_ACTIVITY          = 0x1000020;
        public static final int SIGN_UP_FRAGMENT          = 0x1000021;
        public static final int HOME_ACTIVITY             = 0x2000110;
        public static final int HOME_FRAGMENT             = 0x2000120;
        public static final int CONTACTS_ACTIVITY         = 0x2000210;
        public static final int CONTACTS_FRAGMENT         = 0x2000220;
        public static final int CHATS_ACTIVITY            = 0x2000310;
        public static final int CHATS_FRAGMENT            = 0x2000320;
        public static final int CHAT_ACTIVITY             = 0x3000311;
        public static final int CHAT_FRAGMENT             = 0x3000321;

        // Other Views
        public static final int SNACKBAR                  = 0x5000001;
        public static final int TOAST                     = 0x5000002;
        public static final int PROGRESS_DIALOG           = 0x5000003;
    }

    public static final class Bools {
        public static final Boolean PROTOTYPE_MODE = true;
        public static final Boolean FEATURE_AVAILABLE = true;
    }

    public static final class FeaturesAvailable {
        public static final Boolean SIGN_UP = false;
        public static final Boolean SIGN_IN = false;
        public static final Boolean RECOVER_PASSWORD = false;
        public static final Boolean CHANGE_PASSWORD = false;
        public static final Boolean INVITE_TEAM_MEMBER = false;
        public static final Boolean UPDATE_TEAM_MEMBER_ROLE = false;
        public static final Boolean SIGN_OUT = false;
        public static final Boolean REQUEST_ADMIN_ROLE = false;
        public static final Boolean SEND_CHAT_MESSAGE = false;
        public static final Boolean UPDATE_TEAM_STATUS = false;
    }


}