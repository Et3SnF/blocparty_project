package com.ngynstvn.android.blocparty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.Gson;

import org.jinstagram.entity.users.feed.MediaFeedData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import twitter4j.Status;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class BPUtils {

    // ----- Static Variables ----- //

    public static final String FILE_NAME = "log_states";
    public static final String CHECKED_STATE = "checked_states";
    public static final String SN_UPLOAD_STATES = "checked_states";
    public static final String FB_TP_ID = "FB_TP_ID";
    public static final String CURRENT_COLLECTION = "current_collection";

    // Upload Variables

    public static final String FB_UPLOAD = "fb_upload";
    public static final String TW_UPLOAD = "tw_upload";
    public static final String IG_UPLOAD = "ig_upload";
    public static final String FB_TP_ALB_ID = "fb_timeline_photos_album_id";

    // Facebook Variables

    public static final String FB_LOGIN = "isFBLoggedIn";
    public static final String FB_POSITION = "fbAdapterPosition";
    public static final String FB_OBJECT = "fb_object";
    public static final String FB_IMG_BASE_URL = "fbcdn.net";

    // Twitter Variables

    public static final String TW_OBJECT = "tw_object";
    public static final String TW_LOGIN = "isTWLoggedIn";
    public static final String TW_POSITION = "twAdapterPosition";
    public static final String TW_ACCESS_TOKEN = "twAccessToken";
    public static final String TW_ACCESS_TOKEN_SECRET = "twAccessTokenSecret";
    public static final String TW_CONSUMER_KEY = "twConsumerKey";
    public static final String TW_CONSUMER_SECRET = "twConsumerSecret";
    public static final String TW_POST_IMG_URL = "post_image_url";
    public static final String TW_IMG_BASE_URL = "http://pbs.twimg.com";

    // Instagram Variables

    public static final String IG_LOGIN = "isIGLoggedIn";
    public static final String IG_POSITION ="igAdapterPosition";
    public static final String IG_TOKEN = "ig_token";
    public static final String IG_AUTH_CODE = "ig_auth_code";
    public static final String IG_OBJECT = "ig_object";
    public static final String IG_IMG_BASE_URL = "https://scontent.cdninstagram.com/hphotos";

    // Table Related

    public static final String DB_NAME = "blocparty_db";
    public static final String POST_ITEM_TABLE = "post_item_table";
    public static final String COLLECTION_TABLE = "collection_table";
    public static final String USER_TABLE = "user_table";
    public static final String ROW_ID = "_id";
    public static final String USER_PROFILE_ID = "user_profile_id";
    public static final String COLLECTION_NAME = "collection_name";
    public static final String OP_PROFILE_ID = "op_profile_id";
    public static final String POST_ID = "post_id";
    public static final String PUBLISH_DATE = "publish_date";
    public static final String IS_POST_LIKED = "is_post_liked";
    public static final String USER_SOCIAL_NETWORK = "user_social_network";
    public static final String USER_FULL_NAME = "user_full_name";

    // Camera Related

    public static final String CAM_STATE = "cam_state";
    public static final String FLASH_STATE = "flash_state";
    public static final String IMAGE_URI = "image_uri";
    public static final String TEMP_PATH = Environment.getExternalStorageDirectory() + "/Pictures/bp_tmp/";
    public static final String IMG_FOLDER_PATH = Environment.getExternalStorageDirectory() + "/Blocparty/";

    // Pan Zoom Related

    public static final String POST_IMAGE_URL = "post_image_url";

    // View Related

    public static final String LAST_POST_ITEM_POSITION = "last_post_item_position";

    // ----- Static Methods ----- //

    // Logging methods

    public static String classTag(Class className) {
        return "(" + className.getSimpleName() + ") ";
    }

    public static void logMethod(String className) {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String message = stackTraceElements[3].getMethodName() + "() called | Thread: " + Thread.currentThread().getName();
        Log.e(className, message);
    }

    public static void logMethod(String className, String additional) {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String message = additional + "'s " + stackTraceElements[3].getMethodName() + "() called | Thread: " + Thread.currentThread().getName();
        Log.e(className, message);
    }

    // SharedPreferences

    public static SharedPreferences newSPrefInstance(String name) {
        return BlocpartyApplication.getSharedInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void putSPrefLoginValue(SharedPreferences sharedPreferences, String fileName, String posKey,
                                          int adapterPosition, String stateKeyName, boolean state) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(posKey, adapterPosition);
        editor.putBoolean(stateKeyName, state);
        editor.apply();
    }

    public static void putSPrefIntValue(SharedPreferences sharedPreferences, String fileName, String key, int value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putSPrefStrValue(SharedPreferences sharedPreferences, String fileName, String key, String value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putSPrefBooleanValue(SharedPreferences sharedPreferences, String fileName, String key, boolean value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putSPrefFloatValue(SharedPreferences sharedPreferences, String fileName, String key, float value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void putSPrefLongValue(SharedPreferences sharedPreferences, String fileName, String key, long value) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void putSPrefObject(SharedPreferences sharedPreferences, String fileName, String key, Object objectValue) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(objectValue);
        editor.putString(key, json);
        editor.apply();
    }

    public static <T> T getSPrefObject(SharedPreferences sharedPreferences, Class<T> tClass, String key) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);

        if(json == null) {
            return null;
        }

        return gson.fromJson(json, tClass);
    }

    public static void delSPrefValue(SharedPreferences sharedPreferences, String fileName, String key) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clearSPrefTable(SharedPreferences sharedPreferences, String fileName) {
        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static String dateConverter(Long rawDate) {
        DateFormat dateFormat = new SimpleDateFormat("h:mm a 'on' MM/dd/yy", Locale.ENGLISH);
        Date date = new Date(rawDate);
        return dateFormat.format(date);
    }

    // Time converter specifically for Facebook

    public static long dateConverter(String rfcDate) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(rfcDate.substring(0, 19) + ".000" + rfcDate.substring(19, rfcDate.length()));
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static File getImageDirectory() {
        return new File(Environment.getExternalStorageDirectory() + "/Blocparty/");
    }

    public static void displayDialog(Context context, String errorMessage) {

        AlertDialog.Builder alertDialog;

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(context);
        }
        else {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }

        alertDialog
                .setMessage(errorMessage)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void saveRawJSONResponse(String fileName, String rawResponse) {
        try {
            PrintWriter printWriter = new PrintWriter(Environment
                    .getExternalStorageDirectory().getAbsolutePath() + "/" + fileName);

            if(rawResponse != null) {
                printWriter.write(rawResponse);
            }

            printWriter.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Logging out social network information

    public static void logInstagramPostItemInfo(String classTag, MediaFeedData mediaFeedData) {
        Log.v(classTag, "User: " + mediaFeedData.getUser().getFullName());
        Log.v(classTag, "User ID: " + mediaFeedData.getUser().getId());
        Log.v(classTag, "Created time: " + mediaFeedData.getCreatedTime());
        Log.v(classTag, "Image Link: " + mediaFeedData.getImages().getStandardResolution().getImageUrl());
        Log.v(classTag, "Raw ID: " + mediaFeedData.getId());
        Log.v(classTag, "Inserted Post ID: " + Long.parseLong(mediaFeedData.getId().split("_")[0]));
        Log.v(classTag, "Post Caption: " + mediaFeedData.getCaption().getText());
        Log.v(classTag, "Post Created Time: " + mediaFeedData.getCaption().getCreatedTime());
    }

    public static void logTwitterPostItemInfo(String classTag, Status status) {
            Log.v(classTag, "User: " + status.getUser().getName());
            Log.v(classTag, "User ID: " + status.getUser().getId());
            Log.v(classTag, "Profile Pic: " + status.getUser().getBiggerProfileImageURL());
            Log.v(classTag, "Status: " + status.getText());
            Log.v(classTag, "Post ID: " + status.getId());
            Log.e(classTag, "Image URL: " + status.getMediaEntities()[0].getMediaURL());
    }

    public static Configuration getTwitterConfigBuilder(String consumerKey, String consumerSecret,
                                                        String token, String tokenSecret) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        configurationBuilder
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(tokenSecret);

        return configurationBuilder.build();
    }

    /**
     *
     * For Android 6.0 or higher only.
     *
     * As of Marshmallow, an additional permission request is required even if the permission
     * is already declared in AndroidManifest. A dialog will pop up to request access to a permission.
     *
     */

    public static void requestPermission(Activity activity, String permissionType) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    permissionType);

            if(permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permissionType}, 1);
            }
        }
    }
}
