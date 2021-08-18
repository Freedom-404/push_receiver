package com.artemkjv.push_receiver.notification;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

abstract class UserState {

    // Object to synchronize on to prevent concurrent modifications on syncValues and dependValues
    private static final Object LOCK = new Object();

    public static final String TAGS = "tags";
    public static final int DEVICE_TYPE_ANDROID = 1;
    public static final int DEVICE_TYPE_FIREOS = 2;
    public static final int DEVICE_TYPE_EMAIL = 11;
    public static final int DEVICE_TYPE_HUAWEI = 13;
    public static final int DEVICE_TYPE_SMS = 14;

    public static final int PUSH_STATUS_SUBSCRIBED = 1;
    static final int PUSH_STATUS_NO_PERMISSION = 0;
    static final int PUSH_STATUS_UNSUBSCRIBE = -2;
    static final int PUSH_STATUS_MISSING_ANDROID_SUPPORT_LIBRARY = -3;
    static final int PUSH_STATUS_MISSING_FIREBASE_FCM_LIBRARY = -4;
    static final int PUSH_STATUS_OUTDATED_ANDROID_SUPPORT_LIBRARY = -5;
    static final int PUSH_STATUS_INVALID_FCM_SENDER_ID = -6;
    static final int PUSH_STATUS_OUTDATED_GOOGLE_PLAY_SERVICES_APP = -7;
    static final int PUSH_STATUS_FIREBASE_FCM_INIT_ERROR = -8;
    static final int PUSH_STATUS_FIREBASE_FCM_ERROR_SERVICE_NOT_AVAILABLE = -9;
    // -10 is a server side detection only from FCM that the app is no longer installed
    static final int PUSH_STATUS_FIREBASE_FCM_ERROR_IOEXCEPTION = -11;
    static final int PUSH_STATUS_FIREBASE_FCM_ERROR_MISC_EXCEPTION = -12;
    // -13 to -24 reserved for other platforms
    public static final int PUSH_STATUS_HMS_TOKEN_TIMEOUT = -25;
    // Most likely missing "client/app_id".
    // Check that there is "apply plugin: 'com.huawei.agconnect'" in your app/build.gradle
    public static final int PUSH_STATUS_HMS_ARGUMENTS_INVALID = -26;
    public static final int PUSH_STATUS_HMS_API_EXCEPTION_OTHER = -27;
    public static final int PUSH_STATUS_MISSING_HMS_PUSHKIT_LIBRARY = -28;

    private static final String[] LOCATION_FIELDS = new String[] { "lat", "long", "loc_acc", "loc_type", "loc_bg", "loc_time_stamp" };
    private static final Set<String> LOCATION_FIELDS_SET = new HashSet<>(Arrays.asList(LOCATION_FIELDS));

}
