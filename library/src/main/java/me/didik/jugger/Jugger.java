package me.didik.jugger;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.lang.reflect.Field;

/**
 * Created by didik on 9/11/17.
 * App Updater
 */

public class Jugger {
    private static final String
            IS_ACTIVE = "is_active",
            APP_MESSAGE = "app_message",
            UPDATE_MESSAGE = "update_message",
            IS_MANDATORY = "is_mandatory",
            VERSION_CODE = "version_code";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private AppCompatActivity activity;
    private String positiveTextColor = "#00796B", negativeTextColor = "#00796B";

    private Jugger(AppCompatActivity activity) {
        this.activity = activity;
    }

    public static Jugger with(@NonNull AppCompatActivity activity) {
        return new Jugger(activity);
    }

    public Jugger setPositiveTextColor(@NonNull String color) {
        positiveTextColor = color;
        return this;
    }

    public Jugger setNegativeTextColor(@NonNull String color) {
        negativeTextColor = color;
        return this;
    }

    public void check() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(isInDebugFlavour())
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchData();
    }

    private void fetchData() {
        long cacheExpiration = 3600; // 1 hour in seconds.

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();

                            boolean isActive = mFirebaseRemoteConfig.getBoolean(IS_ACTIVE);
                            long versionCode = mFirebaseRemoteConfig.getLong(VERSION_CODE);
                            Log.d("Jugger", "Version code: " + versionCode);
                            if (!isActive) {
                                String message = mFirebaseRemoteConfig.getString(APP_MESSAGE);
                                disableApp(message);
                            } else if (versionCode > getVersionCode()) {
                                String updateMsg = mFirebaseRemoteConfig.getString(UPDATE_MESSAGE);
                                boolean isMandatory = mFirebaseRemoteConfig.getBoolean(IS_MANDATORY);
                                updateApp(updateMsg, isMandatory);
                            }
                        }
                    }
                });
    }

    private void disableApp(String message) {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("App Message")
                .content(message)
                .positiveColor(Color.parseColor(positiveTextColor))
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.finish();
                    }
                })
                .build();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void updateApp(String message, boolean isMandatory) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title("Update Available")
                .content(message)
                .positiveText("OK")
                .positiveColor(Color.parseColor(positiveTextColor))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + activity.getPackageName())));
                    }
                });

        if (!isMandatory) {
            builder.negativeText("Later")
                    .negativeColor(Color.parseColor(negativeTextColor))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    });
        }

        MaterialDialog dialog = builder.build();

        dialog.setCancelable(!isMandatory);
        dialog.setCanceledOnTouchOutside(!isMandatory);
        dialog.show();
    }

    private boolean isInDebugFlavour() {
        boolean sDebugFlavour;
        try {
            final String packageName = activity.getPackageName();
            final Class<?> buildConfig = Class.forName(packageName + ".BuildConfig");
            final Field DEBUG = buildConfig.getField("DEBUG");
            DEBUG.setAccessible(true);
            sDebugFlavour = DEBUG.getBoolean(null);
        } catch (final Throwable t) {
            sDebugFlavour = false;
        }
        return sDebugFlavour;
    }

    private int getVersionCode() {
        int v = 0;
        try {
            v = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            v = 0;
        }
        return v;
    }
}
