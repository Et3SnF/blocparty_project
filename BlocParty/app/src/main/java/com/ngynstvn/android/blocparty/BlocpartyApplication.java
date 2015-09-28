package com.ngynstvn.android.blocparty;

import android.app.Application;
import android.util.Log;

import com.facebook.login.DefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.oauth.InstagramService;

/**
 * Created by Ngynstvn on 9/22/15.
 */

public class BlocpartyApplication extends Application {

    private static final String TAG = "(" + BlocpartyApplication.class.getSimpleName() + "): ";

    private static final String APP_ID = "449190111920808";
    private static final String APP_NAMESPACE = "partybloc";

    private InstagramService instagramService;

    private static BlocpartyApplication sharedInstance;

    public static BlocpartyApplication getSharedInstance() {
        return sharedInstance;
    }

    public static InstagramService getSharedInstagramService() {
        return BlocpartyApplication.getSharedInstance().getInstagramService();
    }

    private InstagramService getInstagramService() {
        return instagramService;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate() called");
        super.onCreate();
        sharedInstance = this;

        Permission[] permissions = new Permission[] {
                Permission.USER_PHOTOS,
                Permission.EMAIL,
                Permission.PUBLISH_ACTION
        };

        // Facebook

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(APP_ID)
                .setNamespace(APP_NAMESPACE)
                .setPermissions(permissions)
                .setDefaultAudience(DefaultAudience.FRIENDS)
                .build();

        SimpleFacebook.setConfiguration(configuration);

        // Instagram

        instagramService = new InstagramAuthService()
                .apiKey(getString(R.string.igc))
                .apiSecret(getString(R.string.igcs))
                .callback(getString(R.string.igcu))
                .scope("basic relationships likes")
                .build();
    }
}
