package infotech.atom.com.creativejewel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import infotech.atom.com.creativejewel.FCM.QuickstartPreferences;
import infotech.atom.com.creativejewel.FCM.RegistrationIntentService;

public class Splash extends AppCompatActivity {

    // Create Preference to check if application is going to be called first
    // time.
//    SharedPreferences appPref;
//    boolean isFirstTime = true;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        // Get preference value to know that is it first time application is
        // being called.
       /* appPref = getSharedPreferences("isFirstTime", 0);
        isFirstTime = appPref.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            // Create explicit intent which will be used to call Our application
            // when some one clicked on short cut
            Intent shortcutIntent = new Intent(getApplicationContext(),
                    Splash.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();

            // Create Implicit intent and assign Shortcut Application Name, Icon
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Creative Jewel");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(
                            getApplicationContext(), R.mipmap.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);

            // Set preference to inform that we have created shortcut on
            // Homescreen
            SharedPreferences.Editor editor = appPref.edit();
            editor.putBoolean("isFirstTime", false);
            editor.commit();
        }*/

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //
                } else {
//
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        Thread background = new Thread()
        {// Create Thread that will sleep for 5 seconds
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(5*1000);

                        // After 5 seconds redirect to another intent
                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {

                finish();
            }
            return false;
        }
        return true;
    }

}
