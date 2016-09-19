package infotech.atom.com.creativejewel.FCM;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import infotech.atom.com.creativejewel.network.Constants;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    long totalSize = 0;
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin";
    SharedPreferences sharedpreferencesLogin;
    SharedPreferences.Editor editorLogin;
    String token;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            token = FirebaseInstanceId.getInstance().getToken();

            Log.i("token",token);

            sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
            editorLogin = sharedpreferencesLogin.edit();
            editorLogin.putString("token", token);
            editorLogin.apply();

            // Subscribe to topic channels
            subscribeTopics(token);

            uploadGCMData(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }






    private void sendRegistrationToServer(String tokenGCM) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
    }


    private void subscribeTopics(String token) throws IOException {
        FirebaseMessaging.getInstance().subscribeToTopic("mytopic");
    }

    private void uploadGCMData(final String GCM){
        try{


            String serverUrl = Constants.CREATIVE_URL_VERSION_TWO + "app_gcm.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog

                            Log.i("success", s.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog

                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    //Creating parameters
                    Map<String,String> params = new Hashtable<String, String>();


                    //Adding parameters
                    params.put("gcm", GCM);

                    Log.i(TAG, params.toString());

                    //returning parameters
                    return params;
                }
            };

            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Adding request to the queue
            requestQueue.add(stringRequest);

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        }catch (NullPointerException npe)
        {
            Toast.makeText(getApplicationContext(), "Null pointer exception", Toast.LENGTH_SHORT).show();
        }
    }

}