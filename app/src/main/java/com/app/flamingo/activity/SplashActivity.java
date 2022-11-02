package com.app.flamingo.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

import com.app.flamingo.BuildConfig;
import com.app.flamingo.R;
import com.app.flamingo.utils.CommonMethods;
import com.app.flamingo.utils.SharePreferences;

public class SplashActivity extends AppCompatActivity {

    private FirebaseRemoteConfig mFirebaseRemoteConfig = null;
    public static final String LATEST_APPLICATION_VERSION_KEY = "Latest_Application_Version";
    private HashMap<String, Object> firebaseDefaultMap;
    private RelativeLayout mRlContainer;
    private AVLoadingIndicatorView mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonMethods.setTransparentStatusBar(SplashActivity.this);
        setContentView(R.layout.activity_splash);

        //This is default Map
        //Setting the Default Map Value with the current version code
        firebaseDefaultMap = new HashMap<>();
        firebaseDefaultMap.put(LATEST_APPLICATION_VERSION_KEY, BuildConfig.VERSION_CODE);

        init();
        checkForApplicationUpdate();
        SharePreferences.setInt(SharePreferences.KEY_VERSION_CODE,BuildConfig.VERSION_CODE);
    }

    private void init() {

        mProgressBar = findViewById(R.id.pb_check_update);
        mRlContainer = findViewById(R.id.rl_container);
    }

    //https://firebasetutorials.com/firebase-remote-config-complete-android-tutorials-with-sample-app/

    /**
     * Used to check whether application update is available or not
     */
    private void checkForApplicationUpdate() {
        if (CommonMethods.isNetworkConnected(this)) {
            if (mFirebaseRemoteConfig == null) {
                mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            }
            mFirebaseRemoteConfig.setDefaults(firebaseDefaultMap);
            //Setting that default Map to Firebase Remote Config

            //Setting Developer Mode enabled to fast retrieve the values
            mFirebaseRemoteConfig.setConfigSettings(
                    new FirebaseRemoteConfigSettings.Builder()
                            .setDeveloperModeEnabled(BuildConfig.DEBUG)
                            .setMinimumFetchIntervalInSeconds(3600)
                            .build());

            mProgressBar.setVisibility(View.VISIBLE);
            //Fetching the values here
            mFirebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        mFirebaseRemoteConfig.activateFetched();
                        Log.d("dsfasdfasd", "Fetched value: " + mFirebaseRemoteConfig.getString(LATEST_APPLICATION_VERSION_KEY));
                        //calling function to check if new version is available or not
                        checkForUpdate();
                    } else {
                        Toast.makeText(SplashActivity.this, getString(R.string.msg_something_went_wrong),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            callNextScreen();
        }
    }

    private void checkForUpdate() {
        int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(LATEST_APPLICATION_VERSION_KEY);
        if (latestAppVersion > BuildConfig.VERSION_CODE) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    SplashActivity.this);
            alertDialogBuilder.setTitle(getString(R.string.alert_title_update_available));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder
                    .setMessage(getString(R.string.alert_msg_update_available))
                    .setCancelable(false)
                    .setPositiveButton(R.string.action_update,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    final String appPackageName = getPackageName(); // package name of the app

                                    try {

                                        Uri uri = Uri.parse("market://details?id=" + appPackageName);
                                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                        // To count with Play market backstack, After pressing back button,
                                        // to taken back to our application, we need to add following flags to intent.
                                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        startActivity(goToMarket);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            })
                    .setNegativeButton(R.string.action_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            //Toast.makeText(this, "This app is already up to date", Toast.LENGTH_SHORT).show();
            callNextScreen();
        }
    }


    private void callNextScreen() {

        Thread splashTread = new Thread() {
            public void run() {
                try {
                    synchronized (this) {
                        wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {
                    if (SharePreferences.getBool(SharePreferences.KEY_IS_WELCOME_SCREEN_SHOWN, SharePreferences.DEFAULT_BOOLEAN)) {

                        if (SharePreferences.getBool(SharePreferences.KEY_IS_USER_LOGGED_IN, SharePreferences.DEFAULT_BOOLEAN)) {

                            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();

                                /*if(SharePreferences.getBool(SharePreferences.KEY_IS_ADMIN_USER,SharePreferences.DEFAULT_BOOLEAN)){
                                    startActivity(new Intent(SplashActivity.this,AdminDashboardActivity.class));
                                }else{
                                    startActivity(new Intent(SplashActivity.this,UserDashboardActivity.class));
                                }
                                finish();*/

                        } else {
                            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        };
        splashTread.start();
    }


    public void loadFragment(Fragment fragment, Bundle bundle) {
        mRlContainer.setVisibility(View.VISIBLE);
        if (fragment != null) {
            fragment.setArguments(bundle);
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right,
                                R.anim.slide_to_left,
                                R.anim.slide_from_left,
                                R.anim.slide_to_right)
                        .replace(R.id.rl_container, fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addFragment(Fragment fragment, Bundle bundle) {
        if (fragment != null) {
            fragment.setArguments(bundle);
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right,
                                R.anim.slide_to_left,
                                R.anim.slide_from_left,
                                R.anim.slide_to_right)
                        .add(R.id.rl_container, fragment).addToBackStack(null).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
